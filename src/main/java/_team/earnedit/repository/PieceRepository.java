package _team.earnedit.repository;

import _team.earnedit.entity.Piece;
import _team.earnedit.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PieceRepository extends JpaRepository<Piece, Long> {
    List<Piece> user(User user);
    List<Piece> findByUserId(long userId);
}
