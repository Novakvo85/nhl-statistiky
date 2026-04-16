package org.novak.statistics.dto;

import org.novak.statistics.entity.Player;
import org.novak.statistics.entity.Roster;

public class PlayerSeasonStatsDto {
    private Player player;
    private Roster roster;

    private int gamesPlayed;
    private int goals;
    private int assists;
    private int points;
    private int plusMinus;
    private int pim;
    private int blockedShots;
    private int hits;
    private int shots;
    private int shifts;
    private String toi;

    private int shotsAgainst;
    private int goalsAgainst;

    private float savePctg;
    private float gaa; // Goals Against Average

    public PlayerSeasonStatsDto() {
    }

    public PlayerSeasonStatsDto(Player player, Roster roster, int gamesPlayed, int goals, int assists, int points, int plusMinus, int pim, int blockedShots, int hits, int shots, int shifts, String toi, int shotsAgainst, int goalsAgainst, float savePctg, float gaa) {
        this.player = player;
        this.roster = roster;
        this.gamesPlayed = gamesPlayed;
        this.goals = goals;
        this.assists = assists;
        this.points = points;
        this.plusMinus = plusMinus;
        this.pim = pim;
        this.blockedShots = blockedShots;
        this.hits = hits;
        this.shots = shots;
        this.shifts = shifts;
        this.toi = toi;
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

    public Roster getRoster() {
        return roster;
    }

    public void setRoster(Roster roster) {
        this.roster = roster;
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

    public int getPlusMinus() {
        return plusMinus;
    }

    public void setPlusMinus(int plusMinus) {
        this.plusMinus = plusMinus;
    }

    public int getPim() {
        return pim;
    }

    public void setPim(int pim) {
        this.pim = pim;
    }

    public int getBlockedShots() {
        return blockedShots;
    }

    public void setBlockedShots(int blockedShots) {
        this.blockedShots = blockedShots;
    }

    public int getHits() {
        return hits;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }

    public int getShots() {
        return shots;
    }

    public void setShots(int shots) {
        this.shots = shots;
    }

    public int getShifts() {
        return shifts;
    }

    public void setShifts(int shifts) {
        this.shifts = shifts;
    }

    public String getToi() {
        return toi;
    }

    public void setToi(String toi) {
        this.toi = toi;
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
