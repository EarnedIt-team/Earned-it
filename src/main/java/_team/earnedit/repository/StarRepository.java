package _team.earnedit.repository;

import _team.earnedit.entity.Star;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StarRepository extends JpaRepository<Star, Long> {

    int countByUserId(long userId);

    void deleteByUserIdAndWishId(Long userId, Long wishId);

    List<Star> findByUserIdOrderByRankAsc(Long userId);

    List<Star> findByUserId(Long userId);

    void deleteByWishId(Long wishId);

    boolean existsByUserIdAndWishId(Long userId, Long wishId);
}