package org.novak.statistics.service;

import org.novak.statistics.entity.Competition;
import org.novak.statistics.entity.Season;
import org.novak.statistics.exception.EntityNotFoundException;
import org.novak.statistics.repository.SeasonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeasonService {

    private static final Logger log = LoggerFactory.getLogger(SeasonService.class);

    private final SeasonRepository seasonRepository;
    private final SeasonImportStatusService seasonImportStatusService;
    private final CompetitionService competitionService;

    public SeasonService(SeasonRepository seasonRepository, SeasonImportStatusService seasonImportStatusService, CompetitionService competitionService) {
        this.seasonRepository = seasonRepository;
        this.seasonImportStatusService = seasonImportStatusService;
        this.competitionService = competitionService;
    }


    public List<Season> getAllByOrderByStartYearDesc(){
        return seasonRepository.findAllByOrderByStartYearDesc();
    }

    public Season getById(Long id){
        return seasonRepository.findById(id).orElseThrow(() -> {
            log.error("Sezóna {} nenalezena", id);
            return new EntityNotFoundException("Sezóna " + id + " nenalezena");
        });
    }

    public Season createSeason(Long seasonId, int seasonStart, int seasonEnd, String competitionName){
        Competition competition = competitionService.getByName(competitionName);
        Season season;
        if (existsById(seasonId)){
            season = getById(seasonId);
            if (!seasonImportStatusService.existsBySeasonAndCompetition(season, competition)){
                seasonImportStatusService.createUnimportedSeasonStatus(season, competition.getId());
            }
        }
        else {
            season = new Season(seasonId, seasonStart, seasonEnd);
            seasonRepository.save(season);
            seasonImportStatusService.createUnimportedSeasonStatus(season, competition.getId());
        }
        return season;
    }

    public void createSeason(Season season, Competition competition){
        if (existsById(season.getId())){
            season = getById(season.getId());
            if (!seasonImportStatusService.existsBySeasonAndCompetition(season, competition)){
                seasonImportStatusService.createUnimportedSeasonStatus(season, competition.getId());
            }
        }
        else {
            season = new Season(season.getId(), season.getStartYear(), season.getEndYear());
            seasonRepository.save(season);
            seasonImportStatusService.createUnimportedSeasonStatus(season, competition.getId());
        }
    }

    public boolean existsById(Long id){
        return seasonRepository.existsById(id);
    }
}
