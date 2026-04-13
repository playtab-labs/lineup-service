package com.playtab.lineupservice.repository;

import com.playtab.lineupservice.entity.FestivalDay;
import com.playtab.lineupservice.entity.PerformanceSchedule;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerformanceScheduleRepository extends JpaRepository<PerformanceSchedule, Long> {

    List<PerformanceSchedule> findByFestivalDayOrderByStartAtAsc(FestivalDay festivalDay);

    List<PerformanceSchedule> findByPerformerId(Long performerId);
}