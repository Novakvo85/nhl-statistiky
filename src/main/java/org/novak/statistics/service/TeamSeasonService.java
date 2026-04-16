package org.novak.statistics.service;

import org.novak.statistics.entity.Season;
import org.novak.statistics.entity.Team;
import org.novak.statistics.entity.TeamSeason;
import org.novak.statistics.exception.EntityNotFoundException;
import org.novak.statistics.repository.SeasonRepository;
import org.novak.statistics.repository.TeamSeasonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamSeasonService {

    private static final Logger log = LoggerFactory.getLogger(TeamSeasonService.class);

    private final TeamSeasonRepository teamSeasonRepository;
    private final SeasonRepository seasonRepository;

    public TeamSeasonService(TeamSeasonRepository teamSeasonRepository, SeasonRepository seasonRepository) {
        this.teamSeasonRepository = teamSeasonRepository;
        this.seasonRepository = seasonRepository;
    }

    public List<TeamSeason> getTeamSeasonsBySeasonId(long seasonId) {
        Season season = seasonRepository.findById(seasonId).orElseThrow(() -> {
            log.error("Sezóna {} nenalezena", seasonId);
            return new EntityNotFoundException("Sezóna " + seasonId + " nenalezena");
        });
        return teamSeasonRepository.findAllBySeason(season);
    }

    public TeamSeason getTeamSeasonBySeasonIdAndTeam(long seasonId, Team team) {
        Season season = seasonRepository.findById(seasonId).orElseThrow(() -> {
            log.error("Sezóna {} nenalezena pro tým {}", seasonId , team.getName());
            return new EntityNotFoundException("Sezóna " + seasonId + " nenalezena");
        });
        return teamSeasonRepository.findBySeasonAndTeam(season, team);
    }

    public List<TeamSeason> getTeamSeasonsByTeam(Team team) {
        return teamSeasonRepository.findAllByTeam(team);
    }
}
