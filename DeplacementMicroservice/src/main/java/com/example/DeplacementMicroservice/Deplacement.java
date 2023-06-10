package com.example.DeplacementMicroservice;



import jakarta.persistence.*;

import javax.swing.text.StyledEditorKit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Deplacement {

    @Id
    @GeneratedValue
    private Integer deplacementId;


    String teamuuid;
    Integer vehiculeId;
    Float maxSpeed;


    Integer lineId;
    Integer lineIdMax;
    Integer step;

    Double finalLat;
    Double finalLon;
    Boolean initialize;


    @ElementCollection(fetch = FetchType.EAGER)
    List<Integer> stepsMax;

    @ElementCollection(fetch = FetchType.EAGER)
    List<Double> deltasLon;

    @ElementCollection(fetch = FetchType.EAGER)
    List<Double> deltasLat;

    @ElementCollection(fetch = FetchType.EAGER)
    List<Double> deltasLonAdjust;

    @ElementCollection(fetch = FetchType.EAGER)
    List<Double> deltasLatAdjust;





    public Deplacement() {
    }


    public Deplacement(String teamuuid, Integer vehiculeId, Float maxSpeed, Integer lineIdMax, List<Integer> stepsMax, List<Double> deltasLon, List<Double> deltasLat, List<Double> deltasLonAdjust, List<Double> deltasLatAdjust, Double finalLon, Double finalLat, Boolean initialize) {
        this.teamuuid = teamuuid;
        this.vehiculeId = vehiculeId;
        this.maxSpeed = maxSpeed;
        this.lineId = 0;
        this.lineIdMax = lineIdMax;
        this.step = 0;
        this.stepsMax = stepsMax;
        this.deltasLon = deltasLon;
        this.deltasLat = deltasLat;
        this.deltasLonAdjust = deltasLonAdjust;
        this.deltasLatAdjust = deltasLatAdjust;
        this.finalLon = finalLon;
        this.finalLat = finalLat;
        this.initialize = initialize;
    }


    public Boolean getInitialize() {
        return initialize;
    }

    public void setInitialize(Boolean initialize) {
        this.initialize = initialize;
    }

    public Integer getDeplacementId() {
        return deplacementId;
    }

    public void setDeplacementId(Integer deplacementId) {
        this.deplacementId = deplacementId;
    }

    public String getTeamuuid() {
        return teamuuid;
    }

    public void setTeamuuid(String teamuuid) {
        this.teamuuid = teamuuid;
    }

    public Integer getVehiculeId() {
        return vehiculeId;
    }

    public void setVehiculeId(Integer vehiculeId) {
        this.vehiculeId = vehiculeId;
    }

    public Float getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(Float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public Integer getLineId() {
        return lineId;
    }

    public void setLineId(Integer lineId) {
        this.lineId = lineId;
    }

    public Integer getLineIdMax() {
        return lineIdMax;
    }

    public void setLineIdMax(Integer lineIdMax) {
        this.lineIdMax = lineIdMax;
    }

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }

    public List<Integer> getStepsMax() {
        return stepsMax;
    }

    public void setStepsMax(List<Integer> stepsMax) {
        this.stepsMax = stepsMax;
    }

    public List<Double> getDeltasLon() {
        return deltasLon;
    }

    public void setDeltasLon(List<Double> deltasLon) {
        this.deltasLon = deltasLon;
    }

    public List<Double> getDeltasLat() {
        return deltasLat;
    }

    public void setDeltasLat(List<Double> deltasLat) {
        this.deltasLat = deltasLat;
    }

    public List<Double> getDeltasLonAdjust() {
        return deltasLonAdjust;
    }

    public void setDeltasLonAdjust(List<Double> deltasLonAdjust) {
        this.deltasLonAdjust = deltasLonAdjust;
    }

    public List<Double> getDeltasLatAdjust() {
        return deltasLatAdjust;
    }

    public void setDeltasLatAdjust(List<Double> deltasLatAdjust) {
        this.deltasLatAdjust = deltasLatAdjust;
    }


    public Double getFinalLat() {
        return finalLat;
    }

    public void setFinalLat(Double finalLat) {
        this.finalLat = finalLat;
    }

    public Double getFinalLon() {
        return finalLon;
    }

    public void setFinalLon(Double finalLon) {
        this.finalLon = finalLon;
    }
}