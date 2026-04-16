package org.novak.statistics.service.importService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.novak.statistics.data.DataInitializer;
import org.novak.statistics.dto.LocalizedString;
import org.novak.statistics.dto.PlayerDto;
import org.novak.statistics.dto.response.TeamApiResponse;
import org.novak.statistics.entity.Player;
import org.novak.statistics.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class PlayerImportServiceIntegrationTest {

    @Autowired
    private PlayerImportService playerImportService;

    @Autowired
    private PlayerRepository playerRepository;

    @MockitoBean
    private RestTemplate restTemplate;

    @MockitoBean
    private DataInitializer dataInitializer;

    @BeforeEach
    void setUp() {
        TeamApiResponse teamResponse = new TeamApiResponse();
        teamResponse.setData(List.of());
        when(restTemplate.getForObject(contains("stats/rest/en/team"), eq(TeamApiResponse.class)))
                .thenReturn(teamResponse);

        playerRepository.deleteAll();
    }

    private PlayerDto createPlayerDto() {
        PlayerDto dto = new PlayerDto();
        dto.setActive(false);

        LocalizedString firstName = new LocalizedString();
        firstName.setValue("Jaromír");
        dto.setFirstName(firstName);

        LocalizedString lastName = new LocalizedString();
        lastName.setValue("Jágr");
        dto.setLastName(lastName);

        dto.setSweaterNumber(68);
        dto.setPosition("R");
        dto.setShootsCatches("L");
        dto.setHeightInCentimeters(192);
        dto.setWeightInKilograms(113);
        dto.setBirthDate(LocalDate.of(1972, 2, 15));
        dto.setBirthCountry("CZE");

        LocalizedString birthCity = new LocalizedString();
        birthCity.setValue("Kladno");
        dto.setBirthCity(birthCity);

        return dto;
    }

    @Test
    void importPlayer_savesPlayerToDatabase() {
        when(restTemplate.getForObject(anyString(), eq(PlayerDto.class)))
                .thenReturn(createPlayerDto());

        playerImportService.importPlayer(8449726L);

        Player saved = playerRepository.findById(8449726L).orElseThrow();
        assertThat(saved.getFirstName()).isEqualTo("Jaromír");
        assertThat(saved.getLastName()).isEqualTo("Jágr");
        assertThat(saved.getSweaterNumber()).isEqualTo(68);
        assertThat(saved.getPosition()).isEqualTo("R");
        assertThat(saved.getShootsCatches()).isEqualTo("L");
        assertThat(saved.getHeightInCentimetres()).isEqualTo(192);
        assertThat(saved.getWeightInKilograms()).isEqualTo(113);
        assertThat(saved.getBirthDate()).isEqualTo(LocalDate.of(1972, 2, 15));
        assertThat(saved.getBirthCountry()).isEqualTo("CZE");
        assertThat(saved.getBirthCity()).isEqualTo("Kladno");
        assertThat(saved.isActive()).isFalse();
    }

    @Test
    void importPlayer_skipsIfAlreadyExists() {
        when(restTemplate.getForObject(anyString(), eq(PlayerDto.class)))
                .thenReturn(createPlayerDto());

        playerImportService.importPlayer(8449726L);
        playerImportService.importPlayer(8449726L);

        assertThat(playerRepository.findAll()).hasSize(1);
    }

    @Test
    void importPlayer_handlesNullFirstName() {
        PlayerDto dto = createPlayerDto();
        dto.setFirstName(null);

        when(restTemplate.getForObject(anyString(), eq(PlayerDto.class)))
                .thenReturn(dto);

        playerImportService.importPlayer(8449726L);

        Optional<Player> saved = playerRepository.findById(8449726L);
        assertThat(saved).isPresent();
        assertThat(saved.get().getFirstName()).isNull();
    }

    @Test
    void importPlayer_handlesNullBirthCity() {
        PlayerDto dto = createPlayerDto();
        dto.setBirthCity(null);

        when(restTemplate.getForObject(anyString(), eq(PlayerDto.class)))
                .thenReturn(dto);

        playerImportService.importPlayer(8449726L);

        Player saved = playerRepository.findById(8449726L).orElseThrow();
        assertThat(saved.getBirthCity()).isNull();
    }
}