package _team.earnedit.dto.profile;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProfileInfoResponseDto {
    private String profileImage;
    private String nickname;
    private Long monthlySalary;
}
