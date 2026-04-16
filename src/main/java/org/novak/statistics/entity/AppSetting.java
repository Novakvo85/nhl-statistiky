package org.novak.statistics.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class AppSetting {

    @Id
    private long id;
    @Column(nullable = false, unique = true, length = 100)
    private String name;
    @Column(name = "setting_value", nullable = false)
    private long value;

    public AppSetting() {
    }

    public AppSetting(long id, String name, long value) {
        this.id = id;
        this.name = name;
        this.value = value;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }
}
