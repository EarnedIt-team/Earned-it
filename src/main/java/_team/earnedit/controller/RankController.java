package _team.earnedit.controller;

import _team.earnedit.dto.jwt.JwtUserInfoDto;
import _team.earnedit.dto.rank.RankPageResponse;
import _team.earnedit.global.ApiResponse;
import _team.earnedit.service.RankService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rank")
@Tag(name = "Rank", description = "Rank API")
public class RankController {

    private final RankService rankService;

    @GetMapping
    @Operation(summary = "랭킹 페이지 조회",
            description = "랭킹 페이지의 정보를 조회합니다 \n" +
                    "내 랭크 정보, top10 정보 조회",
            security = {@SecurityRequirement(name = "bearer-key")})
    public ResponseEntity<ApiResponse<RankPageResponse>> getRankPage(
            @AuthenticationPrincipal JwtUserInfoDto userInfoDto) {

        long userId = userInfoDto.getUserId();
        RankPageResponse rankPage = rankService.getRankPage(userId);

        return ResponseEntity.ok(ApiResponse.success("랭킹 정보를 조회했습니다.", rankPage));
    }
}
