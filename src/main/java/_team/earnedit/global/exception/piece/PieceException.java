package _team.earnedit.global.exception.piece;

import _team.earnedit.global.ErrorCode;
import _team.earnedit.global.exception.CustomException;

public class PieceException extends CustomException {
    private final ErrorCode errorCode;

    public PieceException(ErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }

    public PieceException(ErrorCode errorCode, String customMessage) {
        super(errorCode, errorCode.getDefaultMessage() + " " + customMessage);
        this.errorCode = errorCode;
    }
}
