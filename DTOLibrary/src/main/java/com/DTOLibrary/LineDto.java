package com.DTOLibrary;

public class LineDto {

    private Double deltaLon;
    private Double deltaLat;
    private Integer stepMax;
    private Double deltaLatAdjust;
    private Double deltaLonAdjust;


    public LineDto() {
    }


    public LineDto(Double deltaLon, Double deltaLat, Integer stepMax, Double deltaLatAdjust, Double deltaLonAdjust) {
        this.deltaLon = deltaLon;
        this.deltaLat = deltaLat;
        this.stepMax = stepMax;
        this.deltaLatAdjust = deltaLatAdjust;
        this.deltaLonAdjust = deltaLonAdjust;
    }


    public Double getDeltaLon() {
        return deltaLon;
    }

    public void setDeltaLon(Double deltaLon) {
        this.deltaLon = deltaLon;
    }

    public Double getDeltaLat() {
        return deltaLat;
    }

    public void setDeltaLat(Double deltaLat) {
        this.deltaLat = deltaLat;
    }

    public Integer getStepMax() {
        return stepMax;
    }

    public void setStepMax(Integer stepMax) {
        this.stepMax = stepMax;
    }

    public Double getDeltaLatAdjust() {
        return deltaLatAdjust;
    }

    public void setDeltaLatAdjust(Double deltaLatAdjust) {
        this.deltaLatAdjust = deltaLatAdjust;
    }

    public Double getDeltaLonAdjust() {
        return deltaLonAdjust;
    }

    public void setDeltaLonAdjust(Double deltaLonAdjust) {
        this.deltaLonAdjust = deltaLonAdjust;
    }
}
