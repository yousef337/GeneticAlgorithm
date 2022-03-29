package com.example.geneticalgorithm.GA.TSP;

import java.util.*;

public class TSPMain {

    private final static int POPULATION_SIZE = 10;
    private final static int MAX_GENERATION = 100;
    private final static int TOTAL_SELECTION_SIZE = 10;
    private final static int ELITE_SELECTION_SIZE = 10;
    private final static double MUTATE_PROBABILITY = 0.1;
    private static HashSet<Point> worldVertices;

    public static void main(String[] args) {
        worldVertices = new HashSet<>();
        generateRandomWorld(0, 45000, 0, 45000, 200);
        mainGA();

    }

    private static void mainGA(){

        Individual[] population = generateRandomPopulation();


        for (int i = 0; i < MAX_GENERATION; i++) {
            // Selection - elite
            Individual[] selectedPopulation = selection(population);

            // Crossover - fitness driven order crossover
            Individual[] newGeneration = crossover(selectedPopulation);

            // Mutation - fitness driven shift mutation
            for (Individual individual: newGeneration)
                mutate(individual);

            population = newGeneration;

            System.out.println(Arrays.stream(population).mapToInt(TSPMain::fitness).min().getAsInt());
        }
    }


    private static Individual[] crossover(Individual[] population){
        Random rand = new Random();

       Individual[] newGeneration = new Individual[POPULATION_SIZE];

        for (int i = 0; i < POPULATION_SIZE; i+=2) {

           Individual parent1 = population[rand.nextInt(TOTAL_SELECTION_SIZE)];
           Individual parent2 = population[rand.nextInt(TOTAL_SELECTION_SIZE)];

           while (parent1 == parent2)
               parent2 = population[rand.nextInt(TOTAL_SELECTION_SIZE)];


           Individual[] offspring = breed(parent1, parent2);


            Object[] bestTwo = Arrays.stream(new Individual[]{offspring[0], offspring[1], parent1, parent2})
                    .sorted(Comparator.comparing(TSPMain::fitness)).toArray();

            newGeneration[i] = (Individual) bestTwo[0];
            newGeneration[i+1] = (Individual) bestTwo[1];

        }

        return newGeneration;
    }

    private static Individual[] breed(Individual parent1, Individual parent2){
        Individual[] offspring = new Individual[2];
        Individual[] parents = {parent1, parent2};

        Random rand = new Random();
        int startPosition = rand.nextInt(parent1.getOrderedLocations().size()/2);
        int endPosition = startPosition + rand.nextInt(parent1.getOrderedLocations().size()-startPosition);


        for (int i = 0; i < offspring.length ; i++) {

            Point[] points = new Point[parent1.getOrderedLocations().size()];

            System.arraycopy(parents[(i+1)%2].getOrderedLocations().toArray(Point[]::new), startPosition, points, startPosition, endPosition - startPosition);

            for (int j = 0; j < points.length; j++) {
                if (points[j] == null) {
                    // find a valid point
                    for (Point point : parents[i].getOrderedLocations()){
                        if (!contains(points, point)){
                            points[j] = point;
                            break;
                        }
                    }

                }
            }

            offspring[i] = new Individual(new ArrayList<>(List.of(points)));

        }

        return offspring;
    }


    private static boolean contains(Point[] points, Point point){
        for (Point p: points)
            if (p != null && p.equals(point))
                return true;

        return false;
    }


    private static Individual[] selection(Individual[] population){
        Individual[] selected = Arrays.stream(population)
                .sorted(Comparator.comparing(TSPMain::fitness))
                .skip(POPULATION_SIZE-ELITE_SELECTION_SIZE)
                .toArray(Individual[]::new);

        return selected;
    }

    private static void mutate(Individual individual){
        Random rand = new Random();
        int startShift = rand.nextInt(individual.getOrderedLocations().size()/2);
        if (individual.getOrderedLocations().size()-startShift-1 <= 0)
            return;

        int endShift = startShift + rand.nextInt(individual.getOrderedLocations().size()-startShift-1);

        if (rand.nextDouble() < MUTATE_PROBABILITY){
            Point shifted = individual.getOrderedLocations().remove(startShift);
            individual.getOrderedLocations().add(endShift, shifted);
        }

    }

    // calculate the total walked distance
    private static int fitness(Individual individual){

        int walkedDistance = 0;
        Point previousPoint = null;

        for (Point point : individual.getOrderedLocations()) {
            if (previousPoint == null)
                previousPoint = point;
            else {
                walkedDistance += distance(previousPoint, point);
                previousPoint = point;
            }

        }

        return walkedDistance;

    }

    private static double distance(Point a, Point b){
        return Math.sqrt(Math.pow(a.getX() - b.getX(),2) + Math.pow(a.getY() - b.getY(),2));
    }

    private static Individual[] generateRandomPopulation(){
        Individual[] population = new Individual[POPULATION_SIZE];
        for (int i = 0; i < population.length; i++)
            population[i] = generateRandomIndividual();

        return population;
    }

    private static Individual generateRandomIndividual(){
        //Used to avoid the ordering by hashcode in sets
        ArrayList<Point> worldVerticesList = new ArrayList<>(worldVertices);
        Collections.shuffle(worldVerticesList);

        return new Individual(worldVerticesList);
    }

    //generate random world
    private static void generateRandomWorld(int minX, int maxX, int minY, int maxY, int numberOfVertices){
        Random rand = new Random();

        while (worldVertices.size() < numberOfVertices){
            Point point = new Point(rand.nextInt(maxX-minX+1) + minX, rand.nextInt(maxY-minY+1) + minY);
            worldVertices.add(point);
        }

    }


}
