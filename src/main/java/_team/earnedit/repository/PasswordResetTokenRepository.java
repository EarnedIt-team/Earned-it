package _team.earnedit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import _team.earnedit.entity.PasswordResetToken;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    void deleteByEmail(String email);

    Optional<PasswordResetToken> findByToken(String token);

    Optional<PasswordResetToken> findByEmail(String email);
}
