package com.example.TransfertMicroservice;


import com.DTOLibrary.Communication;
import com.project.model.dto.Coord;
import com.project.model.dto.FacilityDto;
import com.project.model.dto.FireDto;
import com.project.model.dto.VehicleDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@CrossOrigin(origins = "*")
public class TransfertController {

    private final Communication communication = new Communication();


    @RequestMapping(method = RequestMethod.GET, path = "/fire")
    private List<FireDto> getAllFires(){
        return communication.getAllFires();
    }



    @RequestMapping(method = RequestMethod.POST, path = "/vehicle/{teamuuid}")
    public void addVehicle(@PathVariable String teamuuid,@RequestBody VehicleDto vehicleDto){
        communication.addVehicle(teamuuid,vehicleDto);
    }

    @RequestMapping(method = RequestMethod.DELETE, path ="/vehicle/{teamuuid}/{id}")
    public void deleteVehicle(@PathVariable String teamuuid, @PathVariable String id){
        communication.deleteVehicle(teamuuid,id);
    }

    @RequestMapping(method = RequestMethod.GET, path ="/vehicle")
    public List<VehicleDto> getAllVehicles(){
        return communication.getAllVehicles();
    }

    @RequestMapping(method = RequestMethod.PUT, path ="/vehicle/{teamuuid}/{id}")
    public boolean updateVehicle(@PathVariable String teamuuid,@PathVariable Integer id, @RequestBody VehicleDto vehicleDto){
        return communication.updateVehicle(teamuuid,id, vehicleDto);
    }

    @RequestMapping(method = RequestMethod.PUT, path ="/vehicle/move/{teamuuid}/{id}/{maxSpeed}/{mode}")
    public void moveVehicle(@PathVariable Integer mode, @PathVariable String teamuuid, @PathVariable Float maxSpeed, @PathVariable Integer id, @RequestBody Coord coord){
        communication.moveVehicle(teamuuid,id,coord, maxSpeed, mode);
    }



    @RequestMapping(method = RequestMethod.GET, path ="/facility")
    public List<FacilityDto> getAllFacilities(){
        return communication.getAllFacilities();
    }
}
