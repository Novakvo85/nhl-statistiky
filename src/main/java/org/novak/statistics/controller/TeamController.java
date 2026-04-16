package org.novak.statistics.controller;

import org.novak.statistics.dto.TeamSeasonStatsDto;
import org.novak.statistics.entity.*;
import org.novak.statistics.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;

@Controller
public class TeamController {

    private final TeamService teamService;
    private final TeamSeasonService teamSeasonService;
    private final GameService gameService;
    private final TeamSeasonStatsService teamSeasonStatsService;
    private final UserService userService;
    private final AppSettingService appSettingService;
    private final SeasonImportStatusService seasonImportStatusService;
    private final RosterService rosterService;

    public TeamController(TeamService teamService, TeamSeasonService teamSeasonService, GameService gameService, TeamSeasonStatsService teamSeasonStatsService, UserService userService, AppSettingService appSettingService, SeasonImportStatusService seasonImportStatusService, RosterService rosterService) {
        this.teamService = teamService;
        this.teamSeasonService = teamSeasonService;
        this.gameService = gameService;
        this.teamSeasonStatsService = teamSeasonStatsService;
        this.userService = userService;
        this.appSettingService = appSettingService;
        this.seasonImportStatusService = seasonImportStatusService;
        this.rosterService = rosterService;
    }

    @GetMapping("/standings")
    public String showStandings(@RequestParam(value = "season", required = false) Long seasonId, @RequestParam(defaultValue = "points") String sortBy, @RequestParam(defaultValue = "desc") String direction, Model model) {
        List<Season> seasons = seasonImportStatusService.getAllImportedSeasons();

        if (seasons.isEmpty()) {
            model.addAttribute("error", "Neexistují žádné záznamy.");
            model.addAttribute("standings", List.of());
            model.addAttribute("seasons", List.of());
            return "standings";
        }

        if (seasonId == null) {
            seasonId = appSettingService.getCurrentSeason().getValue();
        }

        List<TeamSeason> teamSeasons = teamSeasonService.getTeamSeasonsBySeasonId(seasonId);
        List<TeamSeasonStatsDto> standings = teamSeasonStatsService.calculateStandings(teamSeasons);

        Comparator<TeamSeasonStatsDto> comparator = switch (sortBy) {
            case "wins" -> Comparator.comparingInt(TeamSeasonStatsDto::getWins);
            case "losses" -> Comparator.comparingInt(TeamSeasonStatsDto::getLosses);
            case "overtimeLosses" -> Comparator.comparingInt(TeamSeasonStatsDto::getOvertimeLosses);
            case "matches" -> Comparator.comparingInt(TeamSeasonStatsDto::getMatches);
            case "goalsFor" -> Comparator.comparingInt(TeamSeasonStatsDto::getGoalsFor);
            case "goalsAgainst" -> Comparator.comparingInt(TeamSeasonStatsDto::getGoalsAgainst);
            case "pointPctg" -> Comparator.comparingDouble(TeamSeasonStatsDto::getPointPctg);
            default -> Comparator.comparingInt(TeamSeasonStatsDto::getPoints)
                    .thenComparingDouble(TeamSeasonStatsDto::getPointPctg)
                    .thenComparingInt(TeamSeasonStatsDto::getWins);
        };
        if (direction.equals("desc")) comparator = comparator.reversed();
        standings.sort(comparator);

        model.addAttribute("standings", standings);
        model.addAttribute("seasons", seasons);
        model.addAttribute("selectedSeasonId", seasonId);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);

