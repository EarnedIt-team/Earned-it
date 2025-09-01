package _team.earnedit.repository;

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

    @Query(value = "SELECT * FROM users WHERE is_public = true ORDER BY RANDOM() LIMIT :count", nativeQuery = true)
    List<User> findRandomPublicUsers(@Param("count") long count);

    // 로그인한 유저 id를 제외한 유저 리스트를 반환
    @Query(value = "SELECT * FROM users " +
            "WHERE is_public = true AND id <> :userId " +
            "ORDER BY RANDOM() LIMIT :count", nativeQuery = true)
    List<User> findRandomPublicUsersExcept(@Param("userId") Long userId,
                                           @Param("count") long count);
}