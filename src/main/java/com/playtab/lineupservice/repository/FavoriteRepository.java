package com.playtab.lineupservice.repository;

import com.playtab.lineupservice.entity.Favorite;
import com.playtab.lineupservice.entity.Performer;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    Optional<Favorite> findByUserIdAndPerformer(Long userId, Performer performer);

    boolean existsByUserIdAndPerformer(Long userId, Performer performer);

    long deleteByUserIdAndPerformer(Long userId, Performer performer);

    @EntityGraph(attributePaths = {"performer"})
    List<Favorite> findByUserId(Long userId);
}