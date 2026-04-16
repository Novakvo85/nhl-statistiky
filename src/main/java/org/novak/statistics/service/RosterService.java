package org.novak.statistics.service;


import org.novak.statistics.entity.Player;
import org.novak.statistics.entity.Roster;
import org.novak.statistics.entity.TeamSeason;
import org.novak.statistics.repository.RosterRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RosterService {

    private final RosterRepository rosterRepository;

    public RosterService(RosterRepository rosterRepository) {
        this.rosterRepository = rosterRepository;
    }

    public List<Roster> getRostersByPlayer(Player player) {
        return rosterRepository.findByPlayer(player);
    }
    public List<Roster> getRostersByTeamSeason(TeamSeason teamSeason) {
        return rosterRepository.findByTeamSeason(teamSeason);
    }
}
