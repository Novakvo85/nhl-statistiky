package org.novak.statistics.dto.response;

import org.novak.statistics.dto.GameDto;

import java.util.List;

public class GameApiResponse {

    private List<GameDto> games;

    public List<GameDto> getGames() {
        return games;
    }

    public void setGames(List<GameDto> games) {
        this.games = games;
    }
}
