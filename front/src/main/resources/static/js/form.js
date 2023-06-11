// ======================== CONSTANTS ====================== 
const FUELS = {"CAR":50, "FIRE_ENGINE": 60, "PUMPER_TRUCK": 500, "WATER_TENDERS": 500, "TURNTABLE_LADDER_TRUCK": 500, "TRUCK": 500}
const LIQUID_QTY = {"CAR":10, "FIRE_ENGINE": 50, "PUMPER_TRUCK": 1000, "WATER_TENDERS": 1000, "TURNTABLE_LADDER_TRUCK": 1000, "TRUCK": 2000} 
const FACILITIES_COORDINATES = {"36": {lat: 45.756364, lon: 4.873336}, "3923": { lat: 45.676736,lon:4.984350} };

// ======================== CREATE VEHICULE FORM ====================== 
function loadCreateVehicleForm(){

  const createVehicleform = document.getElementById('vehicleCreateForm');
  
  createVehicleform.addEventListener('submit', function(event) {
    event.preventDefault(); 
  
    const type = document.getElementById('create-vehicle-type').value;
    const crewMember = document.getElementById('create-vehicle-crew-members').value;
    const liquidType = document.getElementById('create-vehicle-liquid-type').value;
    const facilityRefID = document.getElementById('create-vehicle-facility').value;
  
    const vehicleData = {
      crewMember: crewMember,
      facilityRefID: facilityRefID,
      fuel: FUELS[type], 
      lat: facilityRefID == 36 ? FACILITIES_COORDINATES["36"]["lat"] : FACILITIES_COORDINATES["3923"]["lat"] ,
      liquidQuantity: LIQUID_QTY[type], 
      liquidType: liquidType,
      lon: facilityRefID == 36 ? FACILITIES_COORDINATES["36"]["lon"] : FACILITIES_COORDINATES["3923"]["lon"],
      type: type
    };
  
  
    fetch('http://localhost:8081/vehicle/2531ccbe-1fe7-40c8-b528-4b2953dc4166', {
      method: "POST",
      headers: {
          "Content-Type": "application/json"
      },
      body: JSON.stringify(vehicleData)
      })
      .then(response => {
          if(response.ok){
            console.log("ok")
            document.getElementById("create-ok-message").innerHTML = "Le véhicule a été créé avec succès. <br> Raffraîchissez la page pour le voir à la caserne " + facilityRefID;
            document.getElementById("create-ok-message-container").classList.add("active");
            createVehicleform.reset();

          }
      })
      .catch(error => {
          console.error("Erreur lors de la requête :", error);
      });
  
    
  });
}

// ======================== UPDATE VEHICULE FORM ====================== 
function loadUpdateVehicleForm(){

  console.log("update vehicle form does nothing for now...")

  const updateVehicleform = document.getElementById('vehicleUpdateForm');
  
  updateVehicleform.addEventListener('submit', function(event) {
  
    //TO DO
    
    
  });

}



