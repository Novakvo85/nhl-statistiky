package org.novak.statistics.service.importService;

import org.novak.statistics.dto.GameDto;
import org.novak.statistics.dto.response.GameApiResponse;
import org.novak.statistics.entity.TeamSeason;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Set;


@Service
public class GameImportService {

    private static final Logger log = LoggerFactory.getLogger(GameImportService.class);

    private final SpecificGameImportService specificGameImportService;
    private final RestTemplate restTemplate;

    public GameImportService(SpecificGameImportService specificGameImportService, RestTemplate restTemplate) {
        this.specificGameImportService = specificGameImportService;
        this.restTemplate = restTemplate;
    }


    public void importGames(TeamSeason teamSeason, Set<Long> importedGameIds) {
        String url = "https://api-web.nhle.com/v1/club-schedule-season/" + teamSeason.getTeam().getAbbreviation() + "/" + teamSeason.getSeason().getId();
        log.info("Importuji zápasy z: {}", url);
        GameApiResponse response = restTemplate.getForObject(url, GameApiResponse.class);

        if (response.getGames() == null) {
            log.warn("Žádná data pro tým {}", teamSeason.getTeam().getAbbreviation());
            return;
        }

        for(GameDto dto : response.getGames()) {
            if (importedGameIds.contains(dto.getId())){
                continue;
            }
            if(dto.getGameType() == 3 || dto.getGameType() == 2) {
                try {
                    specificGameImportService.saveGame(dto, teamSeason.getSeason());
                    importedGameIds.add(dto.getId());
                } catch (Exception e) {
                    log.error("Chyba při importu zápasu {}", dto.getId(), e);
                }
            }
        }
    }


}
