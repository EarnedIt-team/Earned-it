package _team.earnedit.dto.rank;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserRankInfo {
    private long userId;
    private long rank;
    private String nickname;
    private long score;
    private String profileImage;

    // JPA 네이티브 쿼리 매핑용 생성자
    public UserRankInfo(long userId, long rank, String nickname, long score, String profileImage) {
        this.userId = userId;
        this.rank = rank;
        this.nickname = nickname;
        this.score = score;
        this.profileImage = profileImage;
    }
}
