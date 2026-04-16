package org.novak.statistics.service;

import org.novak.statistics.entity.Team;
import org.novak.statistics.exception.EntityNotFoundException;
import org.novak.statistics.repository.TeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamService {

    private static final Logger log = LoggerFactory.getLogger(TeamService.class);

    private final TeamRepository teamRepository;

    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public List<Team> getAllTeams(){
        return teamRepository.findAll();
    }


    public Team getTeamById(Long id){
        return teamRepository.findById(id).orElseThrow(() -> {
            log.error("Tým {} nenalezen", id);
            return new EntityNotFoundException("Tým " + id + " nenalezen");
        });
    }

    public Boolean existsById(Long id){
        return teamRepository.existsById(id);
    }



}
