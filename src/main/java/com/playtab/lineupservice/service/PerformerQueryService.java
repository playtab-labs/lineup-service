package com.playtab.lineupservice.service;

import com.playtab.lineupservice.entity.Performer;
import com.playtab.lineupservice.entity.PerformanceSchedule;
import com.playtab.lineupservice.repository.FavoriteRepository;
import com.playtab.lineupservice.repository.PerformanceScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PerformerQueryService {

    private final PerformanceScheduleRepository performanceScheduleRepository;
    private final FavoriteRepository favoriteRepository;

    public List<Performer> getPerformers(boolean activeOnly, String stageName) {
        return performanceScheduleRepository.findAll().stream()
                .filter(schedule -> matchesStageName(schedule, stageName))
                .map(PerformanceSchedule::getPerformer)
                .filter(performer -> !activeOnly || Boolean.TRUE.equals(performer.getIsActive()))
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(
                                Performer::getId,
                                performer -> performer,
                                (existing, replacement) -> existing,
                                LinkedHashMap::new
                        ),
                        map -> List.copyOf(map.values())
                ));
    }

    public Set<Long> getFavoritePerformerIds(Long userId, List<Performer> performers) {
        if (userId == null || performers.isEmpty()) {
            return Set.of();
        }

        Map<Long, Performer> performerMap = performers.stream()
                .collect(Collectors.toMap(Performer::getId, performer -> performer, (a, b) -> a));

        return favoriteRepository.findByUserId(userId).stream()
                .map(favorite -> favorite.getPerformer().getId())
                .filter(performerMap::containsKey)
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