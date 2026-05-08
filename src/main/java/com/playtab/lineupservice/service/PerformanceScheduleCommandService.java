package com.playtab.lineupservice.service;

import com.playtab.lineupservice.entity.FestivalDay;
import com.playtab.lineupservice.entity.PerformanceSchedule;
import com.playtab.lineupservice.entity.Performer;
import com.playtab.lineupservice.entity.Stage;
import com.playtab.lineupservice.entity.enums.ScheduleStatus;
import com.playtab.lineupservice.exception.ErrorCode;
import com.playtab.lineupservice.exception.InvalidArgumentException;
import com.playtab.lineupservice.exception.NotFoundException;
import com.playtab.lineupservice.grpc.proto.ScheduleStatusProto;
import com.playtab.lineupservice.repository.FestivalDayRepository;
import com.playtab.lineupservice.repository.PerformanceScheduleRepository;
import com.playtab.lineupservice.repository.PerformerRepository;
import com.playtab.lineupservice.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

@Service
@RequiredArgsConstructor
@Transactional
public class PerformanceScheduleCommandService {

    private final PerformanceScheduleRepository scheduleRepository;
    private final PerformerRepository performerRepository;
    private final StageRepository stageRepository;
    private final FestivalDayRepository festivalDayRepository;

    public PerformanceSchedule create(long performerId, long stageId, long festivalDayId,
                                      String startAtStr, String endAtStr, ScheduleStatusProto statusProto) {
        Performer performer = performerRepository.findById(performerId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.PERFORMER_NOT_FOUND));
        Stage stage = stageRepository.findById(stageId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.STAGE_NOT_FOUND));
        FestivalDay festivalDay = festivalDayRepository.findById(festivalDayId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.FESTIVAL_DAY_NOT_FOUND));

        LocalDateTime startAt = parseDateTime("startAt", startAtStr);
        LocalDateTime endAt = parseDateTime("endAt", endAtStr);
        validateTimeRange(startAt, endAt);

        PerformanceSchedule schedule = PerformanceSchedule.builder()
                .performer(performer)
                .stage(stage)
                .festivalDay(festivalDay)
                .startAt(startAt)
                .endAt(endAt)
                .status(toEntityStatus(statusProto))
                .build();

        return scheduleRepository.save(schedule);
    }

    public PerformanceSchedule update(long id, long performerId, long stageId, long festivalDayId,
                                      String startAtStr, String endAtStr, ScheduleStatusProto statusProto) {
        PerformanceSchedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.SCHEDULE_NOT_FOUND));

        Performer performer = performerRepository.findById(performerId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.PERFORMER_NOT_FOUND));
        Stage stage = stageRepository.findById(stageId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.STAGE_NOT_FOUND));
        FestivalDay festivalDay = festivalDayRepository.findById(festivalDayId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.FESTIVAL_DAY_NOT_FOUND));

        LocalDateTime startAt = parseDateTime("startAt", startAtStr);
        LocalDateTime endAt = parseDateTime("endAt", endAtStr);
        validateTimeRange(startAt, endAt);

        schedule.update(performer, stage, festivalDay, startAt, endAt, toEntityStatus(statusProto));
        return scheduleRepository.save(schedule);
    }

    public void delete(long id) {
        PerformanceSchedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.SCHEDULE_NOT_FOUND));
        scheduleRepository.delete(schedule);
    }

    // ─── helpers ───

    private LocalDateTime parseDateTime(String field, String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidArgumentException(field + " is required (yyyy-MM-ddTHH:mm:ss)");
        }
        try {
            return LocalDateTime.parse(value);
        } catch (DateTimeParseException e) {
            throw new InvalidArgumentException(field + " must be ISO_LOCAL_DATE_TIME: " + value);
        }
    }

    private void validateTimeRange(LocalDateTime startAt, LocalDateTime endAt) {
        if (!startAt.isBefore(endAt)) {
            throw new InvalidArgumentException(ErrorCode.INVALID_TIME_RANGE.getMessage());
        }
    }

    private ScheduleStatus toEntityStatus(ScheduleStatusProto proto) {
        if (proto == null) return ScheduleStatus.SCHEDULED;
        return switch (proto) {
            case SCHEDULE_STATUS_PROTO_CANCELLED -> ScheduleStatus.CANCELLED;
            case SCHEDULE_STATUS_PROTO_COMPLETED -> ScheduleStatus.COMPLETED;
            default -> ScheduleStatus.SCHEDULED;
        };
    }
}