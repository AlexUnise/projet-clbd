/**
 * @author Alexandre Ouenadio 
 * @description The main js script of the project. It initializes forms, modals, generates the map, draws initial layers and update the map every second.
 */

async function main(){

    //Initialization of forms
    loadCreateVehicleForm();
    loadUpdateVehicleForm();

    //Initialization of modals
    loadModal();

    //Map setup
    const map = loadMap();

    //Draw initial layers onto the map. Ids are necessary for splendidLayer

    //Facilities...
    drawLayerById(0,map);

    //Fires...
    drawLayerById(1,map);
    drawLayerById(2,map);
    drawLayerById(3,map);
    drawLayerById(4,map);
    drawLayerById(5,map);
    drawLayerById(6,map);
    drawLayerById(7,map);

    //Vehicles...
    drawLayerById(8,map);
    drawLayerById(9,map);
    drawLayerById(10,map);
    drawLayerById(11,map);
    drawLayerById(12,map);
    drawLayerById(13,map);
    
    //SplendidLayer - Custom Leaflet Plugin for control layer - Author: Alexandre Ouenadio
    const controlLayer = L.control.splendidLayer({position: "topright"});
    controlLayer.addTo(map);

    //Update map layers every second (1000ms)
    setInterval(async () =>{
        await map.update();
    }, 1000);


}

main();

      
      





