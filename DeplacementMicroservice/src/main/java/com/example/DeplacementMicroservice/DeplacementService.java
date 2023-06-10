package com.example.DeplacementMicroservice;

import com.DTOLibrary.*;
import com.project.model.dto.Coord;
import com.project.model.dto.FacilityDto;
import com.project.model.dto.VehicleDto;
import com.project.tools.GisTools;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


import javax.swing.text.StyledEditorKit;
import java.util.ArrayList;
import java.util.List;


@Component
@Service
public class DeplacementService {

    private final DeplacementRepository deplacementRepository;
    private final RouteRepository routeRepository;
    private final Communication communication = new Communication();

    public DeplacementService(DeplacementRepository deplacementRepository, RouteRepository routeRepository) {
        this.deplacementRepository = deplacementRepository;
        this.routeRepository = routeRepository;
    }




    public void init(String teamuuid, Boolean initialize) {
        List<VehicleDto> vehicles = communication.getOurVehicles();

        for (VehicleDto vehicle : vehicles) {
            FacilityDto facility = communication.getFacility(vehicle.getFacilityRefID());
            saveRoadMove(vehicle.getId(), teamuuid, new Coord(facility.getLon(), facility.getLat()), vehicle.getType().getMaxSpeed(), vehicle, initialize);
        }
    }


    public Float getDistance(Coord coordInit, Coord coordFinal){

        return communication.getDistance(coordInit, coordFinal);
    }

    public ArrayList<ArrayList<List<Double>>> getAllRoutes() {
        ArrayList<ArrayList<List<Double>>> allRoutesCoordinates = new ArrayList<>();

        List<Route> routes = routeRepository.findAllAsList();

        for (Route route : routes) {
            ArrayList<List<Double>> coordinates = convertToCoordinates(route.getCoordinates());
            allRoutesCoordinates.add(coordinates);
        }

        return allRoutesCoordinates;
    }

    private ArrayList<List<Double>> convertToCoordinates(List<RouteCoordinate> routeCoordinates) {
        ArrayList<List<Double>> coordinates = new ArrayList<>();
        List<Double> coordinateList = new ArrayList<>();

        for (RouteCoordinate routeCoordinate : routeCoordinates) {
            coordinateList.add(routeCoordinate.getValue());

            if (coordinateList.size() == 2) {
                coordinates.add(coordinateList);
                coordinateList = new ArrayList<>();
            }
        }
        return coordinates;
    }





    public void manualMoveVehicle(String teamuuid, Integer id, Coord coord){
        communication.fireSimulatorMoveVehicule(teamuuid, id, coord);
    }





    //Processes the deltas of Latitude and Longitude as well as the adjustment for a given line
    private LineDto lineProcess (Coord vehicleCoord, Coord fireCoord, Float maxSpeed){

        //Coord vehicleCoord = GisTools.transformCoord(vehiculeCoordUnprojected, "3857");
        //Coord fireCoord = GisTools.transformCoord(fireCoordUnprojected, "3857");


        //Get incrementation values of coordinates
        double latDiff = fireCoord.getLat() - vehicleCoord.getLat();
        double lonDiff = fireCoord.getLon() - vehicleCoord.getLon();
        System.out.println("LatDiff:" + latDiff);
        System.out.println("lonDiff:" + lonDiff);

        double L = (double) Math.sqrt(Math.pow(lonDiff, 2D) + Math.pow(latDiff, 2d));

        //double L = GisTools.computeDistance2(vehiculeCoordUnprojected, fireCoordUnprojected);

        System.out.println(maxSpeed);
        float deltaL = (maxSpeed * (7F / 3600F))/111;

        System.out.println("L:"+L);
        System.out.println("deltaL:"+deltaL);

        double deltaLat = (deltaL / L) * latDiff;
        double deltaLon = (deltaL / L) * lonDiff;
        System.out.println("deltaLat:"+deltaLat);
        System.out.println("deltaLon:"+deltaLon);

        //Get step count and rest
        int stepMax = (int)Math.floor((double)(L/deltaL));
        double adjustment = (double)((L/deltaL) - stepMax)*deltaL;

        System.out.println("stepMax:"+stepMax);
        System.out.println("adjustment:"+adjustment);

        double deltaLatAdjust = (adjustment / L) * latDiff;
        double deltaLonAdjust = (adjustment / L) * lonDiff;
        System.out.println("deltaLatAdjust:"+deltaLatAdjust);
        System.out.println("deltaLonAdjust:"+deltaLonAdjust);
        System.out.println();
        System.out.println();

        return new LineDto(deltaLon, deltaLat, stepMax, deltaLonAdjust, deltaLatAdjust);
    }





