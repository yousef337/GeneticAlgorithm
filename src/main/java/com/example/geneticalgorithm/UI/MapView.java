package com.example.geneticalgorithm.UI;

import com.example.geneticalgorithm.GA.RadarPlacement.MapGraph;
import com.example.geneticalgorithm.GA.RadarPlacement.RadarMain;
import com.example.geneticalgorithm.GA.RadarPlacement.Territories;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.HashMap;

public class MapView extends Application {

    private final int WIDTH = 200;
    private final int HEIGHT = 150;
    private HashMap<Territories, Color> mapColors;

    private void fillMapColors(){
        mapColors = new HashMap<>();
        mapColors.put(Territories.CITY, new Color(0.3,0.6,0.2,1));
        mapColors.put(Territories.HILL, new Color(0.87,0.18,0.12,1));
        mapColors.put(Territories.WATER, new Color(0,0.12,0.8,1));
        mapColors.put(Territories.LAND, new Color(0,0.5,0.5,1));
    }

    @Override
    public void start(Stage stage){
        fillMapColors();

        RadarMain radarMap = new RadarMain(WIDTH, HEIGHT);

        MapGraph view = new MapGraph(WIDTH*8, HEIGHT*6);

        Territories[][] map = radarMap.getMap();
        for (int i = 0; i < map.length; i++)
            for (int j = 0; j < map[i].length; j++)
                view.draw(i, j, mapColors.get(map[i][j]));

        Scene scene = new Scene(new BorderPane(view));
        stage.setScene(scene);
        stage.show();

    }
}
