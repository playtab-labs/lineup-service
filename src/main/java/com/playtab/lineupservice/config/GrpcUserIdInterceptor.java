package com.playtab.lineupservice.config;

import io.grpc.*;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.springframework.stereotype.Component;

@Component
@GrpcGlobalServerInterceptor
public class GrpcUserIdInterceptor implements ServerInterceptor {

    public static final Metadata.Key<String> USER_ID_HEADER =
            Metadata.Key.of("user_id", Metadata.ASCII_STRING_MARSHALLER);

    public static final Context.Key<Long> USER_ID_CONTEXT_KEY =
            Context.key("user_id");

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next
    ) {
        String userIdHeader = headers.get(USER_ID_HEADER);

        if (userIdHeader == null || userIdHeader.isBlank()) {
            return Contexts.interceptCall(Context.current(), call, headers, next);
        }

        try {
            Long userId = Long.parseLong(userIdHeader);
            Context context = Context.current().withValue(USER_ID_CONTEXT_KEY, userId);
            return Contexts.interceptCall(context, call, headers, next);
        } catch (NumberFormatException e) {
            call.close(
                    Status.INVALID_ARGUMENT.withDescription("Invalid user_id metadata"),
                    new Metadata()
            );
            return new ServerCall.Listener<>() {};
        }
    }
}