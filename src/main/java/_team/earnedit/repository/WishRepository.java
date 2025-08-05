package _team.earnedit.repository;

import _team.earnedit.entity.User;
import _team.earnedit.entity.Wish;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishRepository extends JpaRepository<Wish, Long> {
    List<Wish> findByUserIdOrderByNameAsc(Long userId);
    int countByUser(User user);
}
