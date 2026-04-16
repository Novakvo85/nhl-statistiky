package org.novak.statistics.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerGameStatsDto {

    private Long playerId;
    private int sweaterNumber;
    private String position;
    private int goals;
    private int assists;
    private int points;
    private int plusMinus;

    private int powerPlayGoals;

    private float faceoffWinningPctg;
    private int blockedShots;
    private int hits;

    @JsonProperty("sog")
    private int shots;
    private int shifts;
    private int pim;
    private String toi;

    private int shotsAgainst;
    private int goalsAgainst;

    private float savePctg;

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public int getSweaterNumber() {
        return sweaterNumber;
    }

    public void setSweaterNumber(int sweaterNumber) {
        this.sweaterNumber = sweaterNumber;
    }
    public String getPosition() {
        return position;
    }
    public void setPosition(String position) {
        this.position = position;
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

    public int getPowerPlayGoals() {
        return powerPlayGoals;
    }

    public void setPowerPlayGoals(int powerPlayGoals) {
        this.powerPlayGoals = powerPlayGoals;
    }

    public float getFaceoffWinningPctg() {
        return faceoffWinningPctg;
    }

    public void setFaceoffWinningPctg(float faceoffWinningPctg) {
        this.faceoffWinningPctg = faceoffWinningPctg;
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

    public int getPim() {
        return pim;
    }

    public void setPim(int pim) {
        this.pim = pim;
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
}
