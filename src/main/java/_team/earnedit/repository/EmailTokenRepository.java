package _team.earnedit.repository;

import _team.earnedit.entity.EmailToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailTokenRepository extends JpaRepository<EmailToken, Long> {
    Optional<EmailToken> findByToken(String token);

    Optional<EmailToken> findByEmail(String email);

    void deleteByEmail(String email);
}