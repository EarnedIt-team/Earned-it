package _team.earnedit.dto.dailyCheck;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class RewardCandidate {
    private UUID rewardToken;
    private List<RewardItem> candidates;
}