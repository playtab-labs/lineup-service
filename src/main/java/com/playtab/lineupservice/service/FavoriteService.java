package com.playtab.lineupservice.service;

import com.playtab.lineupservice.entity.Favorite;
import com.playtab.lineupservice.entity.Performer;
import com.playtab.lineupservice.exception.DuplicateFavoriteException;
import com.playtab.lineupservice.exception.ErrorCode;
import com.playtab.lineupservice.exception.NotFoundException;
import com.playtab.lineupservice.repository.FavoriteRepository;
import com.playtab.lineupservice.repository.PerformerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final PerformerRepository performerRepository;

    public Favorite addFavorite(Long userId, Long performerId) {
        Performer performer = performerRepository.findById(performerId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.PERFORMER_NOT_FOUND));

        boolean exists = favoriteRepository.existsByUserIdAndPerformer(userId, performer);
        if (exists) {
            throw new DuplicateFavoriteException(ErrorCode.FAVORITE_ALREADY_EXISTS);
        }

        Favorite favorite = Favorite.builder()
                .userId(userId)
                .performer(performer)
                .build();

        return favoriteRepository.save(favorite);
    }

    public void removeFavorite(Long userId, Long performerId) {
        Performer performer = performerRepository.findById(performerId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.PERFORMER_NOT_FOUND));

        favoriteRepository.deleteByUserIdAndPerformer(userId, performer);
    }

    @Transactional
    public List<Performer> getFavorites(Long userId) {
        return favoriteRepository.findByUserId(userId).stream()
                .map(Favorite::getPerformer)
                .toList();
    }
}