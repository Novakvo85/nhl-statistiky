package org.novak.statistics.service.importService;

import org.novak.statistics.dto.response.TeamSeasonApiResponse;
import org.novak.statistics.dto.TeamSeasonDto;
import org.novak.statistics.entity.*;
import org.novak.statistics.repository.TeamSeasonRepository;
import org.novak.statistics.service.TeamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class TeamSeasonImportService {

    private static final Logger log = LoggerFactory.getLogger(TeamSeasonImportService.class);

    private final TeamSeasonRepository teamSeasonRepository;
    private final TeamService teamService;
    private final TeamImportService teamImportService;
    private final RestTemplate restTemplate;

    public TeamSeasonImportService(TeamSeasonRepository teamSeasonRepository, TeamService teamService, TeamImportService teamImportService, RestTemplate restTemplate) {
        this.teamSeasonRepository = teamSeasonRepository;
        this.teamService = teamService;
        this.teamImportService = teamImportService;
        this.restTemplate = restTemplate;
    }


    @Transactional
    public List<TeamSeason> importNhlTeamSeason(Season season){
        List<TeamSeason> teamSeasons = new ArrayList<>();

            try {
                TeamSeasonApiResponse response = fetchTeamSeasonWithFallback(season);
                if (response == null) {
                    throw new RuntimeException("Nepodařilo se získat data pro sezónu " + season.getId());
                }

                List<Long> teamIds = response.getTeams().stream()
                        .map(TeamSeasonDto::getId)
                        .toList();
                teamImportService.ensureTeamsComplete(teamIds);

                for (TeamSeasonDto dto : response.getTeams()) {
                    log.debug("Team abbrev: {}", dto.getAbbrev());

                    Team team = teamService.getTeamById(dto.getId());
                    TeamSeason teamSeason = teamSeasonRepository.findBySeasonAndTeam(season, team);
                    if (teamSeason == null) {
                        teamSeason = new TeamSeason();
                    }
                    saveTeamSeasonFromDto(teamSeason, team, season);
                    teamSeasons.add(teamSeason);
                }
            } catch (Exception e) {
                throw new RuntimeException("Nepodařilo se získat data pro sezónu " + season.getId(), e);
            }
        return teamSeasons;
    }

    private void saveTeamSeasonFromDto(TeamSeason teamSeason, Team team, Season season) {
        teamSeason.setTeam(team);
        teamSeason.setSeason(season);
        teamSeasonRepository.save(teamSeason);

        log.info("Týmová sezóna vytvořena pro: {}{}/{}", team.getAbbreviation(), season.getStartYear(), season.getEndYear());
    }


    private TeamSeasonApiResponse fetchTeamSeasonWithFallback(Season season) {
        String[] possibleDates = {
                season.getStartYear() + "-09-01",
                season.getStartYear() + "-10-01",
                season.getEndYear() + "-01-01",
        };

        for (String date : possibleDates) {
            try {
                String url = "https://api-web.nhle.com/v1/schedule-calendar/" + date;
                log.debug("Zkouším datum: {}", url);
                TeamSeasonApiResponse response = restTemplate.getForObject(url, TeamSeasonApiResponse.class);

                if (response.getTeams() != null && !response.getTeams().isEmpty()) {
                    log.info("Úspěch! Data nalezena pro datum: {}", date);
                    return response;
                }
            } catch (Exception e) {
                log.debug("Datum {} nefungovalo, zkouším další...", date);
            }
        }

        log.warn("Žádné datum nefungovalo pro sezónu {}", season.getId());
        return null;
    }

}
