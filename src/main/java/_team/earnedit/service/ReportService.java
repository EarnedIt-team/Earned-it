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

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReportService {

    private final ReportReasonRepository reportReasonRepository;
    private final UserReportRepository userReportRepository;
    private final EntityFinder entityFinder;

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

        UserReport report = UserReport.builder()
                .reportingUser(reportingUser)
                .reportedUser(reportedUser)
                .reason(reason)
                .reasonText(dto.getReasonText())
                .build();

        userReportRepository.save(report);
    }
}

