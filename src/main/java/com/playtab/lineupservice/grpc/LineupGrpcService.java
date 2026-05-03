package com.playtab.lineupservice.grpc;

import com.playtab.lineupservice.config.CurrentUserProvider;
import com.playtab.lineupservice.entity.Favorite;
import com.playtab.lineupservice.entity.FestivalDay;
import com.playtab.lineupservice.entity.Performer;
import com.playtab.lineupservice.entity.PerformanceSchedule;
import com.playtab.lineupservice.entity.Stage;
import com.playtab.lineupservice.exception.GlobalGrpcExceptionHandler;
import com.playtab.lineupservice.grpc.proto.*;
import com.playtab.lineupservice.repository.PerformanceScheduleRepository;
import com.playtab.lineupservice.service.FavoriteService;
import com.playtab.lineupservice.service.FestivalDayQueryService;
import com.playtab.lineupservice.service.PerformerQueryService;
import com.playtab.lineupservice.service.ScheduleQueryService;
import io.grpc.stub.StreamObserver;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class LineupGrpcService extends LineupServiceGrpc.LineupServiceImplBase {

    private final FavoriteService favoriteService;
    private final PerformerQueryService performerQueryService;
    private final ScheduleQueryService scheduleQueryService;
    private final FestivalDayQueryService festivalDayQueryService;
    private final PerformanceScheduleRepository performanceScheduleRepository;

    private final CurrentUserProvider currentUserProvider;
    private final LineupGrpcMapper lineupGrpcMapper;
    private final GlobalGrpcExceptionHandler globalGrpcExceptionHandler;

    @Override
    public void getPerformers(GetPerformersRequest request, StreamObserver<GetPerformersResponse> responseObserver) {
        try {
            String userId = getOptionalCurrentUserId();
            String locale = request.getLocale();

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
                    request.getStageId()
            );

            Set<Long> favoriteIds = performerQueryService.getFavoritePerformerIds(userId, performers);

            GetPerformersResponse.Builder response = GetPerformersResponse.newBuilder();

            for (Performer performer : performers) {
                boolean isFavorited = favoriteIds.contains(performer.getId());

                List<Stage> stages = performanceScheduleRepository.findByPerformerId(performer.getId()).stream()
                        .map(PerformanceSchedule::getStage)
                        .filter(stage -> stage != null && stage.getId() != null)
                        .collect(Collectors.collectingAndThen(
                                Collectors.toMap(
                                        Stage::getId,
                                        stage -> stage,
                                        (existing, replacement) -> existing,
                                        LinkedHashMap::new
                                ),
                                map -> new ArrayList<>(map.values())
                        ));

                response.addPerformers(
                        lineupGrpcMapper.toPerformerProto(performer, isFavorited, locale, stages)
                );
            }

            responseObserver.onNext(response.build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(globalGrpcExceptionHandler.toStatusRuntimeException(e));
        }
    }

    @Override
    public void getSchedulesByDay(GetSchedulesByDayRequest request,
                                  StreamObserver<GetSchedulesByDayResponse> responseObserver) {
        try {
            String locale = request.getLocale();
            String userId = getOptionalCurrentUserId();

            List<PerformanceSchedule> schedules =
                    scheduleQueryService.getSchedulesByDay(
                            request.getDayId(),
                            request.getStageId()
                    );

            Set<Long> favoriteIds = scheduleQueryService.getFavoritePerformerIds(userId, schedules);

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

    @Override
    public void getPerformersByDay(GetPerformersByDayRequest request,
                                   StreamObserver<GetPerformersByDayResponse> responseObserver) {
        try {
            String locale = request.getLocale();
            String userId = getOptionalCurrentUserId();

            List<FestivalDay> allDays = festivalDayQueryService.getFestivalDays();
            List<PerformanceSchedule> allSchedules = performanceScheduleRepository.findAllActiveWithFestivalDay();

            List<Performer> allPerformers = allSchedules.stream()
                    .map(PerformanceSchedule::getPerformer)
                    .distinct()
                    .toList();
            Set<Long> favoriteIds = performerQueryService.getFavoritePerformerIds(userId, allPerformers);

            // 공연자별 스테이지 목록 (중복 제거)
            Map<Long, List<Stage>> stagesByPerformerId = new LinkedHashMap<>();
            for (PerformanceSchedule schedule : allSchedules) {
                Long performerId = schedule.getPerformer().getId();
                Stage stage = schedule.getStage();
                stagesByPerformerId.computeIfAbsent(performerId, k -> new ArrayList<>());
                List<Stage> stages = stagesByPerformerId.get(performerId);
                if (stages.stream().noneMatch(s -> s.getId().equals(stage.getId()))) {
                    stages.add(stage);
                }
            }

            // 날짜별 공연자 그룹핑 (날짜 내 중복 제거)
            Map<Long, LinkedHashMap<Long, PerformanceSchedule>> schedulesByDayId = new LinkedHashMap<>();
            for (PerformanceSchedule schedule : allSchedules) {
                long dayId = schedule.getFestivalDay().getId();
                schedulesByDayId.computeIfAbsent(dayId, k -> new LinkedHashMap<>())
                        .putIfAbsent(schedule.getPerformer().getId(), schedule);
            }

            GetPerformersByDayResponse.Builder response = GetPerformersByDayResponse.newBuilder();

            for (FestivalDay day : allDays) {
                PerformersByDay.Builder dayBuilder = PerformersByDay.newBuilder()
                        .setFestivalDay(lineupGrpcMapper.toFestivalDayProto(day));

                Map<Long, PerformanceSchedule> daySchedules =
                        schedulesByDayId.getOrDefault(day.getId(), new LinkedHashMap<>());

                for (PerformanceSchedule schedule : daySchedules.values()) {
                    Performer performer = schedule.getPerformer();
                    boolean isFavorited = favoriteIds.contains(performer.getId());
                    List<Stage> stages = stagesByPerformerId.getOrDefault(performer.getId(), List.of());
                    dayBuilder.addPerformers(
                            lineupGrpcMapper.toPerformerProto(performer, isFavorited, locale, stages));
                }

                response.addDays(dayBuilder.build());
            }

            responseObserver.onNext(response.build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(globalGrpcExceptionHandler.toStatusRuntimeException(e));
        }
    }

    private String getOptionalCurrentUserId() {
        try {
            return currentUserProvider.getCurrentUserId();
        } catch (Exception e) {
            return null;
        }
    }
}