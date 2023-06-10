package com.example.AffectationMicroservice;

import com.DTOLibrary.*;
import com.example.Evaluation.Evaluator;
import com.example.Evaluation.Solution;
import com.project.model.dto.Coord;
import com.project.model.dto.FacilityDto;
import com.project.model.dto.FireDto;
import com.project.model.dto.VehicleDto;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.*;


@Component
@Service
public class AffectationService {

    private Integer mode = 3; //1 =  téléportation, 2 = rectiligne, 3 = suivre les routes
    private String affectationType = "optimale"; //"gloutonne" ou "optimale"

    private final AffectationRepository affectationRepository;

    public AffectationService(AffectationRepository affectationRepository) {
        this.affectationRepository = affectationRepository;
    }

    private final Communication communication = new Communication();
    private final Evaluator evaluator = new Evaluator();

    //Trouve une affectation dans la base de donnée par l'id de son véhicule et
    //le marque comme arrivé
    public void vehicleFreed(Integer id){

        Affectation affectation = affectationRepository.findOneByVehicleId(id).get();
        System.out.println("affectation get "+ affectation.toString());
        affectation.setArrived(true);
        System.out.println(affectationRepository.save(affectation));

    }



    //Trouve les feux auxquels un véhicule n'a pas encore été affecté
    private List<FireDto> findActiveFires(List<FireDto> fires, Iterable<Affectation> affectations) {
        ArrayList<FireDto> fireList = new ArrayList<FireDto>();

        //Pour chaque feu...
        for (FireDto fire: fires) {
            boolean notAffected = true;

            //Pour chaque affectation...
            for(Affectation affectation: affectations){

                //Est-ce que l'id du feu est égal au fireId de l'affectation?
                if((affectation.getType()=="fire")&(affectation.getDestinationId().equals(fire.getId()))){
                    notAffected = false;
                    break;
                }
            }
            if(notAffected){
                fireList.add(fire);
            }
        }

        //Pour l'affectation gloutonne, si aucun feu n'est trouvé, la méthode doit
        // retourner null
        if(affectationType == "gloutonne"){
            if(fireList.isEmpty()){
                fireList.add(null);
            }
        }
        return fireList;
    }

    //Trouve les véhicules qui ne sont pas déjà affectés et efface les affectations finies
    private List<VehicleDto> findAvailableVehicles(List<VehicleDto> vehicles, Iterable<Affectation> affectations) {
        ArrayList<VehicleDto> vehicleList = new ArrayList<VehicleDto>();

        //Pour chaque véhicule...
        for (VehicleDto vehicle: vehicles) {
            boolean notAffected = true;

            //Pour chaque affectation...
            for (Affectation affectation : affectations)

                //Est-ce que l'id du véhicule est égal au vehicleId de l'affectation?
                if (affectation.getVehicleId().equals(vehicle.getId())) {

                    //Est-il arrivé?
                    if (affectation.isArrived()) {

                        //As-t-il fini?
                        if (affectation.isFinished()) {

                            //On efface l'affectation
                            affectationRepository.deleteById(affectation.getAffectationId());
                            continue;   //Normalement, un véhicule ne peut avoir qu'une
                                        //seule affectation mais par sécurité, nous
                                        //vérifions le reste, donc "continue" au lieu de "break"
                        }
                    }
                    notAffected = false;
                    break;
                }
            if (notAffected) {
                vehicleList.add(vehicle);
            }
        }

        //Pour l'affectation gloutonne, si aucun véhicule n'est trouvé, la méthode doit
        // retourner null
        if(affectationType == "gloutonne"){
            if(vehicleList.isEmpty()){
                vehicleList.add(null);
            }
        }
        return vehicleList;
    }

    //Essaye de faire une affectation periodiquement
    @Scheduled(fixedRate = 6000)
    private void affectation(){
        if (affectationType == "gloutonne"){
            affectationGloutonne();
        }
        if (affectationType == "optimale"){
            affectationOptimale();
        }

    }

