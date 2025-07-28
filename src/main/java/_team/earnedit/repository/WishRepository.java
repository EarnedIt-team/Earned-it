package _team.earnedit.repository;

import _team.earnedit.entity.Wish;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WishRepository extends JpaRepository<Wish, Long> {
    List<Wish> findByUserId(Long userId);

    @Query("SELECT COUNT(w) FROM Wish w WHERE w.user.id = :userId AND w.isStarred = true")
    int countStarredByUserId(@Param("userId") Long userId);
}
