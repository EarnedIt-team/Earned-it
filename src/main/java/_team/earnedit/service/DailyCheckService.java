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
import _team.earnedit.global.exception.user.UserException;
import _team.earnedit.global.util.EntityFinder;
import _team.earnedit.repository.ItemRepository;
import _team.earnedit.repository.PieceRepository;
import _team.earnedit.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class DailyCheckService {

    private final ObjectMapper objectMapper;

    private final ItemRepository itemRepository;
    private final PieceRepository pieceRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final EntityFinder entityFinder;

    private static final Duration REWARD_TTL = Duration.ofMinutes(10); // 10분만 유효
    private final UserRepository userRepository;

    @Transactional
    public PieceResponse addPieceToPuzzle(Long userId, long itemId) {
        User user = entityFinder.getUserOrThrow(userId);
        Item item = entityFinder.getItemOrThrow(itemId);

        List<Piece> pieceList = pieceRepository.findByItemAndUser(item, user);

        // 이미 해당 itemId가 퍼즐에 등록되어있을 때
        if (!pieceList.isEmpty()) {
            throw new ItemException(ErrorCode.PIECE_ALREADY_ADD);
        }

        Piece piece = Piece.builder()
                .user(user)
                .item(item)
                .isCollected(true)
                .build();
        pieceRepository.save(piece);

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

        return RewardCandidate.builder()
                .rewardToken(rewardToken)
                .candidates(candidates)
                .build();
    }


    @Transactional
    public void selectReward(Long userId, RewardSelectionRequest request) {
        User user = entityFinder.getUserOrThrow(userId);
        Item item = entityFinder.getItemOrThrow(request.getSelectedItemId());

        // 이미 보상이 지급된 회원 예외
        if (user.getIsCheckedIn()) {
            throw new UserException(ErrorCode.ALREADY_REWARDED);
        }

        String key = "reward:" + request.getRewardToken();

        List<Long> candidateIds;
        try {
            String json = redisTemplate.opsForValue().get(key);
            if (json == null) throw new IllegalArgumentException("보상 정보가 없습니다.");
            candidateIds = objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException("Redis 조회 실패", e);
        }

        if (!candidateIds.contains(request.getSelectedItemId())) {
            throw new IllegalArgumentException("유효하지 않은 보상 선택입니다.");
        }

        pieceRepository.save(Piece.builder()
                .user(user)
                .item(item)
                .isCollected(true)
                .collectedAt(LocalDateTime.now())
                .build());

        redisTemplate.delete(key);

        // 출석 체크 여부 업데이트
        user.checkIn();
    }
}
