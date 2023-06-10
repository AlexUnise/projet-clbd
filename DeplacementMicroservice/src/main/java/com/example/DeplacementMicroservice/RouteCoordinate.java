package com.example.DeplacementMicroservice;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class RouteCoordinate {

    @Id
    @GeneratedValue
    private Integer coordinateId;

    private Double value;

    @ManyToOne
    private Route route;

    public RouteCoordinate() {
    }

    public RouteCoordinate(Double value) {
        this.value = value;
    }

    // Getters and setters


    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }
}
