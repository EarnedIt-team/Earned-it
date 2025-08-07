package _team.earnedit.service;

import _team.earnedit.dto.puzzle.PieceResponse;
import _team.earnedit.dto.puzzle.PuzzleResponse;
import _team.earnedit.entity.Item;
import _team.earnedit.entity.Piece;
import _team.earnedit.entity.PuzzleSlot;
import _team.earnedit.entity.Theme;
import _team.earnedit.global.ErrorCode;
import _team.earnedit.global.exception.piece.PieceException;
import _team.earnedit.global.exception.user.UserException;
import _team.earnedit.global.util.EntityFinder;
import _team.earnedit.repository.PieceRepository;
import _team.earnedit.repository.PuzzleSlotRepository;
import _team.earnedit.repository.UserRepository;
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
                .themes(responseMap)
                .build();
    }

    @Transactional(readOnly = true)
    public PieceResponse getPieceInfo(Long userId, Long pieceId) {
        entityFinder.getUserOrThrow(userId);

        Piece piece = entityFinder.getPieceOrThrow(pieceId);

        return PieceResponse.builder()
                .pieceId(piece.getId())
                .collectedAt(piece.getCollectedAt())
                .price(piece.getItem().getPrice())
                .description(piece.getItem().getDescription())
                .image(piece.getItem().getImage())
                .vendor(piece.getItem().getVendor())
                .rarity(piece.getItem().getRarity())
                .name(piece.getItem().getName())
                .build();

    }

    @Transactional(readOnly = true)
    public PieceResponse getPieceRecent(Long userId) {
        entityFinder.getUserOrThrow(userId);

        Piece piece = pieceRepository.findTopByUserIdOrderByCollectedAtDesc(userId)
                .orElseThrow(() -> new PieceException(ErrorCode.PIECE_NOT_FOUND));

        return PieceResponse.builder()
                .pieceId(piece.getId())
                .collectedAt(piece.getCollectedAt())
                .price(piece.getItem().getPrice())
                .description(piece.getItem().getDescription())
                .image(piece.getItem().getImage())
                .vendor(piece.getItem().getVendor())
                .rarity(piece.getItem().getRarity())
                .name(piece.getItem().getName())
                .build();
    }
}
