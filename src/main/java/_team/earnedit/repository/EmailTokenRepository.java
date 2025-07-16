package _team.earnedit.repository;

import _team.earnedit.entity.EmailToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailTokenRepository extends JpaRepository<EmailToken, Long> {

}