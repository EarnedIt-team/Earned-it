package _team.earnedit.service;

import _team.earnedit.dto.dailyCheck.RewardCandidate;
import _team.earnedit.dto.dailyCheck.RewardItem;
import _team.earnedit.dto.dailyCheck.RewardSelectionRequest;
import _team.earnedit.dto.puzzle.PieceResponse;
import _team.earnedit.entity.*;
import _team.earnedit.global.ErrorCode;
import _team.earnedit.global.exception.item.ItemException;
import _team.earnedit.global.exception.user.UserException;
import _team.earnedit.global.util.EntityFinder;
import _team.earnedit.mapper.PieceMapper;
import _team.earnedit.repository.ItemRepository;
import _team.earnedit.repository.PieceRepository;
import _team.earnedit.repository.PuzzleSlotRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
    private final PieceMapper pieceMapper;

    private static final Duration REWARD_TTL = Duration.ofMinutes(10); // 10분만 유효
    private final PuzzleSlotRepository puzzleSlotRepository;

    @Transactional
    public PieceResponse addPieceToPuzzle(Long userId, long itemId) {
        log.info("[DailyCheckService] 퍼즐에 조각 추가 요청 - userId = {}, itemId = {}", userId, itemId);
        User user = entityFinder.getUserOrThrow(userId);
        Item item = entityFinder.getItemOrThrow(itemId);

        // 이미 해당 itemId가 퍼즐에 등록되어있을 때
        checkAlreadyAddedToPuzzle(user, item);

        // Piece 저장
        Piece piece = savePiece(user, item);

        log.info("[DailyCheckService] 퍼즐에 조각 추가 성공 - userId = {}, itemId = {}", userId, itemId);
        return pieceMapper.toPieceResponse(piece);
    }

    @Transactional
    public RewardCandidate generateRewardCandidates(Long userId) {
        log.info("[DailyCheckService] 출석 보상 목록 생성 요청 - userId = {}", userId);
        User user = entityFinder.getUserOrThrow(userId);

        // 이미 출석 했다면 보상 선택 불가
        if(user.getIsCheckedIn()) throw new UserException(ErrorCode.ALREADY_REWARDED);

        return randomRewardPickAndGenerateToken(userId);
    }

    @Transactional
    public void selectReward(Long userId, RewardSelectionRequest request) {
        log.info("[DailyCheckService] 출석 보상 요청  - userId = {}", userId);
        User user = entityFinder.getUserOrThrow(userId);
        Item item = entityFinder.getItemOrThrow(request.getSelectedItemId());

        // 이미 보상이 지급된 회원 예외
        validateRewardNotAlreadyClaimed(user);

        String key = "reward:" + request.getRewardToken();

        // redis로부터 보상후보 조회
        List<Long> candidateIds = getCandidateIdsFromRedis(key);

        // 선택한 아이템이 보상 후보에서 선택됐는지 검증
        validateSelectedItemInCandidates(userId, request, candidateIds);

        // 출석 상태 즉시 업데이트 & 저장 & 트랜잭션 보장
        updateUserCheckedIn(userId);

        // 출석 시 점수 제공
        user.checkedInReward();

        // 이미 해당 아이템이 퍼즐에 추가되어있는지 검증
        checkAlreadyAddedToPuzzle(user, item);

        // piece 저장
        savePiece(user, item);

        // 여기서 퍼즐 테마 완성 여부 체크
        rewardIfThemeCompleted(user, item);

        // 레어도에 따라 점수 차등 지급
        Rarity rarity = item.getRarity();
        rewardScoreToUser(user, rarity);

        log.info("[DailyCheckService] 출석 보상 정상 지급  - userId = {}", userId);
        redisTemplate.delete(key);
    }

    // ------------------------------------------ 아래는 메서드 ------------------------------------------ //

    // 해당 조각으로 완성된 테마가 있다면 100pt 지급
    private void rewardIfThemeCompleted(User user, Item item) {

        // 해당 아이템의 테마 확인
        Theme theme = puzzleSlotRepository.findByItem(item).getTheme();

        // 테마들을 순회
        List<PuzzleSlot> themeSlots = puzzleSlotRepository.findByTheme(theme);

        // 해당 테마에 속한 아이템들의 id 리스트
        List<Long> itemIdList = themeSlots.stream()
                .map(PuzzleSlot::getItem)
                .map(Item::getId)
                .toList();

        // 해당 유저가 그 아이디의 아이템들을 전부 가지고 있는지 확인
        Set<Long> userItemIds = pieceRepository.findByUserId(user.getId()).stream()
                .map(piece -> piece.getItem().getId())
                .collect(Collectors.toSet());

        // 유저가 가진 아이템 집합에 테망의 아이템 집합이 속하는지 확인
        boolean completed = userItemIds.containsAll(itemIdList);

        // 만약 테마 완성이 됐다면 100 지급
        if(completed) {
            user.reward_CompleteTheme();
        }
    }

    // 아이템 등급에 따른 보상 지급
    private void rewardScoreToUser(User user, Rarity rarity) {
        // 출석 보상으로 점수 제공
        switch (rarity) {
            case S:
                user.reward_S();
                break;
            case A:
                user.reward_A();
                break;
            case B:
                user.reward_B();
                break;
            default:
                break;
        }
    }

    // redis로부터 보상후보 조회
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
                return objectMapper.readValue(json, new TypeReference<>() {
                });
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

    // 조각 저장
    private Piece savePiece(User user, Item item) {
        Piece piece = Piece.builder()
                .user(user)
                .item(item)
                .isCollected(true)
                .build();
        pieceRepository.save(piece);
        return piece;
    }

    // 이미 유저가 해당 아이템을 퍼즐에 가지고 있는지 확인
    private void checkAlreadyAddedToPuzzle(User user, Item item) {
        List<Piece> pieceList = pieceRepository.findByItemAndUser(item, user);

        if (!pieceList.isEmpty()) {
            log.warn("[DailyCheckService] 이미 퍼즐에 등록된 아이템 - userId = {}, itemId = {}", user.getId(), item.getId());
            throw new ItemException(ErrorCode.PIECE_ALREADY_ADD);
        }
    }

    // 랜덤 보상 3개를 생성하고, 임의의 토큰을 생성하여 Redis에 저장
    private RewardCandidate randomRewardPickAndGenerateToken(long userId) {
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

        log.info("[DailyCheckService] 출석 보상 목록 생성 성공 - userId = {}", userId);
        return RewardCandidate.builder()
                .rewardToken(rewardToken)
                .candidates(candidates)
                .build();
    }

    // 해당 유저의 출석 상태를 업데이트 합니다. (트랜 잭션 보장)
    private void updateUserCheckedIn(Long userId) {
        rewardCheckInService.checkInUser(userId);
        log.info("[DailyCheckService] 출석 상태 업데이트 - userId = {}", userId);
    }

    // 선택한 아이템이 보상 후보에 선택됐는지 검증
    private void validateSelectedItemInCandidates(Long userId, RewardSelectionRequest request, List<Long> candidateIds) {
        if (!candidateIds.contains(request.getSelectedItemId())) {
            log.warn("[DailyCheckService] 보상목록에 포함되지 않은 아이템으로 요청 - userId = {}, itemId = {}", userId, request.getSelectedItemId());
            throw new IllegalArgumentException("유효하지 않은 보상 선택입니다.");
        }
    }

    // 이미 보상이 지급된 회원 예외
    private void validateRewardNotAlreadyClaimed(User user) {
        if (user.getIsCheckedIn()) {
            throw new UserException(ErrorCode.ALREADY_REWARDED);
        }
    }
}
