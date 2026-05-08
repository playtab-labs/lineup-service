package com.playtab.lineupservice.service;

import com.playtab.lineupservice.entity.Performer;
import com.playtab.lineupservice.exception.ErrorCode;
import com.playtab.lineupservice.exception.InvalidArgumentException;
import com.playtab.lineupservice.exception.NotFoundException;
import com.playtab.lineupservice.repository.PerformerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class PerformerCommandService {

    private final PerformerRepository performerRepository;

    public Performer create(Map<String, String> name, Map<String, String> description,
                            String imageUrl, boolean isActive) {
        validateName(name);

        Performer performer = Performer.builder()
                .name(name)
                .description(description)
                .imageUrl(imageUrl)
                .isActive(isActive)
                .build();

        return performerRepository.save(performer);
    }

    public Performer update(long id, Map<String, String> name, Map<String, String> description,
                            String imageUrl, boolean isActive) {
        validateName(name);

        Performer performer = performerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.PERFORMER_NOT_FOUND));

        performer.update(name, description, imageUrl, isActive);
        return performerRepository.save(performer);
    }

    public void delete(long id) {
        Performer performer = performerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.PERFORMER_NOT_FOUND));
        performerRepository.delete(performer);
    }

    private void validateName(Map<String, String> name) {
        if (name == null || name.isEmpty()) {
            throw new InvalidArgumentException("name is required (at least one locale)");
        }
    }
}