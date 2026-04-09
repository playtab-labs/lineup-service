package com.playtab.lineupservice.service;

import com.playtab.lineupservice.entity.FestivalDay;
import com.playtab.lineupservice.repository.FestivalDayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FestivalDayQueryService {

    private final FestivalDayRepository festivalDayRepository;

    public List<FestivalDay> getFestivalDays() {
        return festivalDayRepository.findAllByOrderByDayNumberAsc();
    }
}
