package _team.earnedit.controller;

import _team.earnedit.dto.profile.SalaryRequestDto;
import _team.earnedit.dto.profile.SalaryResponseDto;
import _team.earnedit.global.ApiResponse;
import _team.earnedit.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/profile")
public class ProfileController {
    private final ProfileService profileService;

    @PostMapping("/salary")
    public ResponseEntity<ApiResponse<SalaryResponseDto>> saveSalary(@RequestParam long userId, @RequestBody SalaryRequestDto requestDto) {
        SalaryResponseDto responseDto = profileService.updateSalary(userId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("수익 정보를 업데이트했습니다",responseDto));
    }
}
