package org.novak.statistics.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class SeasonImportStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "seasonId", nullable = false)
    private Season season;

    @ManyToOne
    @JoinColumn(name = "competitionId", nullable = false)
    private Competition competition;

    private boolean imported;
    private LocalDateTime lastImportedAt;

    public SeasonImportStatus() {
    }

    public SeasonImportStatus(Long id, Season season, Competition competition, boolean imported, LocalDateTime lastImportedAt) {
        this.id = id;
        this.season = season;
        this.competition = competition;
        this.imported = imported;
        this.lastImportedAt = lastImportedAt;
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

    public Competition getCompetition() {
        return competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    public boolean isImported() {
        return imported;
    }

    public void setImported(boolean imported) {
        this.imported = imported;
    }

    public LocalDateTime getLastImportedAt() {
        return lastImportedAt;
    }

    public void setLastImportedAt(LocalDateTime lastImportedAt) {
        this.lastImportedAt = lastImportedAt;
    }
}