    //------------------------SAVING FUNCTIONS--------------------------------


    //Calcule et sauvegarde le deplacement par ligne dans la BDD
    public void saveLinearMove(Integer id, String teamuuid, Coord fireCoord, Float maxSpeed, VehicleDto vehicleDto, Boolean initialize){

        //Process the line
        LineDto move = lineProcess(new Coord(vehicleDto.getLon(), vehicleDto.getLat()), fireCoord, maxSpeed);

        //Add the info into the required Lists for saving, here only one
        List<Double> longitudes = new ArrayList<>(); longitudes.add(move.getDeltaLon());
        List<Double> latitudes = new ArrayList<>(); latitudes.add(move.getDeltaLat());
        List<Integer> stepsMax = new ArrayList<>(); stepsMax.add(move.getStepMax());

        List<Double> latitudesAdjust = new ArrayList<Double>(); latitudesAdjust.add(fireCoord.getLat());
        List<Double> longitudesAdjust = new ArrayList<Double>(); longitudesAdjust.add(fireCoord.getLon());

        //Save the move in database
        Deplacement deplacement = new Deplacement(teamuuid, id, maxSpeed, 2, stepsMax, longitudes, latitudes, longitudesAdjust, latitudesAdjust, fireCoord.getLon(), fireCoord.getLat(), initialize);
        deplacementRepository.save(deplacement);

    }



    //Calcule et sauvegarde le deplacement par routes dans la BDD
    public void saveRoadMove(Integer id, String teamuuid, Coord fireCoord, Float maxSpeed, VehicleDto vehicleDto, Boolean initialize){
        //Fetch route
        //[[4.828326, 45.752975], [4.827331, 45.751892], [4.831935, 45.750629], [4.831326, 45.74976], [4.831072, 45.749427], [4.828901, 45.746846], [4.81535, 45.730195], [4.818399, 45.719737], [4.821453, 45.714202], [4.823806, 45.712071], [4.828845, 45.709304], [4.834175, 45.703835], [4.836951, 45.697547], [4.843423, 45.693649], [4.843811, 45.69279], [4.843242, 45.693238], [4.843864, 45.694321], [4.844675, 45.694913], [4.845669, 45.700219], [4.844042, 45.705173], [4.84325, 45.714458], [4.84376, 45.715748], [4.846533, 45.716074], [4.876984, 45.719004], [4.895265, 45.72442], [4.904358, 45.738389], [4.906168, 45.744139], [4.907695, 45.750199], [4.907341, 45.750852], [4.906361, 45.74635], [4.905945, 45.745843], [4.904641, 45.746062], [4.904849, 45.747796], [4.902226, 45.748718], [4.902187, 45.750318], [4.899385, 45.750663], [4.899797, 45.750006]]
        ArrayList<List<Double>> coordinates = communication.getRoute(new Coord(fireCoord.getLon(), fireCoord.getLat()), vehicleDto);
        routeRepository.save(new Route(coordinates, id));
        System.out.println(coordinates);

        //Initialize the lists for the deltas of each lines
        List<Double> latitudes= new ArrayList<Double>();
        List<Double> longitudes= new ArrayList<Double>();
        List<Integer> stepsMax= new ArrayList<Integer>();
        List<Double> latitudesAdjust = new ArrayList<Double>();
        List<Double> longitudesAdjust = new ArrayList<Double>();

        //For each segment
        for (int i = 0; i<coordinates.size()-1; i++) {

            double longitudeInit = coordinates.get(i).get(0);
            double latitudeInit = coordinates.get(i).get(1);

            if (i == 0){
                longitudeInit = vehicleDto.getLon();
                latitudeInit = vehicleDto.getLat();
            }


            double longitudeFinal = coordinates.get(i+1).get(0);
            double latitudeFinal = coordinates.get(i+1).get(1);

            //Process the line
            LineDto move = lineProcess(new Coord(longitudeInit, latitudeInit), new Coord(longitudeFinal, latitudeFinal), maxSpeed);


            //Add the deltas into the required Lists
            longitudes.add(move.getDeltaLon());
            latitudes.add(move.getDeltaLat());
            //longitudesAdjust.add(move.getDeltaLonAdjust());
            longitudesAdjust.add(longitudeFinal);
            //latitudesAdjust.add(move.getDeltaLatAdjust());
            latitudesAdjust.add(latitudeFinal);

            //Add stepMax of the line
            stepsMax.add(move.getStepMax());
        }


        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println(coordinates.size());
        System.out.println("stepsMax"+ stepsMax);
        System.out.println("longitudes"+longitudes);
        System.out.println("latitudes"+latitudes);
        System.out.println("longitudesAdjust"+longitudesAdjust);
        System.out.println("latitudesAdjust"+latitudesAdjust);

        //Save the move in database
        Deplacement deplacement = new Deplacement(teamuuid, id, maxSpeed, coordinates.size(), stepsMax, longitudes, latitudes, longitudesAdjust, latitudesAdjust, fireCoord.getLon(), fireCoord.getLat(), initialize);
        if (stepsMax.size() != 1){
            deplacementRepository.save(deplacement);
        }
        else {
            //communication.vehicleFreed(deplacement.getVehiculeId());
        }

    }








