package com.playtab.lineupservice.grpc;

import com.google.protobuf.Timestamp;
import com.google.protobuf.util.Timestamps;
import com.playtab.lineupservice.entity.FestivalDay;
import com.playtab.lineupservice.entity.PerformanceSchedule;
import com.playtab.lineupservice.entity.Stage;
import com.playtab.lineupservice.entity.enums.ScheduleStatus;
import com.playtab.lineupservice.grpc.proto.LocalizedText;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

@Component
public class LineupGrpcMapper {

    public com.playtab.lineupservice.grpc.proto.Performer toPerformerProto(
            com.playtab.lineupservice.entity.Performer performer,
            boolean isFavorited
    ) {
        com.playtab.lineupservice.grpc.proto.Performer.Builder builder =
                com.playtab.lineupservice.grpc.proto.Performer.newBuilder()
                        .setId(performer.getId())
                        .setIsActive(Boolean.TRUE.equals(performer.getIsActive()))
                        .setIsFavorited(isFavorited);

        if (performer.getName() != null) {
            builder.setName(toLocalizedText(performer.getName()));
        }

        if (performer.getDescription() != null) {
            builder.setDescription(toLocalizedText(performer.getDescription()));
        }

        if (performer.getImageUrl() != null) {
            builder.setImageUrl(performer.getImageUrl());
        }

        if (performer.getCreatedAt() != null) {
            builder.setCreatedAt(toTimestamp(performer.getCreatedAt()));
        }

        if (performer.getUpdatedAt() != null) {
            builder.setUpdatedAt(toTimestamp(performer.getUpdatedAt()));
        }

        return builder.build();
    }

    public com.playtab.lineupservice.grpc.proto.Favorite toFavoriteProto(
            com.playtab.lineupservice.entity.Favorite favorite
    ) {
        com.playtab.lineupservice.grpc.proto.Favorite.Builder builder =
                com.playtab.lineupservice.grpc.proto.Favorite.newBuilder()
                        .setId(favorite.getId())
                        .setUserId(favorite.getUserId())
                        .setPerformerId(favorite.getPerformer().getId());

        if (favorite.getCreatedAt() != null) {
            builder.setCreatedAt(toTimestamp(favorite.getCreatedAt()));
        }

        return builder.build();
    }

    public com.playtab.lineupservice.grpc.proto.Stage toStageProto(Stage stage) {
        com.playtab.lineupservice.grpc.proto.Stage.Builder builder =
                com.playtab.lineupservice.grpc.proto.Stage.newBuilder()
                        .setId(stage.getId())
                        .setDisplayOrder(stage.getDisplayOrder());

        if (stage.getName() != null) {
            builder.setName(toLocalizedText(stage.getName()));
        }

        if (stage.getLocationDesc() != null) {
            builder.setLocationDesc(toLocalizedText(stage.getLocationDesc()));
        }

        if (stage.getCreatedAt() != null) {
            builder.setCreatedAt(toTimestamp(stage.getCreatedAt()));
        }

        if (stage.getUpdatedAt() != null) {
            builder.setUpdatedAt(toTimestamp(stage.getUpdatedAt()));
        }

        return builder.build();
    }

    public com.playtab.lineupservice.grpc.proto.FestivalDay toFestivalDayProto(FestivalDay festivalDay) {
        com.playtab.lineupservice.grpc.proto.FestivalDay.Builder builder =
                com.playtab.lineupservice.grpc.proto.FestivalDay.newBuilder()
                        .setId(festivalDay.getId())
                        .setDayNumber(festivalDay.getDayNumber());

        if (festivalDay.getEventDate() != null) {
            builder.setEventDate(festivalDay.getEventDate().toString());
        }

        if (festivalDay.getCreatedAt() != null) {
            builder.setCreatedAt(toTimestamp(festivalDay.getCreatedAt()));
        }

        if (festivalDay.getUpdatedAt() != null) {
            builder.setUpdatedAt(toTimestamp(festivalDay.getUpdatedAt()));
        }

        return builder.build();
    }

