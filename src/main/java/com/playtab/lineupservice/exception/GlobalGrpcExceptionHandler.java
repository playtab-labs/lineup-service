package com.playtab.lineupservice.exception;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.springframework.stereotype.Component;

@Component
public class GlobalGrpcExceptionHandler {

    public StatusRuntimeException toStatusRuntimeException(Exception e) {
        if (e instanceof NotFoundException ex) {
            return ex.getErrorCode().getGrpcStatus()
                    .withDescription(ex.getMessage())
                    .asRuntimeException();
        }

        if (e instanceof DuplicateFavoriteException ex) {
            return ex.getErrorCode().getGrpcStatus()
                    .withDescription(ex.getMessage())
                    .asRuntimeException();
        }

        if (e instanceof PermissionDeniedException ex) {
            return ex.getErrorCode().getGrpcStatus()
                    .withDescription(ex.getMessage())
                    .asRuntimeException();
        }

        if (e instanceof ConflictException ex) {
            return ex.getErrorCode().getGrpcStatus()
                    .withDescription(ex.getMessage())
                    .asRuntimeException();
        }

        if (e instanceof InvalidArgumentException ex) {
            return ex.getErrorCode().getGrpcStatus()
                    .withDescription(ex.getMessage())
                    .asRuntimeException();
        }

        return Status.INTERNAL
                .withDescription(e.getMessage())
                .asRuntimeException();
    }
}