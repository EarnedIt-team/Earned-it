package _team.earnedit.repository;

import _team.earnedit.entity.Item;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query(value = "SELECT * FROM item ORDER BY RANDOM() LIMIT :count", nativeQuery = true)
    List<Item> findRandomItems(@Param("count") int count);
    
    // productId로 상품 찾기
    Item findByProductId(String productId);
    
    // productId 존재 여부 확인
    boolean existsByProductId(String productId);
}
