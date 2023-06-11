
/**
 * @author Alexandre Ouenadio
 * @param {Object} fireDTO passed by the fireSimulator API response
 * @description Generate a fireMarker from a fireDTO 
 * @returns fireMarker (Leaflet L.Marker) 
 */
function createFireMarker(fireDTO) {

    const scale = 0.7;

    const fireIcon = L.icon({
        iconUrl: `./img/flame_${fireDTO.type}.svg`,
        shadowUrl: null,
    
        iconSize:     [32 * scale,32 * scale], // size of the icon
        shadowSize:   null, // size of the shadow
        iconAnchor:   [16 * scale, 16 * scale], // point of the icon which will correspond to marker's location
        shadowAnchor: null,  // the same for the shadow
        popupAnchor:  [-3, -10] // point from which the popup should open relative to the iconAnchor
    });

    const fireMarker = L.marker([fireDTO.lat, fireDTO.lon], {
        icon: fireIcon 
    });

    fireMarker.bindPopup(`
        <div class="map-popup">
            <h3 class="map-popup-title">${fireDTO.type}</h3> 
            <div class="map-popup-info">
                <p><span class="map-popup-field">Intensity: </span> ${fireDTO.intensity}</p> 
                <p><span class="map-popup-field">Range:</span> ${fireDTO.range}</p> 
            </div>
        </div>
   `);
    fireMarker.idLayer = fireDTO.id;
    //Return Fire marker
    return fireMarker;
}

/**
 * @author Alexandre Ouenadio
 * @param {Object} facilityDTO passed by the fireSimulator API response
 * @description Generate a facilityMarker from a facilityDTO 
 * @returns facilityMarker (Leaflet L.Marker) 
 */

function createFacilityMarker(facilityDTO) {

    const scale = 1.2;

    const facilityIcon = L.icon({
        iconUrl: "./img/fire-station.svg" ,
        shadowUrl: null,
        iconSize:     [32 * scale ,32 * scale], 
        shadowSize:   null, 
        iconAnchor:   [16 * scale, 16 * scale], 
        shadowAnchor: null,  
        popupAnchor:  [-3, -10] 
    });

    const facilityMarker = L.marker([facilityDTO.lat, facilityDTO.lon], {
        icon: facilityIcon
    });

    facilityMarker.bindPopup(`
        <div class="map-popup">
            <h3 class="map-popup-title">${facilityDTO.name}</h3> 
            <div class="map-popup-info">
                <p><span class="map-popup-field">Id: </span> ${facilityDTO.id}</p> 
                <p><span class="map-popup-field">Capacité maximale en véhicule: </span> ${facilityDTO.maxVehicleSpace}</p> 
                <p><span class="map-popup-field"> Capacité maximale en pompier: </span> ${facilityDTO.peopleCapacity}</p> 
            </div>
        </div>
    
    `);
    
    return facilityMarker;

}

/**
 * @author Alexandre Ouenadio
 * @param {Object} vehicleDTO passed by the fireSimulator API response
 * @description Generate a vehicleMarker from a vehicleDTO 
 * @returns vehicleMarker (Leaflet L.Marker)
 */
function createVehicleMarker(vehicleDTO) {

    const scale = 0.9;

    const camionIcon = L.icon({
        iconUrl: `./img/vehicle-${vehicleDTO.type}.svg`,
        shadowUrl: null,
    
        iconSize:     [94.2 * scale ,32.39 * scale], 
        shadowSize:   null, 
        iconAnchor:   [47.1 * scale, 16.5 * scale],
        shadowAnchor:  null,  
        popupAnchor:  [-3, -10] 
    });

    const vehicleMarker = L.marker([vehicleDTO.lat, vehicleDTO.lon], {
        icon: camionIcon,
         draggable: false
    });

    vehicleMarker.bindPopup(`
        <div class="map-popup">
            <h3 class="map-popup-title">${vehicleDTO.type}</h3>
            <div class="map-popup-info">
            <p><span class="map-popup-field">Id: </span> ${vehicleDTO.id}</p> 
                <p><span class="map-popup-field">Type de liquide: </span> ${vehicleDTO.liquidType}</p> 
                <p><span class="map-popup-field"> Quantité de liquide: </span> ${vehicleDTO.liquidQuantity} L</p> 
                <p><span class="map-popup-field">Carburant restant:</span> ${vehicleDTO.fuel} L</p> 
                <p><span class="map-popup-field">Nombre de pompiers:</span> ${vehicleDTO.crewMember}</p> 
                <p><span class="map-popup-field">FacilityRefID:</span> ${vehicleDTO.facilityRefID}</p>
            </div>
            ${ 
                vehicleDTO.facilityRefID === 36 ||  vehicleDTO.facilityRefID === 3923 ? 
                `
                <div class="map-popup-button-container">
                    <button class="map-popup-button" data-modal-open="#modal-vehicleUpdate" data-vehicle-id=${vehicleDTO.id}>Modifier</button>
                    <button class="map-popup-button" data-delete-vehicle data-vehicle-id=${vehicleDTO.id}>Supprimer</button>
                </div>
                ` 
                : ""
            }
        </div>
    `,{maxWidth: 800});

    vehicleMarker.idLayer = parseInt(vehicleDTO.id);

    vehicleMarker.facilityRefID = parseInt(vehicleDTO.facilityRefID);

    return vehicleMarker;
}