    private void affectationGloutonne(){

        //On récupère tous les feus actifs, véhicules, et affectations
        List<FireDto> fires = communication.getAllFires();
        List<VehicleDto> vehicles = communication.getOurVehicles();
        Iterable<Affectation> affectations = affectationRepository.findAll();

        //Existe-t-il un un feu actif concerné par aucune affectation actuelle?
        FireDto firstActiveFire = findActiveFires(fires,affectations).get(0);

        //Si oui...
        if(!(firstActiveFire == null)){

            //...existe-t-il un un véhicule concerné par aucune affectation actuelle?
            VehicleDto firstAvailableVehicle = findAvailableVehicles(vehicles,affectations).get(0);

            //Si oui, on déplace le véhicule au feu et on sauvegarde l'affectation.
            if(!(firstAvailableVehicle == null)){

                float vehicleMaxSpeed = firstAvailableVehicle.getType().getMaxSpeed();


                communication.moveVehicle("2531ccbe-1fe7-40c8-b528-4b2953dc4166",firstAvailableVehicle.getId(),
                        new Coord(firstActiveFire.getLon(),firstActiveFire.getLat()), vehicleMaxSpeed,mode);
                affectationRepository.save(new Affectation(firstAvailableVehicle.getId(),"fire",firstActiveFire.getId(),false,false));

        }


        }
    }

    //Cherche un véhicule à partir d'une affectation
    private VehicleDto findAffectationVehicle(Affectation affectation, List<VehicleDto> vehicles) {
        for (VehicleDto vehicle : vehicles) {
            if (vehicle.getId().equals(affectation.getVehicleId())) {
                return vehicle;
            }
        }
        return null;
    }

    //Cherche un feu à partir d'une affectation
    private FireDto findAffectationFire(Affectation affectation, List<FireDto> fires) {
        for (FireDto fire : fires){
            if (fire.getId().equals(affectation.getDestinationId())) {
                return fire;
            }
        }
        return null;
    }

    //Cherche une caserne à partir d'une affectation
    private FacilityDto findVehicleFacility(VehicleDto vehicle, List<FacilityDto> facilities){
        //System.out.println("Trouvons la facility de " + vehicle.getId());
        for(FacilityDto facility: facilities){
            //System.out.println("Facility "+facility.getId()+"?");
            //System.out.println("Celle du vehicule est "+vehicle.getFacilityRefID());
            if(facility.getId().equals(vehicle.getFacilityRefID())){
                //System.out.println("Elles sont égales");
                return facility;
            }
            //System.out.println("Elles ne sont pas égales");
        }
        //System.out.println("Pas trouvée???");
        return null;
    }

    //Met à jour les affectations, notamment marque si elles sont finies
    private void updateAffectations(Iterable<Affectation> affectations,
                                   List<VehicleDto> vehicles,List<FireDto> fires){

        //Pour chaque affectation...
        for(Affectation affectation:affectations){

            //Le véhicule est-il arrivé?
            if(affectation.isArrived()){
                VehicleDto vehicle = findAffectationVehicle(affectation,vehicles);

                //Arrivé à une caserne?
                if(affectation.getType().equals("facility")){

                    //As-t-il fini de faire le plein?
                    if((vehicle.getFuel() >= (vehicle.getType().getFuelCapacity()*0.9f))
                            & (vehicle.getLiquidQuantity()
                            >= (vehicle.getType().getLiquidCapacity()*0.9f))){

                        affectation.setFinished(true); //Fini
                    }
                }

                //Arrivé à un feu?
                if(affectation.getType().equals("fire")){
                    FireDto fire = findAffectationFire(affectation,fires);

                    //Le feu n'existe plus?
                    if(fire == null){
                        affectation.setFinished(true); //Fini
                        continue;
                    }

                    //Le feu a une intensité de 0 ou moins?
                    if((fire.getIntensity() <= 0f) | (vehicle.getLiquidQuantity() == 0f)){
                        affectation.setFinished(true); //Fini
                    }
                }
            }
        }
    }

