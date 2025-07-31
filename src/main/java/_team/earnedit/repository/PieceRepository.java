package _team.earnedit.repository;

import _team.earnedit.entity.Item;
import _team.earnedit.entity.Piece;
import _team.earnedit.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PieceRepository extends JpaRepository<Piece, Long> {
    List<Piece> user(User user);
    List<Piece> findByUserId(long userId);
    List<Piece> findByItemAndUser(Item item, User user);
    Optional<Piece> findTopByUserIdOrderByCollectedAtDesc(Long userId);
}
