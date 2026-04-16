package org.novak.statistics.controller;


import org.novak.statistics.entity.Competition;
import org.novak.statistics.entity.SeasonImportStatus;
import org.novak.statistics.service.*;
import org.novak.statistics.service.importService.SeasonImportService;
import org.novak.statistics.service.updateService.SeasonUpdateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.ui.Model;
import org.novak.statistics.entity.User;
import org.novak.statistics.entity.Season;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;


@Controller
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    private final UserService userService;
    private final SeasonImportStatusService seasonImportStatusService;
    private final CompetitionService competitionService;
    private final SeasonService seasonService;
    private final ImportLockService importLockService;
    private final SeasonImportService seasonImportService;
    private final AppSettingService appSettingService;
    private final SeasonUpdateService seasonUpdateService;

    public AdminController(UserService userService, SeasonImportStatusService seasonImportStatusService, CompetitionService competitionService, SeasonService seasonService, ImportLockService importLockService, SeasonImportService seasonImportService, AppSettingService appSettingService, SeasonUpdateService seasonUpdateService) {
        this.userService = userService;
        this.seasonImportStatusService = seasonImportStatusService;
        this.competitionService = competitionService;
        this.seasonService = seasonService;
        this.importLockService = importLockService;
        this.seasonImportService = seasonImportService;
        this.appSettingService = appSettingService;
        this.seasonUpdateService = seasonUpdateService;
    }

    @GetMapping("/admin/users")
    public String showUsers(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "admin/users";
    }

    @PostMapping("/admin/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, Principal principal, RedirectAttributes ra) {
        try{
            User currentUser = userService.getByUsername(principal.getName());
            if (currentUser.getId().equals(id)) {
                ra.addFlashAttribute("error", "Nemůžete smazat sám sebe!");
                return "redirect:/admin/users";
            }
            userService.deleteById(id);
            ra.addFlashAttribute("message", "Uživatel odstraněn");
        } catch (Exception e) {
            log.warn("Chyba při mazání uživatele {}: {}", id, e.getMessage());
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }


    @GetMapping("/admin/import")
    public String showImport(@RequestParam(value = "competitionName", required = false)String competitionName, Model model) {

        List<Competition> competitions = competitionService.getAll();
        Competition competition;
        if(competitionName != null) {
            competition = competitionService.getByName(competitionName);
        }
        else{
            competition = competitions.getFirst();
        }


        List<SeasonImportStatus> seasonImportStatuses = seasonImportStatusService.getAllByCompetitionOrderBySeasonStartYearDesc(competition);

        model.addAttribute("competitions", competitions);
        model.addAttribute("selectedCompetition", competition);
        model.addAttribute("seasonStatuses", seasonImportStatuses);
        return "admin/import";
    }

    @CacheEvict(value = {"playerStats", "goalieStats"}, allEntries = true)
    @PostMapping("/admin/import/importSeason")
    public String importSeason(@RequestParam String competitionName, @RequestParam Integer seasonStart, @RequestParam Integer seasonEnd, RedirectAttributes ra) {

        log.info("Calling Import {} {}", seasonStart, seasonEnd);

        if(!(seasonEnd == seasonStart+1)) {
            ra.addFlashAttribute("error", "Neplatné roky začátku nebo konce sezóny.");
            return "redirect:/admin/import";
        }
        Season currentSeason = seasonService.getById(appSettingService.getCurrentSeason().getValue());
        if(seasonStart > currentSeason.getStartYear()) {
            ra.addFlashAttribute("error", "Neplatná sezóna. Tato sezóna ještě nezačala");
            return "redirect:/admin/import";
        }

        Competition competition = competitionService.getByName(competitionName);

        if(!competition.getName().equals("NHL")) {
            ra.addFlashAttribute("error","Tato soutěž není podporována.");
            return "redirect:/admin/import";
        }

        if (importLockService.isLocked()) {
            ra.addFlashAttribute("error", "Již probíhá import nebo aktualizace. Počkejte na dokončení.");
            return "redirect:/admin/import";
        }

        try {
            long seasonId = (seasonStart * 10000L) + seasonEnd;

            Season season = seasonService.createSeason(seasonId, seasonStart, seasonEnd, competition.getName());
            seasonImportService.importSeasonDataAsync(season, false);

            ra.addFlashAttribute("message", "Import byl zahájen a probíhá na pozadí. Po několika minutách zkontrolujte stav.");
        }
        catch (Exception e) {
            log.error("Chyba při spuštění importu sezóny {}/{}", seasonStart, seasonEnd, e);
            ra.addFlashAttribute("error", "Chyba při spuštění importu: " + e.getMessage());
        }

        return "redirect:/admin/import";
    }

    @CacheEvict(value = {"playerStats", "goalieStats"}, allEntries = true)
    @PostMapping("/admin/import/updateGames")
    public String updateGames(@RequestParam(defaultValue = "3") int daysBack, RedirectAttributes ra) {
        if (daysBack < 1 || daysBack > 30) {
            ra.addFlashAttribute("error", "Počet dní musí být mezi 1 a 30.");
            return "redirect:/admin/import";
        }

        if (importLockService.isLocked()) {
            ra.addFlashAttribute("error", "Již probíhá import nebo aktualizace. Počkejte na dokončení.");
            return "redirect:/admin/import";
        }

        try {
            seasonUpdateService.manualUpdate(daysBack);
            ra.addFlashAttribute("message", "Aktualizace zápasů z posledních " + daysBack + " dní byla spuštěna.");
        } catch (Exception e) {
            log.error("Chyba při aktualizaci zápasů", e);
            ra.addFlashAttribute("error", "Chyba při aktualizaci: " + e.getMessage());
        }
        return "redirect:/admin/import";
    }

    @CacheEvict(value = {"playerStats", "goalieStats"}, allEntries = true)
    @PostMapping("/admin/import/updateEntireSeason")
    public String updateEntireSeason(RedirectAttributes ra) {
        if (importLockService.isLocked()) {
            ra.addFlashAttribute("error", "Již probíhá import nebo aktualizace. Počkejte na dokončení.");
            return "redirect:/admin/import";
        }

        try {
            seasonUpdateService.updateEntireSeason();
            ra.addFlashAttribute("message", "Aktualizace všech zápasů aktuální sezóny byla spuštěna.");
        } catch (Exception e) {
            log.error("Chyba při aktualizaci zápasů", e);
            ra.addFlashAttribute("error", "Chyba při aktualizaci: " + e.getMessage());
        }
        return "redirect:/admin/import";
    }

    @CacheEvict(value = {"playerStats", "goalieStats"}, allEntries = true)
    @PostMapping("/admin/import/reimportSeason")
    public String reimportSeason(@RequestParam Long seasonId, RedirectAttributes ra) {
        Season season = seasonService.getById(seasonId);

        if (importLockService.isLocked()) {
            ra.addFlashAttribute("error", "Již probíhá import nebo aktualizace. Počkejte na dokončení.");
            return "redirect:/admin/import";
        }

        try {
            seasonImportService.importSeasonDataAsync(season, true);
            ra.addFlashAttribute("message", "Reimport sezóny " + season.getStartYear() + "-" + season.getEndYear() + " byl zahájen.");
        } catch (Exception e) {
            log.error("Chyba při reimportu sezóny", e);
            ra.addFlashAttribute("error", "Chyba při reimportu: " + e.getMessage());
        }
        return "redirect:/admin/import";
    }


}
