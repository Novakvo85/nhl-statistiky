package org.novak.statistics.service.updateService;

import org.novak.statistics.dto.GameDto;
import org.novak.statistics.dto.response.GameApiResponse;
import org.novak.statistics.entity.Game;
import org.novak.statistics.entity.Season;
import org.novak.statistics.entity.Team;
import org.novak.statistics.exception.EntityNotFoundException;
import org.novak.statistics.repository.GameRepository;
import org.novak.statistics.repository.TeamRepository;
import org.novak.statistics.service.importService.PlayerGameStatsImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
public class GameUpdateService {

    private static final Logger log = LoggerFactory.getLogger(GameUpdateService.class);
    private final GameRepository gameRepository;
    private final TeamRepository teamRepository;
    private final PlayerGameStatsImportService playerGameStatsImportService;
    private final RestTemplate restTemplate;

    public GameUpdateService(GameRepository gameRepository, TeamRepository teamRepository, PlayerGameStatsImportService playerGameStatsImportService, RestTemplate restTemplate) {
        this.gameRepository = gameRepository;
        this.teamRepository = teamRepository;
        this.playerGameStatsImportService = playerGameStatsImportService;
        this.restTemplate = restTemplate;
    }

    public void updateRecentGames(Season season, int daysBack) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysBack);
        log.info("Aktualizuji zápasy z posledních {} dní pro sezónu {}", daysBack, season.getId());
        processTeamGames(season, cutoffDate);
    }

    public void updateAllSeasonGames(Season season) {
        log.info("Kontrola všech zápasů sezóny {}", season.getId());
        processTeamGames(season, null);
    }

    private void processTeamGames(Season season, LocalDateTime cutoffDate) {
        List<Team> teams = teamRepository.findAll();
        Set<Long> processedGameIds = new HashSet<>();
        int newGames = 0;
        int updatedGames = 0;

        for (Team team : teams) {
            try {
                String url = "https://api-web.nhle.com/v1/club-schedule-season/" + team.getAbbreviation() + "/" + season.getId();
                GameApiResponse response = restTemplate.getForObject(url, GameApiResponse.class);

                if (response.getGames() == null) {
                    log.warn("Žádná data pro tým {}", team.getAbbreviation());
                    continue;
                }

                for (GameDto dto : response.getGames()) {
                    if (processedGameIds.contains(dto.getId())) continue;
                    if (dto.getGameType() != 2 && dto.getGameType() != 3) continue;
                    if (cutoffDate != null && dto.getStartTimeUTC().isBefore(cutoffDate)) continue;
                    if (cutoffDate == null && !shouldUpdateGame(dto)) continue;

                    boolean isNew = updateOrCreateGame(dto, season);
                    processedGameIds.add(dto.getId());
                    if (isNew) newGames++; else updatedGames++;
                }

            } catch (Exception e) {
                log.error("Chyba při aktualizaci zápasů pro tým {}", team.getAbbreviation(), e);
            }
        }

        log.info("Aktualizace dokončena: {} nových zápasů, {} aktualizovaných.", newGames, updatedGames);
    }


    private boolean shouldUpdateGame(GameDto dto) {
        if (!gameRepository.existsById(dto.getId())) {
            return true;
        }
        if (!"OFF".equals(dto.getGameState())) {
            return true;
        }

        LocalDateTime nDaysAgo = LocalDateTime.now().minusDays(3);
        return dto.getStartTimeUTC().isAfter(nDaysAgo);
    }


    public void updateGameWithMissingStats(Season season) {
        List<Game> games = gameRepository.findFinishedGamesWithoutStats(season);
        if (games.isEmpty()) {
            log.info("Při aktualizaci nebyli nalezeny žádné zýpasy bez statistik");
            return;
        }
        log.info("Doimportování statistik pro {} zápasů bez statistik", games.size());
        for (Game game : games) {
            try {
                playerGameStatsImportService.importPlayerGameStats(game);
            } catch (Exception e) {
                log.error("Chyba při doimportování statistik pro zápas {}", game.getId(), e);
            }
        }
    }


    private boolean updateOrCreateGame(GameDto dto, Season season) {
        Game game;
        boolean isNewGame = false;

        if (gameRepository.existsById(dto.getId())) {
            game = gameRepository.findById(dto.getId()).orElseThrow(() -> new EntityNotFoundException("Game not found: " + dto.getId()));

            String oldState = game.getGameState();
            String newState = dto.getGameState();

            if (!oldState.equals(newState)) {
                log.info("Zápas {} změnil stav: {} → {}", dto.getId(), oldState, newState);
            }
        } else {
            game = new Game();
            isNewGame = true;
            log.info("Nový zápas: {} ({} @ {}, {})", dto.getId(), dto.getAwayTeam().getId(), dto.getHomeTeam().getId(), dto.getStartTimeUTC());
        }

        game.setId(dto.getId());
        game.setSeason(season);
        game.setGameType(dto.getGameType());
        game.setStartTimeUtc(dto.getStartTimeUTC());
        game.setGameState(dto.getGameState());
        game.setNeutralSite(dto.isNeutralSite());
        game.setVenueName(dto.getVenue() != null ? dto.getVenue().getName() : null);

        game.setHomeTeam(teamRepository.findById(dto.getHomeTeam().getId())
                .orElseThrow(() -> new EntityNotFoundException("Home team " + dto.getHomeTeam().getId() + " not found")));
        game.setVisitingTeam(teamRepository.findById(dto.getAwayTeam().getId())
                .orElseThrow(() -> new EntityNotFoundException("Away team " + dto.getAwayTeam().getId() + " not found")));

        if ("OFF".equals(dto.getGameState())) {
            game.setGameOutcome(dto.getGameOutcome() != null ? dto.getGameOutcome().getLastPeriodType() : null);
            game.setHomeScore(dto.getHomeTeam().getScore());
            game.setVisitingScore(dto.getAwayTeam().getScore());
            gameRepository.save(game);
            try {
                playerGameStatsImportService.importPlayerGameStats(game);
            } catch (Exception e) {
                log.error("Chyba při importu statistik hráčů pro zápas {}", game.getId(), e);
            }

        } else {
            gameRepository.save(game);
        }
        return isNewGame;
    }

}
