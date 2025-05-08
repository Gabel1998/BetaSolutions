package com.example.betasolutions.model;

public class EmployeeSkills {
    private Integer emskEmId; //composite key
    private Integer emskSkId; //composite key

    //getters og setters
    public void setEmskEmId(Integer emskEmId) {
        this.emskEmId = emskEmId;
    }

    public Integer getEmskEmId() {
        return emskEmId;
    }

    public void setEmskSkId(Integer emskSkId) {
        this.emskSkId = emskSkId;
    }

    public Integer getEmskSkId() {
        return emskSkId;
    }
}
