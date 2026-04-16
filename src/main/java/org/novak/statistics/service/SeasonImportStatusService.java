package org.novak.statistics.service;

import org.novak.statistics.entity.Competition;
import org.novak.statistics.entity.Season;
import org.novak.statistics.entity.SeasonImportStatus;
import org.novak.statistics.repository.SeasonImportStatusRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SeasonImportStatusService {

    private final SeasonImportStatusRepository seasonImportStatusRepository;
    private final CompetitionService competitionService;

    public SeasonImportStatusService(SeasonImportStatusRepository seasonImportStatusRepository, CompetitionService competitionService) {
        this.seasonImportStatusRepository = seasonImportStatusRepository;
        this.competitionService = competitionService;
    }

    public void createUnimportedSeasonStatus(Season season, long competitionId){
        Competition competition = competitionService.getById(competitionId);
        SeasonImportStatus status = new SeasonImportStatus();
        status.setSeason(season);
        status.setCompetition(competition);
        status.setImported(false);
        seasonImportStatusRepository.save(status);
    }

    public void markSeasonStatusAsImported(Season season, long competitionId){
        Competition competition = competitionService.getById(competitionId);
        SeasonImportStatus status = seasonImportStatusRepository.findBySeasonAndCompetition(season, competition);
        status.setImported(true);
        status.setLastImportedAt(LocalDateTime.now());
        seasonImportStatusRepository.save(status);
    }

    public void markSeasonStatusAsImported(SeasonImportStatus status){
        status.setImported(true);
        status.setLastImportedAt(LocalDateTime.now());
        seasonImportStatusRepository.save(status);
    }

    public List<SeasonImportStatus> getAllByCompetitionOrderBySeasonStartYearDesc(Competition competition){
        return seasonImportStatusRepository.findAllByCompetitionOrderBySeasonStartYearDesc(competition);
    }

    public SeasonImportStatus getBySeason(Season season){
        return seasonImportStatusRepository.findBySeason(season);
    }

    public boolean existsBySeasonAndCompetition(Season season, Competition competition){
        return seasonImportStatusRepository.existsBySeasonAndCompetition(season, competition);
    }

    public List<Season> getAllImportedSeasons(){
        return getAllByCompetitionOrderBySeasonStartYearDesc(competitionService.getById(1L))
                .stream()
                .filter(SeasonImportStatus::isImported)
                .map(SeasonImportStatus::getSeason)
                .toList();
    }


}
