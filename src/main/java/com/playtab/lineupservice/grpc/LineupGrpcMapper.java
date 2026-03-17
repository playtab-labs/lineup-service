package com.playtab.lineupservice.grpc;

import com.google.protobuf.Timestamp;
import com.google.protobuf.util.Timestamps;
import com.playtab.lineupservice.entity.Favorite;
import com.playtab.lineupservice.entity.Performer;
import com.playtab.lineupservice.grpc.proto.LocalizedText;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

@Component
public class LineupGrpcMapper {

    public com.playtab.lineupservice.grpc.proto.Performer toPerformerProto(Performer performer, boolean isFavorited) {
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

    public com.playtab.lineupservice.grpc.proto.Favorite toFavoriteProto(Favorite favorite) {
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

    private LocalizedText toLocalizedText(Map<String, String> values) {
        return LocalizedText.newBuilder()
                .putAllValues(values)
                .build();
    }

    private Timestamp toTimestamp(LocalDateTime localDateTime) {
        return Timestamps.fromMillis(localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli());
    }
}