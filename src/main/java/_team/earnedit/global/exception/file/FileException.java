package _team.earnedit.global.exception.file;

import _team.earnedit.global.ErrorCode;
import _team.earnedit.global.exception.CustomException;

public class FileException extends CustomException {

    private final ErrorCode errorCode;

    public FileException(ErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }

    public FileException(ErrorCode errorCode, String customMessage) {
        super(errorCode, errorCode.getDefaultMessage() + " " + customMessage);
        this.errorCode = errorCode;
    }
}
