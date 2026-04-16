package org.novak.statistics.entity;

import jakarta.persistence.*;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"playerId", "gameId"})
})
public class PlayerGameStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "gameId", nullable = false)
    private Game game;

    @ManyToOne
    @JoinColumn(name = "playerId", nullable = false)
    private Player player;

    @ManyToOne
    @JoinColumn(name = "teamId", nullable = false)
    private Team team;

    private boolean isGoalie;

    private int goals;
    private int assists;
    private int points;
    private int plusMinus;

    private int powerPlayGoals;

    private float faceoffWinningPctg;
    private int blockedShots;
    private int hits;
    private int shots;
    private int shifts;
    private int pim;
    @Column(length = 10)
    private String toi;

    private int shotsAgainst;
    private int goalsAgainst;

    private float savePctg;

    public PlayerGameStats() {
    }

    public PlayerGameStats(Long id, Game game, Player player, Team team, boolean isGoalie, int goals, int assists, int points, int plusMinus, int powerPlayGoals, float faceoffWinningPctg, int blockedShots, int hits, int shots, int shifts, int pim, String toi, int shotsAgainst, int goalsAgainst, float savePctg) {
        this.id = id;
        this.game = game;
        this.player = player;
        this.team = team;
        this.isGoalie = isGoalie;
        this.goals = goals;
        this.assists = assists;
        this.points = points;
        this.plusMinus = plusMinus;
        this.powerPlayGoals = powerPlayGoals;
        this.faceoffWinningPctg = faceoffWinningPctg;
        this.blockedShots = blockedShots;
        this.hits = hits;
        this.shots = shots;
        this.shifts = shifts;
        this.pim = pim;
        this.toi = toi;
        this.shotsAgainst = shotsAgainst;
        this.goalsAgainst = goalsAgainst;
        this.savePctg = savePctg;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
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

    public boolean isGoalie() {
        return isGoalie;
    }

    public void setGoalie(boolean goalie) {
        isGoalie = goalie;
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
