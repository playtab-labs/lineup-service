package com.playtab.lineupservice.entity;

import com.playtab.lineupservice.entity.enums.ScheduleStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "performance_schedules")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PerformanceSchedule extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "performer_id", nullable = false)
    private Performer performer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stage_id", nullable = false)
    private Stage stage;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "festival_day_id", nullable = false)
    private FestivalDay festivalDay;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ScheduleStatus status;

    @Builder
    public PerformanceSchedule(
            Performer performer,
            Stage stage,
            FestivalDay festivalDay,
            LocalDateTime startAt,
            LocalDateTime endAt,
            ScheduleStatus status
    ) {
        if (startAt != null && endAt != null && !startAt.isBefore(endAt)) {
            throw new IllegalArgumentException("startAt must be before endAt");
        }
        this.performer = performer;
        this.stage = stage;
        this.festivalDay = festivalDay;
        this.startAt = startAt;
        this.endAt = endAt;
        this.status = status != null ? status : ScheduleStatus.SCHEDULED;
    }

    public void update(Performer performer, Stage stage, FestivalDay festivalDay,
                       LocalDateTime startAt, LocalDateTime endAt, ScheduleStatus status) {
        if (startAt != null && endAt != null && !startAt.isBefore(endAt)) {
            throw new IllegalArgumentException("startAt must be before endAt");
        }
        this.performer = performer;
        this.stage = stage;
        this.festivalDay = festivalDay;
        this.startAt = startAt;
        this.endAt = endAt;
        this.status = status != null ? status : ScheduleStatus.SCHEDULED;
    }
}