package com.playtab.lineupservice.config;

import com.playtab.lineupservice.exception.ErrorCode;
import com.playtab.lineupservice.exception.PermissionDeniedException;
import io.grpc.*;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.springframework.stereotype.Component;

@Component
@GrpcGlobalServerInterceptor
public class GrpcIdentityInterceptor implements ServerInterceptor {

    private static final Metadata.Key<String> IDENTITY_ID_KEY =
            Metadata.Key.of("x-identity-id", Metadata.ASCII_STRING_MARSHALLER);

    private static final Metadata.Key<String> ROLE_KEY =
            Metadata.Key.of("x-role", Metadata.ASCII_STRING_MARSHALLER);

    public static final Context.Key<String> IDENTITY_ID_CONTEXT_KEY =
            Context.key("x-identity-id");

    public static final Context.Key<String> ROLE_CONTEXT_KEY =
            Context.key("x-role");

    private static final String ROLE_ADMIN = "ADMIN";

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next
    ) {
        String identityId = headers.get(IDENTITY_ID_KEY);
        String role = headers.get(ROLE_KEY);

        if (identityId == null || identityId.isBlank()) {
            return Contexts.interceptCall(Context.current(), call, headers, next);
        }

        Context context = Context.current()
                .withValue(IDENTITY_ID_CONTEXT_KEY, identityId)
                .withValue(ROLE_CONTEXT_KEY, role);
        return Contexts.interceptCall(context, call, headers, next);
    }

    /**
     * 현재 호출자의 role이 ADMIN인지 확인. 아니면 PermissionDeniedException.
     * Admin gRPC 메서드 진입점에서 호출.
     */
    public static void requireAdmin() {
        String role = ROLE_CONTEXT_KEY.get();
        if (!ROLE_ADMIN.equals(role)) {
            throw new PermissionDeniedException(ErrorCode.ADMIN_REQUIRED);
        }
    }
}