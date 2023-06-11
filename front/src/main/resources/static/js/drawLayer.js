async function drawLayerById(idLayerGroup,map){
    let markers = null;

    switch(idLayerGroup){
        

        case 0:
            markers = await getFacilityMarkers(map);
            _drawLayer(markers, null,0,map);
            break;
        case 1:
            markers = await getFireMarkers(map);
            _drawLayer(markers, "A",1,map);
            break; 
        case 2:
            markers = await getFireMarkers(map);
            _drawLayer(markers, "B_Gasoline",2,map);
            break; 
        case 3:
            markers = await getFireMarkers(map);
            _drawLayer(markers, "B_Alcohol",3,map);
            break; 
        case 4:
            markers = await getFireMarkers(map);
            _drawLayer(markers, "B_Plastics",4,map);
            break; 
        case 5:
            markers = await getFireMarkers(map);
            _drawLayer(markers, "C_Flammable_Gases",5,map);
            break; 
        case 6:
            markers = await getFireMarkers(map);
            _drawLayer(markers, "D_Metals",6,map);
            break; 
        case 7:
            markers = await getFireMarkers(map);
            _drawLayer(markers, "E_Electric",7,map);
            break; 
        case 8:
            markers = await getVehicleMarkers(map);
            _drawLayer(markers, "TRUCK",8,map);
            break; 
        case 9:
            markers = await getVehicleMarkers(map);
            _drawLayer(markers, "CAR",9,map);
            break; 
        case 10:
            markers = await getVehicleMarkers(map);
            _drawLayer(markers, "FIRE_ENGINE",10,map);
            break; 
        case 11:
            markers = await getVehicleMarkers(map);
            _drawLayer(markers, "PUMPER_TRUCK",11,map);
            break; 
        case 12:
            markers = await getVehicleMarkers(map);
            _drawLayer(markers, "WATER_TENDERS",12,map);
            break; 
        case 13:
            markers = await getVehicleMarkers(map);
            _drawLayer(markers, "TURNTABLE_LADDER_TRUCK",13,map);
            break;
        default:
            console.log("Id not recognized");
            break;



    }


}


function drawWorkingZone(map){

    let bounds = [[45.825779, 4.989201],[45.67, 4.762418]];

    const workingZone = L.rectangle(bounds, {color: "#ff0000", weight: 1});
    workingZone.idLayer = 14; 
    workingZone.addTo(map);

}


function _drawLayer(markers, type, idLayerGroup, map){

    if(type){

        //Fires,Vehicles
        if(markers[type].length > 0){
            let layerGroup = L.layerGroup(markers[type]);
            layerGroup.idLayerGroup = idLayerGroup;
    
            layerGroup.addTo(map);
        }else{
    
            console.log("There is nothing to draw for " + type);
        }
        
    }else{

        //Facility
        if(markers.length > 0){
            let layerGroup = L.layerGroup(markers);
            layerGroup.idLayerGroup = idLayerGroup;
            layerGroup.addTo(map);

        }else{

            console.log("There is nothing to draw for facility");
        }
        

    }
    
}
