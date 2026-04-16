package org.novak.statistics.data;

import org.novak.statistics.entity.Competition;
import org.novak.statistics.entity.User;
import org.novak.statistics.repository.UserRepository;
import org.novak.statistics.service.AppSettingService;
import org.novak.statistics.service.CompetitionService;
import org.novak.statistics.service.ImportLockService;
import org.novak.statistics.service.SeasonCleanupService;
import org.novak.statistics.service.importService.TeamImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private final CompetitionService competitionService;
    private final TeamImportService teamImportService;
    private final AppSettingService appSettingService;
    private final SeasonCleanupService seasonCleanupService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ImportLockService importLockService;

    public DataInitializer(CompetitionService competitionService, TeamImportService teamImportService, AppSettingService appSettingService, SeasonCleanupService seasonCleanupService, UserRepository userRepository, PasswordEncoder passwordEncoder, ImportLockService importLockService) {
        this.competitionService = competitionService;
        this.teamImportService = teamImportService;
        this.appSettingService = appSettingService;
        this.seasonCleanupService = seasonCleanupService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.importLockService = importLockService;
    }

    @Override
    public void run(String[] args){
        importLockService.unlock();
        if(userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setEmail("admin@admin.com");
            admin.setRole("ROLE_ADMIN");

            userRepository.save(admin);
            log.info("Admin registrován");
        }
        else {
            log.info("Admin existuje");
        }

        Competition nhl = new Competition(1L,"NHL");

        competitionService.createInitialCompetitions(List.of(nhl));

        appSettingService.setCurrentSeason();

        seasonCleanupService.deleteUnimportedSeasons();
        teamImportService.importTeams();
    }

}
