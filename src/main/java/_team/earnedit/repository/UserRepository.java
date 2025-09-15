package _team.earnedit.repository;

import _team.earnedit.dto.rank.UserRankInfo;
import _team.earnedit.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByNickname(String nickname);

    Optional<User> findByEmail(String email);

    Optional<User> findByProviderAndProviderId(User.Provider provider, String providerId);

    boolean existsByEmailAndProvider(String email, User.Provider provider);

    Optional<User> findByEmailAndProvider(String email, User.Provider provider);

    List<User> findByStatusAndDeletedAtBefore(User.Status status, LocalDateTime threshold);

    List<User> findByStatusAndDeletedAtBeforeAndEmailNotContaining(User.Status status, LocalDateTime threshold, String deleted);

    Optional<User> findByProviderAndProviderIdAndStatus(User.Provider provider, String kakaoId, User.Status status);

    boolean existsByEmail(String email);


    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("update User u set u.isCheckedIn = false where u.isCheckedIn = true")
    int resetAllCheckedIn();


    // 내가 신고한 대상 유저 ID들
    @Query(value = """
        SELECT DISTINCT r.reported_user_id
        FROM user_report r
        WHERE r.reporting_user_id = :userId
        """, nativeQuery = true)
    List<Long> findReportedUserIdsByReporter(@Param("userId") Long userId);


    /* =======================
       랜덤 공개 유저 찾기 (기존 ver.)
       ======================= */

    // 랜덤 public 유저 찾기
    @Query(value = "SELECT * FROM users WHERE is_public = true ORDER BY RANDOM() LIMIT :count", nativeQuery = true)
    List<User> findRandomPublicUsers(@Param("count") long count);

    // 로그인한 유저 id를 제외한 유저 리스트를 반환
    @Query(value = "SELECT * FROM users " +
            "WHERE is_public = true AND id <> :userId " +
            "ORDER BY RANDOM() LIMIT :count", nativeQuery = true)
    List<User> findRandomPublicUsersExcept(@Param("userId") Long userId,
                                           @Param("count") long count);



    /* =======================
       랜덤 공개 유저 찾기 (신고자에게만 숨김 반영 : new ver.)
       ======================= */

    // 랜덤 public 유저 찾기 - 신고미만자 제외
    @Query(value = """
    SELECT * FROM users u
    WHERE u.is_public = true
      AND u.status = 'ACTIVE'
      AND NOT EXISTS (
            SELECT 1 FROM user_report r
            WHERE r.reporting_user_id = :userId
              AND r.reported_user_id  = u.id
      )
    ORDER BY RANDOM()
    LIMIT :count
    """, nativeQuery = true)
    List<User> findRandomPublicUsersForMe(@Param("userId") Long userId,
                                          @Param("count") long count);

    // 로그인한 유저 id를 제외한 유저 리스트를 반환 - 신고미만자 제외
    @Query(value = """
        SELECT * FROM users u
        WHERE u.is_public = true
          AND u.status = 'ACTIVE'
          AND u.id <> :userId
          AND NOT EXISTS (
                SELECT 1 FROM user_report r
                WHERE r.reporting_user_id = :userId
                  AND r.reported_user_id  = u.id
          )
        ORDER BY RANDOM()
        LIMIT :count
        """, nativeQuery = true)
    List<User> findRandomPublicUsersExceptForMe(@Param("userId") Long userId,
                                                @Param("count") long count);



    /* =======================
       랭킹 (기존 ver.)
       ======================= */

    /***
     * Rank() : 같은 점수면 같은 순위, 다음 순위는 건너뜀(1, 2, 2, 4 ...)
     * DENSE_RANK() : 같은 점수면 같은 순위, 순위 건너뛰지 않음(1, 2, 2, 3 ...)
     * ROW_NUMBER() : 무조건 고유 순위(1, 2, 3, 4 ...)
     */
    @Query(value = """
            SELECT u.id AS userId,
                   ROW_NUMBER() OVER (ORDER BY u.score DESC) AS rank,
                   u.nickname AS nickname,
                   u.score AS score,
                   u.profile_image AS profileImage,
                   u.is_public AS isPublic
            FROM users u
            WHERE u.status = 'ACTIVE'
            LIMIT 10
            """, nativeQuery = true)
    List<UserRankInfo> findTop10UsersWithRanking();


    @Query(value = """
            SELECT ranked.ranking
            FROM (
                SELECT u.id,
                       ROW_NUMBER() OVER (ORDER BY u.score DESC) AS ranking
                FROM users u
                WHERE u.status = 'ACTIVE'
            ) ranked
            WHERE ranked.id = :userId
            """, nativeQuery = true)
    int findUserRanking(@Param("userId") Long userId);

}