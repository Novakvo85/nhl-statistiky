package org.novak.statistics.entity;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class Season {

    @Id
    private Long id;

    @Column(nullable = false)
    private int startYear;

    @Column(nullable = false)
    private int endYear;

    public Season() {
    }

    public Season(Long id, int startYear, int endYear) {
        this.id = id;
        this.startYear = startYear;
        this.endYear = endYear;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getStartYear() {
        return startYear;
    }

    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }

    public int getEndYear() {
        return endYear;
    }

    public void setEndYear(int endYear) {
        this.endYear = endYear;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Season season)) return false;
        return Objects.equals(id, season.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}


