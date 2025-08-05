package _team.earnedit.controller.admin;

import _team.earnedit.dto.puzzle.PuzzleSlotForm;
import _team.earnedit.entity.PuzzleSlot;
import _team.earnedit.entity.Theme;
import _team.earnedit.repository.PuzzleSlotRepository;
import _team.earnedit.service.admin.PuzzleSlotService;
import _team.earnedit.service.admin.item.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/puzzle-slots")
@RequiredArgsConstructor
public class PuzzleSlotAdminController {

    private final PuzzleSlotService puzzleSlotService;
    private final ItemService itemService;
    private final PuzzleSlotRepository puzzleSlotRepository;

    @GetMapping("/view")
    public String list(Model model) {
        model.addAttribute("slots", puzzleSlotService.findAll());
        return "admin/puzzle-slot-list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("slot", new PuzzleSlotForm());
        model.addAttribute("themes", Theme.values());
        model.addAttribute("items", itemService.findAll());
        return "admin/puzzle-slot-form";
    }

    @PostMapping
    public String save(@ModelAttribute PuzzleSlotForm form) {
        puzzleSlotService.save(form);
        return "redirect:/admin/puzzle-slots";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        PuzzleSlotForm form = puzzleSlotService.findFormById(id);
        model.addAttribute("slot", form);
        model.addAttribute("themes", Theme.values());
        model.addAttribute("items", itemService.findAll());
        return "admin/puzzle-slot-form";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        puzzleSlotService.delete(id);
        return "redirect:/admin/puzzle-slots";
    }

    @GetMapping("/grid")
    public String gridView(Model model) {
        List<PuzzleSlot> allSlots = puzzleSlotRepository.findAllWithItem(); // item까지 fetch join

        Map<Theme, List<PuzzleSlot>> grouped = allSlots.stream()
                .collect(Collectors.groupingBy(PuzzleSlot::getTheme));

        Map<Theme, List<List<PuzzleSlot>>> gridMap = new LinkedHashMap<>();

        for (Map.Entry<Theme, List<PuzzleSlot>> entry : grouped.entrySet()) {
            Theme theme = entry.getKey();
            List<PuzzleSlot> slots = entry.getValue();

            // 슬롯 인덱스 순으로 정렬
            slots.sort(Comparator.comparingInt(PuzzleSlot::getSlotIndex));

            // 정사각형 만들기 (예: 9개면 3x3)
            int size = (int) Math.ceil(Math.sqrt(slots.size()));
            List<List<PuzzleSlot>> grid = new ArrayList<>();

            for (int i = 0; i < size; i++) {
                List<PuzzleSlot> row = new ArrayList<>();
                for (int j = 0; j < size; j++) {
                    int index = i * size + j;
                    row.add(index < slots.size() ? slots.get(index) : null);
                }
                grid.add(row);
            }

            gridMap.put(theme, grid);
        }

        model.addAttribute("gridMap", gridMap);
        model.addAttribute("items", itemService.findAll());
        return "admin/puzzle-slot-grid";
    }

    @GetMapping
    public String redirectToGridView() {
        return "redirect:/admin/puzzle-slots/grid";
    }

    @PostMapping("/swap")
    @ResponseBody
    public String swapSlots(@RequestParam Long from, @RequestParam Long to) {
        puzzleSlotService.swapSlotItems(from, to);
        return "ok";
    }

    @PostMapping("/replace")
    @ResponseBody
    public String replaceItem(@RequestParam Long slotId, @RequestParam Long itemId) {
        puzzleSlotService.replaceSlotItem(slotId, itemId);
        return "ok";
    }
}
