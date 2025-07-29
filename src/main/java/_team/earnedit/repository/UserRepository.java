package _team.earnedit.repository;

import _team.earnedit.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

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

}