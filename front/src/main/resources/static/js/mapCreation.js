
/**
 * @author Alexandre Ouenadio
 * @description Set up the map and return it
 * @returns map (Leaflet L.Map) 
 */

function loadMap(){

    //Generation of the map
    const map = L.map('map', {doubleClickZoom: false});
    map.setView([45.746, 4.87], 14); 
    
    //Base layer (OpenStreetMap)
    const osm = L.tileLayer('https://cartodb-basemaps-{s}.global.ssl.fastly.net/light_all/{z}/{x}/{y}.png', {
        maxZoom: 19,
        attribution: '&copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors, &copy; <a href="https://carto.com/attribution/">CARTO</a>'
    });
    osm.addTo(map);
    
    //Map event listeners
    
    //It allows user to open the update modal when they click on the update button of a popup.
    const addModalOpenToUpdateBtn = (e) =>{
    
            map.closePopup();
            //Recherche du modal associé
            const modal = document.querySelector(e.target.dataset.modalOpen);
            document.getElementById("update-vehicle-to-be-upadated").textContent = e.target.dataset.vehicleId;
            overlay.classList.add('active');
            modal.classList.add("active");
            
    
    }
    
    //It allows user to delete a vehicule when they click on the delete button of their vehicle' popup.
    const deleteVehicule = async (e) =>{
    
        map.closePopup();
    
        const id = e.currentTarget.dataset.vehicleId;
        const res = await fetch(`http://localhost:8081/vehicle/2531ccbe-1fe7-40c8-b528-4b2953dc4166/${id}`,{method: "DELETE"})
        if(res.ok){
    
            window.alert(`Véhicule ${id} supprimé!`);
            map.removeLayer(map.getLayerById(parseInt(id)));
        }
        
    
    
    }
    
    map.on('popupopen', () =>{
    
    
        //UPDATE BUTTON
        const updateBtn = document.querySelector(".map-popup-button-container [data-modal-open]");
        if(updateBtn){
            updateVehicleListener = updateBtn.addEventListener('click', addModalOpenToUpdateBtn, {once: true});
        }
    
        //DELETE BTN
        const deleteBtn = document.querySelector(".map-popup-button-container [data-delete-vehicle]");
        if(deleteBtn){
            deleteBtn.addEventListener('click', deleteVehicule, {once: true});
        }
    
    });

    console.log("Map loaded");

    return map;

}