    //Fais le tri de véhicules par ordre croissant d'efficacité
    // Il s'agit d'un tri fusion
    private List<VehicleDto> sortVehiclesByEfficiency(List<VehicleDto> vehicles){
        int listSize = vehicles.size();

        if(listSize<=1){
            return vehicles;
        }
        List<VehicleDto> sortedVehicles = new ArrayList<VehicleDto>();
        if(listSize==2) {
            float efficiency0 = vehicles.get(0).getType().getEfficiency();
            float efficiency1 = vehicles.get(1).getType().getEfficiency();

            if(efficiency0 <= efficiency1){
                sortedVehicles.add(vehicles.get(0));
                sortedVehicles.add(vehicles.get(1));
            }
            if(efficiency0 > efficiency1){
                sortedVehicles.add(vehicles.get(1));
                sortedVehicles.add(vehicles.get(0));
            }
            return sortedVehicles;
        }

        List<VehicleDto> vehicles0 = vehicles.subList(0,listSize/2);
        List<VehicleDto> vehicles1 = vehicles.subList((listSize/2),listSize);

        List<VehicleDto> sorted0 = sortVehiclesByEfficiency(vehicles0);
        List<VehicleDto> sorted1 = sortVehiclesByEfficiency(vehicles1);

        int index0 = 0;
        int index1 = 0;

        for(int i=0;i<sorted0.size()+sorted1.size();i++){
            float efficiency0 = sorted0.get(index0).getType().getEfficiency();
            float efficiency1 = sorted1.get(index1).getType().getEfficiency();
            if(efficiency0<=efficiency1){
                sortedVehicles.add(sorted0.get(index0));
                index0+=1;
                if(index0==sorted0.size()){
                    sortedVehicles.addAll(sorted1.subList(index1,sorted1.size()));
                    break;
                }
            }
            if(efficiency0>efficiency1){
                sortedVehicles.add(sorted1.get(index1));
                index1+=1;
                if(index1==sorted1.size()){
                    sortedVehicles.addAll(sorted0.subList(index0,sorted0.size()));
                    break;
                }
            }
        }

        return sortedVehicles;
    }

    //Calcule la distance parcourue pour un intinéraire de trois points
    private float totalDistance(Coord A, Coord B, Coord C){
        return communication.getDistance(A,B)+communication.getDistance(B,C);
    }

    //Calcule la distance euclidienne entre deux points
    private float rawDistance(Coord A, Coord B){
        return (float) sqrt((pow((abs(A.getLon()-B.getLon())),2)+pow((abs(A.getLat()-B.getLat())),2)));
    }

    //Calcule la distance parcourue pour un intinéraire de trois points (avec déplacement
    //    rectiligne)
    private float rawDistance3(Coord A, Coord B, Coord C){
        return rawDistance(A,B)+rawDistance(B,C);
    }

    //Trouve la meilleure solution selon les ratings
    private Solution findBestSolution(List<Solution> solutions, List<Float> ratings){
        float bestRating=0f;
        int index=0;
        for(int i = 0; i<solutions.size(); i++){
            //System.out.println(solutions.get(i).toString() +" Rating: "+ratings.get(i));
            if(ratings.get(i)>bestRating){
                bestRating = ratings.get(i);
                index = i;
            }
        }
        return solutions.get(index);
    }

    //Execute la solution en initialisant un déplacement et sauvegardant une affectation
    private void executeSolution(Solution solution){
        //System.out.println("EXECUTION");
        VehicleDto vehicle = solution.getVehicle();
        Coord coord = new Coord();
        int id = 0;
        if(solution.getType() == "fire"){
            coord = new Coord(solution.getFire().getLon(),solution.getFire().getLat());
            id = solution.getFire().getId();

        }

        if(solution.getType() == "facility"){
            coord = new Coord(solution.getFacility().getLon(),solution.getFacility().getLat());
            id = solution.getFacility().getId();
        }

        communication.moveVehicle("2531ccbe-1fe7-40c8-b528-4b2953dc4166",
                vehicle.getId(),coord,vehicle.getType().getMaxSpeed(),mode);

        Affectation affectation = new Affectation(vehicle.getId(),solution.getType(),
                                            id,false,false);
        affectationRepository.save(affectation);
    }

    //Cherche un feu par son id
    private boolean fireInFireList(FireDto fire,List<Integer> fireIds){
        for(int i=0; i<fireIds.size();i++){
            if(fireIds.get(i).equals(fire.getId())){
                System.out.println("Forbidden");
                return true;
            }
        }
        return false;
    }

