package org.novak.statistics.service.importService;

import org.novak.statistics.dto.response.TeamApiResponse;
import org.novak.statistics.dto.TeamDto;
import org.novak.statistics.entity.Competition;
import org.novak.statistics.entity.Team;
import org.novak.statistics.exception.EntityNotFoundException;
import org.novak.statistics.repository.CompetitionRepository;
import org.novak.statistics.repository.TeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class TeamImportService {

    private static final Logger log = LoggerFactory.getLogger(TeamImportService.class);
    private final TeamRepository teamRepository;
    private final CompetitionRepository competitionRepository;
    private final RestTemplate restTemplate;

    public TeamImportService(TeamRepository teamRepository, CompetitionRepository competitionRepository, RestTemplate restTemplate) {
        this.teamRepository = teamRepository;
        this.competitionRepository = competitionRepository;
        this.restTemplate = restTemplate;
    }


    public void importTeams(){
        String url = "https://api.nhle.com/stats/rest/en/team";

        try {
            TeamApiResponse response = restTemplate.getForObject(url, TeamApiResponse.class);

            if (response.getData() == null) {
                log.warn("Žádná data z NHL API pro týmy");
                return;
            }

            Competition competition = competitionRepository.findById(1L)
                    .orElseThrow(() -> new EntityNotFoundException("Soutěž nenalezena"));

            for(TeamDto dto : response.getData()){
                if (!teamRepository.existsById(dto.getId())) {
                    Team team = new Team();
                    team.setId(dto.getId());
                    team.setName(dto.getFullName());
                    team.setAbbreviation(dto.getTriCode());
                    team.setCompetition(competition);
                    teamRepository.save(team);
                }
            }
        } catch (Exception e) {
            log.error("Chyba při importu týmů", e);
            throw new RuntimeException("Chyba při importu týmů", e);
        }
    }

    public void ensureTeamsComplete(List<Long> teamIds) {
        boolean anyMissing = teamIds.stream().anyMatch(id -> !teamRepository.existsById(id));
        if (anyMissing) {
            importTeams();
        }
    }
}
