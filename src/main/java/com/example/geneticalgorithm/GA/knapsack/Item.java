package com.example.geneticalgorithm.GA.knapsack;

/**
 * This class is intended to represent an item in a knapsack
 */
public class Item {

    private double price;
    private double weight;

    /**
     *
     * @param price item's price
     * @param weight item's weight
     */
    public Item(double price, double weight){
        this.price = price;
        this.weight = weight;
    }

    /**
     *
     * @return item's price
     */
    public double getPrice() {
        return price;
    }

    /**
     *
     * @return item's weight
     */
    public double getWeight() {
        return weight;
    }
}
