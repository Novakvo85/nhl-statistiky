package org.novak.statistics.service;

import org.novak.statistics.entity.AppSetting;
import org.novak.statistics.entity.Season;
import org.novak.statistics.exception.EntityNotFoundException;
import org.novak.statistics.repository.AppSettingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class AppSettingService {

    private static final Logger log = LoggerFactory.getLogger(AppSettingService.class);
    private final AppSettingRepository appSettingRepository;
    private final SeasonService seasonService;

    public AppSettingService(AppSettingRepository appSettingRepository, SeasonService seasonService) {
        this.appSettingRepository = appSettingRepository;
        this.seasonService = seasonService;
    }

    public void setCurrentSeason(){
        LocalDate now = LocalDate.now();

        long seasonId;
        int startYear, endYear;

        if (now.getMonthValue() >= 9) {
            startYear = now.getYear();
            endYear = now.getYear() + 1;
        } else {
            startYear = now.getYear() - 1;
            endYear = now.getYear();
        }
        seasonId = startYear * 10000L + endYear;

        Season season = seasonService.createSeason(seasonId, startYear, endYear, "NHL");
        appSettingRepository.save(new AppSetting(1,"currentSeason", season.getId()));
    }

    public AppSetting getCurrentSeason(){
        return appSettingRepository.findById(1L).orElseThrow(() -> {
            log.error("Aktuální sezóna nenalezena");
            return new EntityNotFoundException("Aktuální sezóna nenalezena");
        });
    }
}
