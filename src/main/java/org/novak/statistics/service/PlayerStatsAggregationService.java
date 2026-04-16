package org.novak.statistics.service;

import org.novak.statistics.dto.GoalieSeasonAggregatedStatsDto;
import org.novak.statistics.dto.PlayerSeasonAggregatedStatsDto;
import org.novak.statistics.dto.PlayerSeasonStatsDto;
import org.novak.statistics.entity.*;
import org.novak.statistics.repository.PlayerGameStatsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class PlayerStatsAggregationService {

    private static final Logger log = LoggerFactory.getLogger(PlayerStatsAggregationService.class);
    private final PlayerGameStatsRepository playerGameStatsRepository;
    private final RosterService rosterService;
    private final PlayerGameStatsService playerGameStatsService;

    public PlayerStatsAggregationService(PlayerGameStatsRepository playerGameStatsRepository, RosterService rosterService, PlayerGameStatsService playerGameStatsService) {
        this.playerGameStatsRepository = playerGameStatsRepository;
        this.rosterService = rosterService;
        this.playerGameStatsService = playerGameStatsService;
    }


    @Cacheable("playerStats")
    public List<PlayerSeasonAggregatedStatsDto> calculateAllAggregatedPlayerStats(Season season) {
        List<PlayerGameStats> allStats = playerGameStatsRepository.findBySeasonAndRegularSeasonAndNotGoalie(season);

        Map<Player, List<PlayerGameStats>> statsByPlayer = allStats.stream()
                .collect(Collectors.groupingBy(PlayerGameStats::getPlayer));

        List<PlayerSeasonAggregatedStatsDto> aggregatedStats = new ArrayList<>();

        for (Map.Entry<Player, List<PlayerGameStats>> entry : statsByPlayer.entrySet()) {
            Player player = entry.getKey();
            List<PlayerGameStats> playerStats = entry.getValue();

            Team team = playerStats.stream()
                    .max(Comparator.comparing(pgs -> pgs.getGame().getStartTimeUtc()))
                    .map(PlayerGameStats::getTeam)
                    .orElse(player.getCurrentTeam());

            PlayerSeasonStatsDto pssd = sumOfGameStats(playerStats, player);

            aggregatedStats.add(new PlayerSeasonAggregatedStatsDto(
                    player,
                    team,
                    pssd.getGamesPlayed(),
                    pssd.getGoals(),
                    pssd.getAssists(),
                    pssd.getPoints()
            ));
        }

        return aggregatedStats;
    }

    @Cacheable("goalieStats")
    public List<GoalieSeasonAggregatedStatsDto> calculateAllAggregatedGoalieStats(Season season) {
        List<PlayerGameStats> allStats = playerGameStatsRepository.findBySeasonAndRegularSeasonAndGoalie(season);

        Map<Player, List<PlayerGameStats>> statsByPlayer = allStats.stream()
                .collect(Collectors.groupingBy(PlayerGameStats::getPlayer));

        List<GoalieSeasonAggregatedStatsDto> aggregatedStats = new ArrayList<>();

        for (Map.Entry<Player, List<PlayerGameStats>> entry : statsByPlayer.entrySet()) {
            Player player = entry.getKey();
            List<PlayerGameStats> playerStats = entry.getValue();

            Team team = playerStats.stream()
                    .max(Comparator.comparing(pgs -> pgs.getGame().getStartTimeUtc()))
                    .map(PlayerGameStats::getTeam)
                    .orElse(player.getCurrentTeam());

            PlayerSeasonStatsDto pssd = sumOfGameStats(playerStats, player);

            aggregatedStats.add(new GoalieSeasonAggregatedStatsDto(
                    player,
                    team,
                    pssd.getGamesPlayed(),
                    pssd.getShotsAgainst(),
                    pssd.getGoalsAgainst(),
                    pssd.getSavePctg(),
                    pssd.getGaa()
            ));
        }

        return aggregatedStats;
    }



    public List<PlayerSeasonStatsDto> getAllPlayerSeasons(Player player){
        List<PlayerSeasonStatsDto> seasonStats = new ArrayList<>();
        List<Roster> rosters = rosterService.getRostersByPlayer(player);

        List<PlayerGameStats> gameStats = playerGameStatsService.getPlayerGameStatsByPlayer(player);

        for (Roster roster : rosters) {
            List<PlayerGameStats> gameStatsForRoster = new ArrayList<>();
            for (PlayerGameStats gameStat : gameStats) {
                if (gameStat.getGame().getSeason().equals(roster.getTeamSeason().getSeason()) &&
                        gameStat.getGame().getGameType() == 2 &&
                        gameStat.getTeam().equals(roster.getTeamSeason().getTeam())) {
                    gameStatsForRoster.add(gameStat);
                }
            }
            PlayerSeasonStatsDto stats = sumOfGameStats(gameStatsForRoster, player);
            stats.setRoster(roster);
            seasonStats.add(stats);
        }

        return seasonStats;
    }

    private PlayerSeasonStatsDto sumOfGameStats(List<PlayerGameStats> gameStats, Player player) {
        PlayerSeasonStatsDto stats = new PlayerSeasonStatsDto();

        stats.setPlayer(player);

        int gamesPlayed = 0;
        int goals = 0;
        int assists = 0;
        int points = 0;
        int plusMinus = 0;
        int pim = 0;
        int blockedShots = 0;
        int hits = 0;
        int shots = 0;
        int shifts = 0;
        int toiInSeconds = 0;

        int shotsAgainst = 0;
        int goalsAgainst = 0;


        for(PlayerGameStats gameStat : gameStats){
            gamesPlayed++;
            goals += gameStat.getGoals();
            assists += gameStat.getAssists();
            points += gameStat.getPoints();
            pim += gameStat.getPim();
            if(gameStat.getToi() != null && !gameStat.getToi().isEmpty() && !gameStat.getToi().equals("00:00")){
                try {
                        String[] s = gameStat.getToi().split(":");
                        toiInSeconds += Integer.parseInt(s[0]) * 60 + Integer.parseInt(s[1]);

                } catch (NumberFormatException e) {
                    log.warn("Nepodařilo se zparsovat TOI: {}", gameStat.getToi());
                }
            }
            if(gameStat.isGoalie()){
                shotsAgainst += gameStat.getShotsAgainst();
                goalsAgainst += gameStat.getGoalsAgainst();

            }else{
                plusMinus += gameStat.getPlusMinus();
                blockedShots += gameStat.getBlockedShots();
                hits += gameStat.getHits();
                shots += gameStat.getShots();
                shifts += gameStat.getShifts();
            }



        }
        float savePctg = 0f;
        if (shotsAgainst > 0) {
            savePctg = (float)(shotsAgainst - goalsAgainst) / shotsAgainst;
        }

        float gaa = 0f;
        if (toiInSeconds > 0 && goalsAgainst > 0) {
            gaa = ((float) goalsAgainst / toiInSeconds) * 3600;
        }



        stats.setGamesPlayed(gamesPlayed);
        stats.setGoals(goals);
        stats.setAssists(assists);
        stats.setPoints(points);
        stats.setPlusMinus(plusMinus);
        stats.setPim(pim);
        stats.setBlockedShots(blockedShots);
        stats.setHits(hits);
        stats.setShots(shots);
        stats.setShifts(shifts);
        stats.setToi(String.format("%d:%02d", toiInSeconds / 60, toiInSeconds % 60));

        stats.setShotsAgainst(shotsAgainst);
        stats.setGoalsAgainst(goalsAgainst);
        stats.setSavePctg(savePctg);
        stats.setGaa(gaa);

        return stats;
    }
}