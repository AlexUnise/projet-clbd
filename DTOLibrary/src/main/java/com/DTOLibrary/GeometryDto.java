package com.DTOLibrary;

import java.util.List;

public class GeometryDto {

    //{"routes":[{"weight_name":"auto",
    // "weight":2436.643,
    // "duration":1498.068,
    // "distance":19696.902,
    // "legs":[{"via_waypoints":[],
    // "admins":[{"iso_3166_1_alpha3":"FRA","iso_3166_1":"FR"}],
    // "weight":2436.643,
    // "duration":1498.068,
    // "steps":[],
    // "distance":19696.902,
    // "summary":"M 7,
    // Boulevard Laurent Bonnevay"}],
    // "geometry":{"coordinates":[[4.828326,45.752975],[4.827331,45.751892],[4.831935,45.750629],[4.831326,45.74976],[4.831072,45.749427],[4.828901,45.746846],[4.81535,45.730195],[4.818399,45.719737],[4.821453,45.714202],[4.823806,45.712071],[4.828845,45.709304],[4.834175,45.703835],[4.836951,45.697547],[4.843423,45.693649],[4.843811,45.69279],[4.843242,45.693238],[4.843864,45.694321],[4.844675,45.694913],[4.845669,45.700219],[4.844042,45.705173],[4.84325,45.714458],[4.84376,45.715748],[4.846533,45.716074],[4.876984,45.719004],[4.895265,45.72442],[4.904358,45.738389],[4.906168,45.744139],[4.907695,45.750199],[4.907341,45.750852],[4.906361,45.74635],[4.905945,45.745843],[4.904641,45.746062],[4.904849,45.747796],[4.902226,45.748718],[4.902187,45.750318],[4.899385,45.750663],[4.899797,45.750006]],"type":"LineString"}}],
    // "waypoints":[{"distance":5.411,"name":"Rue Henri IV","location":[4.828326,45.752975]},{"distance":15.775,"name":"Impasse des Coquelicots","location":[4.899797,45.750006]}],
    // "code":"Ok",
    // "uuid":"UIdFWRskONJnNZdwdsYLU31Orw_rNavL4Btit5DX2VlVXwLOhb2V6w=="}

    private List routes;

    public GeometryDto() {
    }

    public GeometryDto(List routes) {
        this.routes = routes;
    }

    public List getDuration() {
        return routes;
    }

    public void setDuration(List routes) {
        this.routes = routes;
    }
}
