package com.playtab.lineupservice.exception;

import io.grpc.Status;

public enum ErrorCode {

    PERFORMER_NOT_FOUND(Status.NOT_FOUND, "Performer not found"),
    FESTIVAL_DAY_NOT_FOUND(Status.NOT_FOUND, "Festival day not found"),
    FAVORITE_ALREADY_EXISTS(Status.ALREADY_EXISTS, "Favorite already exists");

    private final Status grpcStatus;
    private final String message;

    ErrorCode(Status grpcStatus, String message) {
        this.grpcStatus = grpcStatus;
        this.message = message;
    }

    public Status getGrpcStatus() {
        return grpcStatus;
    }

    public String getMessage() {
        return message;
    }
}