package org.novak.statistics.repository;

import org.novak.statistics.entity.Season;
import org.novak.statistics.entity.Team;
import org.novak.statistics.entity.TeamSeason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeamSeasonRepository extends JpaRepository<TeamSeason, Long> {
    List<TeamSeason> findAllBySeason(Season season);
    TeamSeason findBySeasonAndTeam(Season season, Team team);
    List<TeamSeason> findAllByTeam(Team team);

    @Modifying
    @Query("DELETE FROM TeamSeason ts WHERE ts.season.id = :seasonId")
    int deleteBySeasonId(@Param("seasonId") long seasonId);
}
