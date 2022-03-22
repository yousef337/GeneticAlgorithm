package com.example.geneticalgorithm.GA.TSP;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;

public class TSPMain {

    private final static int POPULATION_SIZE = 10;
    private static HashSet<Point> worldVertices;

    public static void main(String[] args) {
        worldVertices = new HashSet<>();
        generateRandomWorld(0, 40, 0, 40, 10);
        mainGA();

    }

    private static void mainGA(){

        Individual[] population = generateRandomPopulation();

        // Selection - elite

        // Crossover - fitness driven order crossover

        // Mutation - fitness driven shift mutation

    }

    // calculate the total walked distance
    private static int fitness(Individual individual){

        int walkedDistance = 0;
        Point previousPoint = null;

        for (Point point : individual.getOrderedLocations()) {
            if (previousPoint == null) {
                previousPoint = point;
            }else{
                walkedDistance += distance(previousPoint, point);
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

        return new Individual(worldVerticesList.toArray(Point[]::new));
    }

    //generate random world
    private static void generateRandomWorld(int minX, int maxX, int minY, int maxY, int numberOfVertices){
        Random rand = new Random();

        while (worldVertices.size() < numberOfVertices){
            Point point = new Point(rand.nextInt(maxX-minX+1) + minX, rand.nextInt(maxY-minY+1) + minY);
            if (!worldVertices.contains(point))
                worldVertices.add(point);
        }

    }


}
