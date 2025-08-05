package _team.earnedit.controller.admin;

import _team.earnedit.dto.puzzle.PuzzleSlotResponse;
import _team.earnedit.dto.puzzle.PuzzleSlotSwapRequest;
import _team.earnedit.entity.Theme;
import _team.earnedit.service.admin.PuzzleSlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/puzzle-slots")
@RequiredArgsConstructor
public class PuzzleSlotAdminApiController {

    private final PuzzleSlotService puzzleSlotService;

    @PostMapping("/swap")
    public ResponseEntity<Void> swapSlots(@RequestBody PuzzleSlotSwapRequest request) {
        puzzleSlotService.swapSlotItems(request.getSourceSlotId(), request.getTargetSlotId());
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<PuzzleSlotResponse>> getAllSlots() {
        List<PuzzleSlotResponse> slots = puzzleSlotService.getAllSlots();
        return ResponseEntity.ok(slots);
    }

    @GetMapping(params = "theme")
    public ResponseEntity<List<PuzzleSlotResponse>> getSlotsByTheme(@RequestParam Theme theme) {
        List<PuzzleSlotResponse> slots = puzzleSlotService.getSlotsByTheme(theme);
        return ResponseEntity.ok(slots);
    }
}