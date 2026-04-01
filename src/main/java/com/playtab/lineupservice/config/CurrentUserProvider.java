package com.playtab.lineupservice.config;

import io.grpc.Context;
import io.grpc.Status;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserProvider {

    public String getCurrentUserId() {
        String identityId = GrpcIdentityInterceptor.IDENTITY_ID_CONTEXT_KEY.get(Context.current());

        if (identityId == null) {
            throw Status.UNAUTHENTICATED
                    .withDescription("x-identity-id metadata is required")
                    .asRuntimeException();
        }

        return identityId;
    }

    public String getCurrentRole() {
        return GrpcIdentityInterceptor.ROLE_CONTEXT_KEY.get(Context.current());
    }
}