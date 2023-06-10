package com.example.AffectationMicroservice;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;


@Entity
public class Affectation {
    @Id
    @GeneratedValue
    private Integer affectationId;

    private Integer vehicleId;

    private String type;
    private Integer destinationId;

    private boolean isArrived;

    private boolean isFinished;

    public Affectation(){

    }

    public Affectation(Integer vehicleId, String type, Integer destinationId, boolean isArrived, boolean isFinished) {
        this.vehicleId = vehicleId;
        this.type = type;
        this.destinationId = destinationId;
        this.isArrived = isArrived;
        this.isFinished = isFinished;
    }

    public Integer getAffectationId() {
        return affectationId;
    }

    public Integer getVehicleId() {
        return vehicleId;
    }

    public Integer getDestinationId() {
        return destinationId;
    }

    public String getType() {
        return type;
    }

    public boolean isArrived() {
        return isArrived;
    }

    public void setArrived(boolean arrived) {
        isArrived = arrived;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    @Override
    public String toString(){
        return type + " vehicule: "+vehicleId+" destination: "+destinationId;
    }
}