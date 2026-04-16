package org.novak.statistics.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.novak.statistics.dto.TeamSeasonStatsDto;
import org.novak.statistics.entity.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamSeasonStatsServiceTest {

    @Mock
    private GameService gameService;

    @InjectMocks
    private TeamSeasonStatsService service;

    private Team team;
    private TeamSeason teamSeason;

    @BeforeEach
    void setUp() {
        Season season = new Season();
        season.setId(20242025L);

        team = new Team();
        team.setId(1L);
        team.setName("Boston Bruins");

        teamSeason = new TeamSeason();
        teamSeason.setSeason(season);
        teamSeason.setTeam(team);
    }

    private Game createGame(Team homeTeam, Team visitingTeam, int homeScore, int visitingScore, String outcome) {
        Game game = new Game();
        game.setHomeTeam(homeTeam);
        game.setVisitingTeam(visitingTeam);
        game.setHomeScore(homeScore);
        game.setVisitingScore(visitingScore);
        game.setGameState("OFF");
        game.setGameType(2);
        game.setGameOutcome(outcome);
        game.setStartTimeUtc(LocalDateTime.now());
        return game;
    }

    @Test
    void calculateTeamSeasonStats_countsWinAsHomeTeam() {
        Team opponent = new Team();
        opponent.setId(2L);

        Game game = createGame(team, opponent, 3, 1, "REG");

        when(gameService.getGamesBySeasonIdAndTeam(20242025L, team)).thenReturn(List.of(game));

        TeamSeasonStatsDto result = service.calculateTeamSeasonStats(teamSeason);

        assertThat(result.getMatches()).isEqualTo(1);
        assertThat(result.getWins()).isEqualTo(1);
        assertThat(result.getLosses()).isEqualTo(0);
        assertThat(result.getPoints()).isEqualTo(2);
        assertThat(result.getGoalsFor()).isEqualTo(3);
        assertThat(result.getGoalsAgainst()).isEqualTo(1);
    }

    @Test
    void calculateTeamSeasonStats_countsOvertimeLoss() {
        Team opponent = new Team();
        opponent.setId(2L);

        Game game = createGame(team, opponent, 2, 3, "OT");

        when(gameService.getGamesBySeasonIdAndTeam(20242025L, team)).thenReturn(List.of(game));

        TeamSeasonStatsDto result = service.calculateTeamSeasonStats(teamSeason);

        assertThat(result.getOvertimeLosses()).isEqualTo(1);
        assertThat(result.getLosses()).isEqualTo(0);
        assertThat(result.getPoints()).isEqualTo(1);
    }

    @Test
    void calculateTeamSeasonStats_countsRegularLoss() {
        Team opponent = new Team();
        opponent.setId(2L);

        Game game = createGame(team, opponent, 1, 4, "REG");

        when(gameService.getGamesBySeasonIdAndTeam(20242025L, team)).thenReturn(List.of(game));

        TeamSeasonStatsDto result = service.calculateTeamSeasonStats(teamSeason);

        assertThat(result.getLosses()).isEqualTo(1);
        assertThat(result.getOvertimeLosses()).isEqualTo(0);
        assertThat(result.getPoints()).isEqualTo(0);
    }

    @Test
    void calculateTeamSeasonStats_countsWinAsVisitingTeam() {
        Team opponent = new Team();
        opponent.setId(2L);

        Game game = createGame(opponent, team, 1, 3, "REG");

        when(gameService.getGamesBySeasonIdAndTeam(20242025L, team)).thenReturn(List.of(game));

        TeamSeasonStatsDto result = service.calculateTeamSeasonStats(teamSeason);

        assertThat(result.getWins()).isEqualTo(1);
        assertThat(result.getPoints()).isEqualTo(2);
        assertThat(result.getGoalsFor()).isEqualTo(3);
        assertThat(result.getGoalsAgainst()).isEqualTo(1);
    }

    @Test
    void calculateTeamSeasonStats_pointPctgIsCorrect() {
        Team opponent = new Team();
        opponent.setId(2L);

        Game win1 = createGame(team, opponent, 3, 1, "REG");
        Game win2 = createGame(team, opponent, 2, 0, "REG");
        Game loss = createGame(team, opponent, 0, 2, "REG");

        when(gameService.getGamesBySeasonIdAndTeam(20242025L, team)).thenReturn(List.of(win1, win2, loss));

        TeamSeasonStatsDto result = service.calculateTeamSeasonStats(teamSeason);

        assertThat(result.getPoints()).isEqualTo(4);
        assertThat(result.getPointPctg()).isCloseTo(0.667f, offset(0.001f));
    }

    @Test
    void calculateTeamSeasonStats_returnsZerosWhenNoGames() {
        when(gameService.getGamesBySeasonIdAndTeam(20242025L, team)).thenReturn(List.of());

        TeamSeasonStatsDto result = service.calculateTeamSeasonStats(teamSeason);

        assertThat(result.getMatches()).isEqualTo(0);
        assertThat(result.getPoints()).isEqualTo(0);
        assertThat(result.getPointPctg()).isEqualTo(0f);
    }
}