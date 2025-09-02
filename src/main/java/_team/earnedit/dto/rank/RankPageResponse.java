package _team.earnedit.dto.rank;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RankPageResponse {
    private UserRankInfo myRank;
    private List<UserRankInfo> top10;
}
