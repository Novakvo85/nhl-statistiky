package org.novak.statistics.dto.response;

import org.novak.statistics.dto.TeamSeasonDto;

import java.util.List;

public class TeamSeasonApiResponse {

    private List<TeamSeasonDto> teams;

    public List<TeamSeasonDto> getTeams() {
        return teams;
    }

    public void setTeams(List<TeamSeasonDto> teams) {
        this.teams = teams;
    }
}
