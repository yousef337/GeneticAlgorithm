package com.company.MaximaFunction;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class Main {

    private final static int POPULATION = 20;
    private final static int MAX_GENERATIONS = 30;
    private final static int MAX_DOMAIN = 6;
    private final static int MIN_DOMAIN = -6;
    private final static int ELITE_MAX = 5;
    private final static double mutateProbability = 0.1;

    public static void main(String[] args) {

        Random rand = new Random();
        Individual[] population = new Individual[POPULATION];

        for (int i = 0; i < POPULATION; i++)
            population[i] = createRandomIndiv(rand);

        for (int i = 0; i < MAX_GENERATIONS; i++) {
            population = run(population);
        }

        Individual[] pop = population.clone();
        Arrays.sort(pop, Comparator.comparing(Individual::getFitness).reversed());
        System.out.println(Math.round(pop[0].getFitness()*100)/100.0);
        System.out.println(pop[0].getGeneList()[0] + ", " + pop[0].getGeneList()[1]);

    }

    // generate

    private static Individual createRandomIndiv(Random rand){
        return new Individual(new double[]{(rand.nextDouble()*MAX_DOMAIN)+MIN_DOMAIN, (rand.nextDouble()*MAX_DOMAIN)+MIN_DOMAIN});
    }


    // To account for negative fitness, all values were increased by the fitness of the least individual
    // ELITE + Proportional
    private static Individual[] run(Individual[] population){
        Individual[] pop = population.clone();
        Arrays.sort(pop, Comparator.comparing(Individual::getFitness).reversed());

        Individual[] newGeneration = new Individual[POPULATION];

        // ELITE SELECTION
        System.arraycopy(pop, 0, newGeneration, 0, ELITE_MAX);

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

            newGeneration[i + ELITE_MAX] = newPopulation(pop[a], pop[b]);
            mutate(newGeneration[i + ELITE_MAX]);
        }

        return newGeneration;
    }

    private static int selectIndividual(Double[] proportions){
        int a = -1;

        Random rand = new Random();
        double prob = Math.round(rand.nextDouble()*100)/100.0;

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

    // Crossover - Linear combination
    //
    private static Individual newPopulation(Individual parent1, Individual parent2){
        /*System.out.println("--------");
        System.out.println(parent1.getFitness());
        System.out.println(parent2.getFitness());
        System.out.println("--------");*/
        Random rand = new Random();
        double alpha = rand.nextDouble();

        double gene1 = alpha*(parent1.getGeneList()[0]-parent2.getGeneList()[0])+parent2.getGeneList()[0];
        double gene2 = alpha*(parent1.getGeneList()[1]-parent2.getGeneList()[1])+parent2.getGeneList()[0];

        /*
        System.out.println("---///---");
        System.out.println(gene1);
        System.out.println(parent1.getGeneList()[0]);
        System.out.println(parent2.getGeneList()[0]);
        System.out.println("---///---");*/

        return new Individual(new double[]{gene1, gene2});
    }

    // Mutate

    private static void mutate(Individual ind){
        Random rand = new Random();
        for (int i = 0; i < ind.getGeneList().length; i++)
            if (rand.nextDouble() < mutateProbability)
                ind.changeGeneX(rand.nextDouble(),i);
    }


}