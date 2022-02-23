package com.example.geneticalgorithm.GA.MaximaFunction;


import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class Main {

    private final static int POPULATION = 20;
    private final static int MAX_GENERATIONS = 30;
    private final static int MAX_DOMAIN = 6;
    private final static int MIN_DOMAIN = -6;
    private final static int ELITE_MAX = 5;
    private final static double MUTATE_PROBABILITY = 0.1;
    private final static Random RAND = new Random();

    /**
     * the method that is used to run the program
     * @param args args
     */
    public static void main(String[] args) {

        Individual[] population = new Individual[POPULATION];

        for (int i = 0; i < POPULATION; i++)
            population[i] = createRandomIndividual();

        for (int i = 0; i < MAX_GENERATIONS; i++) {
            population = run(population);
        }

        Individual[] pop = population.clone();
        Arrays.sort(pop, Comparator.comparing(Individual::getFitness).reversed());
        System.out.println(Math.round(pop[0].getFitness()*100)/100.0);
        System.out.println(pop[0].getGeneList()[0] + ", " + pop[0].getGeneList()[1]);

    }

    /**
     * This method creates a random individual
     * @return a random individual
     */
    private static Individual createRandomIndividual(){
        return new Individual(new double[]{(RAND.nextDouble()*MAX_DOMAIN)+MIN_DOMAIN, (RAND.nextDouble()*MAX_DOMAIN)+MIN_DOMAIN});
    }



    // ELITE + Proportional

    /**
     * This method is used to generate the new generation
     * @param population the population
     * @return a nre generation
     */
    private static Individual[] run(Individual[] population){
        Individual[] pop = population.clone();
        Arrays.sort(pop, Comparator.comparing(Individual::getFitness).reversed());

        Individual[] newGeneration = new Individual[POPULATION];

        // Elite selection
        System.arraycopy(pop, 0, newGeneration, 0, ELITE_MAX);

        // Proportional selection
        double totalFitness = Math.round(Arrays.stream(pop).map(Individual::getFitness).reduce((double) 0, Double::sum)*100)/100.0;
        Double[] fitnessArray = Arrays.stream(pop).map(Individual::getFitness).map(i -> i/totalFitness).toArray(Double[]::new);

        for (int i = 0; i < POPULATION-ELITE_MAX; i++) {
            int a = selectIndividual(fitnessArray);
            int b = selectIndividual(fitnessArray);

            while (a == b) {
                a = selectIndividual(fitnessArray);
                b = selectIndividual(fitnessArray);
            }

            // mutate

            newGeneration[i + ELITE_MAX] = newIndividual(pop[a], pop[b]);
            mutate(newGeneration[i + ELITE_MAX]);
        }

        return newGeneration;
    }

    /**
     * This method select a random, based on their probability, individual from the population
     * @param proportions an array that contains the probability of each individual in a sorted way that align with fitness values of the population.
     * @return an integer that represents the selected individual's position
     */
    private static int selectIndividual(Double[] proportions){
        int a = -1;

        double prob = Math.round(RAND.nextDouble()*100)/100.0;

        int counter = 0;
        double sum = proportions[0];

        while (a == -1){
            if (prob < sum)
                a = counter;
            sum += proportions[counter];
            counter++;
        }

        return a;
    }

    /**
     * This method returns an offspring of two parents. The applied methodology uses linear combination
     * in order to create the new gene
     *
     * @param parent1 the first parent
     * @param parent2 the second parent
     * @return the new child
     */
    private static Individual newIndividual(Individual parent1, Individual parent2){

        double alpha = RAND.nextDouble();

        double gene1 = alpha*(parent1.getGeneList()[0]-parent2.getGeneList()[0])+parent2.getGeneList()[0];
        double gene2 = alpha*(parent1.getGeneList()[1]-parent2.getGeneList()[1])+parent2.getGeneList()[0];

        return new Individual(new double[]{gene1, gene2});
    }

    /**
     * This method simulate the mutation of an individual in a random way
     * @param ind an individual
     */
    private static void mutate(Individual ind){
        for (int i = 0; i < ind.getGeneList().length; i++)
            if (RAND.nextDouble() < MUTATE_PROBABILITY)
                ind.changeGeneX(RAND.nextDouble(),i);
    }


}