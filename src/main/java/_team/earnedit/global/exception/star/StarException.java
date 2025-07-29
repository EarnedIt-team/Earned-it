package _team.earnedit.global.exception.star;

import _team.earnedit.global.ErrorCode;
import _team.earnedit.global.exception.CustomException;

public class StarException extends CustomException {
    private final ErrorCode errorCode;

    public StarException(ErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }

    public StarException(ErrorCode errorCode, String customMessage) {
        super(errorCode, errorCode.getDefaultMessage() + " " + customMessage);
        this.errorCode = errorCode;
    }
}
