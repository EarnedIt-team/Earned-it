package _team.earnedit.global.exception.term;

import _team.earnedit.global.ErrorCode;
import _team.earnedit.global.exception.CustomException;

public class TermException extends CustomException {
  private final ErrorCode errorCode;

  public TermException(ErrorCode errorCode) {
    super(errorCode);
    this.errorCode = errorCode;
  }

  public TermException(ErrorCode errorCode, String customMessage) {
    super(errorCode, errorCode.getDefaultMessage() + " " + customMessage);
    this.errorCode = errorCode;
  }
}