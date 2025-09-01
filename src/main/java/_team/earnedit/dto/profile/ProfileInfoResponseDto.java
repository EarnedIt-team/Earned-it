package _team.earnedit.dto.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "프로필 정보 응답 DTO")
public class ProfileInfoResponseDto {

    @Schema(description = "유저 id", example = "1")
    private long userId;

    @Schema(description = "프로필 이미지 URL", example = "https://s3.../profile.png")
    private String profileImage;

    @Schema(description = "사용자 닉네임", example = "요림짱")
    private String nickname;

    @Schema(description = "월 수익 (단위: 원)", example = "4000000")
    private Long monthlySalary;
}
