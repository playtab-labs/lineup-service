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
import com.playtab.lineupservice.service.FestivalDayQueryService;
import com.playtab.lineupservice.service.PerformerQueryService;
import com.playtab.lineupservice.service.ScheduleQueryService;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.*;
import java.util.stream.Collectors;

@GrpcService
@RequiredArgsConstructor
public class LineupGrpcService extends LineupServiceGrpc.LineupServiceImplBase {

    private final FavoriteService favoriteService;


    // 조회 전용 서비스 필드 추가
    private final PerformerQueryService performerQueryService;
    private final ScheduleQueryService scheduleQueryService;
    private final FestivalDayQueryService festivalDayQueryService;

    private final CurrentUserProvider currentUserProvider;
    private final LineupGrpcMapper lineupGrpcMapper;
    private final GlobalGrpcExceptionHandler globalGrpcExceptionHandler;


    // GetPerformers
    // 비로그인/로그인 모두 공연자 목록 조회 가능
    // 로그인 사용자인 경우 favorite 여부를 계산해서 is_favorited 반영
    @Override
    public void getPerformers(GetPerformersRequest request, StreamObserver<GetPerformersResponse> responseObserver) {
        try {
            String userId = getOptionalCurrentUserId();

            boolean activeOnly;
            if (!request.hasActiveOnly()) {
                activeOnly = true;
            } else if (!request.getActiveOnly()) {
                responseObserver.onNext(GetPerformersResponse.newBuilder().build());
                responseObserver.onCompleted();
                return;
            } else {
                activeOnly = true;
            }

            List<Performer> performers = performerQueryService.getPerformers(
                    activeOnly,
                    request.getStageName()
            );

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
            String locale = request.getLocale();
            String userId = getOptionalCurrentUserId();

            List<PerformanceSchedule> schedules =
                    scheduleQueryService.getSchedulesByDay(
                            request.getDayId(),
                            request.getStageName()
                    );

            Set<Long> favoriteIds = scheduleQueryService.getFavoritePerformerIds(userId, schedules);

            // display_order 순 정렬 후 stage별 그룹핑 (삽입 순서 유지)
            Map<Long, List<PerformanceSchedule>> byStage = schedules.stream()
                    .sorted(Comparator.comparingInt(s -> s.getStage().getDisplayOrder()))
                    .collect(Collectors.groupingBy(
                            s -> s.getStage().getId(),
                            LinkedHashMap::new,
                            Collectors.toList()
                    ));

            GetSchedulesByDayResponse.Builder response = GetSchedulesByDayResponse.newBuilder();

            byStage.forEach((stageId, stageSchedules) -> {
                com.playtab.lineupservice.grpc.proto.StageSchedule.Builder stageSchedule =
                        com.playtab.lineupservice.grpc.proto.StageSchedule.newBuilder()
                                .setStage(lineupGrpcMapper.toStageSlimProto(
                                        stageSchedules.get(0).getStage(), locale));

                stageSchedules.forEach(schedule -> {
                    boolean isFavorited = favoriteIds.contains(schedule.getPerformer().getId());
                    stageSchedule.addArtists(
                            lineupGrpcMapper.toArtistScheduleProto(schedule, isFavorited, locale));
                });

                response.addStages(stageSchedule.build());
            });

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


    @Override
    public void getFestivalDays(GetFestivalDaysRequest request,
                                StreamObserver<GetFestivalDaysResponse> responseObserver) {
        try {
            GetFestivalDaysResponse.Builder response = GetFestivalDaysResponse.newBuilder();

            festivalDayQueryService.getFestivalDays()
                    .forEach(day -> response.addFestivalDays(lineupGrpcMapper.toFestivalDayProto(day)));

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