package org.novak.statistics.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GameDto {

    private Long id;
    private int gameType;
    private LocalDateTime startTimeUTC;
    private String gameState;
    private GameOutcome gameOutcome;

    private boolean neutralSite;

    private Venue venue;

    private TeamInfo homeTeam;

    private TeamInfo awayTeam;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getStartTimeUTC() {
        return startTimeUTC;
    }

    public void setStartTimeUTC(LocalDateTime startTimeUTC) {
        this.startTimeUTC = startTimeUTC;
    }

    public String getGameState() {
        return gameState;
    }

    public void setGameState(String gameState) {
        this.gameState = gameState;
    }

    public boolean isNeutralSite() {
        return neutralSite;
    }

    public void setNeutralSite(boolean neutralSite) {
        this.neutralSite = neutralSite;
    }

    public int getGameType() {
        return gameType;
    }

    public void setGameType(int gameType) {
        this.gameType = gameType;
    }

    public GameOutcome getGameOutcome() {
        return gameOutcome;
    }

    public void setGameOutcome(GameOutcome gameOutcome) {
        this.gameOutcome = gameOutcome;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public TeamInfo getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(TeamInfo homeTeam) {
        this.homeTeam = homeTeam;
    }

    public TeamInfo getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(TeamInfo awayTeam) {
        this.awayTeam = awayTeam;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GameOutcome {
        @JsonProperty("lastPeriodType")
        private String lastPeriodType;

        public String getLastPeriodType() {
            return lastPeriodType;
        }

        public void setLastPeriodType(String lastPeriodType) {
            this.lastPeriodType = lastPeriodType;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Venue {
        @JsonProperty("default")
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TeamInfo {
        private Long id;
        private int score;
        private String abbrev;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public String getAbbrev() {
            return abbrev;
        }

        public void setAbbrev(String abbrev) {
            this.abbrev = abbrev;
        }
    }
}
