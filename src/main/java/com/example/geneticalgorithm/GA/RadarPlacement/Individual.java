package com.example.geneticalgorithm.GA.RadarPlacement;

public class Individual {

    private final int SINGLE_RADAR_COVERAGE = 3;

    private int[][] radarLocations;


    public Individual(int[][] locations){
        radarLocations = locations;
    }

    public int getSINGLE_RADAR_COVERAGE() {
        return SINGLE_RADAR_COVERAGE;
    }

    public int[][] getRadarLocations() {
        return radarLocations;
    }
}
