//Includes of pre-exiting Leaflet classes
L.Layer.include({

    idLayer: null,
    idLayerGroup: null


});
L.LayerGroup.include({
    getLayerById : function(id) {
        let layerFound = null;

        this.eachLayer(function (layer) {
            if(layer.id === id){
                layerFound = layer;
            }
        });

        return layerFound;
    }

});
L.Map.include({


    getLayerById : function(id) {
        let layerFound = null;

        this.eachLayer(function (layer) {
            if(layer.idLayer === id){
                layerFound = layer;
                
            }
        });

        return layerFound;
    },


    getLayerGroupById : function(id) {
        let layerGroupFound = null;

        this.eachLayer(function (layer) {
            if(layer.idLayerGroup === id){
                layerGroupFound = layer;
                
            }
        });

        return layerGroupFound;
    },

    getRepereById: function (id){
        let repereFound = null;
        this.eachLayer(function(layer){
            if (layer.attachedTo == id){
                repereFound = layer;
            }
        })

        return  repereFound;

    },



    update: async function (){

        const facilityMarkers = await getFacilityMarkers(this);
        const fireMarkers = await getFireMarkers(this);
        const vehicleMarkers = await getVehicleMarkers(this);

        //Get current moving vehicles' route paths
        const resRoutes = await fetch("http://localhost:8083/getRoute")
        let routes = await resRoutes.json();
        

        if(routes.length > 0){
            console.log("L'un de vos véhicle est en mouvement... (voir cercle bleu autour du véhicule)")
            //Switch lat and long (wrong order) 
            routes = routes.map(route => {
                return route.map(coord => {
                    let temp = coord[0];
                    coord[0] = coord[1];

                    coord[1] = temp;
                    return coord;
                })
            })
            //Draw a polyline for each route
            routes.forEach(route =>{

                L.polyline(route, {color: 'red'}).addTo(this);

            })
            

        }else{
            console.log("Aucun de vos véhicules n'est en mouvement...")

            this.eachLayer((layer) => {
                // Check if the layer is a polyline
                if (layer instanceof L.Polyline) {
                
                  // Remove the polyline layer from the map
                  if(!(layer instanceof L.Rectangle)){
                    this.removeLayer(layer);
                  }
                  
                }
              });
        }

        
        //Marker refreshing if any has moved 

        const layersDB = [facilityMarkers]; //Array of arrays representing layer groups containing markers. 


        for(let type in fireMarkers){

            layersDB.push(fireMarkers[type]);

        }

        for(let type in vehicleMarkers){

            layersDB.push(vehicleMarkers[type]);

        }


        for (let i = 0; i < layersDB.length; i++){ //Total of 14 arrays in layersDB


            for(let marker of layersDB[i]){ //Looping through layerGroup i
                
                    const markerFoundInMap = this.getLayerById(marker.idLayer);

                    
                    if(markerFoundInMap){ //Marker in DB correspond to Marker present in the map
                    
                        if(!marker.getLatLng().equals(markerFoundInMap.getLatLng()) ){ //Marker was moved in DB ==> must be moved in the map

                            markerFoundInMap.setLatLng(marker.getLatLng());
                            //Update user vehicles' spot 
                            if(markerFoundInMap.facilityRefID === 36 || markerFoundInMap.facilityRefID === 3923){
                                const repereFound = this.getRepereById(markerFoundInMap.idLayer);
                                if(!repereFound){

                                    //Attach spot to user's vehicles
                                    let repere = L.circleMarker(markerFoundInMap.getLatLng(), {
                                        radius: 15,
                                        fillColor: "blue",
                                        fillOpacity: 0.3
                                        
                                    })
                    
                                    repere.attachedTo = markerFoundInMap.idLayer;
                                    repere.addTo(this);

                                }else{

                                    repereFound.setLatLng(markerFoundInMap.getLatLng());

                                }
                
                
                                
            
                            
                    
                                
                                
                                
                            }
                            
                            

                        }

                    }
                        
                        


            } 


        }
               
                    

    }


})

