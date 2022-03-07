package com.example.geneticalgorithm.GA.RadarPlacement;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class MapGraph extends Canvas {

    private int xScale = 10;
    private int yScale = 10;

    private GraphicsContext gc = getGraphicsContext2D();

    public MapGraph(int width, int height){
        super(width, height);
    }

    public void draw(int i, int j, Color color){
        gc.setFill(color);
        gc.fillRect(i*xScale,j*yScale,xScale,yScale);
    }

}
