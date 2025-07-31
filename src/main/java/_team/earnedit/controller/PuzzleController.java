package _team.earnedit.controller;

import _team.earnedit.dto.jwt.JwtUserInfoDto;
import _team.earnedit.global.ApiResponse;
import _team.earnedit.service.PuzzleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/puzzle")
public class PuzzleController {
    private final PuzzleService puzzleService;

    @GetMapping
    @Operation(summary = "", description = "", security = {@SecurityRequirement(name = "bearer-key")})
    public ResponseEntity<ApiResponse<String>> getPuzzle(
            @AuthenticationPrincipal JwtUserInfoDto userInfoDto) {
        puzzleService.getPuzzle(userInfoDto.getUserId());

    }


}

