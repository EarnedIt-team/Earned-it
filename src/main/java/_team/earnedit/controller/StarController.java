package _team.earnedit.controller;

import _team.earnedit.dto.jwt.JwtUserInfoDto;
import _team.earnedit.dto.star.StarListResponse;
import _team.earnedit.dto.star.StarOrderUpdateRequest;
import _team.earnedit.global.ApiResponse;
import _team.earnedit.service.StarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/star")
@RequiredArgsConstructor
@Tag(name = "Star API", description = "Star 관련 기능 (조회, 수정 등)")
public class StarController {

    private final StarService starService;

    @PatchMapping("/{wishId}")
    @Operation(summary = "Star 상태 변경", description = "위시의 Star 상태를 변경합니다.", security = {@SecurityRequirement(name = "bearer-key")})
    public ResponseEntity<ApiResponse<Boolean>> updateStar(
            @AuthenticationPrincipal JwtUserInfoDto userInfo,
            @PathVariable long wishId) {
        boolean isStar = starService.updateStar(userInfo.getUserId(), wishId);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(String.format("Star 상태를 변경했습니다. %s", isStar)));
    }

    @GetMapping
    @Operation(summary = "Star 목록 조회", description = "Star 목록을 조회합니다.", security = {@SecurityRequirement(name = "bearer-key")})
    public ResponseEntity<ApiResponse<List<StarListResponse>>> getStarsWish(
            @AuthenticationPrincipal JwtUserInfoDto userInfo
    ) {
        List<StarListResponse> starsWish = starService.getStarsWish(userInfo.getUserId());

        return ResponseEntity.ok(ApiResponse.success("Star 목록을 조회했습니다.", starsWish));
    }

    @PatchMapping("/order")
    @Operation(summary = "Star 순서 저장", description = "요청 받은 순서대로 서버에 Star 순서를 저장한다.", security = {@SecurityRequirement(name = "bearer-key")})
    public ResponseEntity<ApiResponse<String>> updateStarOrder(
            @AuthenticationPrincipal JwtUserInfoDto userInfo,
            @RequestBody StarOrderUpdateRequest orderUpdateRequest
            ) {
        starService.updateStarOrder(userInfo.getUserId(), orderUpdateRequest.getOrderedWishIds());

        return ResponseEntity.ok(ApiResponse.success("Star의 순서를 성공적으로 변경하였습니다."));
    }




}
