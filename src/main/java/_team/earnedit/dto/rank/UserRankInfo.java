package _team.earnedit.dto.rank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRankInfo {
    private long userId;
    private long rank;
    private String nickname;
    private long score;
    private String profileImage;
    private boolean isPublic;
}
