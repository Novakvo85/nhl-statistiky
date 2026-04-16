package org.novak.statistics.service.importService;

import org.novak.statistics.dto.GameDto;
import org.novak.statistics.entity.Game;
import org.novak.statistics.entity.Season;
import org.novak.statistics.exception.EntityNotFoundException;
import org.novak.statistics.repository.GameRepository;
import org.novak.statistics.repository.TeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SpecificGameImportService {

    private static final Logger log = LoggerFactory.getLogger(SpecificGameImportService.class);
    private final GameRepository gameRepository;
    private final TeamRepository teamRepository;
    private final PlayerGameStatsImportService playerGameStatsImportService;

    public SpecificGameImportService(GameRepository gameRepository, TeamRepository teamRepository, PlayerGameStatsImportService playerGameStatsImportService) {
        this.gameRepository = gameRepository;
        this.teamRepository = teamRepository;
        this.playerGameStatsImportService = playerGameStatsImportService;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveGame( GameDto dto, Season season){
        Game game = new Game();
        game.setId(dto.getId());
        game.setSeason(season);
        game.setGameType(dto.getGameType());
        game.setStartTimeUtc(dto.getStartTimeUTC());
        game.setGameState(dto.getGameState());
        game.setNeutralSite(dto.isNeutralSite());
        game.setVenueName(dto.getVenue() != null ? dto.getVenue().getName() : null);
        game.setHomeTeam(teamRepository.findById(dto.getHomeTeam().getId()).orElseThrow(() -> new EntityNotFoundException("Home team " + dto.getHomeTeam().getId() + " not found")));
        game.setVisitingTeam(teamRepository.findById(dto.getAwayTeam().getId()).orElseThrow(() -> new EntityNotFoundException("Away team " + dto.getAwayTeam().getId() + " not found")));
        if("OFF".equals(dto.getGameState())) {
            game.setGameOutcome(dto.getGameOutcome() != null ? dto.getGameOutcome().getLastPeriodType() : null);
            game.setHomeScore(dto.getHomeTeam().getScore());
            game.setVisitingScore(dto.getAwayTeam().getScore());
            gameRepository.save(game);

            try {
                playerGameStatsImportService.importPlayerGameStats(game);
            } catch (Exception e) {
                log.error("Chyba při importu statistik hráčů pro zápas {}", game.getId(), e);
            }
        }
        else {
            gameRepository.save(game);
        }


    }
}
