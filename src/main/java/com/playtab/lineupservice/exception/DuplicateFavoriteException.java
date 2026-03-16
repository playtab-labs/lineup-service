package com.playtab.lineupservice.exception;

public class DuplicateFavoriteException extends RuntimeException {

    private final ErrorCode errorCode;

    public DuplicateFavoriteException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}