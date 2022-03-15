package com.example.geneticalgorithm.GA.RadarPlacement;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * This class represent that map that would be drawn
 * @author Yousef Altaher
 */
public class MapGraph extends Canvas {

    private int xScale = 10;
    private int yScale = 10;

    private GraphicsContext gc = getGraphicsContext2D();

    /**
     * This method construct the map's dimensions
     * @param width map's width
     * @param height map's height
     */
    public MapGraph(int width, int height){
        super(width, height);
    }

    /**
     * This method draw a specific cell on the map
     * @param i x position
     * @param j y position
     * @param color the color
     */
    public void draw(int i, int j, Color color){
        gc.setFill(color);
        gc.fillRect(i*xScale,j*yScale,xScale,yScale);
    }

}
