package org.novak.statistics.dto.response;

import org.novak.statistics.dto.TeamDto;

import java.util.List;

public class TeamApiResponse {

    private List<TeamDto> data;

    public List<TeamDto> getData() {
        return data;
    }

    public void setData(List<TeamDto> data) {
        this.data = data;
    }
}
