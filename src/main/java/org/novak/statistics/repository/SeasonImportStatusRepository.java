package org.novak.statistics.repository;

import org.novak.statistics.entity.Competition;
import org.novak.statistics.entity.Season;
import org.novak.statistics.entity.SeasonImportStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeasonImportStatusRepository extends JpaRepository<SeasonImportStatus, Long> {
    SeasonImportStatus findBySeasonAndCompetition(Season season, Competition competition);
    SeasonImportStatus findBySeason(Season season);
    List<SeasonImportStatus> findAllByCompetitionOrderBySeasonStartYearDesc(Competition competition);
    boolean existsBySeasonAndCompetition(Season season, Competition competition);
    List<SeasonImportStatus> findByImported(boolean isImported);
}
