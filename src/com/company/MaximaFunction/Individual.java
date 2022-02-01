package com.company.MaximaFunction;

/**
 * Individual class represent an individual in the population.
 */

public class Individual {

    // BASE is created to avoid the complexity of dealing with negative fitness values as they
    // can be generated from the fitness function
    private static final int BASE = 10;
    private double fitness = 0;
    private double[] geneList;

    /**
     *
     * @param geneList the genes of an individual
     */
    public Individual(double[] geneList){
        this.geneList = geneList.clone();
        fitness = fitnessFunction(geneList);
    }

    /**
     * This method calculate the fitness of an individual.
     * @param gen genes array
     * @return the fitness of an individual
     */
    private static double fitnessFunction(double[] gen){
        return BASE + Math.round((Math.sin(gen[0])+Math.cos(gen[1]))*100)/100.0;
    }

    /**
     * This method returns a clone of the gene array
     * @return A clone of the gene array
     */
    public double[] getGeneList() {
        return geneList.clone();
    }

    /**
     * This method is implemented to simulate the mutation in a gene by a specific numeric value
     * This method will not have any action of the specified positional gene does not exist
     * @param margin the quantitative numerical
     * @param gene the position of the gene
     */
    public void changeGeneX(double margin, int gene){
        if (gene < geneList.length)
            geneList[gene] += margin;
    }

    /**
     * Return the fitness of the individual
     * @return fitness of an individual
     */
    public double getFitness(){
        return fitness;
    }

}
