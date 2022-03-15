package com.example.geneticalgorithm.UI;

import com.example.geneticalgorithm.GA.RadarPlacement.Individual;
import com.example.geneticalgorithm.GA.RadarPlacement.MapGraph;
import com.example.geneticalgorithm.GA.RadarPlacement.RadarMain;
import com.example.geneticalgorithm.GA.RadarPlacement.Territories;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.HashMap;

/**
 * This class represent a UI map implemented in javaFX.
 * @author Yousef Altaher
 */
public class MapView extends Application {

    private final int WIDTH = 200;
    private final int HEIGHT = 150;
    private HashMap<Territories, Color> mapColors;

    /**
     * The main start method for javaFX. Used to set up the main view.
     * @param stage the stage
     */
    @Override
    public void start(Stage stage){
        fillMapColors();

        RadarMain radarMap = new RadarMain(WIDTH, HEIGHT);
        BorderPane borderPane = new BorderPane();

        borderPane.setCenter(drawTerritoryMap(radarMap));
        borderPane.setLeft(drawCoverage(radarMap, radarMap.getBestIndividual()));

        Scene scene = new Scene(borderPane);
        stage.setScene(scene);
        stage.show();

    }

    /**
     * This method returns the map representation of the territories
     * @param radarMap the main class that includes the radar functionalities
     * @return the territory map
     */
    private MapGraph drawTerritoryMap(RadarMain radarMap){
        MapGraph view = new MapGraph(WIDTH*5, HEIGHT*4);

        Territories[][] map = radarMap.getMap();
        for (int i = 0; i < map.length; i++)
            for (int j = 0; j < map[i].length; j++)
                view.draw(i, j, mapColors.get(map[i][j]));

            return view;
    }


    /**
     *
     * This method returns a map that has the coverage of radars of an individual.
     *
     * @param radarMap the main class that includes the radar functionalities
     * @param individual the individual to be used for the map
     * @return the coverage map
     */
    private MapGraph drawCoverage(RadarMain radarMap, Individual individual){
        MapGraph view = new MapGraph(WIDTH*5, HEIGHT*4);

        Territories[][] map = radarMap.getMap();
        for (int i = 0; i < map.length; i++)
            for (int j = 0; j < map[i].length; j++)
                if (radarMap.isCovered(individual, i, j))
                    view.draw(i,j, Color.GREEN);

        return view;
    }


    /**
     * This method assign colors to the landscape features (Territories type)
     */
    private void fillMapColors(){
        mapColors = new HashMap<>();
        mapColors.put(Territories.CITY, new Color(0.3,0.6,0.2,1));
        mapColors.put(Territories.HILL, new Color(0.87,0.18,0.12,1));
        mapColors.put(Territories.WATER, new Color(0,0.12,0.8,1));
        mapColors.put(Territories.LAND, new Color(0,0.5,0.5,1));
    }


}
