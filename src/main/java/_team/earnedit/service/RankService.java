package _team.earnedit.service;

import _team.earnedit.dto.rank.RankPageResponse;
import _team.earnedit.dto.rank.UserRankInfo;
import _team.earnedit.entity.User;
import _team.earnedit.global.util.EntityFinder;
import _team.earnedit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RankService {

    private final EntityFinder entityFinder;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public RankPageResponse getRankPage(long userId) {
        User user = entityFinder.getUserOrThrow(userId);

        // 내 랭킹 정보
        int myRank = userRepository.findUserRanking(user.getId());

        UserRankInfo myRankInfo = UserRankInfo.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .score(user.getScore())
                .profileImage(user.getProfileImage())
                .rank(myRank)
                .build();

        // top10 유저들의 랭킹 정보
        List<UserRankInfo> top10 = userRepository.findTop10UsersWithRanking().stream()
                .map(sel_user -> UserRankInfo.builder()
                        .userId(sel_user.getUserId())
                        .nickname(sel_user.getNickname())
                        .score(sel_user.getScore())
                        .profileImage(sel_user.getProfileImage())
                        .rank(sel_user.getRank())
                        .build())
                .toList();

        return RankPageResponse.builder()
                .myRank(myRankInfo)
                .top10(top10)
                .build();
    }
}
