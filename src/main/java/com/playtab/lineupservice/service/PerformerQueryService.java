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
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PerformerQueryService {

    private final PerformanceScheduleRepository performanceScheduleRepository;
    private final FavoriteRepository favoriteRepository;

    public List<Performer> getPerformers(boolean activeOnly, long stageId) {
        return performanceScheduleRepository.findAll().stream()
                .filter(schedule -> matchesStageId(schedule, stageId))
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

    public Set<Long> getFavoritePerformerIds(String userId, List<Performer> performers) {
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

    private boolean matchesStageId(PerformanceSchedule schedule, long stageId) {
        if (stageId == 0L) {
            return true;
        }

        return schedule.getStage() != null
                && schedule.getStage().getId() != null
                && schedule.getStage().getId().equals(stageId);
    }
}