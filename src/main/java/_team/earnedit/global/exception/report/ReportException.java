package _team.earnedit.global.exception.report;

import _team.earnedit.global.ErrorCode;
import _team.earnedit.global.exception.CustomException;

public class ReportException extends CustomException {
    private final ErrorCode errorCode;

    public ReportException(ErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }

    public ReportException(ErrorCode errorCode, String customMessage) {
        super(errorCode, errorCode.getDefaultMessage() + " " + customMessage);
        this.errorCode = errorCode;
    }
}