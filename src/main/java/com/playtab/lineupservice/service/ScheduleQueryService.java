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
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleQueryService {

    private final FestivalDayRepository festivalDayRepository;
    private final PerformanceScheduleRepository performanceScheduleRepository;
    private final FavoriteRepository favoriteRepository;

    public List<PerformanceSchedule> getSchedulesByDay(int dayNumber, String stageName) {
        FestivalDay festivalDay = festivalDayRepository.findByDayNumber(dayNumber)
                .orElseThrow(() -> new NotFoundException(ErrorCode.FESTIVAL_DAY_NOT_FOUND));

        return performanceScheduleRepository.findByFestivalDayOrderByStartAtAsc(festivalDay).stream()
                .filter(schedule -> matchesStageName(schedule, stageName))
                .toList();
    }

    public Set<Long> getFavoritePerformerIds(Long userId, List<PerformanceSchedule> schedules) {
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

    private boolean matchesStageName(PerformanceSchedule schedule, String stageName) {
        if (stageName == null || stageName.isBlank()) {
            return true;
        }

        String normalized = stageName.trim().toUpperCase(Locale.ROOT);

        return schedule.getStage().getName() != null
                && schedule.getStage().getName().values().stream()
                .filter(value -> value != null && !value.isBlank())
                .map(value -> value.trim().toUpperCase(Locale.ROOT))
                .anyMatch(normalized::equals);
    }
}