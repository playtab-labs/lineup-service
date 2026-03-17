package com.playtab.lineupservice.grpc;

import com.playtab.lineupservice.config.CurrentUserProvider;
import com.playtab.lineupservice.entity.Favorite;
import com.playtab.lineupservice.entity.Performer;
import com.playtab.lineupservice.exception.GlobalGrpcExceptionHandler;
import com.playtab.lineupservice.grpc.proto.*;
import com.playtab.lineupservice.service.FavoriteService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;

@GrpcService
@RequiredArgsConstructor
public class LineupGrpcService extends LineupServiceGrpc.LineupServiceImplBase {

    private final FavoriteService favoriteService;
    private final CurrentUserProvider currentUserProvider;
    private final LineupGrpcMapper lineupGrpcMapper;
    private final GlobalGrpcExceptionHandler globalGrpcExceptionHandler;

    @Override
    public void addFavorite(AddFavoriteRequest request, StreamObserver<AddFavoriteResponse> responseObserver) {
        try {
            Long userId = currentUserProvider.getCurrentUserId();

            Favorite favorite = favoriteService.addFavorite(userId, request.getPerformerId());

            AddFavoriteResponse response = AddFavoriteResponse.newBuilder()
                    .setFavorite(lineupGrpcMapper.toFavoriteProto(favorite))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(globalGrpcExceptionHandler.toStatusRuntimeException(e));
        }
    }

    @Override
    public void removeFavorite(RemoveFavoriteRequest request, StreamObserver<RemoveFavoriteResponse> responseObserver) {
        try {
            Long userId = currentUserProvider.getCurrentUserId();

            favoriteService.removeFavorite(userId, request.getPerformerId());

            RemoveFavoriteResponse response = RemoveFavoriteResponse.newBuilder()
                    .setSuccess(true)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(globalGrpcExceptionHandler.toStatusRuntimeException(e));
        }
    }

    @Override
    public void getFavorites(GetFavoritesRequest request, StreamObserver<GetFavoritesResponse> responseObserver) {
        try {
            Long userId = currentUserProvider.getCurrentUserId();

            List<Performer> performers = favoriteService.getFavorites(userId);

            GetFavoritesResponse.Builder response = GetFavoritesResponse.newBuilder();
            for (Performer performer : performers) {
                response.addPerformers(lineupGrpcMapper.toPerformerProto(performer, true));
            }

            responseObserver.onNext(response.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(globalGrpcExceptionHandler.toStatusRuntimeException(e));
        }
    }
}