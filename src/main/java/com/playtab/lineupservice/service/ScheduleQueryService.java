package com.playtab.lineupservice.service;

import com.playtab.lineupservice.entity.FestivalDay;
import com.playtab.lineupservice.entity.PerformanceSchedule;
import com.playtab.lineupservice.exception.ErrorCode;
import com.playtab.lineupservice.exception.NotFoundException;
import com.playtab.lineupservice.repository.FavoriteRepository;
import com.playtab.lineupservice.repository.FestivalDayRepository;
import com.playtab.lineupservice.repository.PerformanceScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleQueryService {

    private final FestivalDayRepository festivalDayRepository;
    private final PerformanceScheduleRepository performanceScheduleRepository;
    private final FavoriteRepository favoriteRepository;

    public List<PerformanceSchedule> getSchedulesByDay(long dayId, long stageId) {
        FestivalDay festivalDay = festivalDayRepository.findById(dayId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.FESTIVAL_DAY_NOT_FOUND));

        return performanceScheduleRepository.findByFestivalDayOrderByStartAtAsc(festivalDay).stream()
                .filter(schedule -> matchesStageId(schedule, stageId))
                .toList();
    }

    public Set<Long> getFavoritePerformerIds(String userId, List<PerformanceSchedule> schedules) {
        if (userId == null || schedules.isEmpty()) {
            return Set.of();
        }

        Set<Long> performerIds = schedules.stream()
                .map(schedule -> schedule.getPerformer().getId())
                .collect(Collectors.toSet());

        return favoriteRepository.findByUserId(userId).stream()
                .map(favorite -> favorite.getPerformer().getId())
                .filter(performerIds::contains)
                .collect(Collectors.toSet());
    }

    private boolean matchesStageId(PerformanceSchedule schedule, long stageId) {
        if (stageId == 0L) {
            return true;
        }

        return schedule.getStage() != null
                && schedule.getStage().getId() != null
                && schedule.getStage().getId().equals(stageId);
    }
}