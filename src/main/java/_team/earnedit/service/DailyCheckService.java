package _team.earnedit.service;

import _team.earnedit.dto.puzzle.PieceResponse;
import _team.earnedit.entity.Item;
import _team.earnedit.entity.Piece;
import _team.earnedit.entity.User;
import _team.earnedit.global.ErrorCode;
import _team.earnedit.global.exception.item.ItemException;
import _team.earnedit.global.exception.user.UserException;
import _team.earnedit.repository.ItemRepository;
import _team.earnedit.repository.PieceRepository;
import _team.earnedit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class DailyCheckService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final PieceRepository pieceRepository;

    @Transactional
    public PieceResponse addPieceToPuzzle(Long userId, long itemId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemException(ErrorCode.ITEM_NOT_FOUND));

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
}
