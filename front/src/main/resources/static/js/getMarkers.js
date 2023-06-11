
/**
 * @author Alexndre Ouenadio
 * @param {L.Map} map 
 * @description Generate an Object of classified fireMarkers by type.
 * @returns  fireMarkers Object with each key corresponding to a fireType | null 
 */

async function getFireMarkers(map){

    let fireMarkers = {
        "A": [],
        "B_Gasoline": [],
        "B_Alcohol": [],
        "B_Plastics": [],
        "C_Flammable_Gases": [],
        "D_Metals": [],
        "E_Electric": []
    };



    const resFire = await fetch("http://localhost:8081/fire");
    const fireDTOs = await resFire.json();


    if(fireDTOs.length > 0){

        
        
        fireDTOs.forEach(fireDTO =>{
            const fireMarker = createFireMarker(fireDTO);
            fireMarkers[fireDTO.type].push(fireMarker);
        })

        return fireMarkers;

        
    }

    return null;

    
}



/**
 * @author Alexndre Ouenadio
 * @param {L.Map} map 
 * @description Generate an Array of facilityMarker Objects.
 * @returns  facilityMarkers Array with each element corresponding to a facilityMarker | null 
 */

async function getFacilityMarkers(map){

    const resFacility = await fetch("http://localhost:8081/facility");
    const facilities = await resFacility.json();
    if(facilities.length > 0){

        const facilityMarkers = [];
        facilities.forEach(facility =>{
            let facilityMarker = createFacilityMarker(facility);
            facilityMarker.idLayer = facility.id;


            facilityMarkers.push(facilityMarker);
        })
        return facilityMarkers;
    }

    return null;


    
}

/**
 * @author Alexndre Ouenadio
 * @param {L.Map} map 
 * @description Generate an Object of classified vehicleMarkers by type.
 * @returns  vehicleMarkers Object with each key corresponding to a vehicleType | null 
 */

async function getVehicleMarkers(map){

    let vehiclesMarkers = {

        "TRUCK": [],
        "CAR": [],
        "FIRE_ENGINE": [],
        "PUMPER_TRUCK": [],
        "WATER_TENDERS": [],
        "TURNTABLE_LADDER_TRUCK": []

    };


    const resVehicle = await fetch("http://localhost:8081/vehicle");
    const vehicles = await resVehicle.json();

    if(vehicles.length >0) {

        

        vehicles.forEach(vehicle =>{

            const vehicleMarker = createVehicleMarker(vehicle);
        
            vehiclesMarkers[vehicle.type].push(vehicleMarker);

            
        })

       return vehiclesMarkers;

        
    }
    
    return null;



}