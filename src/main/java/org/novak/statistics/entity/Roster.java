package org.novak.statistics.entity;

import jakarta.persistence.*;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"playerId", "teamSeasonId"})
})
public class Roster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "teamSeasonId", nullable = false)
    private TeamSeason teamSeason;

    @ManyToOne
    @JoinColumn(name = "playerId", nullable = false)
    private Player player;

    @Column(nullable = true)
    private Integer sweaterNumber;

    @Column(length = 1)
    private String position;

    public Roster() {
    }

    public Roster(Long id, TeamSeason teamSeason, Player player, Integer sweaterNumber, String position) {
        this.id = id;
        this.teamSeason = teamSeason;
        this.player = player;
        this.sweaterNumber = sweaterNumber;
        this.position = position;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TeamSeason getTeamSeason() {
        return teamSeason;
    }

    public void setTeamSeason(TeamSeason teamSeason) {
        this.teamSeason = teamSeason;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Integer getSweaterNumber() {
        return sweaterNumber;
    }

    public void setSweaterNumber(Integer sweaterNumber) {
        this.sweaterNumber = sweaterNumber;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
