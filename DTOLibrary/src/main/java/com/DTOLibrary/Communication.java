package com.DTOLibrary;

import com.project.model.dto.Coord;
import com.project.model.dto.FacilityDto;
import com.project.model.dto.FireDto;
import com.project.model.dto.VehicleDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.json.JSONArray;
import org.json.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;



public class Communication {


    static final String URL  = "http://vps.cpe-sn.fr:8081/";

    public List<FireDto> getAllFires() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(new MediaType[]{MediaType.APPLICATION_JSON}));
        headers.setContentType(MediaType.APPLICATION_JSON);

        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<Object> fireDto = new HttpEntity<>(headers);
        ResponseEntity<List<FireDto>> facilityResponse = restTemplate.exchange(
                URL + "fires",
                HttpMethod.GET,
                fireDto,
                new ParameterizedTypeReference<List<FireDto>>() {}
        );

        return facilityResponse.getBody();
    }



    public void addVehicle(String teamuuid, VehicleDto vehicleDto){
        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<VehicleDto> requestBody = new HttpEntity<>(vehicleDto);

        ResponseEntity<VehicleDto> result
                = restTemplate.postForEntity(URL + "vehicle/" + teamuuid, requestBody, VehicleDto.class);

    }

    public void deleteVehicle(String teamuuid, String id){
        RestTemplate restTemplate = new RestTemplate();
       restTemplate.delete(URL + "vehicle/" + teamuuid + '/' + id);

    }

    public void moveVehicle(String teamuuid, Integer id, Coord coordDto, Float maxSpeed, Integer mode){

        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept",MediaType.APPLICATION_JSON_VALUE);

        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<Coord> requestBody = new HttpEntity<>(coordDto, headers);

        restTemplate.exchange("http://localhost:8083/" +teamuuid+"/"+id+"/" + maxSpeed +"/"+ mode, HttpMethod.PUT ,requestBody, Void.class);

    }


    public Float getDistance(Coord coordInit, Coord coordFinal) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        HttpEntity<Void> requestBody = new HttpEntity<>(headers);

        String URL = "https://api.mapbox.com/directions/v5/mapbox/driving/" + coordInit.getLon() + "," + coordInit.getLat() + ";" + coordFinal.getLon() + "," + coordFinal.getLat() + "?geometries=geojson&access_token=pk.eyJ1IjoicmFwaGFlbDEwIiwiYSI6ImNsaWVreDcxcDBidDIza3BkcHhncmtlYjQifQ.G6GvsuqiB3nJQ1eWRIjF5Q";

        ResponseEntity<String> response = restTemplate.exchange(URL, HttpMethod.GET, requestBody, String.class);

        // Convertir la chaîne JSON en un objet JSON
        JSONObject jsonObject = new JSONObject(response.getBody());

        // Extraire la partie "legs" de l'objet JSON
        Double distance = jsonObject.getJSONArray("routes")
                .getJSONObject(0)
                .getDouble("distance");

        return distance.floatValue();
    }

    public ArrayList<List<Double>> getRoute(Coord coordDto, VehicleDto vehicleDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        HttpEntity<Void> requestBody = new HttpEntity<>(headers);

        String URL = "https://api.mapbox.com/directions/v5/mapbox/driving/" + vehicleDto.getLon() + "," + vehicleDto.getLat() + ";" + coordDto.getLon() + "," + coordDto.getLat() + "?geometries=geojson&access_token=pk.eyJ1IjoicmFwaGFlbDEwIiwiYSI6ImNsaWVreDcxcDBidDIza3BkcHhncmtlYjQifQ.G6GvsuqiB3nJQ1eWRIjF5Q";

        ResponseEntity<String> response = restTemplate.exchange(URL, HttpMethod.GET, requestBody, String.class);

        // Convertir la chaîne JSON en un objet JSON
        JSONObject jsonObject = new JSONObject(response.getBody());

        // Extraire la partie "geometry" de l'objet JSON
        JSONObject geometryObject = jsonObject.getJSONArray("routes")
                .getJSONObject(0)
                .getJSONObject("geometry");

        JSONArray jsonArray = (geometryObject.getJSONArray("coordinates"));
        ArrayList<List<Double>> arrayList = new Gson().fromJson(jsonArray.toString(), new TypeToken<ArrayList<List<Double>>>() {}.getType());

       return arrayList;
    }



    public List<VehicleDto> getAllVehicles() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(new MediaType[]{MediaType.APPLICATION_JSON}));
        headers.setContentType(MediaType.APPLICATION_JSON);

        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<Object> vehicleDto = new HttpEntity<>(headers);
        ResponseEntity<List<VehicleDto>> vehicleResponse = restTemplate.exchange(
                URL + "vehicles",
                HttpMethod.GET,
                vehicleDto,
                new ParameterizedTypeReference<List<VehicleDto>>() {}
        );

        return vehicleResponse.getBody();
    }

    public List<VehicleDto> getOurVehicles() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(new MediaType[]{MediaType.APPLICATION_JSON}));
        headers.setContentType(MediaType.APPLICATION_JSON);

        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<Object> vehicleDto = new HttpEntity<>(headers);
        ResponseEntity<List<VehicleDto>> vehicleResponse = restTemplate.exchange(
                URL + "vehiclebyteam/2531ccbe-1fe7-40c8-b528-4b2953dc4166",
                HttpMethod.GET,
                vehicleDto,
                new ParameterizedTypeReference<List<VehicleDto>>() {}
        );

        return vehicleResponse.getBody();
    }


    public boolean updateVehicle(String teamuuid, Integer id, VehicleDto VehicleDto){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept",MediaType.APPLICATION_JSON_VALUE);

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<VehicleDto> requestBody = new HttpEntity<>(VehicleDto, headers);

        VehicleDto vehicle = getVehicleById(id);
        FacilityDto facility = getFacility(vehicle.getFacilityRefID());

        if ((facility.getLat() == vehicle.getLat()) && (facility.getLon() == facility.getLon())){
            restTemplate.exchange(URL +"vehicle/" + teamuuid + "/" + id, HttpMethod.PUT, requestBody, Void.class);
            return true;
        }
        else {
            return false;
        }


    }

    public List<FacilityDto> getAllFacilities() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(new MediaType[]{MediaType.APPLICATION_JSON}));
        headers.setContentType(MediaType.APPLICATION_JSON);

        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<Object> facilityDto = new HttpEntity<>(headers);
        ResponseEntity<List<FacilityDto>> facilityResponse = restTemplate.exchange(
                URL + "facility",
                HttpMethod.GET,
                facilityDto,
                new ParameterizedTypeReference<List<FacilityDto>>() {}
        );

        return facilityResponse.getBody();
    }

    public FacilityDto getFacility(Integer id){

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(new MediaType[]{MediaType.APPLICATION_JSON}));
        headers.setContentType(MediaType.APPLICATION_JSON);

        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<Object> facilityDto = new HttpEntity<>(headers);
        ResponseEntity<FacilityDto> facilityResponse = restTemplate.exchange(
                URL + "facility/"+ id,
                HttpMethod.GET,
                facilityDto,
                new ParameterizedTypeReference<FacilityDto>() {}
        );

        return facilityResponse.getBody();

    }

    public void fireSimulatorMoveVehicule(String teamuuid, Integer id, Coord coord){

        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept",MediaType.APPLICATION_JSON_VALUE);

        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<Coord> requestBody = new HttpEntity<>(coord, headers);

        restTemplate.exchange(URL + "/vehicle/move/" +teamuuid+"/"+id, HttpMethod.PUT ,requestBody, VehicleDto.class);
    }

    public void vehicleFreed(Integer id){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept",MediaType.APPLICATION_JSON_VALUE);

        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<Void> requestBody = new HttpEntity<>(null, headers);

        restTemplate.exchange("http://localhost:8084/"+id, HttpMethod.PUT, requestBody, Void.class);


        //org.springframework.web.client.ResourceAccessException: I/O error on PUT request for "http://localhost:8084/3954": Connection refused: connect

    }

    public VehicleDto getVehicleById(Integer id) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(new MediaType[]{MediaType.APPLICATION_JSON}));
        headers.setContentType(MediaType.APPLICATION_JSON);

        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<Object> vehicleDto = new HttpEntity<>(headers);
        ResponseEntity<VehicleDto> vehicleResponse = restTemplate.exchange(
                URL + "vehicle/" + id,
                HttpMethod.GET,
                vehicleDto,
                new ParameterizedTypeReference<VehicleDto>() {}
        );
        return vehicleResponse.getBody();
    }
}
