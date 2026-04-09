package com.playtab.lineupservice.repository;

import com.playtab.lineupservice.entity.FestivalDay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FestivalDayRepository extends JpaRepository<FestivalDay, Long> {
    Optional<FestivalDay> findByDayNumber(Integer dayNumber);
    List<FestivalDay> findAllByOrderByDayNumberAsc();
}