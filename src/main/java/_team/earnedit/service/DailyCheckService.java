package _team.earnedit.service;

import _team.earnedit.dto.dailyCheck.RewardCandidate;
import _team.earnedit.dto.dailyCheck.RewardItem;
import _team.earnedit.dto.dailyCheck.RewardSelectionRequest;
import _team.earnedit.dto.puzzle.PieceResponse;
import _team.earnedit.entity.Item;
import _team.earnedit.entity.Piece;
import _team.earnedit.entity.User;
import _team.earnedit.global.ErrorCode;
import _team.earnedit.global.exception.item.ItemException;
import _team.earnedit.global.exception.piece.PieceException;
import _team.earnedit.global.exception.user.UserException;
import _team.earnedit.global.util.EntityFinder;
import _team.earnedit.repository.ItemRepository;
import _team.earnedit.repository.PieceRepository;
import _team.earnedit.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class DailyCheckService {

    private final ObjectMapper objectMapper;

    private final ItemRepository itemRepository;
    private final PieceRepository pieceRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final EntityFinder entityFinder;
    private final RewardCheckInService rewardCheckInService;

    private static final Duration REWARD_TTL = Duration.ofMinutes(10); // 10분만 유효

    @Transactional
    public PieceResponse addPieceToPuzzle(Long userId, long itemId) {
        log.info("[DailyCheckService] 퍼즐에 조각 추가 요청 - userId: {}, itemId: {}", userId, itemId);
        User user = entityFinder.getUserOrThrow(userId);
        Item item = entityFinder.getItemOrThrow(itemId);

        List<Piece> pieceList = pieceRepository.findByItemAndUser(item, user);

        // 이미 해당 itemId가 퍼즐에 등록되어있을 때
        if (!pieceList.isEmpty()) {
            log.warn("[DailyCheckService] 이미 퍼즐에 등록된 아이템 - userId: {}, itemId: {}", userId, itemId);
            throw new ItemException(ErrorCode.PIECE_ALREADY_ADD);
        }

        Piece piece = Piece.builder()
                .user(user)
                .item(item)
                .isCollected(true)
                .build();
        pieceRepository.save(piece);

        log.info("[DailyCheckService] 퍼즐에 조각 추가 성공 - userId: {}, itemId: {}", userId, itemId);
        return PieceResponse.builder()
                .pieceId(piece.getId())
                .name(piece.getItem().getName())
                .vendor(piece.getItem().getVendor())
                .image(piece.getItem().getImage())
                .price(piece.getItem().getPrice())
                .description(piece.getItem().getDescription())
                .rarity(piece.getItem().getRarity())
                .collectedAt(piece.getCollectedAt())
                .build();
    }

    @Transactional
    public RewardCandidate generateRewardCandidates(Long userId) {
        log.info("[DailyCheckService] 출석 보상 목록 생성 요청 - userId: {}", userId);
        entityFinder.getUserOrThrow(userId);

        List<Item> randomItems = itemRepository.findRandomItems(3);
        UUID rewardToken = UUID.randomUUID();
        List<Long> candidateIds = randomItems.stream().map(Item::getId).toList();

        try {
            String json = objectMapper.writeValueAsString(candidateIds);
            redisTemplate.opsForValue().set("reward:" + rewardToken, json, REWARD_TTL);
        } catch (Exception e) {
            throw new RuntimeException("Redis 저장 실패", e);
        }

        List<RewardItem> candidates = randomItems.stream()
                .map(item -> RewardItem.builder()
                        .itemId(item.getId())
                        .name(item.getName())
                        .image(item.getImage())
                        .price(item.getPrice())
                        .build())
                .toList();


        log.info("[DailyCheckService] 출석 보상 목록 생성 성공 - userId: {}", userId);
        return RewardCandidate.builder()
                .rewardToken(rewardToken)
                .candidates(candidates)
                .build();
    }


    @Transactional
    public void selectReward(Long userId, RewardSelectionRequest request) {
        log.info("[DailyCheckService] 출석 보상 요청  - userId: {}", userId);
        User user = entityFinder.getUserOrThrow(userId);
        Item item = entityFinder.getItemOrThrow(request.getSelectedItemId());

        // 이미 보상이 지급된 회원 예외
        if (user.getIsCheckedIn()) {
            throw new UserException(ErrorCode.ALREADY_REWARDED);
        }
        String key = "reward:" + request.getRewardToken();

        // 리팩토링된 메소드 사용
        List<Long> candidateIds = getCandidateIdsFromRedis(key);

        if (!candidateIds.contains(request.getSelectedItemId())) {
            log.warn("[DailyCheckService] 보상목록에 포함되지 않은 아이템으로 요청 - userId: {}, itemId: {}", userId, request.getSelectedItemId());
            throw new IllegalArgumentException("유효하지 않은 보상 선택입니다.");
        }

        // 출석 상태 즉시 업데이트 & 저장 & 트랜잭션 보장
        rewardCheckInService.checkInUser(userId);
        log.info("[DailyCheckService] 출석 상태 업데이트 - userId: {}", userId);

        List<Piece> pieceList = pieceRepository.findByItemAndUser(item, user);

        // 이미 해당 아이템이 퍼즐에 추가되어있는지 검증
        if (!pieceList.isEmpty()) {
            log.warn("[DailyCheckService] 이미 퍼즐에 등록된 조각 추가 시도 - userId: {}, itemId: {}", userId, request.getSelectedItemId());
            throw new PieceException(ErrorCode.PIECE_ALREADY_ADD); // 이미 추가된 조각이라고 예외 던짐
        }

        pieceRepository.save(Piece.builder()
                .user(user)
                .item(item)
                .isCollected(true)
                .collectedAt(LocalDateTime.now())
                .build());
        log.info("[DailyCheckService] 출석 보상 정상 지급  - userId: {}", userId);

        redisTemplate.delete(key);
    }

    private List<Long> getCandidateIdsFromRedis(String key) {
        try {
            String json;
            try {
                json = redisTemplate.opsForValue().get(key);
            } catch (Exception redisEx) {
                throw new RuntimeException("Redis 조회 실패: 네트워크 또는 연결 문제", redisEx);
            }

            if (json == null) {
                throw new IllegalArgumentException("보상 정보가 없습니다. (키: " + key + ")");
            }

            try {
                return objectMapper.readValue(json, new TypeReference<>() {});
            } catch (Exception parseEx) {
                throw new RuntimeException(
                        "Redis 데이터 파싱 실패: 잘못된 JSON 형식 (키: " + key + ", 값: " + json + ")", parseEx
                );
            }

        } catch (RuntimeException e) {
            throw e; // 이미 상세 메시지 포함됨
        } catch (Exception e) {
            throw new RuntimeException("Redis 조회 처리 중 알 수 없는 오류 발생", e);
        }
    }
}
