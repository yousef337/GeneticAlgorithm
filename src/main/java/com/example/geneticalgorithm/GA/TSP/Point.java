package com.example.geneticalgorithm.GA.TSP;

import java.util.Objects;

public class Point {

    private int x;
    private int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object object){
        if (object == null || !(object instanceof Point))
            return false;

        return getX() == ((Point) object).getX() && getY() == ((Point) object).getY();
    }

    @Override
    public int hashCode(){
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y +")";
    }
}
