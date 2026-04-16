package org.novak.statistics.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

public class LocalizedString {

    @JsonProperty("default")
    private String value;

    public LocalizedString() {}

    public LocalizedString(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
