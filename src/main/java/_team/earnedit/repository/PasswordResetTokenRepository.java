package _team.earnedit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import _team.earnedit.entity.PasswordResetToken;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

}
