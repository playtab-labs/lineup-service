package com.playtab.lineupservice.exception;

public class PermissionDeniedException extends RuntimeException {

    private final ErrorCode errorCode;

    public PermissionDeniedException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}