package org.novak.statistics.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TeamDto {
    private Long id;
    private String fullName;
    private String triCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getTriCode() {
        return triCode;
    }

    public void setTriCode(String triCode) {
        this.triCode = triCode;
    }
}
