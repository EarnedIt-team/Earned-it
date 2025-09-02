package _team.earnedit.dto.report;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "타 유저 신고 요청 DTO")
public class ReportUserRequestDto {

    @NotNull
    @Schema(description = "신고 대상 유저 ID", example = "123")
    private Long reportedUserId;

    @NotBlank
    @Schema(description = "신고 사유 코드 (report_reason.code)", example = "INAPPROPRIATE_PROFILE")
    private String reasonCode;

    @Size(max = 500)
    @Schema(description = "직접 입력 사유(기타 선택 시 필수)", example = "프로필 문구에 부적절한 표현이 있어요")
    private String reasonText;
}
