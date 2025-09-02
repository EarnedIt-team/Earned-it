package _team.earnedit.controller;

import _team.earnedit.dto.jwt.JwtUserInfoDto;
import _team.earnedit.dto.report.ReportUserRequestDto;
import _team.earnedit.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import _team.earnedit.service.ReportService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/report")
public class ReportController {

    private final ReportService reportService;

    @Operation(
            summary = "타 유저 신고",
            description = "타 유저의 프로필/닉네임/위시 등을 신고합니다.",
            security = { @SecurityRequirement(name = "bearer-key") }
    )
    @PostMapping("/user")
    public ResponseEntity<ApiResponse<Void>> reportUser(
            @Valid @RequestBody ReportUserRequestDto request,
            @AuthenticationPrincipal JwtUserInfoDto me
    ) {
        reportService.reportUser(me.getUserId(), request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("신고가 접수되었습니다."));
    }
}
