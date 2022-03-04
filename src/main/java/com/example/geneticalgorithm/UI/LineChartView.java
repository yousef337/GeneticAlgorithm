package com.example.geneticalgorithm.UI;

import com.example.geneticalgorithm.GA.SchedulingProblem.Main;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

public class LineChartView extends Application {


    private double[] avgFitness;
    private double[] bestFitness;

    public void setDate(){
        avgFitness = Main.getAvgFitness().clone();
        bestFitness = Main.getBestFitness().clone();
    }

    @Override
    public void start(Stage stage) {
        setDate();

        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Fitness");
        yAxis.setLabel("Generation");

        LineChart lineChart = new LineChart<>(xAxis, yAxis);

        XYChart.Series series = new XYChart.Series();
        series.setName("Average Fitness");

        for (int i = 0; i < avgFitness.length; i++)
            series.getData().add(new XYChart.Data<>(i, avgFitness[i]));

        lineChart.getData().add(series);


        XYChart.Series series1 = new XYChart.Series();
        series1.setName("Best Fitness");

        for (int i = 0; i < bestFitness.length; i++)
            series1.getData().add(new XYChart.Data<>(i, bestFitness[i]));

        lineChart.getData().add(series1);


        Scene scene = new Scene(lineChart);
        stage.setScene(scene);
        stage.show();
    }

}