    //---------------------SCHEDULED FUNCTIONS-----------------------------


    @Scheduled(fixedDelay = 1000)
    public void MoveVehicle(){

        //For all vehicle to move
        Iterable<Deplacement> deplacements = deplacementRepository.findAll();
        for (Deplacement deplacement: deplacements) {

            //Get vehicle data
            VehicleDto vehicleDto = communication.getVehicleById(deplacement.getVehiculeId());


            //Get specific current line info for the trip
            double longitude = deplacement.getDeltasLon().get(deplacement.getLineId());
            double latitude = deplacement.getDeltasLat().get(deplacement.getLineId());
            double stepMax = deplacement.getStepsMax().get(deplacement.getLineId());
            double longitudeAdjust = deplacement.getDeltasLonAdjust().get(deplacement.getLineId());
            double latitudeAdjust = deplacement.getDeltasLatAdjust().get(deplacement.getLineId());

            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println("StepsMax:"+deplacement.getStepsMax());
            System.out.println("DeltasLat"+deplacement.getDeltasLat());
            System.out.println("DeltasLon"+deplacement.getDeltasLon());
            System.out.println("LonAdjust"+longitudeAdjust);
            System.out.println("LatAdjust"+latitudeAdjust);
            System.out.println();


            System.out.println(vehicleDto.getLon());
            System.out.println(vehicleDto.getLat());
            System.out.println("Step:" + deplacement.getStep());
            System.out.println("Line:" + deplacement.getLineId());
            System.out.println("StepMax:" + stepMax);
            System.out.println("LineMax:" + deplacement.getLineIdMax());

            //If the step is the last one
            if (deplacement.getStep() == stepMax) {

                //Add adjustment
                //Coord incrementationalCoord = new Coord(vehicleDto.getLon() + longitudeAdjust, vehicleDto.getLat() + latitudeAdjust);
                Coord incrementationalCoord = new Coord(longitudeAdjust, latitudeAdjust);
                communication.fireSimulatorMoveVehicule(deplacement.teamuuid, deplacement.getVehiculeId(), incrementationalCoord);
                System.out.println("Adjustement added");


                //If the line is the last one
                if (deplacement.getLineId() == deplacement.getLineIdMax() -2){
                    System.out.println("END PROCESS");

                    //Shift manually position to match fire coords
                    //Coord finalCoords = new Coord(deplacement.finalLon, deplacement.finalLat);
                    //communication.fireSimulatorMoveVehicule(deplacement.teamuuid, deplacement.getVehiculeId(), finalCoords);
                    saveLinearMove(deplacement.getVehiculeId(), deplacement.teamuuid, new Coord(longitudeAdjust, latitudeAdjust), deplacement.getMaxSpeed(), vehicleDto, deplacement.getInitialize());
                    //Delete the deplacement instance (route also only if a route exists)
                    deplacementRepository.deleteById(deplacement.getDeplacementId());
                    if (routeRepository.findByVehicleId(deplacement.getVehiculeId()) != null){
                        routeRepository.deleteById(routeRepository.findByVehicleId(deplacement.getVehiculeId()).getRouteId());
                    }

                    //Tell the Affectation uS that the vehicle is now available for assignment
                    if (!deplacement.getInitialize()){
                        communication.vehicleFreed(deplacement.getVehiculeId());
                    }

                }

                //If there are still lines to do
                else{

                    System.out.println("step reset and next line set");

                    //Reset the steps and go to next line
                    deplacement.setStep(0);
                    deplacement.setLineId(deplacement.getLineId() +1);
                    deplacementRepository.save(deplacement);

                }

            }

            //If there are still steps to take
            else {

                //Advance normally
                Coord incrementationalCoord = new Coord(vehicleDto.getLon() + longitude, vehicleDto.getLat() + latitude);
                communication.fireSimulatorMoveVehicule(deplacement.getTeamuuid(), deplacement.getVehiculeId(), incrementationalCoord);
                System.out.println("Advance normally");


                //Go to next step
                deplacement.setStep(deplacement.getStep() + 1);
                deplacementRepository.save(deplacement);
            }

        }
    }
}
