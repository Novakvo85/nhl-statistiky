package org.novak.statistics.dto;

import org.novak.statistics.entity.Player;
import org.novak.statistics.entity.Team;


public class PlayerSeasonAggregatedStatsDto {

    private Player player;
    private Team team;
    private int gamesPlayed;
    private int goals;
    private int assists;
    private int points;

    public PlayerSeasonAggregatedStatsDto() {
    }

    public PlayerSeasonAggregatedStatsDto(Player player, Team team, int gamesPlayed, int goals, int assists, int points) {
        this.player = player;
        this.team = team;
        this.gamesPlayed = gamesPlayed;
        this.goals = goals;
        this.assists = assists;
        this.points = points;
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

    public int getGoals() {
        return goals;
    }

    public void setGoals(int goals) {
        this.goals = goals;
    }

    public int getAssists() {
        return assists;
    }

    public void setAssists(int assists) {
        this.assists = assists;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
