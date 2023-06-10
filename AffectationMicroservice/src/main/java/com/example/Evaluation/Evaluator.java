package com.example.Evaluation;



import com.project.model.dto.FireDto;
import com.project.model.dto.VehicleDto;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

public class Evaluator {
    private float maxDuration = 60*60f;
    private float minDuration = 0*60f;
    private float maxEfficiency = 50;
    private float maxIntensity = 200;
    private float isRouteTooGood = 0.95f;

    private int routeCoef = 1;
    private int adaptationCoef =0;
    private int typeCoef = 1;

    private int attenuationRate=1;
    private int attenuationFactor=1;

    public Evaluator() {
    }

    //Le véhicule as-t-il assez de liquide?
    private boolean enoughLiquid(FireDto fire, VehicleDto vehicle, Float routeDistance) {
        float liquidReserve = vehicle.getLiquidQuantity();
        float liquidConsumption = vehicle.getType().getLiquidConsumption();
        float fireIntensity = fire.getIntensity();
        float efficiency = vehicle.getLiquidType().getEfficiency(fire.getType())
                                * vehicle.getType().getEfficiency();

        float neededLiquidQuantity = (fireIntensity / (efficiency * attenuationFactor))
                                        / attenuationRate;

        if (neededLiquidQuantity > liquidReserve){

            //S'il n'a pas assez de liquide mais le feu est très proche, il peut se
            // permettre de se "vider" même sans l'éteindre complètement
            if((routeEvaluation(routeDistance,vehicle)>isRouteTooGood) & (liquidReserve>0f)){
                return true;
            }
            return false;
        }
        return true;
    }

    //Le véhicule as-t-il assez de carburant?
    private boolean enoughFuel(float autonomousDistance, VehicleDto vehicle) {
        float fuelReserve = vehicle.getFuel();
        float fuelConsumption = vehicle.getType().getFuelConsumption();

        float neededFuelQuantity =  fuelConsumption * (autonomousDistance / 1000);

        if (neededFuelQuantity > fuelReserve){
            //System.out.println("invalid fuel");
            return false;
        }

        return true;
    }

    //Le véhicule as-t-il un type de liquide un minimum efficace contre le feu?
    private boolean canFight(FireDto fire, VehicleDto vehicle){
        if (efficiencyEvaluation(fire,vehicle) == 0f){
            return false;
        }
        return true;
    }

    //La solution est-elle valide
    private boolean isSolutionValid(float autonomousDistance,float routeDistance,
                                    VehicleDto vehicle, FireDto fire) {
        if (enoughFuel(autonomousDistance,vehicle) & enoughLiquid(fire,vehicle,routeDistance) & canFight(fire,vehicle)){
            System.out.println("Solution valide");
            return true;
        }
        return false;
    }

    //Evaluation du temps de trajet
    private float routeEvaluation(float distance,VehicleDto vehicle){
        float speed = vehicle.getType().getMaxSpeed();
        float duration = distance / speed;

        //Mise à jour du maximum par rapport auquel le trajet doit être évalué
        if(duration>maxDuration){
            setMaxDuration(duration);
        }

        return (maxDuration - duration) / (maxDuration - minDuration);
    }

    //Evaluation de l'efficacité du véhicule
    private float efficiencyEvaluation(FireDto fire, VehicleDto vehicle){
        float liquidEfficiency = vehicle.getLiquidType().getEfficiency(fire.getType());
        float vehicleEfficiency = vehicle.getType().getEfficiency();

        //Mise à jour du maximum par rapport auquel l'efficacité doit être évaluée
        if(vehicleEfficiency>maxEfficiency){
            setMaxEfficiency(vehicleEfficiency);
        }

        return liquidEfficiency*vehicleEfficiency/maxEfficiency;
    }

    //Evaluation de la proximité entre l'efficacité du véhicule et de l'intensité du feu
    private float adaptationEvaluation(FireDto fire, VehicleDto vehicle){
        float intensity = fire.getIntensity();
        if(intensity>maxIntensity){
            setMaxIntensity(intensity);
        }
        float fireRating = intensity/maxIntensity;
        float efficiencyRating = efficiencyEvaluation(fire,vehicle);
        return (1 - abs(fireRating - efficiencyRating));
    }

    //Evaluation sur le type de liquide du véhicule par rapport au type de feu
    private float typeEvaluation(FireDto fire, VehicleDto vehicle){
        return vehicle.getLiquidType().getEfficiency(fire.getType());
    }

    //Moyenne pondérée de toutes les évaluations.
    private float finalEvaluation(List<Float> ratingList, List<Integer> coefList){
        float totalRating = 0f;
        int totalCoef = 0;
        for(int i = 0; i<ratingList.size(); i++){
            totalRating += (ratingList.get(i)*coefList.get(i));
            totalCoef += coefList.get(i);
        }

        return totalRating/totalCoef;
    }

    //Evaluation globale
    public float globalEvaluation(Solution solution){

        //La solution est-elle valide?
        if(isSolutionValid(solution.getAutonomousDistance(), solution.getRouteDistance(),
                                            solution.getVehicle(),solution.getFire())){

            //Trajet
            float routeRating = routeEvaluation(solution.getRouteDistance(),
                                                    solution.getVehicle());

            //Adaptation
            float adaptationRating = adaptationEvaluation(solution.getFire(),
                                                        solution.getVehicle());

            //Type
            float typeRating = typeRating = typeEvaluation(solution.getFire(),solution.getVehicle());

            List<Float> ratingList = new ArrayList<>();
            List<Integer> coefList = new ArrayList<>();

            ratingList.add(routeRating);
            coefList.add(routeCoef);
            ratingList.add(adaptationRating);
            coefList.add(adaptationCoef);
            ratingList.add(typeRating);
            coefList.add(typeCoef);

            //Rating final entre 0 et 1: 0 = invalide ou très mauvaise, 1 = idéale
            float finalEva = finalEvaluation(ratingList,coefList);

            return finalEva;
        }
        return 0f;
    }


    public void setMaxEfficiency(float maxEfficiency) {
        System.out.println("Update to max efficiency: "+maxEfficiency);
        this.maxEfficiency = maxEfficiency;
    }


    public void setMaxIntensity(float maxIntensity) {
        System.out.println("Update to max intensity: "+maxIntensity);
        this.maxIntensity = maxIntensity;
    }

    public void setMaxDuration(float maxDuration) {
        this.maxDuration = maxDuration;
    }
}
