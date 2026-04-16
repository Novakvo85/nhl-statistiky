package org.novak.statistics.service.updateService;

import org.novak.statistics.dto.PlayerDto;
import org.novak.statistics.entity.Player;
import org.novak.statistics.entity.PlayerGameStats;
import org.novak.statistics.exception.EntityNotFoundException;
import org.novak.statistics.repository.PlayerGameStatsRepository;
import org.novak.statistics.repository.PlayerRepository;
import org.novak.statistics.repository.TeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
public class PlayerUpdateService {

    private static final Logger log = LoggerFactory.getLogger(PlayerUpdateService.class);
    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;
    private final PlayerGameStatsRepository playerGameStatsRepository;
    private final RestTemplate restTemplate;

    public PlayerUpdateService(PlayerRepository playerRepository, TeamRepository teamRepository, PlayerGameStatsRepository playerGameStatsRepository, RestTemplate restTemplate) {
        this.playerRepository = playerRepository;
        this.teamRepository = teamRepository;
        this.playerGameStatsRepository = playerGameStatsRepository;
        this.restTemplate = restTemplate;
    }


    public void updateAllActivePlayers() {
        List<Player> activePlayers = playerRepository.findByIsActive(true);

        log.info("Aktualizuji {} aktivních hráčů...", activePlayers.size());
        int updatedCount = 0;

        for (Player player : activePlayers) {
            try {
                updatePlayer(player.getId());
                updatedCount++;
            } catch (Exception e) {
                log.error("Chyba při aktualizaci hráče {}", player.getId(), e);
            }
        }

        log.info("Aktualizováno {} hráčů", updatedCount);
    }


    public void updateRecentlyActivePlayers(int daysBack) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysBack);

        Set<Long> playerIds = new HashSet<>();

        List<PlayerGameStats> recentStats = playerGameStatsRepository.findByGameStartTimeUtcAfter(cutoffDate);
        for (var stats : recentStats) {
            playerIds.add(stats.getPlayer().getId());
        }

        log.info("Aktualizuji {} hráčů z posledních {} dní...", playerIds.size(), daysBack);
        int updatedCount = 0;
        for (Long playerId : playerIds) {
            try {
                updatePlayer(playerId);
                updatedCount++;
            } catch (Exception e) {
                log.error("Chyba při aktualizaci hráče {}", playerId, e);
            }
        }

        log.info("Aktualizováno {} hráčů", updatedCount);
    }


    public void updatePlayer(long playerId) {
        Player existingPlayer = playerRepository.findById(playerId).orElse(null);

        if (existingPlayer == null) {
            log.error("Hráč s ID {} nenalezen v databázi", playerId);
            return;
        }

        String url = "https://api-web.nhle.com/v1/player/" + playerId + "/landing";

        PlayerDto dto = restTemplate.getForObject(url, PlayerDto.class);
        existingPlayer.setActive(dto.isActive());

        if(dto.isActive()) {
            try {
                existingPlayer.setCurrentTeam(teamRepository.findById(dto.getCurrentTeamId())
                        .orElseThrow(() -> new EntityNotFoundException("Team not found: " + dto.getCurrentTeamId())));
            } catch (Exception e) {
                log.warn("Tým {} nenajit pro hráče {}, přeskakuji přidání aktuálního týmu", dto.getCurrentTeamId(), playerId);
            }
        } else {
            existingPlayer.setCurrentTeam(null);
        }

        existingPlayer.setFirstName(dto.getFirstName() != null ? dto.getFirstName().getValue() : null);
        existingPlayer.setLastName(dto.getLastName() != null ? dto.getLastName().getValue() : null);
        existingPlayer.setSweaterNumber(dto.getSweaterNumber());
        existingPlayer.setPosition(dto.getPosition());
        existingPlayer.setShootsCatches(dto.getShootsCatches());
        existingPlayer.setHeightInCentimetres(dto.getHeightInCentimeters());
        existingPlayer.setWeightInKilograms(dto.getWeightInKilograms());
        existingPlayer.setBirthDate(dto.getBirthDate());
        existingPlayer.setBirthCity(dto.getBirthCity() != null ? dto.getBirthCity().getValue() : null);
        existingPlayer.setBirthCountry(dto.getBirthCountry());

        playerRepository.save(existingPlayer);

        log.info("Aktualizován hráč: {} {} (ID: {})", existingPlayer.getFirstName(), existingPlayer.getLastName(), playerId);

    }
}