    //Affectation optimale
    private void affectationOptimale(){
        System.out.println("New wave");
        //On récupère tous les feus actifs, véhicules, et affectations
        List<FireDto> fires = communication.getAllFires();
        List<VehicleDto> vehicles = communication.getOurVehicles();
        List<FacilityDto> facilities = communication.getAllFacilities();
        Iterable<Affectation> affectations = affectationRepository.findAll();

        updateAffectations(affectations,vehicles,fires); //Existe-t-il des affectations finies?

        //On ne garde que les véhicules et deux disponibles
        List<VehicleDto> availableVehicles = findAvailableVehicles(vehicles,affectations);
        List<FireDto> activeFires = findActiveFires(fires,affectations);

        //Tri des véhicules par ordre croissant d'efficacité. On laisse aux véhicules moins efficaces
        //  plus de chances de trouver quelque chose qu'il fait bien
        List<VehicleDto> sortedAvailableVehicles = sortVehiclesByEfficiency(availableVehicles);

        List<Integer> forbiddenFires = new ArrayList<Integer>(); //Feux pris par d'autres véhicules

        //Pour chaque véhicule
        for(VehicleDto vehicle: sortedAvailableVehicles){

            List<Solution> solutions = new ArrayList<Solution>();
            List<Float> solutionRatings = new ArrayList<Float>();

            //Caserne du véhicule
            FacilityDto vehicleFacility = findVehicleFacility(vehicle,facilities);

            //Solution par défaut: revenir à la caserne
            solutions.add(new Solution("facility",0f,
                    0f,vehicle,new FireDto(),vehicleFacility));
            solutionRatings.add(0f);

            //Pour chaque feu
            for(FireDto fire: activeFires){
                if(fireInFireList(fire,forbiddenFires)){
                    continue;
                }

                //Distances importantes pour les évaluations
                Coord vehicleCoord = new Coord(vehicle.getLon(),vehicle.getLat());
                Coord fireCoord = new Coord(fire.getLon(),fire.getLat());
                Coord facilityCoord = new Coord(vehicleFacility.getLon(),vehicleFacility.getLat());
                float distanceFireFacility = rawDistance(fireCoord,facilityCoord)*2;
                float distanceVehicleFire = rawDistance(vehicleCoord,fireCoord)*2;

        //-----------SOLUTION DIRECTE: ALLER VERS LE FEU DIRECTEMENT-----------

                float autonomousDistance = distanceVehicleFire + distanceFireFacility; //Distance à parcourir sans pouvoir faire le plein (cela inclu un retour à la caserne)
                float routeDitance = rawDistance(vehicleCoord,fireCoord); //Distance à parcourir pour atteindre le feu

                Solution solutionDirect = new Solution("fire",autonomousDistance,routeDitance,vehicle,
                                                        fire,vehicleFacility);
                solutions.add(solutionDirect);
                solutionRatings.add(evaluator.globalEvaluation(solutionDirect)); //Evaluation

        //-----------SOLUTION INDIRECTE: PASSER D'ABORD PAR LA CASERNE-----------

                if(!((vehicle.getFuel()>=(vehicle.getType().getFuelCapacity()/2))
                        |(vehicle.getLiquidQuantity()>=(vehicle.getType().getLiquidCapacity()/2)))){ //Eviter de revenir à la caserne "pour rien"

                    autonomousDistance = distanceFireFacility*2;
                    routeDitance = rawDistance3(vehicleCoord,facilityCoord,fireCoord);

                    //l'évaluation se fera en fonction du véhicule tel qu'il serait après un retour à la caserne: rempli à bloc
                    VehicleDto fullVehicle = new VehicleDto();
                    fullVehicle.setId(vehicle.getId());
                    fullVehicle.setType(vehicle.getType());
                    fullVehicle.setFuel(vehicle.getType().getFuelCapacity());
                    fullVehicle.setLiquidType(vehicle.getLiquidType());
                    fullVehicle.setLiquidQuantity(vehicle.getType().getLiquidCapacity());

                    Solution solutionIndirect = new Solution("facility",autonomousDistance,routeDitance,fullVehicle,
                                                        fire,vehicleFacility);
                    solutions.add(solutionIndirect);
                    solutionRatings.add(evaluator.globalEvaluation(solutionIndirect));
                }
            }

            //Execution de la meilleure solution pour le véhicule
            Solution bestSolution = findBestSolution(solutions,solutionRatings);
            if(bestSolution.getType().equals("fire")){
                forbiddenFires.add(bestSolution.getFire().getId());
            }
            executeSolution(bestSolution);
        }
    }
}
