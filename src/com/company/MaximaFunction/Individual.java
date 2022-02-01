package com.company.MaximaFunction;

public class Individual {

    private double fitness = 0;
    private double[] geneList;

    private static double fitnessFunction(double[] gen){
        // This function is randomly created
        return 10 + Math.round((Math.sin(gen[0])+Math.cos(gen[1]))*100)/100.0;
    }

    public double[] getGeneList() {
        return geneList.clone();
    }

    public void changeGeneX(double margin, int gene){
        if (gene < geneList.length)
            geneList[gene] += margin;
    }

    public Individual(double[] geneList){
        this.geneList = geneList.clone();
        fitness = fitnessFunction(geneList);
    }

    public double getFitness(){
        return fitness;
    }

}
