package org.novak.statistics.repository;

import org.novak.statistics.entity.Competition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompetitionRepository extends JpaRepository<Competition, Long> {
    Competition findByName(String name);
}
