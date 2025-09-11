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

        // 내가 신고한 대상들 : 마스킹 판정용
        var reportedIds = new java.util.HashSet<>(
                userRepository.findReportedUserIdsByReporter(user.getId())
        );

        // top10 유저들의 랭킹 정보
        List<UserRankInfo> top10 = userRepository.findTop10UsersWithRanking().stream()
                .map(sel_user -> {

                    // 비공개 유저 or 내가 신고한 유저
                    boolean treatedPrivate = !sel_user.isPublic() || reportedIds.contains(sel_user.getUserId());

                    if (treatedPrivate) {
                        String nickname = sel_user.getNickname();

                        // 앞 2글자만 남기고 나머지는 * 처리
                        String maskedNickname;
                        if (nickname.length() > 2) {
                            maskedNickname = nickname.substring(0, 2)
                                    + "*".repeat(nickname.length() - 2);
                        } else {
                            // 닉네임이 2글자 이하인 경우 전부 * 처리
                            maskedNickname = "*".repeat(nickname.length());
                        }

                        return UserRankInfo.builder()
                                .userId(sel_user.getUserId())
                                .nickname(maskedNickname)
                                .score(sel_user.getScore())
                                .profileImage(null) // 비공개 유저 or 취급이면 프로필 이미지 제공 X
                                .rank(sel_user.getRank())
                                .isPublic(false)    // 화면 표현용: 비공개 취급
                                .build();
                    }

                    // 공개 유저
                    return UserRankInfo.builder()
                            .userId(sel_user.getUserId())
                            .nickname(sel_user.getNickname())
                            .score(sel_user.getScore())
                            .profileImage(sel_user.getProfileImage())
                            .rank(sel_user.getRank())
                            .isPublic(sel_user.isPublic())
                            .build();
                }).toList();

        return RankPageResponse.builder()
                .myRank(myRankInfo)
                .top10(top10)
                .build();
    }
}
