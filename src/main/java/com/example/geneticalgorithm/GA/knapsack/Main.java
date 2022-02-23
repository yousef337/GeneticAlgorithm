package com.example.geneticalgorithm.GA.knapsack;

import java.util.*;

public class Main {

    private final static int POPULATION = 50;
    private final static int SELECTED = 15;
    private final static int ELITE_MAX = 3;
    private final static int MAX_GENERATIONS = 15;
    private final static double MUTATE_PROBABILITY = 0.1;
    private static final double KNAPSACK_WEIGHT = 15;
    private final static Random RAND = new Random();
    private static Item[] items;

    /**
     * The main method is used to direct the GA algorithm
     * @param args args - not used
     */
    public static void main(String[] args) {
        items = new Item[10];

        double[][] info = {{43,1.5},{46,2.6},{34, 6.4},{43, 5.4},{34, 2.4},{30, 5.6},{36, 4.3},{34, 4.5},{24, 0.6},{12, 0.9}};

        for (int i = 0; i < items.length; i++)
            items[i] = new Item(info[i][0], info[i][1]);

        // Generate population

        int[][] population = new int[POPULATION][items.length];

        for (int i = 0; i < POPULATION; i++)
            population[i] = generateIndividual();

        for (int generation = 0; generation < MAX_GENERATIONS; generation++) {

            // Select (ELITE + SUS)

            population = selection(population);

            // Crossover

            int[][] newGeneration = new int[POPULATION][items.length];

            for (int i = 0; i < POPULATION; i += 2) {

                int[] p1 = population[RAND.nextInt(SELECTED)];
                int[] p2 = population[RAND.nextInt(SELECTED)];

                while (p1 == p2) {
                    p2 = population[RAND.nextInt(SELECTED)];
                }

                int[][] crossOvered = crossover(p1, p2);
                while (!isValid(crossOvered[0]) || !isValid(crossOvered[1])) {
                    p1 = population[RAND.nextInt(SELECTED)];
                    p2 = population[RAND.nextInt(SELECTED)];

                    while (p1 == p2) {
                        p2 = population[RAND.nextInt(SELECTED)];
                    }
                    crossOvered = crossover(p1, p2);
                }

                newGeneration[i] = crossOvered[0];
                newGeneration[i + 1] = crossOvered[1];
            }

            // Mutation

            for (int[] i : newGeneration)
                if (MUTATE_PROBABILITY <= RAND.nextDouble())
                    mutate(i);

            population = newGeneration;

            // Get the best individual from each generation

            System.out.println(Arrays.stream(population.clone()).mapToDouble(Main::fitness).max().orElse(-1));

        }

    }

    /**
     * This method perform a single mutation to an individual
     * @param individual the individual to be mutated
     */
    private static void mutate(int[] individual){
        int randomPosition = RAND.nextInt(items.length);
        individual[RAND.nextInt(items.length)] = (individual[randomPosition]+1)%2;
        if (!isValid(individual))
            individual[RAND.nextInt(items.length)] = (individual[randomPosition]+1)%2;

    }

    /**
     * This method perform crossover to the parents' genes at the middle to generate the offspring
     * @param parent1 the first parent
     * @param parent2 the second parent
     * @return the new offspring, in an array
     */
    private static int[][] crossover(int[] parent1, int[] parent2){
        int[][] offspring = new int[2][items.length];
        int splittingPoint = parent1.length/2;


        System.arraycopy(parent1, 0, offspring[0], 0, splittingPoint);
        System.arraycopy(parent2, splittingPoint, offspring[0], splittingPoint, splittingPoint);


        System.arraycopy(parent2, 0, offspring[1], 0, splittingPoint);
        System.arraycopy(parent1, splittingPoint, offspring[1], splittingPoint, splittingPoint);


        return offspring;
    }

    /**
     * This method uses elite selection with stochastic universal sampling (SUS) in order to select from the population
     * @param population an array of individuals
     * @return the selected individual for the next step in the GA algorithm
     */
    private static int[][] selection(int[][] population){
        int[][] selectedPopulation = new int[SELECTED][items.length];

        double[] fitness = Arrays.stream(population.clone()).mapToDouble(Main::fitness).toArray();
        double[] eliteFitness = Arrays.stream(fitness.clone()).sorted().skip(POPULATION-ELITE_MAX).toArray();

        // ELITE
        for (int i = ELITE_MAX-1; i >= 0; i--){
            //find the index of the element
            int index = -1;
            for (int j = 0; j < fitness.length; j++) {
                if (fitness[j] == eliteFitness[i]){
                    index = j;
                    fitness[j] = 0;//To resolve the special case when best 2/N fitness are equal
                    break;
                }
            }
            selectedPopulation[i] = population[index];
        }


        // SUS - Excluding the elites
        double fitnessSum = Arrays.stream(fitness.clone()).sum();
        double[] probabilityArray = Arrays.stream(fitness.clone()).map(i->i/fitnessSum).toArray();
        double choosingInterval = 1.0/ (SELECTED-ELITE_MAX);

        double choosingSum = choosingInterval;
        double probabilitySum = 0;
        int chosen = 0;

        for (int i = 0; i < population.length; i++){
            // The second conditional case is to account for imprecise representation, i.e, 1 != 0.9999999999
            if (choosingSum <= probabilitySum+probabilityArray[i] || (choosingSum-(probabilitySum+probabilityArray[i]) < 1.0/1000)){
                selectedPopulation[chosen+ELITE_MAX] = population[i];
                choosingSum += choosingInterval;
                chosen++;
            }
            probabilitySum += probabilityArray[i];
        }

        return selectedPopulation;
    }

    /**
     *
     * This method calculate and returns individual's fitness
     *
     * @param individual an individual
     * @return individual's fitness
     */
    private static double fitness(int[] individual){
        double sum = 0;
        for (int i = 0; i < individual.length; i++)
            sum += individual[i] * items[i].getPrice();

        return sum;
    }

    /**
     * This method generate a random individual
     * @return an individual
     */
    private static int[] generateIndividual(){
        int[] individual = new int[10];

        while (!isValid(individual)) {
            for (int i = 0; i < 10; i++)
                individual[i] = Math.round(RAND.nextInt(2));
        }

        return individual;
    }

    /**
     * This method is intended to check if an individual is a valid one or not
     * @param individual an individual
     * @return true if it is valid, false otherwise.
     */
    private static boolean isValid(int[] individual){

        double sum = 0;
        for (int i = 0; i < individual.length; i++)
            sum += individual[i] * items[i].getWeight();

        // sum had to be grater than 0 as an empty knapsack is not valid
        return sum <= KNAPSACK_WEIGHT && sum > 0;
    }

}
