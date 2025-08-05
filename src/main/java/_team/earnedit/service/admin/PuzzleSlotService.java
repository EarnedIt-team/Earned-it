package _team.earnedit.service.admin;

import _team.earnedit.dto.puzzle.PuzzleSlotForm;
import _team.earnedit.dto.puzzle.PuzzleSlotResponse;
import _team.earnedit.entity.Item;
import _team.earnedit.entity.PuzzleSlot;
import _team.earnedit.entity.Theme;
import _team.earnedit.repository.ItemRepository;
import _team.earnedit.repository.PuzzleSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PuzzleSlotService {

    private final PuzzleSlotRepository puzzleSlotRepository;
    private final ItemRepository itemRepository;

    public List<PuzzleSlot> findAll() {
        return puzzleSlotRepository.findAll();
    }

    public PuzzleSlotForm findFormById(Long id) {
        PuzzleSlot slot = puzzleSlotRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 퍼즐 슬롯입니다."));
        PuzzleSlotForm form = new PuzzleSlotForm();
        form.setId(slot.getId());
        form.setItemId(slot.getItem().getId());
        form.setTheme(slot.getTheme());
        form.setSlotIndex(slot.getSlotIndex());
        return form;
    }

    public void save(PuzzleSlotForm form) {
        Item item = itemRepository.findById(form.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이템입니다."));
        PuzzleSlot slot = new PuzzleSlot(
                form.getId() == null ? 0L : form.getId(),
                item,
                form.getTheme(),
                form.getSlotIndex()
        );
        puzzleSlotRepository.save(slot);
    }

    public void delete(Long id) {
        puzzleSlotRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<PuzzleSlotResponse> getAllSlots() {
        List<PuzzleSlot> slots = puzzleSlotRepository.findAllWithItem();
        return slots.stream()
                .map(PuzzleSlotResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PuzzleSlotResponse> getSlotsByTheme(Theme theme) {
        List<PuzzleSlot> slots = puzzleSlotRepository.findByThemeOrderBySlotIndexAsc(theme);
        return slots.stream()
                .map(PuzzleSlotResponse::from)
                .toList();
    }

    // ✅ 슬롯 간 아이템 스왑 처리
    @Transactional
    public void swapSlotItems(Long fromId, Long toId) {
        PuzzleSlot from = puzzleSlotRepository.findById(fromId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid fromId")); // 🔧 추가된 예외 메시지
        PuzzleSlot to = puzzleSlotRepository.findById(toId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid toId")); // 🔧 추가된 예외 메시지

        Item temp = from.getItem(); // 🔧 기존 아이템 보관
        from.setItem(to.getItem());
        to.setItem(temp);
    }

    @Transactional
    public void replaceSlotItem(Long slotId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid itemId"));

        if (slotId == -1L) {
            // 💡 UI 상의 빈 슬롯에 새 퍼즐 슬롯을 생성하는 로직
            throw new IllegalArgumentException("slotId가 -1이면 테마 및 슬롯 위치 정보가 필요합니다.");
        }

        PuzzleSlot slot = puzzleSlotRepository.findById(slotId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid slotId"));
        slot.setItem(item);
    }

    @Transactional
    public void replaceSlotItemWithFallback(Long slotId, Long itemId, Theme theme, Integer slotIndex) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid itemId"));

        if (slotId == -1L) {
            if (theme == null || slotIndex == null) {
                throw new IllegalArgumentException("Theme과 slotIndex는 필수입니다 (빈 슬롯에 삽입 시)");
            }

            PuzzleSlot newSlot = new PuzzleSlot();
            newSlot.setItem(item);
            newSlot.setTheme(theme);
            newSlot.setSlotIndex(slotIndex);
            puzzleSlotRepository.save(newSlot);
        } else {
            PuzzleSlot slot = puzzleSlotRepository.findById(slotId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid slotId"));
            slot.setItem(item);
        }
    }

    @Transactional
    public void createSlotAndAssignItem(Theme theme, int slotIndex, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이템입니다."));

        PuzzleSlot slot = new PuzzleSlot();
        slot.setTheme(theme);
        slot.setSlotIndex(slotIndex);
        slot.setItem(item);

        puzzleSlotRepository.save(slot);
    }

}