package _team.earnedit.repository;

import _team.earnedit.entity.UserReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface UserReportRepository extends JpaRepository<UserReport, Long> {

    boolean existsByReportingUser_IdAndReportedUser_IdAndCreatedAtAfter(
            Long reportingUserId, Long reportedUserId, LocalDateTime createdAfter
    );

    long countByReportingUser_IdAndCreatedAtAfter(
            Long reportingUserId, LocalDateTime createdAfter
    );

    long countByReportedUser_Id(Long reportedUserId);
}