package com.example.geneticalgorithm.GA.SchedulingProblem;

import com.example.geneticalgorithm.UI.LineChartView;
import javafx.application.Application;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

/**
 *
 * This class is intended to provide a solution using genetic algorithm to solve the scheduling problem.
 *
 * @version 2022-03-03
 */
public class Main {

    private final static int POPULATION_SIZE = 50;
    private final static int MAX_GENERATIONS = 50;
    private static final Random RAND = new Random();
    private static final int MAX_FITNESS = maximumFitness();
    private static final int TOURNAMENT_SELECTION_GROUP_SIZE = 4;
    private static final int SELECTION_SIZE = 10;
    private static final int ELITE_SIZE = 3;

    private static double[] avgFitness;
    private static double[] bestFitness;

    /**
     * This method calculates the maximum possible fitness for a schedule.
     * @return the maximum & targeted fitness.
     */
    private static int maximumFitness(){
        return (100*Individual.getDAYS()*Individual.getShiftsPerDays()) + Individual.getEMPLOYEES() * Individual.getDAYS() + Individual.getEMPLOYEES() * (Individual.getDAYS()-1);
    }

    public static void main(String[] args) {
        System.out.println("Max Fitness: " + MAX_FITNESS);

        Individual[] population = new Individual[POPULATION_SIZE];
        avgFitness = new double[MAX_GENERATIONS];
        bestFitness = new double[MAX_GENERATIONS];

        for (int i = 0; i < POPULATION_SIZE; i++)
            population[i] = generateSchedule();

        boolean found = false;
        Individual best = null;

        for (int i = 0; i < MAX_GENERATIONS && !found; i++) {

            // ELITE tournament

            Individual[] selected = eliteTournament(population);

            // Crossover ELITE
            Individual[] newGeneration = crossover(selected);

            // 1 bit flip
            for (Individual ind: newGeneration)
                mutate(ind);

            population = newGeneration;

            if (fitness((Individual) Arrays.stream(population).sorted(Comparator.comparing(Main::fitness)).skip(POPULATION_SIZE-1).toArray()[0]) == MAX_FITNESS) {
                found = true;
            }
            best = (Individual) Arrays.stream(population).sorted(Comparator.comparing(Main::fitness)).skip(POPULATION_SIZE-1).toArray()[0];

            avgFitness[i] = Arrays.stream(population).mapToDouble(Main::fitness).average().getAsDouble();
            bestFitness[i] = fitness((Individual) Arrays.stream(population).sorted(Comparator.comparing(Main::fitness)).skip(POPULATION_SIZE-1).toArray()[0]);

        }

        //Print the best
        for (int[] a: best.getSchedule())
            System.out.println(Arrays.toString(a));


        LineChartView lcv = new LineChartView();
        lcv.setDate();
        Application.launch(LineChartView.class);
    }


    /**
     * This method mutate a random position on the individual
     * @param individual the individual to be mutated
     */
    private static void mutate(Individual individual){
        Random rand = new Random();
        individual.getSchedule()[rand.nextInt(Individual.getEMPLOYEES())][rand.nextInt(Individual.getDAYS()*Individual.getShiftsPerDays())] =  (individual.getSchedule()[rand.nextInt(Individual.getEMPLOYEES())][rand.nextInt(Individual.getDAYS()*Individual.getShiftsPerDays())]+1)%2;
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

            Individual parent1 = population[rand.nextInt(SELECTION_SIZE)];
            Individual parent2 = population[rand.nextInt(SELECTION_SIZE)];

            while (parent1 == parent2)
                parent2 = population[rand.nextInt(SELECTION_SIZE)];


            Individual[] offspring = offspring(parent1, parent2);

            // Take only the best

            Object[] bestTwo = Arrays.stream(new Individual[]{offspring[0], offspring[1]}).sorted(Comparator.comparing(Main::fitness)).toArray();


            newPopulation[i] = (Individual) bestTwo[0];
            newPopulation[i+1] = (Individual) bestTwo[1];

        }

        return newPopulation;
    }

    /**
     * This method returns an array of Individuals, of length 2, that have a combination of the parent's genes.
     *
     * @param parent1 the first parent
     * @param parent2 the second parent
     * @return the offspring
     */
    private static Individual[] offspring(Individual parent1, Individual parent2){

        int[][] offspring1 = new int[parent1.getSchedule().length][parent1.getSchedule()[0].length];
        int[][] offspring2 = new int[parent1.getSchedule().length][parent1.getSchedule()[0].length];

        for (int i = 0; i < Individual.getDAYS(); i++) {
            System.arraycopy(parent1.getSchedule()[i],0,offspring1[i],0,parent1.getSchedule()[i].length/2);
            System.arraycopy(parent2.getSchedule()[i],parent1.getSchedule()[i].length/2,offspring1[i],parent1.getSchedule()[i].length/2,parent2.getSchedule()[i].length/2);

            System.arraycopy(parent2.getSchedule()[i],0,offspring2[i],0,parent2.getSchedule()[i].length/2);
            System.arraycopy(parent1.getSchedule()[i],parent2.getSchedule()[i].length/2,offspring2[i],parent2.getSchedule()[i].length/2,parent2.getSchedule()[i].length/2);

        }

        return new Individual[]{new Individual(offspring1), new Individual(offspring2)};
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

        fitness += 100*Math.toIntExact(Arrays.stream(morningShiftT).map(i -> Arrays.stream(i).sum()).filter(i -> i >= 1 && i <= 4).count());

        // Midday shift // Contributing 5 Fitness for 5 midday shifts
        int[][] middayShift = getShift(1, individual);
        int[][] middayShiftT = transpose(middayShift);

        fitness += 100*Math.toIntExact(Arrays.stream(middayShiftT).map(i -> Arrays.stream(i).sum()).filter(i -> i >= 2 && i <= 5).count());

        // Evening shift // Contributing 5 Fitness for 5 evenings
        int[][] eveningShift = getShift(2, individual);
        int[][] eveningShiftT = transpose(eveningShift);

        fitness += 100*Arrays.stream(eveningShiftT).map(i -> Arrays.stream(i).sum()).filter(i -> i >= 1 && i <= 2).count();

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

    public static double[] getAvgFitness() {
        return avgFitness;
    }

    public static double[] getBestFitness() {
        return bestFitness;
    }
}