        return "standings";
    }

    @GetMapping("/teams")
    public String showTeams(@RequestParam(value = "season", required = false)Long seasonId, Model model) {

        List<Season> seasons = seasonImportStatusService.getAllImportedSeasons();

        if (seasons.isEmpty()) {
            model.addAttribute("error", "Neexistují žádné záznamy.");
            model.addAttribute("teams", List.of());
            model.addAttribute("seasons", List.of());
            return "teams";
        }

        if(seasonId == null) {
            seasonId = seasons.getFirst().getId();
        }

        List<TeamSeason> teamsSeasons = teamSeasonService.getTeamSeasonsBySeasonId(seasonId);
        List<Team> teams = teamsSeasons.stream()
                .map(TeamSeason::getTeam)
                .sorted(Comparator.comparing(Team::getName))
                .toList();

        model.addAttribute("seasons", seasons);
        model.addAttribute("selectedSeasonId", seasonId);
        model.addAttribute("teams", teams);
        return "teams";
    }

    @GetMapping("/teams/{id}")
    public String showTeam(@PathVariable Long id, @RequestParam(value = "season", required = false)Long seasonId, Principal principal, Model model) {
        Team team = teamService.getTeamById(id);
        List<TeamSeason> teamSeasons = teamSeasonService.getTeamSeasonsByTeam(team);

        List<Season> seasons = teamSeasons.stream()
                .map(TeamSeason::getSeason).distinct()
                .sorted(Comparator.comparing(Season::getStartYear).reversed()).toList();

        if(seasonId == null) {
            seasonId = seasons.getFirst().getId();
        }

        TeamSeason teamSeason = teamSeasonService.getTeamSeasonBySeasonIdAndTeam(seasonId, team);

        List<Game> allGames = gameService.getGamesBySeasonIdAndTeam(seasonId, team);

        List<Game> playoffGames = allGames.stream().filter(g -> g.getGameType()==3)
                .sorted(Comparator.comparing(Game::getStartTimeUtc)).toList();
        List<Game> finishedGames = allGames.stream().filter(g -> "OFF".equals(g.getGameState()) && g.getGameType()==2)
                .sorted(Comparator.comparing(Game::getStartTimeUtc).reversed()).toList();
        List<Game> upcomingGames = allGames.stream().filter(g -> !"OFF".equals(g.getGameState()) && g.getGameType()==2)
                .sorted(Comparator.comparing(Game::getStartTimeUtc)).toList();

        List<Roster> roster = rosterService.getRostersByTeamSeason(teamSeason);

        List<Roster> goalies = roster.stream().filter(r -> "G".equals(r.getPosition())).sorted(Comparator.comparing(Roster::getSweaterNumber)).toList();
        List<Roster> defencemen = roster.stream().filter(r -> "D".equals(r.getPosition())).sorted(Comparator.comparing(Roster::getSweaterNumber)).toList();
        List<Roster> forwards = roster.stream().filter(r -> "R".equals(r.getPosition()) || "L".equals(r.getPosition()) || "C".equals(r.getPosition()))
                .sorted(Comparator.comparing(Roster::getSweaterNumber)).toList();

        boolean isFavorite = false;
        if (principal != null) {
            User user = userService.getByUsername(principal.getName());
            isFavorite = user.getFavoriteTeams().contains(teamSeason.getTeam());
        }

        TeamSeasonStatsDto teamStats = teamSeasonStatsService.calculateTeamSeasonStats(teamSeason);

        model.addAttribute("isFavorite", isFavorite);
        model.addAttribute("playoffGames", playoffGames);
        model.addAttribute("finishedGames", finishedGames);
        model.addAttribute("upcomingGames", upcomingGames);
        model.addAttribute("goalies", goalies);
        model.addAttribute("defencemen", defencemen);
        model.addAttribute("forwards", forwards);
        model.addAttribute("team", team);
        model.addAttribute("teamStats", teamStats);
        model.addAttribute("seasons", seasons);
        model.addAttribute("selectedSeasonId", seasonId);

        return "team";

    }

    @PostMapping("/teams/{id}/addFavorite")
    public String addFavorite(@PathVariable Long id, Principal principal) {
        if (principal != null) {
            User user = userService.getByUsername(principal.getName());
            Team team = teamService.getTeamById(id);
            userService.addFavoriteTeam(user, team);
        }
        return "redirect:/teams/" + id;
    }

    @PostMapping("/teams/{id}/dropFavorite")
    public String dropFavorite(@PathVariable Long id, Principal principal) {
        if (principal != null) {
            User user = userService.getByUsername(principal.getName());
            Team team = teamService.getTeamById(id);
            userService.removeFavoriteTeam(user, team);
        }
        return "redirect:/teams/" + id;
    }




}