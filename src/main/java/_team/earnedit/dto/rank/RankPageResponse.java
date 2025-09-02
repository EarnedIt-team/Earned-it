package _team.earnedit.dto.rank;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RankPageResponse {

    private UserRankInfo myRank;
    private List<UserRankInfo> top10;

    @Getter
    @Builder
    private static class UserRankInfo {
        private long userId;
        private int rank;
        private String nickname;
        private long score;
        private String profileImage;
    }
}
