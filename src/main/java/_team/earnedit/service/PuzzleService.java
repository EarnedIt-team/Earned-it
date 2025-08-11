package _team.earnedit.service;

import _team.earnedit.dto.puzzle.PieceResponse;
import _team.earnedit.dto.puzzle.PuzzleResponse;
import _team.earnedit.entity.Item;
import _team.earnedit.entity.Piece;
import _team.earnedit.entity.PuzzleSlot;
import _team.earnedit.entity.Theme;
import _team.earnedit.global.ErrorCode;
import _team.earnedit.global.exception.piece.PieceException;
import _team.earnedit.global.util.EntityFinder;
import _team.earnedit.mapper.PieceMapper;
import _team.earnedit.repository.PieceRepository;
import _team.earnedit.repository.PuzzleSlotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PuzzleService {

    private final PuzzleSlotRepository puzzleSlotRepository;
    private final PieceRepository pieceRepository;
    private final EntityFinder entityFinder;
    private final PieceMapper pieceMapper;

    @Transactional(readOnly = true)
    public PuzzleResponse getPuzzle(Long userId) {
        List<PuzzleSlot> slots = puzzleSlotRepository.findAll();
        List<Piece> pieces = pieceRepository.findByUserId(userId);

        Map<Long, Piece> pieceMap = pieces.stream()
                .collect(Collectors.toMap(p -> p.getItem().getId(), Function.identity()));

        Map<Theme, List<PuzzleResponse.SlotInfo>> themeSlotMap = new HashMap<>();
        Map<Theme, Integer> collectedCountMap = new HashMap<>();
        Map<Theme, Long> totalValueMap = new HashMap<>();

        for (PuzzleSlot slot : slots) {
            Item item = slot.getItem();
            Piece piece = pieceMap.get(item.getId());
            boolean isCollected = piece != null && piece.isCollected();

            PuzzleResponse.SlotInfo slotInfo = PuzzleResponse.SlotInfo.builder()
                    .slotIndex(slot.getSlotIndex())
                    .isCollected(isCollected)
                    .pieceId(isCollected ? piece.getId() : null)
                    .itemId(isCollected ? item.getId() : null)
                    .itemName(isCollected ? item.getName() : null)
                    .image(isCollected ? item.getImage() : null)
                    .value(isCollected ? item.getPrice() : null)
                    .collectedAt(isCollected ? piece.getCollectedAt() : null)
                    .build();

            Theme theme = slot.getTheme();
            themeSlotMap.computeIfAbsent(theme, t -> new ArrayList<>()).add(slotInfo);
            collectedCountMap.merge(theme, isCollected ? 1 : 0, Integer::sum);
            totalValueMap.merge(theme, isCollected ? item.getPrice() : 0, Long::sum);
        }

        // 1) 전체 테마 개수 (중복 제거)
        long themeCount = slots.stream()
                .map(PuzzleSlot::getTheme) // Theme Enum 또는 String
                .distinct()
                .count();

        int totalPieceCount = slots.size(); // 2) 전체 조각 개수
        int completedPieceCount = (int) pieces.stream()          // 3) 완성한 조각 개수
                .filter(Piece::isCollected)                      // pieces가 이미 수집된 것만 리턴한다면 .size()로 대체 가능
                .count();

        // 4) 전체 누적 금액 (수집된 아이템 가격 합)
        long totalAccumulatedValue = totalValueMap.values().stream()
                .mapToLong(Long::longValue)
                .sum();

        // 5) 현재 완성한 테마 개수 (그 테마의 모든 슬롯이 수집됨)
        int completedThemeCount = (int) themeSlotMap.entrySet().stream()
                .filter(e -> {
                    Theme t = e.getKey();
                    int total = e.getValue().size();
                    int collected = collectedCountMap.getOrDefault(t, 0);
                    return total > 0 && collected == total;
                })
                .count();

        // 퍼즐 요약 정보
        PuzzleResponse.PuzzleInfo puzzleInfo = PuzzleResponse.PuzzleInfo.builder()
                .themeCount((int) themeCount)
                .completedThemeCount(completedThemeCount)
                .totalPieceCount(totalPieceCount)
                .completedPieceCount(completedPieceCount)
                .totalAccumulatedValue(totalAccumulatedValue)
                .build();


        Map<String, PuzzleResponse.PuzzleThemeData> responseMap = new HashMap<>();
        for (Map.Entry<Theme, List<PuzzleResponse.SlotInfo>> entry : themeSlotMap.entrySet()) {
            Theme theme = entry.getKey();
            List<PuzzleResponse.SlotInfo> themeSlots = entry.getValue();

            PuzzleResponse.PuzzleThemeData themeData = PuzzleResponse.PuzzleThemeData.builder()
                    .themeName(theme.getDisplayName())
                    .collectedCount(collectedCountMap.getOrDefault(theme, 0))
                    .totalCount(themeSlots.size())
                    .totalValue(totalValueMap.getOrDefault(theme, 0L))
                    .slots(themeSlots)
                    .build();

            responseMap.put(theme.name(), themeData);
        }

        return PuzzleResponse.builder()
                .puzzleInfo(puzzleInfo)
                .themes(responseMap)
                .build();
    }

    @Transactional(readOnly = true)
    public PieceResponse getPieceInfo(Long userId, Long pieceId) {
        entityFinder.getUserOrThrow(userId);
        Piece piece = entityFinder.getPieceOrThrow(pieceId);

        return pieceMapper.toPieceResponse(piece);
    }

    @Transactional(readOnly = true)
    public PieceResponse getPieceRecent(Long userId) {
        entityFinder.getUserOrThrow(userId);

        Piece piece = pieceRepository.findTopByUserIdOrderByCollectedAtDesc(userId)
                .orElseThrow(() -> new PieceException(ErrorCode.PIECE_NOT_FOUND));

        return pieceMapper.toPieceResponse(piece);
    }


}
