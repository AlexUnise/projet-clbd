package com.example.DeplacementMicroservice;

import com.DTOLibrary.Communication;
import com.project.model.dto.Coord;
import com.project.model.dto.FacilityDto;
import com.project.model.dto.VehicleDto;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.css.ViewCSS;

import javax.swing.text.StyledEditorKit;
import javax.ws.rs.Path;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class DeplacementController {

    private final DeplacementService deplacementService;
    private final Communication communication = new Communication();

    public DeplacementController(DeplacementService deplacementService){
        this.deplacementService = deplacementService;
    }


    @RequestMapping(method = RequestMethod.PUT, path = "/init/{teamuuid}/{initialize}")
    public void init(@PathVariable String teamuuid, @PathVariable Boolean initialize) {
        deplacementService.init(teamuuid, initialize);
    }


    @RequestMapping(method = RequestMethod.PUT, path = "/{teamuuid}/{id}/{maxSpeed}/{mode}")
    public void moveVehicule(@PathVariable String teamuuid, @PathVariable Integer id, @PathVariable Float maxSpeed, @RequestBody Coord fireCoord, @PathVariable Integer mode){
        System.out.println("Deplacement controller launched");

        Communication communication = new Communication();

        //Get vehicle data
        VehicleDto vehicleDto = communication.getVehicleById(id);

        if (mode == 3){
            deplacementService.saveRoadMove(id, teamuuid, fireCoord, maxSpeed, vehicleDto, false);
        }
        if (mode == 2){
            deplacementService.saveLinearMove(id, teamuuid, fireCoord, maxSpeed, vehicleDto, false);
        }
        if (mode == 1){
            deplacementService.manualMoveVehicle(teamuuid, id, fireCoord);
        }
    }

    @RequestMapping(method=RequestMethod.PUT, path="/{teamuuid}/{id}")
    public void manualMoveVehicle(@PathVariable String teamuuid, @PathVariable Integer id, @RequestBody Coord fireCoord){

        deplacementService.manualMoveVehicle(teamuuid, id, fireCoord);

    }



    @RequestMapping(method=RequestMethod.GET, path="getRoute")
    public ArrayList<ArrayList<List<Double>>> manualMoveVehicle(){

        return deplacementService.getAllRoutes();

    }

    @RequestMapping(method=RequestMethod.GET, path="getDistance")
    public Float getDistance(@RequestBody List<Coord> CoordList){

        return deplacementService.getDistance(CoordList.get(0), CoordList.get(1)).floatValue();

    }


}
