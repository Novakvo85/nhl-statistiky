package org.novak.statistics.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerDto {

    private Long playerId;
    @JsonProperty("isActive")
    private boolean isActive;
    private Long currentTeamId;
    private LocalizedString firstName;
    private LocalizedString lastName;
    private Integer sweaterNumber;
    private String position;
    private String shootsCatches;
    private Integer heightInCentimeters;
    private Integer weightInKilograms;
    private LocalDate birthDate;
    private LocalizedString birthCity;
    private String birthCountry;

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Long getCurrentTeamId() {
        return currentTeamId;
    }

    public void setCurrentTeamId(Long currentTeamId) {
        this.currentTeamId = currentTeamId;
    }

    public LocalizedString getFirstName() {
        return firstName;
    }

    public void setFirstName(LocalizedString firstName) {
        this.firstName = firstName;
    }

    public LocalizedString getLastName() {
        return lastName;
    }

    public void setLastName(LocalizedString lastName) {
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

    public Integer getHeightInCentimeters() {
        return heightInCentimeters;
    }

    public void setHeightInCentimeters(Integer heightInCentimeters) {
        this.heightInCentimeters = heightInCentimeters;
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

    public LocalizedString getBirthCity() {
        return birthCity;
    }

    public void setBirthCity(LocalizedString birthCity) {
        this.birthCity = birthCity;
    }

    public String getBirthCountry() {
        return birthCountry;
    }

    public void setBirthCountry(String birthCountry) {
        this.birthCountry = birthCountry;
    }
}
