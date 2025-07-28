package _team.earnedit.global.exception.salary;

import _team.earnedit.global.ErrorCode;
import _team.earnedit.global.exception.CustomException;

public class SalaryException extends CustomException {
    private final ErrorCode errorCode;

    public SalaryException(ErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }

    public SalaryException(ErrorCode errorCode, String customMessage) {
        super(errorCode, errorCode.getDefaultMessage() + " " + customMessage);
        this.errorCode = errorCode;
    }
}
