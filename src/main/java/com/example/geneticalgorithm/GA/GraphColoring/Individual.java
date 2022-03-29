package com.example.geneticalgorithm.GA.GraphColoring;

import com.google.common.collect.Multimap;

public class Individual {

    private Multimap<String, String> coloredVertices;

    public Individual(Multimap<String, String> coloredVertices){
        this.coloredVertices = coloredVertices;
    }

    public Multimap<String, String> getColoredVertices() {
        return coloredVertices;
    }
}
