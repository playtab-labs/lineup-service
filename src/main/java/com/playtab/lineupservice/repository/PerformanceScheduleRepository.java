package com.playtab.lineupservice.repository;

import com.playtab.lineupservice.entity.FestivalDay;
import com.playtab.lineupservice.entity.Performer;
import com.playtab.lineupservice.entity.PerformanceSchedule;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PerformanceScheduleRepository extends JpaRepository<PerformanceSchedule, Long> {

    @EntityGraph(attributePaths = {"performer", "stage", "festivalDay"})
    List<PerformanceSchedule> findByFestivalDayOrderByStartAtAsc(FestivalDay festivalDay);

    @EntityGraph(attributePaths = {"performer", "stage", "festivalDay"})
    List<PerformanceSchedule> findByPerformerOrderByStartAtAsc(Performer performer);
}