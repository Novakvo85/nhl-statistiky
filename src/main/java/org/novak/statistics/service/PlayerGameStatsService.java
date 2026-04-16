package org.novak.statistics.service;

import org.novak.statistics.entity.*;
import org.novak.statistics.repository.PlayerGameStatsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerGameStatsService {

    private final PlayerGameStatsRepository playerGameStatsRepository;

    public PlayerGameStatsService(PlayerGameStatsRepository playerGameStatsRepository) {
        this.playerGameStatsRepository = playerGameStatsRepository;
    }

    public List<PlayerGameStats> getPlayerGameStatsByGameAndTeam(Game game, Team team) {
        return playerGameStatsRepository.findByGameAndTeam(game, team);
    }

    public List<PlayerGameStats> getPlayerGameStatsByPlayer(Player player) {
        return playerGameStatsRepository.findByPlayer(player);
    }


}
