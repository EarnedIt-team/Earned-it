package _team.earnedit.global.exception.profile;

import _team.earnedit.global.ErrorCode;
import _team.earnedit.global.exception.CustomException;

public class ProfileException extends CustomException {
    private final ErrorCode errorCode;

    public ProfileException(ErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }

    public ProfileException(ErrorCode errorCode, String customMessage) {
        super(errorCode, errorCode.getDefaultMessage() + " " + customMessage);
        this.errorCode = errorCode;
    }
}