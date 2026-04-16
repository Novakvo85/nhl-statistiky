package org.novak.statistics.entity;

import jakarta.persistence.*;

@Entity
public class Competition {

    @Id
    private Long id;
    @Column(nullable = false, length = 100)
    private String name;

    public Competition() {
    }

    public Competition(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
