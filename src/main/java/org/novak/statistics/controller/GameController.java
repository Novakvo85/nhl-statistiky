package org.novak.statistics.controller;

import org.novak.statistics.entity.Game;
import org.novak.statistics.entity.PlayerGameStats;
import org.novak.statistics.entity.Team;
import org.novak.statistics.entity.User;
import org.novak.statistics.service.GameService;
import org.novak.statistics.service.PlayerGameStatsService;
import org.novak.statistics.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.format.annotation.DateTimeFormat;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Controller
public class GameController {

    private final GameService gameService;
    private final UserService userService;
    private final PlayerGameStatsService playerGameStatsService;

    public GameController(GameService gameService, UserService userService,PlayerGameStatsService playerGameStatsService) {
        this.gameService = gameService;
        this.userService = userService;
        this.playerGameStatsService = playerGameStatsService;
    }

    @GetMapping("/games")
    public String showGames(@RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, Model model) {

        if (date == null) {
            date = LocalDate.now();
        }

        List<Game> games = gameService.getGamesByDate(date);

        model.addAttribute("games", games);
        model.addAttribute("selectedDate", date);
        model.addAttribute("previousDate", date.minusDays(1));
        model.addAttribute("nextDate", date.plusDays(1));
        return "games";
    }

    @GetMapping("/games/{id}")
    public String showGameDetail(@PathVariable long id, Model model) {
        Game game = gameService.getGameById(id);

        List<PlayerGameStats> homeTeamStats = playerGameStatsService.getPlayerGameStatsByGameAndTeam(game, game.getHomeTeam());
        List<PlayerGameStats> visitingTeamStats = playerGameStatsService.getPlayerGameStatsByGameAndTeam(game, game.getVisitingTeam());

        List<PlayerGameStats> homeGoalies = homeTeamStats.stream().filter(PlayerGameStats::isGoalie).toList();
        List<PlayerGameStats> homeDefencemen = homeTeamStats.stream().filter(s -> "D".equals(s.getPlayer().getPosition())).toList();
        List<PlayerGameStats> homeForwards = homeTeamStats.stream().filter(s -> "R".equals(s.getPlayer().getPosition()) || "L".equals(s.getPlayer().getPosition()) || "C".equals(s.getPlayer().getPosition())).toList();

        List<PlayerGameStats> visitingGoalies = visitingTeamStats.stream().filter(PlayerGameStats::isGoalie).toList();
        List<PlayerGameStats> visitingDefencemen = visitingTeamStats.stream().filter(s -> "D".equals(s.getPlayer().getPosition())).toList();
        List<PlayerGameStats> visitingForwards = visitingTeamStats.stream().filter(s -> "R".equals(s.getPlayer().getPosition()) || "L".equals(s.getPlayer().getPosition()) || "C".equals(s.getPlayer().getPosition())).toList();

        model.addAttribute("game", game);
        model.addAttribute("homeGoalies", homeGoalies);
        model.addAttribute("homeDefencemen", homeDefencemen);
        model.addAttribute("homeForwards", homeForwards);
        model.addAttribute("visitingGoalies", visitingGoalies);
        model.addAttribute("visitingDefencemen", visitingDefencemen);
        model.addAttribute("visitingForwards", visitingForwards);
        return "game-detail";
    }

    @GetMapping("/my-games")
    public String showMyGames(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }
        User user = userService.getByUsername(principal.getName());
        Set<Team> favoriteTeams = user.getFavoriteTeams();

        List<Game> upcomingGames = gameService.getUpcomingGamesForTeams(favoriteTeams, 10);
        model.addAttribute("upcomingGames", upcomingGames);
        return "my-games";
    }


}