//Leaflet custom plugin for layer control
//Author: Alexandre Ouenadio
L.Control.SplendidLayer = L.Control.extend({
    
    _div: null,
    initialize: (options) =>{
        L.Control.prototype.initialize.call(this,options);
    },

    onAdd: (map) =>{

        this._div = L.DomUtil.create("div");
        this._div.innerHTML = `
        <div class="splendidLayer-logo-container active" id="splendidLayer-logo-container">
            <img class="splendidLayer-logo active" id="splendidLayer-logo" src="./img/layers-2x.png">
        </div>
        <div class="splendidLayer" id="splendidLayer">
            <div class="splendidLayer-section zone-travail">
                <input type="checkbox" id="zone-de-travail-checkbox">
                <p class="splendidLayer-section-title"> Zone de travail</p>
            </div>
            <div class="splendidLayer-section">
                <p class="splendidLayer-section-title">Feux</p>
                <p class="splendidLayer-section-category">Types</p>
                <ul class="splendidLayer-section-list">
                    <li><input type="checkbox" data-layer-category="fire" data-layer="1" checked> <img class="splendidLayer-section-image" src="./img/flame_A.svg"> A</li>
                    <li><input type="checkbox" data-layer-category="fire" data-layer="2" checked> <img class="splendidLayer-section-image" src="./img/flame_B_Gasoline.svg"> B_Gasoline</li>
                    <li><input type="checkbox" data-layer-category="fire" data-layer="3" checked> <img class="splendidLayer-section-image" src="./img/flame_B_Alcohol.svg"> B_Alcohol</li>
                    <li><input type="checkbox" data-layer-category="fire" data-layer="4" checked> <img class="splendidLayer-section-image" src="./img/flame_B_Plastics.svg"> B_Plastics</li>
                    <li><input type="checkbox" data-layer-category="fire" data-layer="5" checked> <img class="splendidLayer-section-image" src="./img/flame_C_Flammable_Gases.svg"> C_Flammable_Gases</li>
                    <li><input type="checkbox" data-layer-category="fire" data-layer="6" checked> <img class="splendidLayer-section-image" src="./img/flame_D_Metals.svg"> D_Metals</li>
                    <li><input type="checkbox" data-layer-category="fire" data-layer="7" checked> <img class="splendidLayer-section-image" src="./img/flame_E_Electric.svg"> E_Electric</li>
                </ul>
            </div>
            <div class="splendidLayer-section">
                <p class="splendidLayer-section-title">Véhicules</p>
                <p class="splendidLayer-section-category">Types</p>
                <ul class="splendidLayer-section-list">
                    <li><input type="checkbox" data-layer-category="vehicle" data-layer="8" checked>  <img class="splendidLayer-section-image" src="./img/vehicle-TRUCK.svg">  TRUCK</li>
                    <li><input type="checkbox" data-layer-category="vehicle" data-layer="9" checked>  <img class="splendidLayer-section-image" src="./img/vehicle-CAR.svg"> CAR</li>
                    <li><input type="checkbox" data-layer-category="vehicle" data-layer="10" checked>  <img class="splendidLayer-section-image" src="./img/vehicle-FIRE_ENGINE.svg"> FIRE_ENGINE</li>
                    <li><input type="checkbox" data-layer-category="vehicle" data-layer="11" checked>  <img class="splendidLayer-section-image" src="./img/vehicle-PUMPER_TRUCK.svg"> PUMPER_TRUCK</li>
                    <li><input type="checkbox" data-layer-category="vehicle" data-layer="12" checked>  <img class="splendidLayer-section-image" src="./img/vehicle-WATER_TENDERS.svg"> WATER_TENDERS</li>
                    <li><input type="checkbox" data-layer-category="vehicle" data-layer="13" checked>  <img class="splendidLayer-section-image" src="./img/vehicle-TURNTABLE_LADDER_TRUCK.svg"> TURNTABLE_LADDER_TRUCK</li>
                </ul>
            </div>
            </div>
        
        `;


        //Visualize working zone
        this._div.querySelector("#zone-de-travail-checkbox").addEventListener("click", ()=>{

            const zoneDeTravailFoundInMap = map.getLayerById(14);
            if(zoneDeTravailFoundInMap){

                map.removeLayer(zoneDeTravailFoundInMap);

            }else{

                drawWorkingZone(map);
            }


        })

        


        //Layer control handling
        
        this._div.querySelector("#splendidLayer-logo-container").addEventListener("click", ()=>{
            
            this._div.querySelector("#splendidLayer").classList.add('active');
            this._div.querySelector("#splendidLayer-logo-container").classList.remove("active");
            this._div.querySelector("#splendidLayer-logo").classList.remove('active');


        })

        document.addEventListener('click', (event) =>{
            const controlLayer = this._div.querySelector("#splendidLayer");
            const isClickInsideElement = controlLayer.contains(event.target) || this._div.querySelector("#splendidLayer-logo-container").contains(event.target);
          
            if (!isClickInsideElement) {
              this._div.querySelector("#splendidLayer").classList.remove('active');
              this._div.querySelector("#splendidLayer-logo-container").classList.add("active");
              this._div.querySelector("#splendidLayer-logo").classList.add('active');

            }
          });
        Array.from(this._div.querySelectorAll('[data-layer]')).forEach(input => {
            let category = null;
            
            switch(input.dataset.layerCategory){
                case 'fire':
                    category = 1;
                    break;
                case 'vehicle':
                    category = 2;
                    break;
                case 'facility':
                    category = 0;
                    break;
                default: 
                   console.log("Problème dans la détection de la catégorie...");
            }

            input.addEventListener("click", async (e) => {

                let layerGroupId = parseInt(e.currentTarget.dataset.layer);
                
       
                    //Look for the groupLayer on the map
                    //If it exists on the map, we remove it, else we add it 
                
                    const layerGroupFound = map.getLayerGroupById(layerGroupId);

                    if(layerGroupFound){

                        map.removeLayer(layerGroupFound);

                    }else{
                        
                        drawLayerById(layerGroupId,map);
                      
                        
                    }
               
     
    
            });
            
            



        })

        

        return this._div;

    },

    onRemove: (map) =>{

        //No implementation

    },


    

});

//Factory method
L.control.splendidLayer = function(opts) {
    return new L.Control.SplendidLayer(opts);
}
