package com.playtab.lineupservice.exception;

import io.grpc.Status;

public enum ErrorCode {

    PERFORMER_NOT_FOUND(Status.NOT_FOUND, "Performer not found"),
    FESTIVAL_DAY_NOT_FOUND(Status.NOT_FOUND, "Festival day not found"),
    STAGE_NOT_FOUND(Status.NOT_FOUND, "Stage not found"),
    SCHEDULE_NOT_FOUND(Status.NOT_FOUND, "Performance schedule not found"),
    FAVORITE_ALREADY_EXISTS(Status.ALREADY_EXISTS, "Favorite already exists"),

    // ─── Admin ───
    ADMIN_REQUIRED(Status.PERMISSION_DENIED, "Admin role required"),
    INVALID_ARGUMENT(Status.INVALID_ARGUMENT, "Invalid argument"),
    DUPLICATE_FESTIVAL_DAY(Status.ALREADY_EXISTS, "Festival day already exists"),
    INVALID_TIME_RANGE(Status.INVALID_ARGUMENT, "startAt must be before endAt");

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