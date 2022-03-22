package com.example.geneticalgorithm.GA.TSP;

public class Individual {

    private Point[] orderedLocations;

    public Individual(Point[] orderedLocations) {
        this.orderedLocations = orderedLocations;
    }

    public Point[] getOrderedLocations() {
        return orderedLocations;
    }

}
