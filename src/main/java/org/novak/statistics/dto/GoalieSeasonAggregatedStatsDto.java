package org.novak.statistics.dto;

import org.novak.statistics.entity.Player;
import org.novak.statistics.entity.Team;

public class GoalieSeasonAggregatedStatsDto {

    private Player player;
    private Team team;
    private int gamesPlayed;
    private int shotsAgainst;
    private int goalsAgainst;
    private float savePctg;
    private float gaa;

    public GoalieSeasonAggregatedStatsDto() {
    }

    public GoalieSeasonAggregatedStatsDto(Player player, Team team, int gamesPlayed, int shotsAgainst, int goalsAgainst, float savePctg, float gaa) {
        this.player = player;
        this.team = team;
        this.gamesPlayed = gamesPlayed;
        this.shotsAgainst = shotsAgainst;
        this.goalsAgainst = goalsAgainst;
        this.savePctg = savePctg;
        this.gaa = gaa;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public int getShotsAgainst() {
        return shotsAgainst;
    }

    public void setShotsAgainst(int shotsAgainst) {
        this.shotsAgainst = shotsAgainst;
    }

    public int getGoalsAgainst() {
        return goalsAgainst;
    }

    public void setGoalsAgainst(int goalsAgainst) {
        this.goalsAgainst = goalsAgainst;
    }

    public float getSavePctg() {
        return savePctg;
    }

    public void setSavePctg(float savePctg) {
        this.savePctg = savePctg;
    }

    public float getGaa() {
        return gaa;
    }

    public void setGaa(float gaa) {
        this.gaa = gaa;
    }
}
