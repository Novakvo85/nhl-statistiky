package org.novak.statistics.controller;

import org.novak.statistics.dto.GoalieSeasonAggregatedStatsDto;
import org.novak.statistics.dto.PlayerSeasonAggregatedStatsDto;
import org.novak.statistics.dto.PlayerSeasonStatsDto;
import org.novak.statistics.entity.Player;
import org.novak.statistics.entity.Season;
import org.novak.statistics.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;
import java.util.List;
@Controller
public class PlayerController {

    private final PlayerService playerService;
    private final PlayerStatsAggregationService playerStatsAggregationService;
    private final SeasonService seasonService;
    private final AppSettingService appSettingService;
    private final SeasonImportStatusService seasonImportStatusService;

    public PlayerController(PlayerService playerService, PlayerStatsAggregationService playerStatsAggregationService, SeasonService seasonService, AppSettingService appSettingService, SeasonImportStatusService seasonImportStatusService) {
        this.playerService = playerService;
        this.playerStatsAggregationService = playerStatsAggregationService;
        this.seasonService = seasonService;
        this.appSettingService = appSettingService;
        this.seasonImportStatusService = seasonImportStatusService;
    }

    @GetMapping("/players")
    public String showPlayers(@RequestParam(value = "q", required = false) String query, Model model) {
        List<Player> players;
        if (query != null && !query.isEmpty()) {
            players = playerService.findPlayersByName(query);
        } else {
            players = List.of();
        }
        model.addAttribute("players", players);
        model.addAttribute("query", query != null ? query : "");
        return "players";
    }

    @GetMapping("/players/{id}")
    public String showPlayerDetail(@PathVariable Long id, Model model) {
        Player player = playerService.getPlayerById(id);

        List<PlayerSeasonStatsDto> seasonStats = playerStatsAggregationService.getAllPlayerSeasons(player);
        seasonStats.sort(Comparator.comparing(
                (PlayerSeasonStatsDto dto) -> dto.getRoster().getTeamSeason().getSeason().getStartYear(), Comparator.nullsLast(Comparator.reverseOrder())
        ));

        model.addAttribute("player", player);
        model.addAttribute("seasonStats", seasonStats);
        return "player-detail";
    }

    @GetMapping("/statistics")
    public String redirectStatistics() {
        return "redirect:/statistics/skaters";
    }

    @GetMapping("/statistics/skaters")
    public String showSkaters(@RequestParam(value = "season", required = false) Long seasonId, @RequestParam(defaultValue = "points") String sortBy, @RequestParam(defaultValue = "desc") String direction, @RequestParam(defaultValue = "0") int page, Model model) {
        List<Season> seasons = seasonImportStatusService.getAllImportedSeasons();

        if (seasons.isEmpty()) {
            model.addAttribute("error", "Neexistují žádné záznamy.");
            model.addAttribute("seasons", List.of());
            return "statistics/skaters";
        }

        if (seasonId == null) {
            seasonId = appSettingService.getCurrentSeason().getValue();
        }

        Season season = seasonService.getById(seasonId);

        List<PlayerSeasonAggregatedStatsDto> stats = playerStatsAggregationService.calculateAllAggregatedPlayerStats(season);

        Comparator<PlayerSeasonAggregatedStatsDto> comparator = switch (sortBy) {
            case "goals" -> Comparator.comparingInt(PlayerSeasonAggregatedStatsDto::getGoals);
            case "assists" -> Comparator.comparingInt(PlayerSeasonAggregatedStatsDto::getAssists);
            case "gamesPlayed" -> Comparator.comparingInt(PlayerSeasonAggregatedStatsDto::getGamesPlayed);
            default -> Comparator.comparingInt(PlayerSeasonAggregatedStatsDto::getPoints)
                    .thenComparingDouble(PlayerSeasonAggregatedStatsDto::getGoals);
        };
        if (direction.equals("desc")) comparator = comparator.reversed();
        stats.sort(comparator);

        if (stats.isEmpty()) {
            model.addAttribute("players", List.of());
            model.addAttribute("totalPages", 0);
            model.addAttribute("currentPage", 0);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("direction", direction);
            model.addAttribute("seasons", seasons);
            model.addAttribute("selectedSeason", season);
            return "statistics/skaters";
        }

        int pageSize = 50;
        int totalPages = (int) Math.ceil((double) stats.size() / pageSize);
        page = Math.max(0, Math.min(page, totalPages - 1));
        int fromIndex = page * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, stats.size());
        List<PlayerSeasonAggregatedStatsDto> pageContent = stats.subList(fromIndex, toIndex);

        model.addAttribute("players", pageContent);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", page);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);
        model.addAttribute("seasons", seasons);
        model.addAttribute("selectedSeason", season);

        return "statistics/skaters";
    }

    @GetMapping("/statistics/goalies")
    public String showGoalies(@RequestParam(value = "season", required = false) Long seasonId, @RequestParam(defaultValue = "savePctg") String sortBy, @RequestParam(defaultValue = "desc") String direction, @RequestParam(defaultValue = "0") int page, Model model) {
        List<Season> seasons = seasonImportStatusService.getAllImportedSeasons();

        if (seasons.isEmpty()) {
            model.addAttribute("error", "Neexistují žádné záznamy.");
            model.addAttribute("seasons", List.of());
            return "statistics/goalies";
        }

        if (seasonId == null) {
            seasonId = appSettingService.getCurrentSeason().getValue();
        }

        Season season = seasonService.getById(seasonId);

        List<GoalieSeasonAggregatedStatsDto> stats = playerStatsAggregationService.calculateAllAggregatedGoalieStats(season);

        Comparator<GoalieSeasonAggregatedStatsDto> comparator = switch (sortBy) {
            case "gamesPlayed" -> Comparator.comparingInt(GoalieSeasonAggregatedStatsDto::getGamesPlayed);
            case "shotsAgainst" -> Comparator.comparingInt(GoalieSeasonAggregatedStatsDto::getShotsAgainst);
            case "goalsAgainst" -> Comparator.comparingInt(GoalieSeasonAggregatedStatsDto::getGoalsAgainst);
            case "gaa" -> Comparator.comparingDouble(GoalieSeasonAggregatedStatsDto::getGaa);
            default -> Comparator.comparingDouble(GoalieSeasonAggregatedStatsDto::getSavePctg);
        };
        if (direction.equals("desc")) comparator = comparator.reversed();
        stats.sort(comparator);

        if (stats.isEmpty()) {
            model.addAttribute("goalies", List.of());
            model.addAttribute("totalPages", 0);
            model.addAttribute("currentPage", 0);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("direction", direction);
            model.addAttribute("seasons", seasons);
            model.addAttribute("selectedSeason", season);
            return "statistics/skaters";
        }

        int pageSize = 50;
        int totalPages = (int) Math.ceil((double) stats.size() / pageSize);
        page = Math.max(0, Math.min(page, totalPages - 1));
        int fromIndex = page * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, stats.size());
        List<GoalieSeasonAggregatedStatsDto> pageContent = stats.subList(fromIndex, toIndex);

        model.addAttribute("goalies", pageContent);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", page);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);
        model.addAttribute("seasons", seasons);
        model.addAttribute("selectedSeason", season);

        return "statistics/goalies";
    }
}