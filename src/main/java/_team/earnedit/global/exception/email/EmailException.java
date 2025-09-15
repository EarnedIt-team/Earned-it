package _team.earnedit.global.exception.email;

import _team.earnedit.global.ErrorCode;
import _team.earnedit.global.exception.CustomException;
import lombok.Getter;

@Getter
public class EmailException extends CustomException {

    private final ErrorCode errorCode;

    public EmailException(ErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }

    public EmailException(ErrorCode errorCode, String customMessage) {
        super(errorCode, errorCode.getDefaultMessage() + " " + customMessage);
        this.errorCode = errorCode;
    }
}