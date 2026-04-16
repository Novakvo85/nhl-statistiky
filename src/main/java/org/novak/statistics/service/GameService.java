package org.novak.statistics.service;

import org.novak.statistics.entity.Game;
import org.novak.statistics.entity.Season;
import org.novak.statistics.entity.Team;
import org.novak.statistics.exception.EntityNotFoundException;
import org.novak.statistics.repository.GameRepository;
import org.novak.statistics.repository.SeasonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class GameService {

    private static final Logger log = LoggerFactory.getLogger(GameService.class);

    private final SeasonRepository seasonRepository;
    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository, SeasonRepository seasonRepository) {
        this.gameRepository = gameRepository;
        this.seasonRepository = seasonRepository;
    }

    public List<Game> getGames() {
            return gameRepository.findAll();
    }

    public List<Game> getGamesBySeasonIdAndTeam(Long seasonId, Team team) {
        Season season = seasonRepository.findById(seasonId).orElseThrow(() -> {
            log.error("Sezóna {} nenalezena", seasonId);
            return new EntityNotFoundException("Sezóna " + seasonId + " nenalezena");
        });
        List<Game> games = gameRepository.findGamesBySeasonAndHomeTeam(season, team);
        games.addAll(gameRepository.findGamesBySeasonAndVisitingTeam(season, team));
        return games;
    }

    public List<Game> getGamesByDate(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);
        return gameRepository.findByStartTimeUtcBetweenOrderByStartTimeUtcAsc(start, end);
    }

    public Game getGameById(Long id) {
        return gameRepository.findById(id).orElseThrow(() -> {
            log.error("Zápas {} nenalezen", id);
            return new EntityNotFoundException("Zápas " + id + " nenalezena");
        });
    }

    public List<Game> getUpcomingGamesForTeams(Set<Team> teams, int limit) {
        if (teams == null || teams.isEmpty()) {
            return List.of();
        }
        return gameRepository.findUpcomingGamesForTeams(
                teams,
                LocalDateTime.now(),
                PageRequest.of(0, limit)
        );

    }



}
