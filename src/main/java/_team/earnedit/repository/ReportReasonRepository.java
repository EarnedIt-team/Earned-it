package _team.earnedit.repository;

import _team.earnedit.entity.ReportReason;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportReasonRepository extends JpaRepository<ReportReason, Long> {

    Optional<ReportReason> findByCode(String code);
}
