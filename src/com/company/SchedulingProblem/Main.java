package com.company.SchedulingProblem;

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


    }

    public static int fitness(Individual individual){
        int fitness = 0;
        int[][] schedule = individual.getSchedule();

        // Every morning, 1-4 workers
        int[][] shifts = new int[Individual.getShiftsPerDays()][];

        // get morning shifts



        return fitness;
    }

    public static Individual generateSchedule(){
        int[][] genes = new int[Individual.getEMPLOYEES()][Individual.getNumberOfGenesPerEmployee()];

        for (int i = 0; i < Individual.getEMPLOYEES();i++)
            for (int j = 0; j < genes[0].length; j++)
                genes[i][j] = RAND.nextInt(2);

        return new Individual(genes);
    }

}
