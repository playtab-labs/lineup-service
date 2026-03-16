package com.playtab.lineupservice.exception;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.springframework.stereotype.Component;

@Component
public class GlobalGrpcExceptionHandler {

    public StatusRuntimeException toStatusRuntimeException(Exception e) {
        if (e instanceof NotFoundException notFoundException) {
            return notFoundException.getErrorCode()
                    .getGrpcStatus()
                    .withDescription(notFoundException.getMessage())
                    .asRuntimeException();
        }

        if (e instanceof DuplicateFavoriteException duplicateFavoriteException) {
            return duplicateFavoriteException.getErrorCode()
                    .getGrpcStatus()
                    .withDescription(duplicateFavoriteException.getMessage())
                    .asRuntimeException();
        }

        return Status.INTERNAL
                .withDescription(e.getMessage())
                .asRuntimeException();
    }
}