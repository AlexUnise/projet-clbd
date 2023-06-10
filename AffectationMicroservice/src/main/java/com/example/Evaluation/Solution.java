package com.example.Evaluation;


import com.project.model.dto.FacilityDto;
import com.project.model.dto.FireDto;
import com.project.model.dto.VehicleDto;

public class Solution {

    private String type;
    private float autonomousDistance;
    private float routeDistance;
    private VehicleDto vehicle;
    private FireDto fire;
    private FacilityDto facility;

    public Solution(String type,float autonomousDistance, float routeDistance, VehicleDto vehicle, FireDto fire, FacilityDto facility) {
        this.type = type;
        this.autonomousDistance = autonomousDistance;
        this.routeDistance = routeDistance;
        this.vehicle = vehicle;
        this.fire = fire;
        this.facility = facility;
    }

    public float getAutonomousDistance() {
        return autonomousDistance;
    }

    public float getRouteDistance() {
        return routeDistance;
    }

    public VehicleDto getVehicle() {
        return vehicle;
    }

    public FireDto getFire() {
        return fire;
    }

    public String getType() {
        return type;
    }

    public FacilityDto getFacility() {
        return facility;
    }

    @Override
    public String toString(){
        return type + ", dois parcourir "+autonomousDistance +
                " sans recharge et dois parcourir "+routeDistance+" pour arriver au feu." +
                "Vehicule "+ vehicle.getId()+", feu "+fire.getId()+", facility "
                +facility.getId();
    }
}
