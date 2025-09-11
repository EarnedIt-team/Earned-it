package _team.earnedit.service;

import _team.earnedit.dto.report.ReportUserRequestDto;
import _team.earnedit.entity.ReportReason;
import _team.earnedit.entity.User;
import _team.earnedit.entity.UserReport;
import _team.earnedit.global.ErrorCode;
import _team.earnedit.global.exception.report.ReportException;
import _team.earnedit.global.util.EntityFinder;
import _team.earnedit.repository.ReportReasonRepository;
import _team.earnedit.repository.UserReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReportService {

    private final ReportReasonRepository reportReasonRepository;
    private final UserReportRepository userReportRepository;
    private final EntityFinder entityFinder;
    private final EmailService emailService;

    public void reportUser(Long reportingUserId, ReportUserRequestDto dto) {
        User reportingUser = entityFinder.getUserOrThrow(reportingUserId);
        User reportedUser  = entityFinder.getUserOrThrow(dto.getReportedUserId());

        if (reportingUser.getId().equals(reportedUser.getId())) {
            throw new ReportException(ErrorCode.CANNOT_REPORT_SELF);
        }

        ReportReason reason = reportReasonRepository.findByCode(dto.getReasonCode())
                .orElseThrow(() -> new ReportException(ErrorCode.REASON_NOT_FOUND));

        // 기타 사유일 때 상세사유 입력 필수
        if ("OTHER".equals(reason.getCode())) {
            if (dto.getReasonText() == null || dto.getReasonText().isBlank()) {
                throw new ReportException(ErrorCode.REASON_TEXT_REQUIRED);
            }
        }

        // 신고 남용 방지 : 동일유저가 동일대상 24시간 내 중복 신고
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
        boolean dup = userReportRepository
                .existsByReportingUser_IdAndReportedUser_IdAndCreatedAtAfter(
                        reportingUser.getId(), reportedUser.getId(), cutoff);
        if (dup) {
            throw new ReportException(ErrorCode.REPORT_DUPLICATE);
        }

        // 신고 남용 방지 : 10분 내 최대 총 신고 횟수 5회 제한
        LocalDateTime abuseCutoff = LocalDateTime.now().minusMinutes(10);
        long recentCount = userReportRepository
                .countByReportingUser_IdAndCreatedAtAfter(reportingUser.getId(), abuseCutoff);
        if (recentCount >= 5) {
            throw new ReportException(ErrorCode.REPORT_RATE_LIMITED);
        }

        UserReport report = UserReport.builder()
                .reportingUser(reportingUser)
                .reportedUser(reportedUser)
                .reason(reason)
                .reasonText(dto.getReasonText())
                .build();
        userReportRepository.save(report);

        // 신고대상자 누적신고수 검사 -> 임계치:5 이상은 강제 비공개 + 고지 메일 전송
        long total = userReportRepository.countByReportedUser_Id(reportedUser.getId());
        if (total >= 5) {
            if (Boolean.TRUE.equals(reportedUser.getIsPublic())) {
                reportedUser.updateVisibility(false);
                emailService.sendReportLimitNotice(reportedUser.getEmail(), total);
            }
        }

    }
}

