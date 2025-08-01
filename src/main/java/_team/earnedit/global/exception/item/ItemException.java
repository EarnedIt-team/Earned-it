package _team.earnedit.global.exception.item;

import _team.earnedit.global.ErrorCode;
import _team.earnedit.global.exception.CustomException;

public class ItemException extends CustomException {
    private final ErrorCode errorCode;

    public ItemException(ErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }

    public ItemException(ErrorCode errorCode, String customMessage) {
        super(errorCode, errorCode.getDefaultMessage() + " " + customMessage);
        this.errorCode = errorCode;
    }
}

