package org.novak.statistics.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Player {

    @Id
    private Long id;

    private boolean isActive;

    @ManyToOne
    @JoinColumn(name = "currentTeamId", nullable = true)
    private Team currentTeam;

    @Column(length = 50)
    private String firstName;

    @Column(length = 50)
    private String lastName;

    @Column(nullable = true)
    private Integer sweaterNumber;

    @Column(length = 1)
    private String position;

    @Column(length = 1)
    private String shootsCatches;

    @Column(nullable = true)
    private Integer heightInCentimetres;

    @Column(nullable = true)
    private Integer weightInKilograms;

    @Column(nullable = true)
    private LocalDate birthDate;

    @Column(nullable = true, length = 100)
    private String birthCity;

    @Column(nullable = true, length = 10)
    private String birthCountry;

    public Player() {
    }

    public Player(Long id, boolean isActive, Team currentTeam, String firstName, String lastName, Integer sweaterNumber, String position, String shootsCatches, Integer heightInCentimetres, Integer weightInKilograms, LocalDate birthDate, String birthCity, String birthCountry) {
        this.id = id;
        this.isActive = isActive;
        this.currentTeam = currentTeam;
        this.firstName = firstName;
        this.lastName = lastName;
        this.sweaterNumber = sweaterNumber;
        this.position = position;
        this.shootsCatches = shootsCatches;
        this.heightInCentimetres = heightInCentimetres;
        this.weightInKilograms = weightInKilograms;
        this.birthDate = birthDate;
        this.birthCity = birthCity;
        this.birthCountry = birthCountry;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Team getCurrentTeam() {
        return currentTeam;
    }

    public void setCurrentTeam(Team currentTeam) {
        this.currentTeam = currentTeam;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getSweaterNumber() {
        return sweaterNumber;
    }

    public void setSweaterNumber(Integer sweaterNumber) {
        this.sweaterNumber = sweaterNumber;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getShootsCatches() {
        return shootsCatches;
    }

    public void setShootsCatches(String shootsCatches) {
        this.shootsCatches = shootsCatches;
    }

    public Integer getHeightInCentimetres() {
        return heightInCentimetres;
    }

    public void setHeightInCentimetres(Integer heightInCentimetres) {
        this.heightInCentimetres = heightInCentimetres;
    }

    public Integer getWeightInKilograms() {
        return weightInKilograms;
    }

    public void setWeightInKilograms(Integer weightInKilograms) {
        this.weightInKilograms = weightInKilograms;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getBirthCity() {
        return birthCity;
    }

    public void setBirthCity(String birthCity) {
        this.birthCity = birthCity;
    }

    public String getBirthCountry() {
        return birthCountry;
    }

    public void setBirthCountry(String birthCountry) {
        this.birthCountry = birthCountry;
    }
}
