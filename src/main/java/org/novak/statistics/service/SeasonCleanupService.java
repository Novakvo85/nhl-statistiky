package org.novak.statistics.service;

import org.springframework.transaction.annotation.Transactional;
import org.novak.statistics.entity.Season;
import org.novak.statistics.entity.SeasonImportStatus;
import org.novak.statistics.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeasonCleanupService {

    private static final Logger log = LoggerFactory.getLogger(SeasonCleanupService.class);
    private final PlayerGameStatsRepository playerGameStatsRepository;
    private final RosterRepository rosterRepository;
    private final GameRepository gameRepository;
    private final TeamSeasonRepository teamSeasonRepository;
    private final SeasonImportStatusRepository seasonImportStatusRepository;

    public SeasonCleanupService(PlayerGameStatsRepository playerGameStatsRepository, RosterRepository rosterRepository, GameRepository gameRepository, TeamSeasonRepository teamSeasonRepository, SeasonImportStatusRepository seasonImportStatusRepository) {
        this.playerGameStatsRepository = playerGameStatsRepository;
        this.rosterRepository = rosterRepository;
        this.gameRepository = gameRepository;
        this.teamSeasonRepository = teamSeasonRepository;
        this.seasonImportStatusRepository = seasonImportStatusRepository;
    }

    @Transactional
    public void deleteUnimportedSeasons(){
        List<SeasonImportStatus> unimportedSeasonStatuses = seasonImportStatusRepository.findByImported(false);

        for (SeasonImportStatus seasonImportStatus : unimportedSeasonStatuses) {
            deleteSeasonData(seasonImportStatus.getSeason());
        }
    }

    public void deleteSeasonData(Season season){
        long seasonId = season.getId();

        int deletedPGS = playerGameStatsRepository.deleteByGame_Season_Id(seasonId);
        int deletedRosters = rosterRepository.deleteByTeamSeason_Season_Id(seasonId);
        int deletedGames = gameRepository.deleteBySeasonId(seasonId);
        int deletedTS = teamSeasonRepository.deleteBySeasonId(seasonId);

        log.info("Smazáno {} záznamů statistik hráčů ze zápasu ze sezóny {}", deletedPGS, seasonId);
        log.info("Smazáno {} záznamů členů soupisek ze sezóny {}", deletedRosters, seasonId);
        log.info("Smazáno {} záznamů zápasů ze sezóny {}", deletedGames, seasonId);
        log.info("Smazáno {} záznamů týmových sezón ze sezóny {}", deletedTS, seasonId);
    }

}
