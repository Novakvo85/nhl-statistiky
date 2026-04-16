package org.novak.statistics.service.importService;

import org.novak.statistics.dto.PlayerGameStatsDto;
import org.novak.statistics.dto.response.PlayerGameStatsApiResponse;
import org.novak.statistics.entity.*;
import org.novak.statistics.exception.EntityNotFoundException;
import org.novak.statistics.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@Service
public class PlayerGameStatsImportService {

    private static final Logger log = LoggerFactory.getLogger(PlayerGameStatsImportService.class);
    private final PlayerRepository playerRepository;
    private final PlayerImportService playerImportService;
    private final PlayerGameStatsRepository playerGameStatsRepository;
    private final TeamSeasonRepository teamSeasonRepository;
    private final RosterRepository rosterRepository;
    private final RestTemplate restTemplate;


    public PlayerGameStatsImportService(PlayerRepository playerRepository, PlayerImportService playerImportService, PlayerGameStatsRepository playerGameStatsRepository, TeamSeasonRepository teamSeasonRepository, RosterRepository rosterRepository, RestTemplate restTemplate){
        this.playerRepository = playerRepository;
        this.playerImportService = playerImportService;
        this.playerGameStatsRepository = playerGameStatsRepository;
        this.teamSeasonRepository = teamSeasonRepository;
        this.rosterRepository = rosterRepository;
        this.restTemplate = restTemplate;
    }

    public void importPlayerGameStats(Game game){
        if(!game.getGameState().equals("OFF")){
            return;
        }

        String url = "https://api-web.nhle.com/v1/gamecenter/" + game.getId() + "/boxscore";

        PlayerGameStatsApiResponse response = null;
        try {
            response = restTemplate.getForObject(url, PlayerGameStatsApiResponse.class);
            if (response.getPlayerByGameStats() == null) {
                log.warn("Odpověď byla null: {}", url);
                return;
            }
        } catch (Exception e) {
            log.error("Error při získávání hráčských statistik: {}", url, e);
            return;
        }
        if (response.getPlayerByGameStats().getAwayTeam() != null) {
            importStatsForPlayers(response.getPlayerByGameStats().getAwayTeam().getForwards(), game, game.getVisitingTeam(), game.getSeason());
            importStatsForPlayers(response.getPlayerByGameStats().getAwayTeam().getDefense(), game, game.getVisitingTeam(), game.getSeason());
            importStatsForPlayers(response.getPlayerByGameStats().getAwayTeam().getGoalies(), game, game.getVisitingTeam(), game.getSeason());
        }
        if (response.getPlayerByGameStats().getHomeTeam() != null) {
            importStatsForPlayers(response.getPlayerByGameStats().getHomeTeam().getForwards(), game, game.getHomeTeam(), game.getSeason());
            importStatsForPlayers(response.getPlayerByGameStats().getHomeTeam().getDefense(), game, game.getHomeTeam(), game.getSeason());
            importStatsForPlayers(response.getPlayerByGameStats().getHomeTeam().getGoalies(), game, game.getHomeTeam(), game.getSeason());
        }

    }

    private void importStatsForPlayers(List<PlayerGameStatsDto> dtos, Game game, Team team, Season season) {
        if (dtos != null) {
            for (PlayerGameStatsDto dto : dtos) {
                importSpecificPlayerGameStats(dto, game, team, season);
            }
        }
    }

    public void importSpecificPlayerGameStats(PlayerGameStatsDto dto, Game game, Team team, Season season){

        if (!playerRepository.existsById(dto.getPlayerId())){
            playerImportService.importPlayer(dto.getPlayerId());
        }

        Player player = playerRepository.findById(dto.getPlayerId()).orElseThrow(() -> new EntityNotFoundException("Player " + dto.getPlayerId() + " not found"));

        if (playerGameStatsRepository.existsByPlayerAndGame(player, game)){
            return;
        }

        PlayerGameStats stats = new PlayerGameStats();
        stats.setGame(game);
        stats.setPlayer(player);
        stats.setTeam(team);
        stats.setGoals(dto.getGoals());
        stats.setAssists(dto.getAssists());
        stats.setPim(dto.getPim());
        stats.setToi(dto.getToi());
        if ("G".equals(player.getPosition())){
            stats.setGoalie(true);
            stats.setShotsAgainst(dto.getShotsAgainst());
            stats.setGoalsAgainst(dto.getGoalsAgainst());
            stats.setSavePctg(dto.getSavePctg());
        }
        else {
            stats.setGoalie(false);
            stats.setPoints(dto.getPoints());
            stats.setPlusMinus(dto.getPlusMinus());
            stats.setPowerPlayGoals(dto.getPowerPlayGoals());
            stats.setFaceoffWinningPctg(dto.getFaceoffWinningPctg());
            stats.setBlockedShots(dto.getBlockedShots());
            stats.setHits(dto.getHits());
            stats.setShots(dto.getShots());
            stats.setShifts(dto.getShifts());
        }
        playerGameStatsRepository.save(stats);
        TeamSeason teamSeason = teamSeasonRepository.findBySeasonAndTeam(season, team);

        if (!rosterRepository.existsByPlayerIdAndTeamSeasonId(player.getId(),teamSeason.getId())){


            Roster roster = new Roster();
            roster.setPlayer(player);
            roster.setPosition(dto.getPosition());
            roster.setTeamSeason(teamSeason);
            roster.setSweaterNumber(dto.getSweaterNumber());
            rosterRepository.save(roster);
        }
    }

}