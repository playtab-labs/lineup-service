package com.playtab.lineupservice.repository;

import com.playtab.lineupservice.entity.Favorite;
import com.playtab.lineupservice.entity.Performer;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    Optional<Favorite> findByUserIdAndPerformer(String userId, Performer performer);

    boolean existsByUserIdAndPerformer(String userId, Performer performer);

    long deleteByUserIdAndPerformer(String userId, Performer performer);

    @EntityGraph(attributePaths = {"performer"})
    List<Favorite> findByUserId(String userId);
}