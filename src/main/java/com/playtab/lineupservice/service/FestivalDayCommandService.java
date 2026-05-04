package com.playtab.lineupservice.service;

import com.playtab.lineupservice.entity.FestivalDay;
import com.playtab.lineupservice.exception.ConflictException;
import com.playtab.lineupservice.exception.ErrorCode;
import com.playtab.lineupservice.exception.InvalidArgumentException;
import com.playtab.lineupservice.exception.NotFoundException;
import com.playtab.lineupservice.repository.FestivalDayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Service
@RequiredArgsConstructor
@Transactional
public class FestivalDayCommandService {

    private final FestivalDayRepository festivalDayRepository;

    public FestivalDay create(int dayNumber, String eventDateStr) {
        validateDayNumber(dayNumber);
        LocalDate eventDate = parseEventDate(eventDateStr);

        festivalDayRepository.findByDayNumber(dayNumber).ifPresent(existing -> {
            throw new ConflictException(ErrorCode.DUPLICATE_FESTIVAL_DAY,
                    "dayNumber " + dayNumber + " already exists");
        });

        FestivalDay entity = FestivalDay.builder()
                .dayNumber(dayNumber)
                .eventDate(eventDate)
                .build();

        try {
            return festivalDayRepository.saveAndFlush(entity);
        } catch (DataIntegrityViolationException e) {
            // eventDate unique 제약 위반 등 DB 레벨 충돌
            throw new ConflictException(ErrorCode.DUPLICATE_FESTIVAL_DAY,
                    "eventDate " + eventDateStr + " already exists");
        }
    }

    public FestivalDay update(long id, int dayNumber, String eventDateStr) {
        validateDayNumber(dayNumber);
        LocalDate eventDate = parseEventDate(eventDateStr);

        FestivalDay festivalDay = festivalDayRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.FESTIVAL_DAY_NOT_FOUND));

        // dayNumber 중복 검사 (자기 자신 제외)
        festivalDayRepository.findByDayNumber(dayNumber).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new ConflictException(ErrorCode.DUPLICATE_FESTIVAL_DAY,
                        "dayNumber " + dayNumber + " already exists");
            }
        });

        festivalDay.update(dayNumber, eventDate);

        try {
            return festivalDayRepository.saveAndFlush(festivalDay);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(ErrorCode.DUPLICATE_FESTIVAL_DAY,
                    "eventDate " + eventDateStr + " already exists");
        }
    }

    public void delete(long id) {
        FestivalDay festivalDay = festivalDayRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.FESTIVAL_DAY_NOT_FOUND));
        festivalDayRepository.delete(festivalDay);
    }

    // ─── private helpers ───

    private void validateDayNumber(int dayNumber) {
        if (dayNumber <= 0) {
            throw new InvalidArgumentException("dayNumber must be positive");
        }
    }

    private LocalDate parseEventDate(String eventDateStr) {
        if (eventDateStr == null || eventDateStr.isBlank()) {
            throw new InvalidArgumentException("eventDate is required (yyyy-MM-dd)");
        }
        try {
            return LocalDate.parse(eventDateStr);
        } catch (DateTimeParseException e) {
            throw new InvalidArgumentException("eventDate must be yyyy-MM-dd: " + eventDateStr);
        }
    }
}