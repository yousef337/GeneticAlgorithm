package com.example.geneticalgorithm.GA.RadarPlacement;

/**
 * This class represent an individual, which is the locations of the radar on the map.
 * @author Yousef Altaher
 */
public class Individual {

    private final int SINGLE_RADAR_COVERAGE = 3;

    private int[][] radarLocations;

    /**
     * This constructor construct an individual
     * @param locations radar's locations as int[][], where 1 indicate there is a radar, 0 there is not.
     */
    public Individual(int[][] locations){
        radarLocations = locations;
    }

    /**
     * This method returns the scope of the radar (its coverage)
     * @return the scope of the radar (its coverage)
     */
    public int getSINGLE_RADAR_COVERAGE() {
        return SINGLE_RADAR_COVERAGE;
    }

    /**
     * Return the locations of radars as int[][], where 1 indicate there is a radar, 0 there is not.
     * @return Return the locations of radars
     */
    public int[][] getRadarLocations() {
        return radarLocations;
    }
}
