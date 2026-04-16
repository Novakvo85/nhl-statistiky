package org.novak.statistics.repository;

import org.novak.statistics.entity.Game;
import org.novak.statistics.entity.Season;
import org.novak.statistics.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface GameRepository extends JpaRepository<Game, Long> {
    List<Game> findGamesBySeasonAndHomeTeam(Season season, Team team);
    List<Game> findGamesBySeasonAndVisitingTeam(Season season, Team team);
    List<Game> findByStartTimeUtcBetweenOrderByStartTimeUtcAsc(LocalDateTime from, LocalDateTime to);

    @Query("SELECT game FROM Game game " +
            "WHERE(game.homeTeam IN :teams OR game.visitingTeam IN :teams) " +
            "AND game.startTimeUtc > :now " +
            "ORDER BY game.startTimeUtc ASC")
    List<Game> findUpcomingGamesForTeams(@Param("teams") Set<Team> teams, @Param("now") LocalDateTime now, Pageable pageable);

    @Query("SELECT g FROM Game g " +
            "WHERE g.gameState = 'OFF' " +
            "AND g.season = :season " +
            "AND NOT EXISTS (SELECT s FROM PlayerGameStats s WHERE s.game = g)")
    List<Game> findFinishedGamesWithoutStats(@Param("season") Season season);

    @Modifying
    @Query("DELETE FROM Game g WHERE g.season.id = :seasonId")
    int deleteBySeasonId(@Param("seasonId") long seasonId);

}
