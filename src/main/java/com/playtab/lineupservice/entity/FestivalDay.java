package com.playtab.lineupservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Entity
@Table(name = "festival_days")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FestivalDay extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "day_number", nullable = false, unique = true)
    private Integer dayNumber;

    @Column(name = "event_date", nullable = false, unique = true)
    private LocalDate eventDate;

    @Builder
    public FestivalDay(Integer dayNumber, LocalDate eventDate) {
        this.dayNumber = dayNumber;
        this.eventDate = eventDate;
    }
}