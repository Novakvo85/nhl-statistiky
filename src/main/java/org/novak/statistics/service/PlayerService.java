package org.novak.statistics.service;

import org.novak.statistics.entity.Player;
import org.novak.statistics.exception.EntityNotFoundException;
import org.novak.statistics.repository.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerService {

    private static final Logger log = LoggerFactory.getLogger(PlayerService.class);

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }
    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }


    public List<Player> findPlayersByName(String query) {
        return playerRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(query, query);
    }

    public Player getPlayerById(Long id) {
        return playerRepository.findById(id).orElseThrow(() -> {
            log.error("Hráč {} nenalezen", id);
            return new EntityNotFoundException("Hráč " + id + " nenalezen");
        });
    }
}
