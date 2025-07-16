package _team.earnedit.global.exception.user;

import _team.earnedit.global.ErrorCode;
import _team.earnedit.global.exception.CustomException;
import lombok.Getter;

@Getter
public class UserException extends CustomException {

    private final ErrorCode errorCode;

    public UserException(ErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }

    public UserException(ErrorCode errorCode, String customMessage) {
        super(errorCode, errorCode.getDefaultMessage() + " " + customMessage);
        this.errorCode = errorCode;
    }
}
