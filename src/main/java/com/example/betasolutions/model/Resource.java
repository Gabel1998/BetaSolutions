package com.example.betasolutions.model;

import java.time.LocalDateTime;

public class Resource {

    private int re_id;
    private String re_name;
    private double re_co2_perHour;
    private LocalDateTime re_created_at;

    // Constructors
    public Resource() {}

    public Resource(int re_id, String re_name, double re_co2_perHour, LocalDateTime re_created_at) {
        this.re_id = re_id;
        this.re_name = re_name;
        this.re_co2_perHour = re_co2_perHour;
        this.re_created_at = re_created_at;
    }

    // ─────────── Getters and Setters ───────────

    public String getRe_name() {
        return re_name;
    }

    public double getRe_co2_perHour() {
        return re_co2_perHour;
    }

    public LocalDateTime getRe_created_at() {
        return re_created_at;
    }
}
