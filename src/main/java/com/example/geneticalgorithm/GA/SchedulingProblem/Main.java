package com.example.geneticalgorithm.GA.SchedulingProblem;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class Main {

    private final static int POPULATION_SIZE = 20;
    private final static int MAX_GENERATIONS = 15;
    private static final Random RAND = new Random();
    private static final int MAX_FITNESS = maximumFitness();
    private static final int TOURNAMENT_SELECTION_GROUP_SIZE = 4;
    private static final int SELECTION_SIZE = 10;
    private static final int ELITE_SIZE = 3;

    /**
     * This method calculates the maximum possible fitness for a schedule.
     * @return the maximum & targeted fitness.
     */
    private static int maximumFitness(){
        return Individual.getDAYS()*Individual.getShiftsPerDays() + Individual.getEMPLOYEES() * Individual.getDAYS() + Individual.getEMPLOYEES() * (Individual.getDAYS()-1);
    }

    public static void main(String[] args) {
        System.out.println("Max Fitness: " + MAX_FITNESS);

        Individual[] population = new Individual[POPULATION_SIZE];

        for (int i = 0; i < POPULATION_SIZE; i++)
            population[i] = generateSchedule();

        // ELITE tournament

        Individual[] selected = eliteTournament(population);

        // Crossover ELITE
        Individual[] newGeneration = crossover(selected);

        // 1 bit flip

    }


    /**
     * This method acts a wrapper for the crossover operation as it only select the parents and append the offspring
     * @param population the older generation
     * @return the new generation
     */
    private static Individual[] crossover(Individual[] population){

        Individual[] newPopulation = new Individual[POPULATION_SIZE];
        Random rand = new Random();

        for (int i = 0; i < POPULATION_SIZE; i+=2) {

            Individual parent1 = population[rand.nextInt(POPULATION_SIZE)];
            Individual parent2 = population[rand.nextInt(POPULATION_SIZE)];

            while (parent1 == parent2)
                parent2 = population[rand.nextInt(POPULATION_SIZE)];

            Individual[] offspring = offspring(parent1, parent2);

            newPopulation[i] = offspring[0];
            newPopulation[i+1] = offspring[1];

        }

        return newPopulation;
    }

    
    private static Individual[] offspring(Individual parent1, Individual parent2){
        return null;
    }

    /**
     *
     * This method returns a selection of the population according to the tournament selection mechanism
     *
     * @param population the population
     * @return the selected individuals according to the described mechanism
     */
    private static Individual[] eliteTournament(Individual[] population){

        Individual[] selected = new Individual[SELECTION_SIZE];

        // Get The ELITES. The order of the elite does not matter
        Object[] orderedByFitness = Arrays.stream(population.clone()).sorted(Comparator.comparing(Main::fitness)).skip(POPULATION_SIZE-ELITE_SIZE).toArray();

        for (int i = 0; i < ELITE_SIZE; i++)
            selected[i] = (Individual) orderedByFitness[i];


        // Tournament Selection

        Individual[] match = new Individual[TOURNAMENT_SELECTION_GROUP_SIZE];

        for (int winnerIndex = ELITE_SIZE; winnerIndex < SELECTION_SIZE; winnerIndex++) {

            for (int i = 0; i < TOURNAMENT_SELECTION_GROUP_SIZE; i++) {
                Random rand = new Random();
                match[i] = population[rand.nextInt(POPULATION_SIZE)];
            }

            selected[winnerIndex] = (Individual) Arrays.stream(match).sorted(Comparator.comparing(Main::fitness)).skip(match.length - 1).toArray()[0];
        }


        return selected;
    }

    /**
     * This function calculates the fitness of an individual
     * @param individual an individual
     * @return the fitness of an individual
     */
    public static int fitness(Individual individual){
        int fitness = 0;

        // Every morning, 1-4 workers // Contributing 5 Fitness for 5 mornings
        int[][] morningShift = getShift(0, individual);
        int[][] morningShiftT = transpose(morningShift);

        fitness += Math.toIntExact(Arrays.stream(morningShiftT).map(i -> Arrays.stream(i).sum()).filter(i -> i >= 1 && i <= 4).count());

        // Midday shift // Contributing 5 Fitness for 5 midday shifts
        int[][] middayShift = getShift(1, individual);
        int[][] middayShiftT = transpose(middayShift);

        fitness += Math.toIntExact(Arrays.stream(middayShiftT).map(i -> Arrays.stream(i).sum()).filter(i -> i >= 2 && i <= 5).count());

        // Evening shift // Contributing 5 Fitness for 5 evenings
        int[][] eveningShift = getShift(2, individual);
        int[][] eveningShiftT = transpose(eveningShift);

        fitness += Arrays.stream(eveningShiftT).map(i -> Arrays.stream(i).sum()).filter(i -> i >= 1 && i <= 2).count();

        // 3 consecutive shifts // Contributing 25 Fitness for 5 employees * 5 days

        for (int e = 0; e < Individual.getEMPLOYEES(); e++) {
            fitness += Arrays.stream(getShiftedDaySchedule(individual.getSchedule()[e])).map(i -> Arrays.stream(i).sum()).filter(i -> i < 3).count();
        }

        // Next Day fitness 20, for 5 employees and 4 possible combination since last day there won't be a next day
        for (int i = 0; i < Individual.getEMPLOYEES(); i++) {
            for (int j = 0; j < Individual.getDAYS(); j++) {
                if (getShiftedDaySchedule(individual.getSchedule()[i])[j][2] == 1) {
                    if (j + 1 < Individual.getDAYS() && Arrays.equals(getShiftedDaySchedule(individual.getSchedule()[i])[j + 1], new int[]{0, 0, 0})) {
                        fitness++;
                    }
                } else {
                    fitness++;
                }
            }
        }

        return fitness;
    }

    /**
     * Return a schedule that is divided into shifts per day for a worker. I.e, every element is a dn array of shifts.
     * @param workerSchedule a worker schedule
     * @return a shifted schedule
     */
    public static int[][] getShiftedDaySchedule(int[] workerSchedule){
        int[][] shiftedSchedule = new int[Individual.getDAYS()][Individual.getShiftsPerDays()];

        for (int i = 0; i < Individual.getDAYS(); i++)
            for (int j = 0; j < Individual.getShiftsPerDays(); j++)
                shiftedSchedule[i][j] = workerSchedule[i*Individual.getShiftsPerDays()+j];


        return shiftedSchedule;
    }

    /**
     * This method returns a schedule that only contains the specified shift
     * @param shift which shift to be used
     * @param individual the individual
     * @return a schedule that only contains the specified shift
     */
    public static int[][] getShift(int shift, Individual individual) {
        int[][] shifts = new int[Individual.getEMPLOYEES()][Individual.getDAYS()];

        for (int i = 0; i < Individual.getEMPLOYEES(); i++)
            for (int j = 0; j < Individual.getDAYS(); j++)
                shifts[i][j] = individual.getSchedule()[i][shift+(j * Individual.getShiftsPerDays())];

        return shifts;
    }

    /**
     * This method returns the transpose of an array
     * @param arr an array
     * @return the transpose of arr
     */
    public static int[][] transpose(int[][] arr){
        int[][] transposed = new int[arr[0].length][arr.length];

        for (int i = 0; i < arr[0].length ; i++)
            for (int j = 0; j < arr.length ; j++)
                transposed[i][j] = arr[j][i];

        return transposed;
    }

    /**
     * This method returns a random individual
     * @return an individual
     */
    public static Individual generateSchedule(){
        int[][] genes = new int[Individual.getEMPLOYEES()][Individual.getNumberOfGenesPerEmployee()];

        for (int i = 0; i < Individual.getEMPLOYEES();i++)
            for (int j = 0; j < genes[0].length; j++)
                genes[i][j] = RAND.nextInt(2);

        return new Individual(genes);
    }

}
