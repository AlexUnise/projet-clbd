package com.example.DeplacementMicroservice;

import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.List;

public interface RouteRepository extends CrudRepository<Route, Integer> {

    default List<Route> findAllAsList() {
        List<Route> routes = new ArrayList<>();
        this.findAll().forEach(routes::add);
        return routes;
    }

    Route findByVehicleId(Integer vehiculeId);
}

