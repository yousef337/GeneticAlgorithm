package com.example.geneticalgorithm.GA.SchedulingProblem;

import java.util.Arrays;
import java.util.Random;

public class Main {

    private final static int POPULATION = 20;
    private final static int MAX_GENERATIONS = 15;
    private static final Random RAND = new Random();

    public static void main(String[] args) {

        Individual[] population = new Individual[POPULATION];
        for (int i = 0; i < 1; i++) {
            population[i] = generateSchedule();
            System.out.println(Arrays.toString(population[i].getSchedule()[0]));
            System.out.println(Arrays.toString(population[i].getSchedule()[1]));
            System.out.println(Arrays.toString(population[i].getSchedule()[2]));
            System.out.println(Arrays.toString(population[i].getSchedule()[3]));
            System.out.println(Arrays.toString(population[i].getSchedule()[4]));
        }

        fitness(population[0]);

        // ELITE tournament -> Crossover ELITE -> 1 bit flip


    }

    /**
     * This function calculates the fitness of an individual
     * @param individual an individual
     * @return the fitness of an individual
     */
    public static int fitness(Individual individual){
        int fitness = 0;

        // Every morning, 1-4 workers
        int[][] morningShift = getShift(0, individual);
        int[][] morningShiftT = transpose(morningShift);

        fitness += Math.toIntExact(Arrays.stream(morningShiftT).map(i -> Arrays.stream(i).sum()).filter(i -> i >= 1 && i <= 4).count());

        // Midday shift
        int[][] middayShift = getShift(1, individual);
        int[][] middayShiftT = transpose(middayShift);

        fitness += Math.toIntExact(Arrays.stream(middayShiftT).map(i -> Arrays.stream(i).sum()).filter(i -> i >= 2 && i <= 5).count());

        // Evening shift
        int[][] eveningShift = getShift(2, individual);
        int[][] eveningShiftT = transpose(eveningShift);

        fitness += Arrays.stream(eveningShiftT).map(i -> Arrays.stream(i).sum()).filter(i -> i >= 1 && i <= 2).count();

        // 3 consecutive shifts

        for (int e = 0; e < Individual.getEMPLOYEES(); e++) {
            fitness += Arrays.stream(getShiftedDaySchedule(individual.getSchedule()[e])).map(i -> Arrays.stream(i).sum()).filter(i -> i < 3).count();
        }


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

        System.out.println(Arrays.toString(getShiftedDaySchedule(individual.getSchedule()[0])[0]));
        System.out.println(Arrays.toString(getShiftedDaySchedule(individual.getSchedule()[1])[1]));
        System.out.println(Arrays.toString(getShiftedDaySchedule(individual.getSchedule()[2])[2]));
        System.out.println(Arrays.toString(getShiftedDaySchedule(individual.getSchedule()[3])[3]));
        System.out.println(Arrays.toString(getShiftedDaySchedule(individual.getSchedule()[4])[4]));

        System.out.println(fitness);


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
    public static int[][] getShift(int shift, Individual individual){
        int[][] shifts = new int[Individual.getEMPLOYEES()][Individual.getDAYS()];

        for (int i = shift; i < Individual.getDAYS(); i++)
            for (int j = 0; j < Individual.getEMPLOYEES(); j++)
                shifts[i][j] = individual.getSchedule()[i][j*Individual.getShiftsPerDays()];

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
