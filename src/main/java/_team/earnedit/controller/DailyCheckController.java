package _team.earnedit.controller;

import _team.earnedit.dto.dailyCheck.RewardCandidate;
import _team.earnedit.dto.dailyCheck.RewardSelectionRequest;
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

    @Operation(summary = "보상 후보 요청", description = "보상 후보 3개를 요청합니다.", security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping("/candidates")
    public ResponseEntity<ApiResponse<RewardCandidate>> getCandidates(
            @AuthenticationPrincipal JwtUserInfoDto userInfo
    ) {
        RewardCandidate rewardCandidate = dailyCheckService.generateRewardCandidates(userInfo.getUserId());

        return ResponseEntity.ok(ApiResponse
                .success("성공", rewardCandidate));
    }

    @Operation(summary = "보상 선택", description = "보상을 선택하여 Piece에 추가합니다.", security = {@SecurityRequirement(name = "bearer-key")})
    @PostMapping("/select")
    public ResponseEntity<ApiResponse<String>> selectReward(
            @AuthenticationPrincipal JwtUserInfoDto userInfo,
            @RequestBody RewardSelectionRequest request
    ) {
        dailyCheckService.selectReward(userInfo.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success("보상이 정상적으로 지급되었습니다."));
    }
}
