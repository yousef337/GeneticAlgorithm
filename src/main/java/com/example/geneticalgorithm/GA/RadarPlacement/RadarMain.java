package com.example.geneticalgorithm.GA.RadarPlacement;

import NonUniformDistribution.NonUniformDistribution.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class RadarMain {

    private Territories[][] map;
    private double[][] cost;
    private final int WIDTH;
    private final int HEIGHT;
    private final double RADAR_PLACEMENT_PROBABILITY = 0.001;
    private final int POPULATION_SIZE = 20;
    private final int SELECTION_SIZE = 10;
    private final int ELITE_SIZE = 3;

    public RadarMain(int width, int height) {
        WIDTH = width;
        HEIGHT = height;
        map = new Territories[width][height];
        generateMap();
        generateCosts();

        mainGAProcess();

    }

    private void mainGAProcess(){
        // generate individuals
        Individual[] population = generatePopulation();

        // SELECTION
        Individual[] selected = selection(population);

        // CROSSOVER - fitness driven uniform crossover

        //MUTATE - fitness driven binary flip

    }

    // SELECTION - ELITE + Proportional
    private Individual[] selection(Individual[] population){
        Individual[] selected = new Individual[SELECTION_SIZE];

        // Get The ELITES. The order of the elite does not matter
        Object[] orderedByFitness = Arrays.stream(population.clone()).sorted(Comparator.comparing(this::fitness)).skip(POPULATION_SIZE-ELITE_SIZE).toArray();

        for (int i = 0; i < ELITE_SIZE; i++)
            selected[i] = (Individual) orderedByFitness[i];

        // Proportional Selection
        double totalFitness = Arrays.stream(population.clone()).mapToDouble(this::fitness).sum() - Arrays.stream(population.clone()).sorted(Comparator.comparing(this::fitness)).skip(POPULATION_SIZE-ELITE_SIZE).mapToDouble(this::fitness).sum();

        for (int i = ELITE_SIZE; i < SELECTION_SIZE; i++) {
            try {

                //TODO remove the elite from the population that will be selected
                double[] probabilities = {}; //TODO
                ArrayNonuniformSelector selector = new ArrayNonuniformSelector(population, probabilities);
                selected[i] = (Individual) selector.getRandomValue();

            } catch (InvalidProbabilitySetException | UnmatchedElementsAndProbabilities e) {
                e.printStackTrace();
            }

        }

        return selected;
    }

    // Cost and coverage
    // The coverage will be the only measure in the meanwhile
    private double fitness(Individual individual){
        int coveredCities = 0;

        for (int i = 0; i < map.length; i++)
            for (int j = 0; j < map[i].length; j++)
                if (map[i][j].equals(Territories.CITY))
                    if (isCovered(individual, i, j))
                        coveredCities++;

        return coveredCities;
    }


    private boolean isCovered(Individual individual, int cityXCoordinate, int cityYCoordinate){
        int[][] radarLocations = individual.getRadarLocations().clone();

        //TODO can be improved by getting a 10*10 subset instead of iterating the whole array
        for (int i = 0; i < radarLocations.length; i++)
            for (int j = 0; j < radarLocations[i].length; j++)
                if (radarLocations[i][j] == 1 && distance(i,j, cityXCoordinate, cityYCoordinate) <= individual.getSINGLE_RADAR_COVERAGE())
                    return true;

        return false;
    }

    private double distance(int x1, int y1, int x2, int y2){
        return Math.sqrt(Math.pow(x2-x1,2) + Math.pow(y2-y1,2));
    }


    // Random cost in the meanwhile
    private void generateCosts(){
        Random rand = new Random();
        cost = new double[WIDTH][HEIGHT];
        for (int i = 0; i < cost.length; i++)
            for (int j = 0; j < cost[i].length; j++)
                cost[i][j] = rand.nextInt(200) + 200;


    }

    private Individual[] generatePopulation(){
        Individual[] population = new Individual[POPULATION_SIZE];
        for (int i = 0; i < POPULATION_SIZE; i++)
            population[i] = new Individual(generateRandomPlacements());

        return population;
    }

    private int[][] generateRandomPlacements(){
        Random rand = new Random();
        int[][] radarMap = new int[WIDTH][HEIGHT];

        for (int i = 0; i < radarMap.length; i++)
            for (int j = 0; j < radarMap[i].length; j++)
                if (map[i][j].equals(Territories.LAND) && rand.nextDouble() < RADAR_PLACEMENT_PROBABILITY)
                    radarMap[i][j] = 1;

        return radarMap;

    }


    private void generateMap(){
        Territories[] territories = Territories.values();
        double[] probabilities = {.09,0.76,0.1,0.05};
        int c = 0;
        try {
            ArrayNonuniformSelector selector = new ArrayNonuniformSelector(territories, probabilities);

            for (int i = 0; i < map.length; i++)
                for (int j = 0; j < map[i].length; j++)
                    map[i][j] = (Territories) selector.getRandomValue();

        } catch (InvalidProbabilitySetException | UnmatchedElementsAndProbabilities e) {
            e.printStackTrace();
        }

    }

    public Territories[][] getMap() {
        return map;
    }
}
