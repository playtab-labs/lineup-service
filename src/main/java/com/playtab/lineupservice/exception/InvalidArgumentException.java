package com.playtab.lineupservice.exception;

public class InvalidArgumentException extends RuntimeException {

    private final ErrorCode errorCode;

    public InvalidArgumentException(String message) {
        super(message);
        this.errorCode = ErrorCode.INVALID_ARGUMENT;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}