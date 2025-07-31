package _team.earnedit.controller;

import _team.earnedit.dto.jwt.JwtUserInfoDto;
import _team.earnedit.dto.puzzle.PieceResponse;
import _team.earnedit.dto.puzzle.PuzzleResponse;
import _team.earnedit.entity.Piece;
import _team.earnedit.global.ApiResponse;
import _team.earnedit.service.PuzzleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PuzzleController {
    private final PuzzleService puzzleService;

    @Operation(summary = "퍼즐 정보 조회", description = "사용자의 퍼즐 정보를 조회합니다.", security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping("/puzzle")
    public ResponseEntity<ApiResponse<PuzzleResponse>> getPuzzleInfo(@AuthenticationPrincipal JwtUserInfoDto userInfo) {
        PuzzleResponse response = puzzleService.getPuzzle(userInfo.getUserId());
        return ResponseEntity.ok(ApiResponse.success("퍼즐 정보를 불러왔습니다.", response));
    }

    @Operation(summary = "조각 정보 조회", description = "조각의 상세 정보를 조회합니다.", security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping("/piece/{pieceId}")
    public ResponseEntity<ApiResponse<PieceResponse>> getPieceInfo(
            @AuthenticationPrincipal JwtUserInfoDto userInfo,
            @PathVariable Long pieceId
    ) {
        PieceResponse pieceInfo = puzzleService.getPieceInfo(userInfo.getUserId(), pieceId);
        return ResponseEntity.ok(ApiResponse.success("퍼즐 정보를 불러왔습니다.", pieceInfo));
    }


}

