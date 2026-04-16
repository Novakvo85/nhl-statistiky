package org.novak.statistics.service.importService;

import org.novak.statistics.dto.PlayerDto;
import org.novak.statistics.entity.Player;
import org.novak.statistics.exception.EntityNotFoundException;
import org.novak.statistics.repository.PlayerRepository;
import org.novak.statistics.repository.TeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class PlayerImportService {

    private static final Logger log = LoggerFactory.getLogger(PlayerImportService.class);
    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;
    private final RestTemplate restTemplate;

    public PlayerImportService(PlayerRepository playerRepository, TeamRepository teamRepository, RestTemplate restTemplate) {
        this.playerRepository = playerRepository;
        this.teamRepository = teamRepository;
        this.restTemplate = restTemplate;
    }

    public void importPlayer(long id) {
        if (playerRepository.existsById(id)) {
            return;
        }

        String url = "https://api-web.nhle.com/v1/player/" + id + "/landing";
        try {
            PlayerDto dto = restTemplate.getForObject(url, PlayerDto.class);

            Player player = new Player();
            player.setId(id);
            player.setActive(dto.isActive());

            if (dto.isActive()) {
                try {
                    player.setCurrentTeam(teamRepository.findById(dto.getCurrentTeamId())
                            .orElseThrow(() -> new EntityNotFoundException("Nepodařilo se najít tým: " + dto.getCurrentTeamId())));
                } catch (Exception e) {
                    log.warn("Tým {} nenajit pro hráče {}, přeskakuji přidání aktuálního týmu", dto.getCurrentTeamId(), id);
                }
            }

            player.setFirstName(dto.getFirstName() != null ? dto.getFirstName().getValue() : null);
            player.setLastName(dto.getLastName() != null ? dto.getLastName().getValue() : null);
            player.setSweaterNumber(dto.getSweaterNumber());
            player.setPosition(dto.getPosition());
            player.setShootsCatches(dto.getShootsCatches());
            player.setHeightInCentimetres(dto.getHeightInCentimeters());
            player.setWeightInKilograms(dto.getWeightInKilograms());
            player.setBirthDate(dto.getBirthDate());
            player.setBirthCity(dto.getBirthCity() != null ? dto.getBirthCity().getValue() : null);
            player.setBirthCountry(dto.getBirthCountry());

            playerRepository.save(player);

        }catch (Exception e) {
            log.error("Chyba při importu hráče {}", id, e);
        }

    }
}
