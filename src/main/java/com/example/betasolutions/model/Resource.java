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

    // Getters and Setters
    public int getRe_id() {
        return re_id;
    }

    public void setRe_id(int re_id) {
        this.re_id = re_id;
    }

    public String getRe_name() {
        return re_name;
    }

    public void setRe_name(String re_name) {
        this.re_name = re_name;
    }

    public double getRe_co2_perHour() {
        return re_co2_perHour;
    }

    public void setRe_co2_perHour(double re_co2_perHour) {
        this.re_co2_perHour = re_co2_perHour;
    }

    public LocalDateTime getRe_created_at() {
        return re_created_at;
    }

    public void setRe_created_at(LocalDateTime re_created_at) {
        this.re_created_at = re_created_at;
    }
}
