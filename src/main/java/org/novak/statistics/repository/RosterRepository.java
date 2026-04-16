package org.novak.statistics.repository;

import org.novak.statistics.entity.Player;
import org.novak.statistics.entity.Roster;
import org.novak.statistics.entity.TeamSeason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RosterRepository extends JpaRepository<Roster, Long>{
    boolean existsByPlayerIdAndTeamSeasonId(long playerId, long teamSeasonId);
    List<Roster> findByPlayer(Player player);
    List<Roster> findByTeamSeason(TeamSeason teamSeason);

    @Modifying
    @Query("DELETE FROM Roster r WHERE r.teamSeason.season.id = :seasonId")
    int deleteByTeamSeason_Season_Id(@Param("seasonId") long seasonId);



}
