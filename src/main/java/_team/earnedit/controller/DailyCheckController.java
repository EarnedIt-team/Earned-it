package _team.earnedit.controller;

import _team.earnedit.dto.jwt.JwtUserInfoDto;
import _team.earnedit.dto.puzzle.PieceResponse;
import _team.earnedit.global.ApiResponse;
import _team.earnedit.service.DailyCheckService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/daily-check")
@Tag(name = "출석체크", description = "출석체크 API")
public class DailyCheckController {

    private final DailyCheckService dailyCheckService;

    @Operation(summary = "퍼즐에 조각 추가", description = "퍼즐에 해당 아이템을 추가합니다.", security = {@SecurityRequirement(name = "bearer-key")})
    @PostMapping("/{itemId}")
    public ResponseEntity<ApiResponse<PieceResponse>> addPieceToPuzzle(
            @AuthenticationPrincipal JwtUserInfoDto userInfo,
            @PathVariable long itemId) {

        PieceResponse response = dailyCheckService.addPieceToPuzzle(userInfo.getUserId(), itemId);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("조각이 퍼즐에 성공적으로 등록되었습니다.", response));
    }
}
