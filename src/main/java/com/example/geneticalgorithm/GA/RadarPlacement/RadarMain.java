package com.example.geneticalgorithm.GA.RadarPlacement;

import NonUniformDistribution.NonUniformDistribution.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

/**
 * This class is the main class of performing the GA for the radar placement problem.
 * @author Yousef Altaher
 */
public class RadarMain {

    private Territories[][] map;
    private double[][] cost;
    private final int WIDTH;
    private final int HEIGHT;
    private final double RADAR_PLACEMENT_PROBABILITY = 0.00061;
    private final int POPULATION_SIZE = 20;
    private final int SELECTION_SIZE = 10;
    private final int ELITE_SIZE = 3;
    private final int MAX_GENERATIONS = 800;
    private final double MUTATE_PROBABILITY = 0.00001;
    private Individual bestIndividual;

    /**
     * This method construct a RadarMain object with the essential functionalities. Note that this method also execute
     * the algorithm.
     * @param width map's width
     * @param height map's height
     */
    public RadarMain(int width, int height) {
        WIDTH = width;
        HEIGHT = height;
        map = new Territories[width][height];
        generateMap();
        generateCosts();

        mainGAProcess();

    }

    /**
     * This method is used to execute the algorithm
     */
    private void mainGAProcess(){
        // generate individuals
        Individual[] population = generatePopulation();

        for (int i = 0; i < MAX_GENERATIONS; i++) {

            // SELECTION
            Individual[] selected = selection(population);

            // CROSSOVER
            Individual[] newGeneration = newGeneration(selected);

            //MUTATE
            for (Individual individual: newGeneration)
                mutate(individual);

            population = newGeneration;

            bestIndividual = (Individual) (Arrays.stream(population).sorted(Comparator.comparing(this::fitness)
                    .reversed()).toArray()[0]);
        }

    }

    /**
     * This method selects the individuals that will reproduce in the GA. This method uses elite with proportional
     * selection.
     * @param population the population
     * @return the selected individual
     */
    private Individual[] selection(Individual[] population){
        Individual[] selected = new Individual[SELECTION_SIZE];

        // Get The ELITES. The order of the elite does not matter
        Object[] orderedByFitness = Arrays.stream(population.clone()).sorted(Comparator.comparing(this::fitness)).skip(POPULATION_SIZE-ELITE_SIZE).toArray();

        for (int i = 0; i < ELITE_SIZE; i++)
            selected[i] = (Individual) orderedByFitness[i];

        // Proportional Selection
        Object[] populationObjectWithoutElite = Arrays.stream(population.clone()).sorted(Comparator.comparing(this::fitness).reversed()).skip(ELITE_SIZE).toArray();
        Individual[] populationWithoutElite = new Individual[populationObjectWithoutElite.length];

        for (int i = 0; i < populationObjectWithoutElite.length; i++)
            populationWithoutElite[i] = (Individual) populationObjectWithoutElite[i];


        double totalFitness = Arrays.stream(populationWithoutElite.clone()).mapToDouble(this::fitness).sum();
        double[] probabilities = Arrays.stream(populationWithoutElite.clone()).mapToDouble(this::fitness).map(i->i/totalFitness).toArray();


        for (int i = ELITE_SIZE; i < SELECTION_SIZE; i++) {
            try {
                ArrayNonuniformSelector selector = new ArrayNonuniformSelector(populationWithoutElite, probabilities);
                selected[i] = (Individual) selector.getRandomValue();

            } catch (InvalidProbabilitySetException | UnmatchedElementsAndProbabilities e) {
                System.out.println("SUM: " + Arrays.stream(probabilities).sum());
            }

        }

        return selected;
    }

    /**
     * This method facilitate the new generation, as a coordinator.
     * @param population the current generation
     * @return the new (next) generation
     */
    private Individual[] newGeneration(Individual[] population){
        Random rand = new Random();

        Individual[] newGeneration = new Individual[POPULATION_SIZE];

        for (int i = 0; i < POPULATION_SIZE; i+=2) {

            Individual parent1 = population[rand.nextInt(SELECTION_SIZE)];
            Individual parent2 = population[rand.nextInt(SELECTION_SIZE)];

            while (parent1 == parent2)
                parent2 = population[rand.nextInt(SELECTION_SIZE)];


            Individual[] offspring = breed(parent1, parent2);


            Object[] bestTwo = Arrays.stream(new Individual[]{offspring[0], offspring[1], parent1, parent2}).sorted(
                    Comparator.comparing(this::fitness).reversed()).toArray();


            newGeneration[i] = (Individual) bestTwo[0];
            newGeneration[i+1] = (Individual) bestTwo[1];

        }

        return newGeneration;
    }

    /**
     * This method perform a crossover at the middle of the genes for every column from the parents, then the offspring
     * will have the genes in a crossed way. I.e, the first offspring will have the first half from parent1 and the second
     * half from parent2, while the other offspring will have the first half from parent2 and the second half from parent1.
     *
     * @param parent1 the first parent
     * @param parent2 the second parent
     * @return an array of the offspring
     */
    private Individual[] breed(Individual parent1, Individual parent2){
        Individual[] offspring = new Individual[2];
        offspring[0] = new Individual(new int[WIDTH][HEIGHT]);
        offspring[1] = new Individual(new int[WIDTH][HEIGHT]);

        for (int i = 0; i < HEIGHT; i++) {

            System.arraycopy(parent1.getRadarLocations()[i], 0, offspring[0].getRadarLocations()[i],0,
                    parent1.getRadarLocations()[i].length/2);
            System.arraycopy(parent2.getRadarLocations()[i], parent2.getRadarLocations()[i].length/2,
                    offspring[0].getRadarLocations()[i],parent2.getRadarLocations()[i].length/2,
                    parent2.getRadarLocations()[i].length/2);

            System.arraycopy(parent2.getRadarLocations()[i], 0, offspring[1].getRadarLocations()[i],0,
                    parent1.getRadarLocations()[i].length/2);
            System.arraycopy(parent1.getRadarLocations()[i], parent1.getRadarLocations()[i].length/2,
                    offspring[1].getRadarLocations()[i],parent1.getRadarLocations()[i].length/2,
                    parent1.getRadarLocations()[i].length/2);
        }

        return offspring;
    }

