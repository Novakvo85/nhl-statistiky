package org.novak.statistics.repository;

import org.novak.statistics.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PlayerGameStatsRepository extends JpaRepository<PlayerGameStats, Long> {
    boolean existsByPlayerAndGame(Player player, Game game);
    List<PlayerGameStats> findByGameAndTeam(Game game, Team team);
    List<PlayerGameStats> findByPlayer(Player player);

    @Modifying
    @Query("DELETE FROM PlayerGameStats p WHERE p.game.season.id = :seasonId")
    int deleteByGame_Season_Id(@Param("seasonId") long seasonId);

    @Query("SELECT pgs FROM PlayerGameStats pgs WHERE pgs.game.startTimeUtc > :cutoffDate")
    List<PlayerGameStats> findByGameStartTimeUtcAfter(@Param("cutoffDate") LocalDateTime cutoffDate);

    @Query("SELECT pgs FROM PlayerGameStats pgs " +
           "WHERE pgs.game.season = :season " +
           "AND pgs.game.gameType = 2 " +
           "AND pgs.isGoalie = false")
    List<PlayerGameStats> findBySeasonAndRegularSeasonAndNotGoalie(@Param("season") Season season);

    @Query("SELECT pgs FROM PlayerGameStats pgs " +
            "WHERE pgs.game.season = :season " +
            "AND pgs.game.gameType = 2 " +
            "AND pgs.isGoalie = true")
    List<PlayerGameStats> findBySeasonAndRegularSeasonAndGoalie(@Param("season") Season season);

}
