package org.novak.statistics.entity;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class Team {

    @Id
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;
    @Column(nullable = false, length = 10)
    private String abbreviation;

    @ManyToOne
    @JoinColumn(name = "competitionId", nullable = false)
    private Competition competition;

    public Team() {
    }

    public Team(Long id, String name, String abbreviation, Competition competition) {
        this.id = id;
        this.name = name;
        this.abbreviation = abbreviation;
        this.competition = competition;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public Competition getCompetition() {
        return competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Team team)) return false;
        return Objects.equals(id, team.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
