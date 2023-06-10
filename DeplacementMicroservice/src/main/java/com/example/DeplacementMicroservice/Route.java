package com.example.DeplacementMicroservice;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
@Entity
public class Route {

    @Id
    @GeneratedValue
    private Integer routeId;

    private Integer vehicleId;

    @OneToMany(cascade = CascadeType.ALL)
    private List<RouteCoordinate> coordinates;

    public Route() {
    }

    public Route(ArrayList<List<Double>> coordinates, Integer vehicleId) {
        this.coordinates = convertToRouteCoordinates(coordinates);
        this.vehicleId = vehicleId;
    }

    // Helper method to convert ArrayList<List<Double>> to List<RouteCoordinate>
    private List<RouteCoordinate> convertToRouteCoordinates(ArrayList<List<Double>> coordinates) {
        List<RouteCoordinate> routeCoordinates = new ArrayList<>();
        for (List<Double> coordinateList : coordinates) {
            for (Double coordinate : coordinateList) {
                routeCoordinates.add(new RouteCoordinate(coordinate));
            }
        }
        return routeCoordinates;
    }

    public Integer getRouteId() {
        return routeId;
    }

    public void setRouteId(Integer routeId) {
        this.routeId = routeId;
    }

    public List<RouteCoordinate> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<RouteCoordinate> coordinates) {
        this.coordinates = coordinates;
    }
}



