//package _team.earnedit.repository;
//
//import _team.earnedit.entity.SearchItem;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//
//import java.util.List;
//
//public interface SearchItemRepository extends JpaRepository<SearchItem, Long> {
//
//    // productId로 검색
//    SearchItem findByProductId(String productId);
//
//    // productId 존재 여부 확인
//    boolean existsByProductId(String productId);
//
//    // 이름으로 검색 (캐시 조회용)
//    @Query("SELECT s FROM SearchItem s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :query, '%')) ORDER BY s.id DESC")
//    List<SearchItem> findByNameContainingIgnoreCase(@Param("query") String query);
//}