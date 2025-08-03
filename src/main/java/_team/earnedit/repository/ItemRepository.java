package _team.earnedit.repository;

import _team.earnedit.entity.Item;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query(value = "SELECT * FROM item ORDER BY RANDOM() LIMIT :count", nativeQuery = true)
    List<Item> findRandomItems(@Param("count") int count);
}