    public com.playtab.lineupservice.grpc.proto.Stage toStageSlimProto(Stage stage, String locale) {
        com.playtab.lineupservice.grpc.proto.Stage.Builder builder =
                com.playtab.lineupservice.grpc.proto.Stage.newBuilder()
                        .setId(stage.getId())
                        .setDisplayOrder(stage.getDisplayOrder());

        if (stage.getName() != null) {
            builder.setName(toLocalizedText(stage.getName(), locale));
        }

        if (stage.getLocationDesc() != null) {
            builder.setLocationDesc(toLocalizedText(stage.getLocationDesc(), locale));
        }

        if (stage.getCreatedAt() != null) {
            builder.setCreatedAt(toTimestamp(stage.getCreatedAt()));
        }

        if (stage.getUpdatedAt() != null) {
            builder.setUpdatedAt(toTimestamp(stage.getUpdatedAt()));
        }

        return builder.build();
    }

    public com.playtab.lineupservice.grpc.proto.ArtistSchedule toArtistScheduleProto(
            PerformanceSchedule schedule,
            boolean isFavorited,
            String locale
    ) {
        com.playtab.lineupservice.grpc.proto.ArtistSchedule.Builder builder =
                com.playtab.lineupservice.grpc.proto.ArtistSchedule.newBuilder()
                        .setScheduleId(schedule.getId())
                        .setPerformer(toPerformerProto(schedule.getPerformer(), isFavorited, locale))
                        .setStatus(toScheduleStatusProto(schedule.getStatus()))
                        .setDuration(toPerformanceDurationProto(schedule));

        if (schedule.getStartAt() != null) {
            builder.setStartAt(toTimestamp(schedule.getStartAt()));
        }

        if (schedule.getEndAt() != null) {
            builder.setEndAt(toTimestamp(schedule.getEndAt()));
        }

        if (schedule.getCreatedAt() != null) {
            builder.setCreatedAt(toTimestamp(schedule.getCreatedAt()));
        }

        if (schedule.getUpdatedAt() != null) {
            builder.setUpdatedAt(toTimestamp(schedule.getUpdatedAt()));
        }

        return builder.build();
    }

    public com.playtab.lineupservice.grpc.proto.PerformanceSchedule toPerformanceScheduleProto(
            PerformanceSchedule schedule,
            boolean isFavorited,
            String locale
    ) {
        com.playtab.lineupservice.grpc.proto.PerformanceSchedule.Builder builder =
                com.playtab.lineupservice.grpc.proto.PerformanceSchedule.newBuilder()
                        .setId(schedule.getId())
                        .setPerformer(toPerformerProto(schedule.getPerformer(), isFavorited, locale))
                        .setStage(toStageProto(schedule.getStage()))
                        .setStatus(toScheduleStatusProto(schedule.getStatus()))
                        .setDuration(toPerformanceDurationProto(schedule));

        if (schedule.getStartAt() != null) {
            builder.setStartAt(toTimestamp(schedule.getStartAt()));
        }

        if (schedule.getEndAt() != null) {
            builder.setEndAt(toTimestamp(schedule.getEndAt()));
        }

        if (schedule.getCreatedAt() != null) {
            builder.setCreatedAt(toTimestamp(schedule.getCreatedAt()));
        }

        if (schedule.getUpdatedAt() != null) {
            builder.setUpdatedAt(toTimestamp(schedule.getUpdatedAt()));
        }

        return builder.build();
    }

    private com.playtab.lineupservice.grpc.proto.PerformanceDuration toPerformanceDurationProto(
            PerformanceSchedule schedule
    ) {
        if (schedule.getStartAt() == null || schedule.getEndAt() == null) {
            return com.playtab.lineupservice.grpc.proto.PerformanceDuration.newBuilder()
                    .setDurationMinutes(0)
                    .setDurationLabel("0m")
                    .build();
        }

        long minutes = Duration.between(schedule.getStartAt(), schedule.getEndAt()).toMinutes();
        long hours = minutes / 60;
        long remainMinutes = minutes % 60;

        String label;
        if (hours > 0 && remainMinutes > 0) {
            label = hours + "h " + remainMinutes + "m";
        } else if (hours > 0) {
            label = hours + "h";
        } else {
            label = minutes + "m";
        }

        return com.playtab.lineupservice.grpc.proto.PerformanceDuration.newBuilder()
                .setDurationMinutes((int) minutes)
                .setDurationLabel(label)
                .build();
    }

