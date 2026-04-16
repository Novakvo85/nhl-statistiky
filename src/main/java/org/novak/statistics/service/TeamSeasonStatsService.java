package org.novak.statistics.service;

import org.novak.statistics.dto.TeamSeasonStatsDto;
import org.novak.statistics.entity.Game;
import org.novak.statistics.entity.TeamSeason;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@Service
public class    TeamSeasonStatsService {

    private final GameService gameService;

    public TeamSeasonStatsService(GameService gameService) {
        this.gameService = gameService;
    }


    public TeamSeasonStatsDto calculateTeamSeasonStats(TeamSeason teamSeason) {
        List<Game> games = gameService.getGamesBySeasonIdAndTeam(teamSeason.getSeason().getId(), teamSeason.getTeam());

        int matches = 0;
        int wins = 0;
        int losses = 0;
        int overtimeLosses = 0;
        int points = 0;
        int goalsFor = 0;
        int goalsAgainst = 0;

        for (Game game : games) {
            if (game.getGameState().equals("OFF") && game.getGameType() == 2) {
                if (game.getHomeTeam().equals(teamSeason.getTeam())) {
                    goalsFor += game.getHomeScore();
                    goalsAgainst += game.getVisitingScore();

                    if (game.getHomeScore() > game.getVisitingScore()) {
                        wins++;
                        points += 2;
                    } else {
                        if (game.getGameOutcome().equals("REG")) {
                            losses++;
                        } else {
                            overtimeLosses++;
                            points += 1;
                        }
                    }
                    matches++;
                } else if (game.getVisitingTeam().equals(teamSeason.getTeam())) {
                    goalsFor += game.getVisitingScore();
                    goalsAgainst += game.getHomeScore();

                    if (game.getHomeScore() < game.getVisitingScore()) {
                        wins++;
                        points += 2;
                    } else {
                        if (game.getGameOutcome().equals("REG")) {
                            losses++;
                        } else {
                            overtimeLosses++;
                            points += 1;
                        }
                    }
                    matches++;
                }
            }
        }

        float pointPctg = (matches > 0) ? (float) points / (matches * 2) : 0f;

        return new TeamSeasonStatsDto(teamSeason, matches, wins, losses, overtimeLosses, pointPctg, points, goalsFor, goalsAgainst);
    }


    public List<TeamSeasonStatsDto> calculateStandings(List<TeamSeason> teamSeasons) {
        List<TeamSeasonStatsDto> standings = new ArrayList<>();
        for (TeamSeason teamSeason : teamSeasons) {
            standings.add(calculateTeamSeasonStats(teamSeason));
        }
        standings.sort(Comparator.comparingInt(TeamSeasonStatsDto::getPoints)
                .thenComparingDouble(TeamSeasonStatsDto::getPointPctg)
                .thenComparingInt(TeamSeasonStatsDto::getWins).reversed());

        return standings;
    }
}
