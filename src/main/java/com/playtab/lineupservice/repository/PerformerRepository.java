package com.playtab.lineupservice.repository;

import com.playtab.lineupservice.entity.Performer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PerformerRepository extends JpaRepository<Performer, Long> {
    List<Performer> findByIsActiveTrue();
}