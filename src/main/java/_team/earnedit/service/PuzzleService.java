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
import _team.earnedit.mapper.PuzzleMapper;
import _team.earnedit.repository.PieceRepository;
import _team.earnedit.repository.PuzzleSlotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
    private final PuzzleMapper puzzleMapper;

    @Transactional(readOnly = true)
    public PuzzleResponse getPuzzle(Long userId) {
        List<PuzzleSlot> slots = puzzleSlotRepository.findAll();
        List<Piece> pieces = pieceRepository.findByUserId(userId);
        Map<Long, Piece> pieceMap = buildPieceMap(pieces);

        // 1. 테마 별 슬롯/카운트/금액 집계
        Map<Theme, List<PuzzleResponse.SlotInfo>> themeSlotMap = new HashMap<>();
        Map<Theme, Integer> collectedCountMap = new HashMap<>();
        Map<Theme, Long> totalValueMap = new HashMap<>();

        for (PuzzleSlot slot : slots) {
            Item item = slot.getItem();
            Piece piece = pieceMap.get(item.getId());

            PuzzleResponse.SlotInfo slotInfo = puzzleMapper.toSlotInfo(slot, piece);
            Theme theme = slot.getTheme();

            themeSlotMap.computeIfAbsent(theme, t -> new ArrayList<>()).add(slotInfo);
            boolean isCollected = piece != null && piece.isCollected();
            collectedCountMap.merge(theme, isCollected ? 1 : 0, Integer::sum);
            totalValueMap.merge(theme, isCollected ? item.getPrice() : 0, Long::sum);
        }

        // 2. 요약값 계산
        Summary summary = computeSummary(slots, pieces, themeSlotMap, collectedCountMap, totalValueMap);

        // 3. 퍼즐 요약 정보
        PuzzleResponse.PuzzleInfo puzzleInfo = getPuzzleInfo(summary);

        // 4. 테마 응답 변환
        Map<String, PuzzleResponse.PuzzleThemeData> themes = buildThemes(themeSlotMap, collectedCountMap, totalValueMap);

        return PuzzleResponse.builder()
                .puzzleInfo(puzzleInfo)
                .themes(themes)
                .build();
    }

    // 퍼즐 요약 정보
    private PuzzleResponse.PuzzleInfo getPuzzleInfo(Summary summary) {
        return PuzzleResponse.PuzzleInfo.builder()
                .themeCount(summary.themeCount)
                .completedThemeCount(summary.completedThemeCount)
                .totalPieceCount(summary.totalPieceCount)
                .completedPieceCount(summary.completedPieceCount)
                .totalAccumulatedValue(summary.totalAccumulatedValue)
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

    // 요약 정보 생성 메서드
    private Summary computeSummary(
            List<PuzzleSlot> slots,
            List<Piece> pieces,
            Map<Theme, List<PuzzleResponse.SlotInfo>> themeSlotMap,
            Map<Theme, Integer> collectedCountMap,
            Map<Theme, Long> totalValueMap
    ) {
        int totalPieceCount = slots.size();
        int completedPieceCount = (int) pieces.stream().filter(Piece::isCollected).count();

        long themeCount = slots.stream()
                .map(PuzzleSlot::getTheme)
                .distinct()
                .count();

        long totalAccumulatedValue = totalValueMap.values().stream()
                .mapToLong(Long::longValue)
                .sum();

        int completedThemeCount = (int) themeSlotMap.entrySet().stream()
                .filter(e -> {
                    int total = e.getValue().size();
                    int collected = collectedCountMap.getOrDefault(e.getKey(), 0);
                    return total > 0 && collected == total;
                }).count();

        return new Summary(
                (int) themeCount,
                completedThemeCount,
                totalPieceCount,
                completedPieceCount,
                totalAccumulatedValue
        );
    }

    private Map<Long, Piece> buildPieceMap(List<Piece> pieces) {
        return pieces.stream().collect(Collectors.toMap(p -> p.getItem().getId(), Function.identity()));
    }

    private Map<String, PuzzleResponse.PuzzleThemeData> buildThemes(
            Map<Theme, List<PuzzleResponse.SlotInfo>> themeSlotMap,
            Map<Theme, Integer> collectedCountMap,
            Map<Theme, Long> totalValueMap
    ) {
        return themeSlotMap.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().name(),
                        e -> PuzzleResponse.PuzzleThemeData.builder()
                                .themeName(e.getKey().getDisplayName())
                                .collectedCount(collectedCountMap.getOrDefault(e.getKey(), 0))
                                .totalCount(e.getValue().size())
                                .totalValue(totalValueMap.getOrDefault(e.getKey(), 0L))
                                .slots(e.getValue())
                                .build(),
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    // 요약값 레코드
    private record Summary (
            int themeCount,
            int completedThemeCount,
            int totalPieceCount,
            int completedPieceCount,
            long totalAccumulatedValue
    ) {}

}
