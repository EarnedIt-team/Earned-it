package _team.earnedit.global.exception.wish;

import _team.earnedit.global.ErrorCode;
import _team.earnedit.global.exception.CustomException;

public class WishException extends CustomException {
    private final ErrorCode errorCode;

    public WishException(ErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }

    public WishException(ErrorCode errorCode, String customMessage) {
        super(errorCode, errorCode.getDefaultMessage() + " " + customMessage);
        this.errorCode = errorCode;
    }
}
