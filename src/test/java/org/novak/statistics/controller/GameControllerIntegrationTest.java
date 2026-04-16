package org.novak.statistics.controller;

import org.junit.jupiter.api.Test;
import org.novak.statistics.data.DataInitializer;
import org.novak.statistics.entity.Team;
import org.novak.statistics.entity.User;
import org.novak.statistics.service.GameService;
import org.novak.statistics.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GameControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private GameService gameService;

    @MockitoBean
    private DataInitializer dataInitializer;


    @Test
    @WithMockUser(username = "testuser")
    void showMyGames_returnsEmptyListWhenNoUpcomingGames() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        Team team = new Team();
        team.setId(1L);
        team.setName("Boston Bruins");
        user.setFavoriteTeams(Set.of(team));

        when(userService.getByUsername("testuser")).thenReturn(user);
        when(gameService.getUpcomingGamesForTeams(Set.of(team), 10)).thenReturn(List.of());

        mockMvc.perform(get("/my-games"))
                .andExpect(status().isOk())
                .andExpect(view().name("my-games"))
                .andExpect(model().attribute("upcomingGames", hasSize(0)));
    }

    @Test
    void showMyGames_redirectsToLoginWhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/my-games"))
                .andExpect(status().is3xxRedirection());
    }
}