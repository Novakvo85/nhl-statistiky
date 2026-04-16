package org.novak.statistics.service.updateService;

import org.novak.statistics.entity.Season;
import org.novak.statistics.service.AppSettingService;
import org.novak.statistics.service.ImportLockService;
import org.novak.statistics.service.SeasonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class SeasonUpdateService {

    private static final Logger log = LoggerFactory.getLogger(SeasonUpdateService.class);
    private final GameUpdateService gameUpdateService;
    private final AppSettingService appSettingService;
    private final SeasonService seasonService;
    private final ImportLockService importLockService;

    public SeasonUpdateService(GameUpdateService gameUpdateService, AppSettingService appSettingService, SeasonService seasonService, ImportLockService importLockService) {
        this.gameUpdateService = gameUpdateService;
        this.appSettingService = appSettingService;
        this.seasonService = seasonService;
        this.importLockService = importLockService;
    }


    @Async
    public void manualUpdate(int daysBack) {
        if (importLockService.isLocked()) {
            log.warn("Nelze spustit manualUpdate - již probíhá import nebo aktualizace.");
            return;
        }
        importLockService.lock();
        try {
            Season currentSeason = seasonService.getById(appSettingService.getCurrentSeason().getValue());
            gameUpdateService.updateRecentGames(currentSeason, daysBack);
        } finally {
            importLockService.unlock();
        }
    }

    @Async
    public void updateEntireSeason() {
        if (importLockService.isLocked()) {
            log.warn("Nelze spustit updateEntireSeason - již probíhá import nebo aktualizace.");
            return;
        }
        importLockService.lock();
        try {
            Season currentSeason = seasonService.getById(appSettingService.getCurrentSeason().getValue());
            gameUpdateService.updateAllSeasonGames(currentSeason);
        } finally {
            importLockService.unlock();
        }
    }
}
