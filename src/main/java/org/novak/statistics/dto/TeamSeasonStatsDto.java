package org.novak.statistics.dto;


import org.novak.statistics.entity.TeamSeason;

public class TeamSeasonStatsDto {

    private TeamSeason teamSeason;
    private int matches;
    private int wins;
    private int losses;
    private int overtimeLosses;
    private float pointPctg;
    private int points;
    private int goalsFor;
    private int goalsAgainst;

    public TeamSeasonStatsDto() {
    }

    public TeamSeasonStatsDto(TeamSeason teamSeason, int matches, int wins, int losses, int overtimeLosses, float pointPctg, int points, int goalsFor, int goalsAgainst) {
        this.teamSeason = teamSeason;
        this.matches = matches;
        this.wins = wins;
        this.losses = losses;
        this.overtimeLosses = overtimeLosses;
        this.pointPctg = pointPctg;
        this.points = points;
        this.goalsFor = goalsFor;
        this.goalsAgainst = goalsAgainst;
    }

    public TeamSeason getTeamSeason() {
        return teamSeason;
    }

    public void setTeamSeason(TeamSeason teamSeason) {
        this.teamSeason = teamSeason;
    }

    public int getMatches() {
        return matches;
    }

    public void setMatches(int matches) {
        this.matches = matches;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public int getOvertimeLosses() {
        return overtimeLosses;
    }

    public void setOvertimeLosses(int overtimeLosses) {
        this.overtimeLosses = overtimeLosses;
    }

    public float getPointPctg() {
        return pointPctg;
    }

    public void setPointPctg(float pointPctg) {
        this.pointPctg = pointPctg;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getGoalsFor() {
        return goalsFor;
    }

    public void setGoalsFor(int goalsFor) {
        this.goalsFor = goalsFor;
    }

    public int getGoalsAgainst() {
        return goalsAgainst;
    }

    public void setGoalsAgainst(int goalsAgainst) {
        this.goalsAgainst = goalsAgainst;
    }
}