    /**
     * This method mutates the radar placement of a random location based on a specific probability.
     * @param individual an individual
     */
    private void mutate(Individual individual){
        Random rand = new Random();
        for (int i = 0; i < WIDTH; i++)
            for (int j = 0; j < HEIGHT; j++)
                if (map[i][j].equals(Territories.LAND) && rand.nextDouble() < MUTATE_PROBABILITY)
                    individual.getRadarLocations()[i][j] = (individual.getRadarLocations()[i][j] + 1) % 2;



    }

    /**
     * This method returns the fitness of an individual. Every covered city will increment the fitness by one, while
     * uncovered cities will decrement it by one. Then, the cost will be subtracted from the fitness.
     * @param individual an individual
     * @return the fitness of an individual
     */
    private double fitness(Individual individual){
        int coveredCities = 0;

        for (int i = 0; i < map.length; i++)
            for (int j = 0; j < map[i].length; j++)
                if (map[i][j].equals(Territories.CITY))
                    if (isCovered(individual, i, j))
                        coveredCities++;
                    else
                        coveredCities--;


        int totalCost = 0;

        for (int i = 0; i < individual.getRadarLocations().length; i++) {
            for (int j = 0; j < individual.getRadarLocations()[i].length; j++) {
                if (individual.getRadarLocations()[i][j] == 1)
                    totalCost += cost[i][j];

            }
        }

        return coveredCities-totalCost;
    }


    /**
     * This method checks if a city is covered by a radar.
     * @param individual the individual to be checked against
     * @param cityXCoordinate city's x coordinate
     * @param cityYCoordinate city's y coordinate
     * @return true if the city is covered, false otherwise.
     */
    public boolean isCovered(Individual individual, int cityXCoordinate, int cityYCoordinate){
        int[][] radarLocations = individual.getRadarLocations().clone();

        for (int i = cityXCoordinate-10; i < cityXCoordinate + 10; i++)
            for (int j = cityYCoordinate - 10; j < cityYCoordinate + 10; j++)
                if (i < WIDTH && i > 0 && j > 0 && j < HEIGHT && radarLocations[i][j] == 1 &&
                        distance(i,j, cityXCoordinate, cityYCoordinate) <= individual.getSINGLE_RADAR_COVERAGE())
                    return true;

        return false;
    }

    /**
     * This method returns the distance between two points
     * @param x1 first point x's position
     * @param y1 first point y's position
     * @param x2 second point x's position
     * @param y2 second point y's position
     * @return the distance between thw two points
     */
    private double distance(int x1, int y1, int x2, int y2){
        return Math.sqrt(Math.pow(x2-x1,2) + Math.pow(y2-y1,2));
    }

    /**
     * This method generates random cost, bounded at 10, for radar placement.
     */
    private void generateCosts(){
        Random rand = new Random();
        cost = new double[WIDTH][HEIGHT];
        for (int i = 0; i < cost.length; i++)
            for (int j = 0; j < cost[i].length; j++)
                cost[i][j] = rand.nextInt(10);


    }

    /**
     * This method generates a population of individuals
     * @return a population of individuals
     */
    private Individual[] generatePopulation(){
        Individual[] population = new Individual[POPULATION_SIZE];
        for (int i = 0; i < POPULATION_SIZE; i++)
            population[i] = new Individual(generateRandomPlacements());

        return population;
    }

    /**
     * This method returns random placement of radars on the map
     * @return random placement of radars on the map as int[][], where 1 indicates the presence of a radar, 0 the absence
     * of it.
     */
    private int[][] generateRandomPlacements(){
        Random rand = new Random();
        int[][] radarMap = new int[WIDTH][HEIGHT];

        for (int i = 0; i < radarMap.length; i++)
            for (int j = 0; j < radarMap[i].length; j++)
                if (map[i][j].equals(Territories.LAND) && rand.nextDouble() < RADAR_PLACEMENT_PROBABILITY)
                    radarMap[i][j] = 1;

        return radarMap;

    }

    /**
     * This method generates the map's features probabilistically.
     */
    private void generateMap(){
        Territories[] territories = Territories.values();
        double[] probabilities = {.09,0.80,0.1,0.01};

        try {
            ArrayNonuniformSelector selector = new ArrayNonuniformSelector(territories, probabilities);

            for (int i = 0; i < map.length; i++)
                for (int j = 0; j < map[i].length; j++)
                    map[i][j] = (Territories) selector.getRandomValue();

        } catch (InvalidProbabilitySetException | UnmatchedElementsAndProbabilities e) {
            e.printStackTrace();
        }

    }

    /**
     * This method returns the best individual over the course of the algorithm
     * @return the best individual over the course of the algorithm
     */
    public Individual getBestIndividual(){
        return bestIndividual;
    }

    /**
     * This method returns the map of the terrain
     * @return the map of the terrain
     */
    public Territories[][] getMap() {
        return map;
    }
}
