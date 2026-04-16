package org.novak.statistics.repository;

import org.novak.statistics.entity.Season;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeasonRepository extends JpaRepository<Season, Long> {
    List<Season> findAllByOrderByStartYearDesc();
}
