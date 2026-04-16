package org.novak.statistics.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.novak.statistics.dto.PlayerGameStatsDto;

import java.util.List;

public class PlayerGameStatsApiResponse {


    private PlayerByGameStats playerByGameStats;


    public PlayerByGameStats getPlayerByGameStats() {
        return playerByGameStats;
    }

    public void setPlayerByGameStats(PlayerByGameStats playerByGameStats) {
        this.playerByGameStats = playerByGameStats;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PlayerByGameStats {
        private TeamPlayers awayTeam;
        private TeamPlayers homeTeam;

        public TeamPlayers getAwayTeam() {
            return awayTeam;
        }

        public void setAwayTeam(TeamPlayers awayTeam) {
            this.awayTeam = awayTeam;
        }

        public TeamPlayers getHomeTeam() {
            return homeTeam;
        }

        public void setHomeTeam(TeamPlayers homeTeam) {
            this.homeTeam = homeTeam;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TeamPlayers {
        private List<PlayerGameStatsDto> forwards;
        private List<PlayerGameStatsDto> defense;
        private List<PlayerGameStatsDto> goalies;

        public List<PlayerGameStatsDto> getForwards() {
            return forwards;
        }

        public void setForwards(List<PlayerGameStatsDto> forwards) {
            this.forwards = forwards;
        }

        public List<PlayerGameStatsDto> getDefense() {
            return defense;
        }

        public void setDefense(List<PlayerGameStatsDto> defense) {
            this.defense = defense;
        }

        public List<PlayerGameStatsDto> getGoalies() {
            return goalies;
        }

        public void setGoalies(List<PlayerGameStatsDto> goalies) {
            this.goalies = goalies;
        }
    }
}
