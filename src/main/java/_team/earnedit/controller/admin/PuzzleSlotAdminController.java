package _team.earnedit.controller.admin;

import _team.earnedit.dto.puzzle.PuzzleSlotForm;
import _team.earnedit.dto.puzzle.PuzzleSlotResponse;
import _team.earnedit.entity.PuzzleSlot;
import _team.earnedit.entity.Theme;
import _team.earnedit.repository.PuzzleSlotRepository;
import _team.earnedit.service.admin.PuzzleSlotService;
import _team.earnedit.service.admin.item.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
        List<PuzzleSlot> allSlots = puzzleSlotRepository.findAllWithItem();

        Map<Theme, List<PuzzleSlot>> grouped = allSlots.stream()
                .collect(Collectors.groupingBy(PuzzleSlot::getTheme));

        Map<Theme, List<List<PuzzleSlot>>> gridMap = new LinkedHashMap<>();

        for (Map.Entry<Theme, List<PuzzleSlot>> entry : grouped.entrySet()) {
            Theme theme = entry.getKey();
            List<PuzzleSlot> slots = entry.getValue();

            slots.sort(Comparator.comparingInt(PuzzleSlot::getSlotIndex));

            int size = (int) Math.ceil(Math.sqrt(slots.size()));
            int totalSlots = size * size;

            // 빈 슬롯을 채워 넣도록 보완
            for (int i = slots.size(); i < totalSlots; i++) {
                PuzzleSlot emptySlot = new PuzzleSlot();
                emptySlot.setId(-1L); // UI용 가짜 ID (주의: 실존 ID 아님)
                emptySlot.setItem(null);
                emptySlot.setTheme(theme);
                emptySlot.setSlotIndex(i);
                slots.add(emptySlot);
            }

            List<List<PuzzleSlot>> grid = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                List<PuzzleSlot> row = new ArrayList<>();
                for (int j = 0; j < size; j++) {
                    int index = i * size + j;
                    row.add(slots.get(index));
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
    public String replaceItem(
            @RequestParam Long slotId,
            @RequestParam Long itemId,
            @RequestParam(required = false) Theme theme,
            @RequestParam(required = false) Integer index
    ) {
        puzzleSlotService.replaceSlotItemWithFallback(slotId, itemId, theme, index);
        return "ok";
    }

    @GetMapping("/grid/{theme}")
    @ResponseBody
    public List<PuzzleSlotResponse> getPuzzleGridByTheme(@PathVariable Theme theme) {
        return puzzleSlotService.getSlotsByTheme(theme);
    }
}