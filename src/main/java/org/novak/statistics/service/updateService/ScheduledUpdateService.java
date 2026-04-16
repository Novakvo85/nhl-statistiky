package org.novak.statistics.service.updateService;

import org.novak.statistics.entity.Season;
import org.novak.statistics.service.AppSettingService;
import org.novak.statistics.service.ImportLockService;
import org.novak.statistics.service.SeasonService;
import org.novak.statistics.service.importService.TeamImportService;
import org.novak.statistics.service.importService.TeamSeasonImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduledUpdateService {

    private static final Logger log = LoggerFactory.getLogger(ScheduledUpdateService.class);
    private final AppSettingService appSettingService;
    private final SeasonService seasonService;
    private final TeamSeasonImportService teamSeasonImportService;
    private final GameUpdateService gameUpdateService;
    private final PlayerUpdateService playerUpdateService;
    private final ImportLockService importLockService;
    private final TeamImportService teamImportService;

    public ScheduledUpdateService(AppSettingService appSettingService, SeasonService seasonService, TeamSeasonImportService teamSeasonImportService, GameUpdateService gameUpdateService, PlayerUpdateService playerUpdateService, ImportLockService importLockService, TeamImportService teamImportService) {
        this.appSettingService = appSettingService;
        this.seasonService = seasonService;
        this.teamSeasonImportService = teamSeasonImportService;
        this.gameUpdateService = gameUpdateService;
        this.playerUpdateService = playerUpdateService;
        this.importLockService = importLockService;
        this.teamImportService = teamImportService;
    }

    @CacheEvict(value = {"playerStats", "goalieStats"}, allEntries = true)
    @Scheduled(cron = "0 0 3 1 9 *", zone = "Europe/Prague")
    public void switchSeason() {
        try {
            appSettingService.setCurrentSeason();
            teamImportService.importTeams();
            teamSeasonImportService.importNhlTeamSeason(seasonService.getById(appSettingService.getCurrentSeason().getValue()));
            playerUpdateService.updateAllActivePlayers();
            log.info("Aktuální sezóna automaticky změněna na novou");
        } catch (Exception e) {
            log.error("Chyba při automatickém přepnutí sezóny", e);
        }
    }

    @CacheEvict(value = {"playerStats", "goalieStats"}, allEntries = true)
    @Scheduled(cron = "0 0 8 * * *", zone = "Europe/Prague")
    public void dailyDataUpdate() {
        if (importLockService.isLocked()) {
            log.warn("Nelze spustit denní aktualizaci - již probíhá import nebo aktualizace.");
            return;
        }
        importLockService.lock();

        try {
            Season currentSeason = seasonService.getById(appSettingService.getCurrentSeason().getValue());
            gameUpdateService.updateRecentGames(currentSeason, 3);
            gameUpdateService.updateGameWithMissingStats(currentSeason);
        } catch (Exception e) {
            log.error("Chyba při denní aktualizaci", e);
        } finally {
            importLockService.unlock();
        }
    }

    @CacheEvict(value = {"playerStats", "goalieStats"}, allEntries = true)
    @Scheduled(cron = "0 30 8 * * *", zone = "Europe/Prague")
    public void dailyPlayerUpdate() {
        if (importLockService.isLocked()) {
            log.warn("Nelze spustit aktualizaci hráčů - již probíhá import nebo aktualizace.");
            return;
        }
        importLockService.lock();

        try {
            playerUpdateService.updateRecentlyActivePlayers(30);
        } catch (Exception e) {
            log.error("Chyba při aktualizaci hráčů", e);
        } finally {
            importLockService.unlock();
        }
    }

}
