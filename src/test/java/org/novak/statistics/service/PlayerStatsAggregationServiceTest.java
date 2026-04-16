package org.novak.statistics.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.novak.statistics.dto.PlayerSeasonStatsDto;
import org.novak.statistics.entity.*;
import org.novak.statistics.repository.PlayerGameStatsRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlayerStatsAggregationServiceTest {

    @Mock
    private PlayerGameStatsRepository playerGameStatsRepository;
    @Mock
    private RosterService rosterService;
    @Mock
    private PlayerGameStatsService playerGameStatsService;

    @InjectMocks
    private PlayerStatsAggregationService service;

    private Player player;
    private Roster roster;
    private Team team;
    private Season season;

    @BeforeEach
    void setUp() {
        player = new Player();
        player.setId(1L);

        season = new Season();
        season.setId(20242025L);

        team = new Team();
        team.setId(1L);

        TeamSeason teamSeason = new TeamSeason();
        teamSeason.setSeason(season);
        teamSeason.setTeam(team);

        roster = new Roster();
        roster.setTeamSeason(teamSeason);
    }

    @Test
    void sumOfSkaterStats_countsGamesPlayed() {
        List<PlayerGameStats> s = createSkaterStats(team, season);
        when(rosterService.getRostersByPlayer(player)).thenReturn(List.of(roster));
        when(playerGameStatsService.getPlayerGameStatsByPlayer(player)).thenReturn(List.of(s.get(0), s.get(1)));

        PlayerSeasonStatsDto stats = service.getAllPlayerSeasons(player).getFirst();
        assertThat(stats.getGamesPlayed()).isEqualTo(2);
    }

    @Test
    void sumOfSkaterStats_aggregatesGoalsAssistsPoints() {
        List<PlayerGameStats> s = createSkaterStats(team, season);
        when(rosterService.getRostersByPlayer(player)).thenReturn(List.of(roster));
        when(playerGameStatsService.getPlayerGameStatsByPlayer(player)).thenReturn(List.of(s.get(0), s.get(1)));

        PlayerSeasonStatsDto stats = service.getAllPlayerSeasons(player).getFirst();
        assertThat(stats.getGoals()).isEqualTo(3);
        assertThat(stats.getAssists()).isEqualTo(3);
        assertThat(stats.getPoints()).isEqualTo(6);
    }

    @Test
    void sumOfSkaterStats_aggregatesPlusMinus() {
        List<PlayerGameStats> s = createSkaterStats(team, season);
        when(rosterService.getRostersByPlayer(player)).thenReturn(List.of(roster));
        when(playerGameStatsService.getPlayerGameStatsByPlayer(player)).thenReturn(List.of(s.get(0), s.get(1)));

        PlayerSeasonStatsDto stats = service.getAllPlayerSeasons(player).getFirst();
        assertThat(stats.getPlusMinus()).isEqualTo(0);
    }

    @Test
    void sumOfSkaterStats_aggregatesPimBlockedShotsHitsShotsShifts() {
        List<PlayerGameStats> s = createSkaterStats(team, season);
        when(rosterService.getRostersByPlayer(player)).thenReturn(List.of(roster));
        when(playerGameStatsService.getPlayerGameStatsByPlayer(player)).thenReturn(List.of(s.get(0), s.get(1)));

        PlayerSeasonStatsDto stats = service.getAllPlayerSeasons(player).getFirst();
        assertThat(stats.getPim()).isEqualTo(6);
        assertThat(stats.getBlockedShots()).isEqualTo(5);
        assertThat(stats.getHits()).isEqualTo(5);
        assertThat(stats.getShots()).isEqualTo(8);
        assertThat(stats.getShifts()).isEqualTo(11);
    }

    @Test
    void sumOfSkaterStats_calculatesToi() {
        List<PlayerGameStats> s = createSkaterStats(team, season);
        when(rosterService.getRostersByPlayer(player)).thenReturn(List.of(roster));
        when(playerGameStatsService.getPlayerGameStatsByPlayer(player)).thenReturn(List.of(s.get(0), s.get(1)));

        PlayerSeasonStatsDto stats = service.getAllPlayerSeasons(player).getFirst();
        assertThat(stats.getToi()).isEqualTo("38:30");
    }

    @Test
    void sumOfGoalieStats_countsGamesPlayed() {
        List<PlayerGameStats> s = createGoalieStats(team, season);
        when(rosterService.getRostersByPlayer(player)).thenReturn(List.of(roster));
        when(playerGameStatsService.getPlayerGameStatsByPlayer(player)).thenReturn(List.of(s.get(0), s.get(1)));

        PlayerSeasonStatsDto stats = service.getAllPlayerSeasons(player).getFirst();
        assertThat(stats.getGamesPlayed()).isEqualTo(2);
    }

    @Test
    void sumOfGoalieStats_aggregatesShotsAndGoalsAgainst() {
        List<PlayerGameStats> s = createGoalieStats(team, season);
        when(rosterService.getRostersByPlayer(player)).thenReturn(List.of(roster));
        when(playerGameStatsService.getPlayerGameStatsByPlayer(player)).thenReturn(List.of(s.get(0), s.get(1)));

        PlayerSeasonStatsDto stats = service.getAllPlayerSeasons(player).getFirst();
        assertThat(stats.getShotsAgainst()).isEqualTo(55);
        assertThat(stats.getGoalsAgainst()).isEqualTo(3);
    }

    @Test
    void sumOfGoalieStats_calculatesSavePctg() {
        List<PlayerGameStats> s = createGoalieStats(team, season);
        when(rosterService.getRostersByPlayer(player)).thenReturn(List.of(roster));
        when(playerGameStatsService.getPlayerGameStatsByPlayer(player)).thenReturn(List.of(s.get(0), s.get(1)));

        PlayerSeasonStatsDto stats = service.getAllPlayerSeasons(player).getFirst();
        assertThat(stats.getSavePctg()).isCloseTo(0.945f, offset(0.001f));
    }

    @Test
    void sumOfGoalieStats_calculatesGaa() {
        List<PlayerGameStats> s = createGoalieStats(team, season);
        when(rosterService.getRostersByPlayer(player)).thenReturn(List.of(roster));
        when(playerGameStatsService.getPlayerGameStatsByPlayer(player)).thenReturn(List.of(s.get(0), s.get(1)));

        PlayerSeasonStatsDto stats = service.getAllPlayerSeasons(player).getFirst();
        assertThat(stats.getGaa()).isCloseTo(1.50f, offset(0.01f));
    }

    @Test
    void sumOfGoalieStats_calculatesToi() {
        List<PlayerGameStats> s = createGoalieStats(team, season);
        when(rosterService.getRostersByPlayer(player)).thenReturn(List.of(roster));
        when(playerGameStatsService.getPlayerGameStatsByPlayer(player)).thenReturn(List.of(s.get(0), s.get(1)));

        PlayerSeasonStatsDto stats = service.getAllPlayerSeasons(player).getFirst();
        assertThat(stats.getToi()).isEqualTo("120:00");
    }

    @Test
    void getAllPlayerSeasons_handlesNullToi() {
        Game game = new Game();
        game.setSeason(season);
        game.setGameType(2);
        game.setStartTimeUtc(LocalDateTime.now());

        PlayerGameStats stat = new PlayerGameStats();
        stat.setGame(game);
        stat.setTeam(team);
        stat.setGoalie(false);
        stat.setGoals(1);
        stat.setAssists(0);
        stat.setPoints(1);
        stat.setToi(null);

        when(rosterService.getRostersByPlayer(player)).thenReturn(List.of(roster));
        when(playerGameStatsService.getPlayerGameStatsByPlayer(player)).thenReturn(List.of(stat));

        List<PlayerSeasonStatsDto> result = service.getAllPlayerSeasons(player);
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getToi()).isEqualTo("0:00");
    }

    @Test
    void getAllPlayerSeasons_returnsEmptyListWhenNoRosters() {
        Player player = new Player();
        player.setId(1L);

        when(rosterService.getRostersByPlayer(player)).thenReturn(List.of());
        when(playerGameStatsService.getPlayerGameStatsByPlayer(player)).thenReturn(List.of());

        List<PlayerSeasonStatsDto> result = service.getAllPlayerSeasons(player);

        assertThat(result).isEmpty();
    }


    private List<PlayerGameStats> createSkaterStats(Team team, Season season) {
        Game game1 = new Game();
        game1.setSeason(season);
        game1.setGameType(2);
        game1.setStartTimeUtc(LocalDateTime.now());

        Game game2 = new Game();
        game2.setSeason(season);
        game2.setGameType(2);
        game2.setStartTimeUtc(LocalDateTime.now());

        PlayerGameStats stat1 = new PlayerGameStats();
        stat1.setGame(game1);
        stat1.setTeam(team);
        stat1.setGoalie(false);
        stat1.setGoals(2);
        stat1.setAssists(1);
        stat1.setPoints(3);
        stat1.setPlusMinus(1);
        stat1.setPim(2);
        stat1.setBlockedShots(3);
        stat1.setHits(4);
        stat1.setShots(5);
        stat1.setShifts(6);
        stat1.setToi("18:30");

        PlayerGameStats stat2 = new PlayerGameStats();
        stat2.setGame(game2);
        stat2.setTeam(team);
        stat2.setGoalie(false);
        stat2.setGoals(1);
        stat2.setAssists(2);
        stat2.setPoints(3);
        stat2.setPlusMinus(-1);
        stat2.setPim(4);
        stat2.setBlockedShots(2);
        stat2.setHits(1);
        stat2.setShots(3);
        stat2.setShifts(5);
        stat2.setToi("20:00");

        return List.of(stat1, stat2);
    }

    private List<PlayerGameStats> createGoalieStats(Team team, Season season) {
        Game game1 = new Game();
        game1.setSeason(season);
        game1.setGameType(2);
        game1.setStartTimeUtc(LocalDateTime.now());

        Game game2 = new Game();
        game2.setSeason(season);
        game2.setGameType(2);
        game2.setStartTimeUtc(LocalDateTime.now());

        PlayerGameStats stat1 = new PlayerGameStats();
        stat1.setGame(game1);
        stat1.setTeam(team);
        stat1.setGoalie(true);
        stat1.setGoals(0);
        stat1.setAssists(0);
        stat1.setPoints(0);
        stat1.setShotsAgainst(30);
        stat1.setGoalsAgainst(2);
        stat1.setToi("60:00");

        PlayerGameStats stat2 = new PlayerGameStats();
        stat2.setGame(game2);
        stat2.setTeam(team);
        stat2.setGoalie(true);
        stat2.setGoals(0);
        stat2.setAssists(1);
        stat2.setPoints(1);
        stat2.setShotsAgainst(25);
        stat2.setGoalsAgainst(1);
        stat2.setToi("60:00");

        return List.of(stat1, stat2);
    }
}