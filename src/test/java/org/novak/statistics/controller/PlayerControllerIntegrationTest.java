package org.novak.statistics.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.novak.statistics.data.DataInitializer;
import org.novak.statistics.entity.Player;
import org.novak.statistics.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PlayerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PlayerRepository playerRepository;

    @MockitoBean
    private DataInitializer dataInitializer;

    @BeforeEach
    void setUp() {
        playerRepository.deleteAll();
    }

    @Test
    void showPlayerDetail_returnsCorrectView() throws Exception {
        Player player = new Player();
        player.setId(1L);
        player.setFirstName("Auston");
        player.setLastName("Matthews");
        player.setPosition("C");
        playerRepository.save(player);

        mockMvc.perform(get("/players/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("player-detail"))
                .andExpect(model().attributeExists("player"))
                .andExpect(model().attribute("player", hasProperty("firstName", is("Auston"))));
    }

    @Test
    void showPlayerDetail_returns404WhenNotFound() throws Exception {
        mockMvc.perform(get("/players/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void showPlayers_returnsEmptyListWithoutQuery() throws Exception {
        mockMvc.perform(get("/players"))
                .andExpect(status().isOk())
                .andExpect(view().name("players"))
                .andExpect(model().attribute("players", hasSize(0)));
    }

    @Test
    void showPlayers_returnsMatchingPlayers() throws Exception {
        Player player = new Player();
        player.setId(2L);
        player.setFirstName("Auston");
        player.setLastName("Matthews");
        playerRepository.save(player);

        mockMvc.perform(get("/players").param("q", "Matthews"))
                .andExpect(status().isOk())
                .andExpect(view().name("players"))
                .andExpect(model().attribute("players", hasSize(1)));
    }
}