package _team.earnedit.global.util;

import _team.earnedit.entity.Item;
import _team.earnedit.entity.Piece;
import _team.earnedit.entity.User;
import _team.earnedit.entity.Wish;
import _team.earnedit.global.ErrorCode;
import _team.earnedit.global.exception.item.ItemException;
import _team.earnedit.global.exception.piece.PieceException;
import _team.earnedit.global.exception.user.UserException;
import _team.earnedit.global.exception.wish.WishException;
import _team.earnedit.repository.ItemRepository;
import _team.earnedit.repository.PieceRepository;
import _team.earnedit.repository.UserRepository;
import _team.earnedit.repository.WishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EntityFinder {

    private final UserRepository userRepository;
    private final WishRepository wishRepository;
    private final PieceRepository pieceRepository;
    private final ItemRepository itemRepository;

    public User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
    }

    public Wish getWishOrThrow(Long wishId) {
        return wishRepository.findById(wishId)
                .orElseThrow(() -> new WishException(ErrorCode.WISH_NOT_FOUND));
    }

    public Piece getPieceOrThrow(Long pieceId) {
        return  pieceRepository.findById(pieceId)
                .orElseThrow(() -> new PieceException(ErrorCode.PIECE_NOT_FOUND));
    }

    public Item getItemOrThrow(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemException(ErrorCode.ITEM_NOT_FOUND));
    }
}
