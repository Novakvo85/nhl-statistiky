package org.novak.statistics.service.importService;

import org.novak.statistics.entity.Season;
import org.novak.statistics.entity.SeasonImportStatus;
import org.novak.statistics.entity.TeamSeason;
import org.novak.statistics.service.CompetitionService;
import org.novak.statistics.service.ImportLockService;
import org.novak.statistics.service.SeasonImportStatusService;
import org.novak.statistics.service.SeasonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SeasonImportService {

    private static final Logger log = LoggerFactory.getLogger(SeasonImportService.class);

    private final SeasonService seasonService;
    private final CompetitionService competitionService;
    private final TeamSeasonImportService teamSeasonImportService;
    private final GameImportService gameImportService;
    private final SeasonImportStatusService seasonImportStatusService;
    private final ImportLockService importLockService;

    public SeasonImportService(SeasonService seasonService, CompetitionService competitionService, TeamSeasonImportService teamSeasonImportService, GameImportService gameImportService, SeasonImportStatusService seasonImportStatusService, ImportLockService importLockService) {
        this.seasonService = seasonService;
        this.competitionService = competitionService;
        this.teamSeasonImportService = teamSeasonImportService;
        this.gameImportService = gameImportService;
        this.seasonImportStatusService = seasonImportStatusService;
        this.importLockService = importLockService;
    }


    public void importSeasonData(Season season, boolean forceReimport){
        if (importLockService.isLocked()) {
            log.warn("Import již probíhá, přeskakuji.");
            return;
        }
        importLockService.lock();
        try {
            if (!seasonService.existsById(season.getId())){
                seasonService.createSeason(season, competitionService.getByName("NHL"));
            }
            SeasonImportStatus status = seasonImportStatusService.getBySeason(season);
            if(!status.isImported() || forceReimport) {
                List<TeamSeason> teamSeasons = teamSeasonImportService.importNhlTeamSeason(season);

                Set<Long> importedGameIds = new HashSet<>();

                for (TeamSeason teamSeason : teamSeasons) {
                    gameImportService.importGames(teamSeason, importedGameIds);
                }
                seasonImportStatusService.markSeasonStatusAsImported(status);
                log.info("Sezóna {}/{} {}", season.getStartYear(), season.getEndYear(), forceReimport ? "re-importována." : "importována.");
            } else {
                log.info("Sezóna {} už je importována. Přeskakuji.", season.getId());
            }
        }finally {
            importLockService.unlock();
        }
    }

    @Async
    public void importSeasonDataAsync(Season season, boolean forceReimport){
        try {
            importSeasonData(season, forceReimport);
        } catch (Exception e) {
            log.error("Chyba při importu sezóny {}", season.getId(), e);
        }
    }
}
