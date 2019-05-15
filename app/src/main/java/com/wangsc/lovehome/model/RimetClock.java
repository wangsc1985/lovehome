package com.wangsc.lovehome.model;

import java.util.UUID;

public class RimetClock {
    private UUID id;
    private int hour;
    private int minite;
    private String summery;

    public RimetClock(UUID id) {
        this.id = id;
    }

    public RimetClock(int hour, int minite, String summery) {
        this.id=UUID.randomUUID();
        this.hour = hour;
        this.minite = minite;
        this.summery = summery;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinite() {
        return minite;
    }

    public void setMinite(int minite) {
        this.minite = minite;
    }

    public String getSummery() {
        return summery;
    }

    public void setSummery(String summery) {
        this.summery = summery;
    }
}
