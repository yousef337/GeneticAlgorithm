package com.example.geneticalgorithm.GA.GraphColoring;


import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import java.util.*;

public class MainGraph {

    private static Graph graph;
    private final static String[] colors = {"Red","Blue","Green"};
    private final static int POPULATION_SIZE = 10;
    private final static double MUTATION_PROBABILITY = 0.2;
    private final static int TOTAL_SELECTION_SIZE = 10;
    private final static int ELITE_SELECTION_SIZE = 10;
    private final static int MAX_GENERATION = 100;


    public static void main(String[] args){
        createGraph(100,2);
        mainGA();

    }

    private static void mainGA() {

        Individual[] population = generateRandomPopulation();
        Individual best = null;

        for (int i = 0; i < MAX_GENERATION; i++) {

            // SELECT - ELITE
            Individual[] selected = selection(population);

            // CROSSOVER - fitness driven
            Individual[] newPopulation = crossover(selected);

            // MUTATE
            for (Individual individual : newPopulation)
                mutate(individual);

            population = newPopulation;


            best = Arrays.stream(population).sorted(Comparator.comparing(MainGraph::fitness).reversed()).findFirst().get();
            System.out.println(fitness(best));

            if (fitness(best) == 0)
                break;

        }

        System.out.println("--------------");
        System.out.println(fitness(best));
        showOneIndividual(best);

    }

    private static void showOneIndividual(Individual individual){
        graph.nodes().forEach(i->i.setAttribute("ui.style", "fill-color: " + getColorOfANodeInIndividual(individual,i.getId()) + ";"));

        System.setProperty("org.graphstream.ui", "javafx");
        graph.display();
    }

    private static String getColorOfANodeInIndividual(Individual individual, String nodeID){
        for (String s: colors) {
            if (individual.getColoredVertices().get(s).contains(nodeID))
                return s;
        }

        return null;
    }

    private static Individual[] crossover(Individual[] population){
        Random rand = new Random();

        Individual[] newGeneration = new Individual[POPULATION_SIZE];

        for (int i = 0; i < TOTAL_SELECTION_SIZE; i+=2) {

            Individual parent1 = population[rand.nextInt(TOTAL_SELECTION_SIZE)];
            Individual parent2 = population[rand.nextInt(TOTAL_SELECTION_SIZE)];

            while (parent1 == parent2)
                parent2 = population[rand.nextInt(TOTAL_SELECTION_SIZE)];

            Individual[] offspring = breed(parent1, parent2);


            Object[] bestTwo = Arrays.stream(new Individual[]{offspring[0], offspring[1], parent1, parent2})
                    .sorted(Comparator.comparing(MainGraph::fitness).reversed()).toArray();

            newGeneration[i] = (Individual) bestTwo[0];
            newGeneration[i+1] = (Individual) bestTwo[1];

        }

        return newGeneration;
    }

    private static Individual[] breed(Individual parent1, Individual parent2){
        Individual[] offspring = new Individual[2];
        Individual[] parents = {parent1, parent2};

        for (int i = 0; i < offspring.length; i++) {

            Multimap<String, String> colorMap = HashMultimap.create();

            for (int j = 0; j < graph.nodes().count()/2; j++) {
                // Locate point location in parents

                for (String s : parents[(i+1)%2].getColoredVertices().keys()){
                    if (parents[(i+1)%2].getColoredVertices().get(s).contains(String.valueOf(j))){
                        colorMap.put(s, String.valueOf(j));
                    }
                }

            }

            for (int j = (int) (graph.nodes().count()/2); j < graph.nodes().count(); j++) {
                // Locate point location in parents

                for (String s : parents[(i+1)%2].getColoredVertices().keys()){
                    if (parents[(i+1)%2].getColoredVertices().get(s).contains(String.valueOf(j))){
                        colorMap.put(s, String.valueOf(j));
                    }
                }

            }

            offspring[i] = new Individual(colorMap);

        }

        return offspring;
    }

    private static Individual[] selection(Individual[] population){
           Individual[] selected = Arrays.stream(population)
                .sorted(Comparator.comparing(MainGraph::fitness))
                .skip(POPULATION_SIZE-ELITE_SELECTION_SIZE)
                .toArray(Individual[]::new);

        return selected;
    }

    private static void mutate(Individual individual){
        Random rand = new Random();

        if (rand.nextDouble() < MUTATION_PROBABILITY) {

            String oldRandomColorSetKey = colors[rand.nextInt(colors.length)];

            int counter = 0;
            while (individual.getColoredVertices().get(oldRandomColorSetKey).size() == 0 && counter < colors.length) {
                oldRandomColorSetKey = colors[rand.nextInt(colors.length)];
                counter++;
            }

            if (counter == colors.length)
                return;

            String newRandomColorSetKey = colors[rand.nextInt(colors.length)];

            counter = 0;
            while (oldRandomColorSetKey.equals(newRandomColorSetKey) && counter < colors.length) {
                newRandomColorSetKey = colors[rand.nextInt(colors.length)];
                counter++;
            }

            if (counter == colors.length)
                return;

            String[] elements = individual.getColoredVertices().get(oldRandomColorSetKey).toArray(String[]::new);
            String element = elements[rand.nextInt(elements.length)];
            individual.getColoredVertices().get(oldRandomColorSetKey).remove(element);
            individual.getColoredVertices().get(newRandomColorSetKey).add(element);
        }

    }

    private static int fitness(Individual individual){
        int fitness = 0;

        for (Edge edge : graph.edges().toArray(Edge[]::new)) {
            for (String set : colors) {
                if (individual.getColoredVertices().get(set).contains(edge.getNode0().getId())
                        && individual.getColoredVertices().get(set).contains(edge.getNode1().getId())) {
                    fitness--;
                    break;
                }
            }

        }

        return fitness;
    }

    private static Individual[] generateRandomPopulation(){
        Individual[] population = new Individual[POPULATION_SIZE];
        for (int i = 0; i < population.length; i++)
            population[i] = generateRandomIndividual();

        return population;
    }

    private static Individual generateRandomIndividual(){
        Multimap<String, String> colorMap = HashMultimap.create();

        for (Node node: graph.nodes().toArray(Node[]::new))
            colorMap.put(colors[new Random().nextInt(colors.length)], node.getId());

        return new Individual(colorMap);
    }

    private static void createGraph(int nodesNumber, int maximumNodeConnections) {
        graph = new SingleGraph("Tutorial 1");
        int counter = 0;

        while (counter != nodesNumber){
            graph.addNode(String.valueOf(counter));
            counter++;
        }

        Random rand = new Random();

        for (int i = 0; i < nodesNumber; i++) {

            for (int j = 0; j < rand.nextInt(maximumNodeConnections)+1; j++) {
                String secondNode = String.valueOf(rand.nextInt(nodesNumber));

                while (secondNode.equals(String.valueOf(i)) || graph.getEdge(""+i+secondNode) != null
                        || graph.getEdge(""+secondNode+i) != null )
                    secondNode = String.valueOf(rand.nextInt(nodesNumber));

                graph.addEdge(""+i+secondNode, String.valueOf(i), secondNode);
            }

        }

    }

    private static void showGraph(){
        System.setProperty("org.graphstream.ui", "javafx");
        graph.display();
    }

}
