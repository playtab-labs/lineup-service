package com.playtab.lineupservice.config;

import io.grpc.Context;
import io.grpc.Status;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserProvider {

    public Long getCurrentUserId() {
        Long userId = GrpcUserIdInterceptor.USER_ID_CONTEXT_KEY.get(Context.current());

        if (userId == null) {
            throw Status.UNAUTHENTICATED
                    .withDescription("user_id metadata is required")
                    .asRuntimeException();
        }

        return userId;
    }
}