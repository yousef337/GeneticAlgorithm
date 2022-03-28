package com.example.geneticalgorithm.GA.GraphColoring;


import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import java.util.*;
import java.util.stream.Stream;

public class MainGraph {

    private static Graph graph;
    private final static String[] colors = {"Red","Blue","Green"};
    private final static int MAX_POPULATION = 10;

    public static void main(String[] args){
        createGraph(100,2);
        showGraph();

        mainGA();


    }

    private static void mainGA() {

       Individual[] population = generateRandomPopulation();

        // SELECT - ELITE

        // CROSSOVER - fitness driven

        // MUTATE - fitness driven - Change color

    }

    private static int fitness(Individual individual){
        int fitness = 0;

        // TOO MUCH TIME COMPLEXITY
        for (String set : individual.getColoredVertices().keySet()){

            HashSet<Edge> recordedEdges = new HashSet<>();
            for (String nodeId : individual.getColoredVertices().get(set)) {

                Stream<Edge> edges = graph.edges()
                            .filter(i -> (i.getNode0().getId().equals(nodeId) && individual.getColoredVertices().get(set).contains(i.getNode1().getId()) ||
                                    (i.getNode1().getId().equals(nodeId) && individual.getColoredVertices().get(set).contains(i.getNode0().getId()))));

                recordedEdges.addAll(List.of(edges.toArray(Edge[]::new)));

            }

            fitness -= Math.toIntExact(recordedEdges.size());


        }

        return fitness;
    }

    private static Individual[] generateRandomPopulation(){
        Individual[] population = new Individual[MAX_POPULATION];
        for (int i = 0; i < population.length; i++)
            population[i] = generateRandomIndividual();

        return population;
    }

    private static Individual generateRandomIndividual(){
        Multimap<String, String> colorMap = ArrayListMultimap.create();

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