    private com.playtab.lineupservice.grpc.proto.ScheduleStatusProto toScheduleStatusProto(
            ScheduleStatus status
    ) {
        if (status == null) {
            return com.playtab.lineupservice.grpc.proto.ScheduleStatusProto.SCHEDULE_STATUS_PROTO_UNSPECIFIED;
        }

        return switch (status) {
            case SCHEDULED ->
                    com.playtab.lineupservice.grpc.proto.ScheduleStatusProto.SCHEDULE_STATUS_PROTO_SCHEDULED;
            case CANCELLED ->
                    com.playtab.lineupservice.grpc.proto.ScheduleStatusProto.SCHEDULE_STATUS_PROTO_CANCELLED;
            case COMPLETED ->
                    com.playtab.lineupservice.grpc.proto.ScheduleStatusProto.SCHEDULE_STATUS_PROTO_COMPLETED;
        };
    }

    public com.playtab.lineupservice.grpc.proto.Performer toPerformerProto(
            com.playtab.lineupservice.entity.Performer performer,
            boolean isFavorited,
            String locale
    ) {
        com.playtab.lineupservice.grpc.proto.Performer.Builder builder =
                com.playtab.lineupservice.grpc.proto.Performer.newBuilder()
                        .setId(performer.getId())
                        .setIsActive(Boolean.TRUE.equals(performer.getIsActive()))
                        .setIsFavorited(isFavorited);

        if (performer.getName() != null) {
            builder.setName(toLocalizedText(performer.getName(), locale));
        }

        if (performer.getDescription() != null) {
            builder.setDescription(toLocalizedText(performer.getDescription(), locale));
        }

        if (performer.getImageUrl() != null) {
            builder.setImageUrl(performer.getImageUrl());
        }

        if (performer.getCreatedAt() != null) {
            builder.setCreatedAt(toTimestamp(performer.getCreatedAt()));
        }

        if (performer.getUpdatedAt() != null) {
            builder.setUpdatedAt(toTimestamp(performer.getUpdatedAt()));
        }

        return builder.build();
    }

    public com.playtab.lineupservice.grpc.proto.Performer toPerformerProto(
            com.playtab.lineupservice.entity.Performer performer,
            boolean isFavorited,
            String locale,
            List<Stage> stages
    ) {
        com.playtab.lineupservice.grpc.proto.Performer.Builder builder =
                com.playtab.lineupservice.grpc.proto.Performer.newBuilder()
                        .setId(performer.getId())
                        .setIsActive(Boolean.TRUE.equals(performer.getIsActive()))
                        .setIsFavorited(isFavorited);

        if (performer.getName() != null) {
            builder.setName(toLocalizedText(performer.getName(), locale));
        }

        if (performer.getDescription() != null) {
            builder.setDescription(toLocalizedText(performer.getDescription(), locale));
        }

        if (performer.getImageUrl() != null) {
            builder.setImageUrl(performer.getImageUrl());
        }

        if (performer.getCreatedAt() != null) {
            builder.setCreatedAt(toTimestamp(performer.getCreatedAt()));
        }

        if (performer.getUpdatedAt() != null) {
            builder.setUpdatedAt(toTimestamp(performer.getUpdatedAt()));
        }

        if (stages != null && !stages.isEmpty()) {
            for (Stage stage : stages) {
                if (stage.getName() != null) {
                    builder.addStageNames(toLocalizedText(stage.getName(), locale));
                }
            }
        }

        return builder.build();
    }

    private LocalizedText toLocalizedText(Map<String, String> values) {
        return LocalizedText.newBuilder()
                .putAllValues(values)
                .build();
    }

    private LocalizedText toLocalizedText(Map<String, String> values, String locale) {
        if (locale == null || locale.isBlank() || !values.containsKey(locale)) {
            return toLocalizedText(values);
        }
        return LocalizedText.newBuilder()
                .putValues(locale, values.get(locale))
                .build();
    }

    private Timestamp toTimestamp(LocalDateTime localDateTime) {
        return Timestamps.fromMillis(localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli());
    }
}