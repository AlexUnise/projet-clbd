package com.example.AffectationMicroservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
public class AffectationController {

    private final AffectationService affectationService;

    public AffectationController(AffectationService affectationService){
        this.affectationService = affectationService;
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/{id}")
    public void vehicleFreed(@PathVariable Integer id){
        System.out.println(id);
        affectationService.vehicleFreed(id);
    }

}
