package com.playtab.lineupservice.grpc;

import com.playtab.lineupservice.config.CurrentUserProvider;
import com.playtab.lineupservice.entity.Favorite;
import com.playtab.lineupservice.entity.Performer;
import com.playtab.lineupservice.entity.PerformanceSchedule;
import com.playtab.lineupservice.exception.GlobalGrpcExceptionHandler;
import com.playtab.lineupservice.grpc.proto.*;
import com.playtab.lineupservice.service.FavoriteService;


// 조회 전용 서비스 2개 추가
// PerformerQueryService: 공연자 조회 전용
// ScheduleQueryService: 스케줄 조회 전용
import com.playtab.lineupservice.service.PerformerQueryService;
import com.playtab.lineupservice.service.ScheduleQueryService;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;
import java.util.Set;

@GrpcService
@RequiredArgsConstructor
public class LineupGrpcService extends LineupServiceGrpc.LineupServiceImplBase {

    private final FavoriteService favoriteService;


    // 조회 전용 서비스 필드 추가
    private final PerformerQueryService performerQueryService;
    private final ScheduleQueryService scheduleQueryService;

    private final CurrentUserProvider currentUserProvider;
    private final LineupGrpcMapper lineupGrpcMapper;
    private final GlobalGrpcExceptionHandler globalGrpcExceptionHandler;


    // GetPerformers
    // 비로그인/로그인 모두 공연자 목록 조회 가능
    // 로그인 사용자인 경우 favorite 여부를 계산해서 is_favorited 반영
    @Override
    public void getPerformers(GetPerformersRequest request, StreamObserver<GetPerformersResponse> responseObserver) {
        try {
            // 비로그인 조회를 위해 optional 방식으로 처리
            String userId = getOptionalCurrentUserId();


            // 공연자 목록 조회를 QueryService에 위임
            List<Performer> performers = performerQueryService.getPerformers(
                    request.getActiveOnly(),
                    request.getStageName()
            );

            // 비로그인이면 빈 집합 반환되도록 QueryService에서 처리.
            Set<Long> favoriteIds = performerQueryService.getFavoritePerformerIds(userId, performers);

            GetPerformersResponse.Builder response = GetPerformersResponse.newBuilder();

            for (Performer performer : performers) {
                boolean isFavorited = favoriteIds.contains(performer.getId());

                response.addPerformers(
                        lineupGrpcMapper.toPerformerProto(performer, isFavorited)
                );
            }

            responseObserver.onNext(response.build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(globalGrpcExceptionHandler.toStatusRuntimeException(e));
        }
    }


    // GetSchedulesByDay
    // 특정 day_number 기준 공연 스케줄 조회
    // stage_name 필터 반영
    // 로그인 사용자인 경우 performer.is_favorited 반영
    @Override
    public void getSchedulesByDay(GetSchedulesByDayRequest request,
                                  StreamObserver<GetSchedulesByDayResponse> responseObserver) {
        try {

            String userId = getOptionalCurrentUserId();

            List<PerformanceSchedule> schedules =
                    scheduleQueryService.getSchedulesByDay(
                            request.getDayNumber(),
                            request.getStageName()
                    );

            // 조회된 스케줄에 포함된 performer 중 현재 사용자가 즐겨찾기한 performer id 집합 조회
            Set<Long> favoriteIds = scheduleQueryService.getFavoritePerformerIds(userId, schedules);

            GetSchedulesByDayResponse.Builder response =
                    GetSchedulesByDayResponse.newBuilder();

            for (PerformanceSchedule schedule : schedules) {

                boolean isFavorited = favoriteIds.contains(schedule.getPerformer().getId());

                // schedule 전체를 proto로 변환
                response.addSchedules(
                        lineupGrpcMapper.toPerformanceScheduleProto(schedule, isFavorited)
                );
            }

            responseObserver.onNext(response.build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(globalGrpcExceptionHandler.toStatusRuntimeException(e));
        }
    }


    @Override
    public void addFavorite(AddFavoriteRequest request, StreamObserver<AddFavoriteResponse> responseObserver) {
        try {
            String userId = currentUserProvider.getCurrentUserId();

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
            String userId = currentUserProvider.getCurrentUserId();

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
            String userId = currentUserProvider.getCurrentUserId();

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


    // 신규 private 헬퍼 메서드 추가
    private String getOptionalCurrentUserId() {
        try {
            return currentUserProvider.getCurrentUserId();
        } catch (Exception e) {
            return null;
        }
    }
}