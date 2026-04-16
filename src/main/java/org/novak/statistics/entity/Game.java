package org.novak.statistics.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Game {

    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "seasonId", nullable = false)
    private Season season;

    private int gameType; // 2 = základní část, 3 = playoff

    @Column(name = "startTimeUtc")
    private LocalDateTime startTimeUtc;

    @Column(length = 10)
    private String gameState; //  "LIVE", "FINAL"
    @Column(length = 5)
    private String gameOutcome; // REG, OT, SO

    private int homeScore;

    @ManyToOne
    @JoinColumn(name = "homeTeamId", nullable = false)
    private Team homeTeam;

    private int visitingScore;

    @ManyToOne
    @JoinColumn(name = "visitingTeamId", nullable = false)
    private Team visitingTeam;

    private Boolean neutralSite;
    @Column(length = 200)
    private String venueName;


    public Game() {
    }

    public Game(Long id, Season season, int gameType, LocalDateTime startTimeUtc, String gameState, String gameOutcome, int homeScore, Team homeTeam, int visitingScore, Team visitingTeam, Boolean neutralSite, String venueName) {
        this.id = id;
        this.season = season;
        this.gameType = gameType;
        this.startTimeUtc = startTimeUtc;
        this.gameState = gameState;
        this.gameOutcome = gameOutcome;
        this.homeScore = homeScore;
        this.homeTeam = homeTeam;
        this.visitingScore = visitingScore;
        this.visitingTeam = visitingTeam;
        this.neutralSite = neutralSite;
        this.venueName = venueName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        this.season = season;
    }

    public int getGameType() {
        return gameType;
    }

    public void setGameType(int gameType) {
        this.gameType = gameType;
    }

    public LocalDateTime getStartTimeUtc() {
        return startTimeUtc;
    }

    public void setStartTimeUtc(LocalDateTime startTimeUtc) {
        this.startTimeUtc = startTimeUtc;
    }

    public String getGameState() {
        return gameState;
    }

    public void setGameState(String gameState) {
        this.gameState = gameState;
    }

    public String getGameOutcome() {
        return gameOutcome;
    }

    public void setGameOutcome(String gameOutcome) {
        this.gameOutcome = gameOutcome;
    }

    public int getHomeScore() {
        return homeScore;
    }

    public void setHomeScore(int homeScore) {
        this.homeScore = homeScore;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(Team homeTeam) {
        this.homeTeam = homeTeam;
    }

    public int getVisitingScore() {
        return visitingScore;
    }

    public void setVisitingScore(int visitingScore) {
        this.visitingScore = visitingScore;
    }

    public Team getVisitingTeam() {
        return visitingTeam;
    }

    public void setVisitingTeam(Team visitingTeam) {
        this.visitingTeam = visitingTeam;
    }

    public Boolean getNeutralSite() {
        return neutralSite;
    }

    public void setNeutralSite(Boolean neutralSite) {
        this.neutralSite = neutralSite;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Game game)) return false;
        return Objects.equals(id, game.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
