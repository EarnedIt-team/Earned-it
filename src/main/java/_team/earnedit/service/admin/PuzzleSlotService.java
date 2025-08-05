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

    @Transactional
    public void swapSlots(Long sourceId, Long targetId) {
        PuzzleSlot source = puzzleSlotRepository.findById(sourceId)
                .orElseThrow(() -> new IllegalArgumentException("소스 슬롯이 존재하지 않음"));
        PuzzleSlot target = puzzleSlotRepository.findById(targetId)
                .orElseThrow(() -> new IllegalArgumentException("타겟 슬롯이 존재하지 않음"));

        // 아이템 교환
        Item temp = source.getItem();
        source.setItem(target.getItem());
        target.setItem(temp);
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

    @Transactional
    public void swapSlotItems(Long fromId, Long toId) {
        PuzzleSlot from = puzzleSlotRepository.findById(fromId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid fromId"));
        PuzzleSlot to = puzzleSlotRepository.findById(toId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid toId"));

        Item temp = from.getItem();
        from.setItem(to.getItem());
        to.setItem(temp);
    }

    @Transactional
    public void replaceSlotItem(Long slotId, Long itemId) {
        PuzzleSlot slot = puzzleSlotRepository.findById(slotId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid slotId"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid itemId"));

        slot.setItem(item);
    }
}