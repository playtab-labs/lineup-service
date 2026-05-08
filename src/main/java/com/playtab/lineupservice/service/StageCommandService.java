package com.playtab.lineupservice.service;

import com.playtab.lineupservice.entity.Stage;
import com.playtab.lineupservice.exception.ErrorCode;
import com.playtab.lineupservice.exception.InvalidArgumentException;
import com.playtab.lineupservice.exception.NotFoundException;
import com.playtab.lineupservice.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class StageCommandService {

    private final StageRepository stageRepository;

    public Stage create(Map<String, String> name, Map<String, String> locationDesc, int displayOrder) {
        validateName(name);

        Stage stage = Stage.builder()
                .name(name)
                .locationDesc(locationDesc)
                .displayOrder(displayOrder)
                .build();

        return stageRepository.save(stage);
    }

    public Stage update(long id, Map<String, String> name, Map<String, String> locationDesc, int displayOrder) {
        validateName(name);

        Stage stage = stageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.STAGE_NOT_FOUND));

        stage.update(name, locationDesc, displayOrder);
        return stageRepository.save(stage);
    }

    public void delete(long id) {
        Stage stage = stageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.STAGE_NOT_FOUND));
        stageRepository.delete(stage);
    }

    private void validateName(Map<String, String> name) {
        if (name == null || name.isEmpty()) {
            throw new InvalidArgumentException("name is required (at least one locale)");
        }
    }
}