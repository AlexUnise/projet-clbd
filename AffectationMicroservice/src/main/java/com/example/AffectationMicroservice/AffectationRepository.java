package com.example.AffectationMicroservice;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface AffectationRepository extends CrudRepository<Affectation, Integer> {
    Optional<Affectation> findOneByVehicleId(Integer vehicleId);
}