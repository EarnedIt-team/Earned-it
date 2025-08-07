package _team.earnedit.repository;

import _team.earnedit.entity.PuzzleSlot;
import _team.earnedit.entity.Theme;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PuzzleSlotRepository extends JpaRepository<PuzzleSlot, Long> {
    @Query("SELECT ps FROM PuzzleSlot ps JOIN FETCH ps.item")
    List<PuzzleSlot> findAllWithItem();

    List<PuzzleSlot> findByThemeOrderBySlotIndexAsc(Theme theme);

    @Modifying
    @Query("DELETE FROM PuzzleSlot ps WHERE ps.item.id = :itemId")
    void deleteByItemId(@Param("itemId") Long itemId);
}
