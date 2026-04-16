package org.novak.statistics.service;

import org.novak.statistics.entity.Competition;
import org.novak.statistics.exception.EntityNotFoundException;
import org.novak.statistics.repository.CompetitionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompetitionService {

    private final CompetitionRepository competitionRepository;

    public CompetitionService(CompetitionRepository competitionRepository) {
        this.competitionRepository = competitionRepository;
    }

    public void createInitialCompetitions(List<Competition> competitions) {
        if(competitionRepository.count() == 0){
            competitionRepository.saveAll(competitions);
        }
    }

    public Competition getByName(String name) {
        return competitionRepository.findByName(name);
    }

    public List<Competition> getAll() {
        return competitionRepository.findAll();
    }

    public Competition getById(Long id) {
        return competitionRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Soutěž " + id + " nenalezena"));
    }

}
