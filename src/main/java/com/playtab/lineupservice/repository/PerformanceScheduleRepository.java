package com.playtab.lineupservice.repository;

import com.playtab.lineupservice.entity.FestivalDay;
import com.playtab.lineupservice.entity.PerformanceSchedule;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PerformanceScheduleRepository extends JpaRepository<PerformanceSchedule, Long> {

    @Query("SELECT ps FROM PerformanceSchedule ps JOIN FETCH ps.stage JOIN FETCH ps.performer WHERE ps.festivalDay = :festivalDay ORDER BY ps.startAt ASC")
    List<PerformanceSchedule> findByFestivalDayOrderByStartAtAsc(@Param("festivalDay") FestivalDay festivalDay);

    @Query("SELECT ps FROM PerformanceSchedule ps JOIN FETCH ps.stage WHERE ps.performer.id = :performerId")
    List<PerformanceSchedule> findByPerformerId(@Param("performerId") Long performerId);
}