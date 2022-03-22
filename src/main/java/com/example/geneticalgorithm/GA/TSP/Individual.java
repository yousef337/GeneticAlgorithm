package com.example.geneticalgorithm.GA.TSP;

import java.util.ArrayList;

public class Individual {

    private ArrayList<Point> orderedLocations;

    public Individual(ArrayList<Point> orderedLocations) {
        this.orderedLocations = orderedLocations;
    }

    public ArrayList<Point> getOrderedLocations() {
        return orderedLocations;
    }

}
