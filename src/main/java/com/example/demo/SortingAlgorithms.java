package com.example.demo;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.PauseTransition;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SortingAlgorithms extends Application {
    private Pane canvas;
    private List<Rectangle> bars;
    private List<Integer> values;
    private VBox leftPanel;

    @Override
    public void start(Stage stage) {
        leftPanel = new VBox(10);
        leftPanel.setPrefWidth(300);
        leftPanel.setPadding(new Insets(10));
        leftPanel.setStyle("-fx-background-color: #ffffff; -fx-border-color: #dcdcdc; -fx-border-width: 1;");

        MenuButton sortMenu = new MenuButton("Select Sorting Algorithm");
        MenuItem bubbleSortItem = new MenuItem("Bubble Sort");
        MenuItem quickSortItem = new MenuItem("Quick Sort");
        sortMenu.getItems().addAll(bubbleSortItem, quickSortItem);
        sortMenu.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-background-radius: 5;");

        TextField sizeField = new TextField();
        sizeField.setPromptText("Enter array size (5-20)");
        Button generateBtn = new Button("Generate Array");
        Button backBtn = new Button("Back to Main");

        generateBtn.setOnAction(e -> {
            try {
                int size = Integer.parseInt(sizeField.getText().trim());
                if (size >= 5 && size <= 20) {
                    generateArray(size);
                } else {
                    showWarning("Enter size between 5 and 20");
                }
            } catch (NumberFormatException ex) {
                showWarning("Invalid size");
            }
        });

        backBtn.setOnAction(e -> new Program().start(stage));

        leftPanel.getChildren().addAll(sortMenu, sizeField, generateBtn, backBtn);

        canvas = new Pane();
        canvas.setStyle("-fx-background-color: #e6f7ff; -fx-border-color: #dcdcdc;");

        bubbleSortItem.setOnAction(e -> {
            sortMenu.setText("Bubble Sort");
            bubbleSort();
        });
        quickSortItem.setOnAction(e -> {
            sortMenu.setText("Quick Sort");
            quickSort(0, values.size() - 1);
        });

        HBox root = new HBox(leftPanel, canvas);
        HBox.setHgrow(canvas, Priority.ALWAYS);

        Scene scene = new Scene(root, 1200, 700);
        scene.getStylesheets().add("styles.css");
        stage.setTitle("Sorting Algorithms Visualizer");
        stage.setScene(scene);
        stage.show();

        generateArray(10); // Default array
    }

    private void generateArray(int size) {
        canvas.getChildren().clear();
        bars = new ArrayList<>();
        values = new ArrayList<>();
        Random rand = new Random();
        double barWidth = canvas.getWidth() / size;
        double maxHeight = canvas.getHeight() - 100;

        for (int i = 0; i < size; i++) {
            int value = rand.nextInt(100) + 1;
            values.add(value);
            Rectangle bar = new Rectangle(i * barWidth, canvas.getHeight() - (value / 100.0 * maxHeight), barWidth - 2, value / 100.0 * maxHeight);
            bar.setFill(Color.LIGHTBLUE);
            bar.setStroke(Color.BLACK);
            bars.add(bar);
            canvas.getChildren().add(bar);
        }
    }

    private void bubbleSort() {
        for (int i = 0; i < values.size() - 1; i++) {
            for (int j = 0; j < values.size() - i - 1; j++) {
                final int fj = j;
                PauseTransition pt = new PauseTransition(Duration.seconds(0.5));
                pt.setOnFinished(e -> {
                    if (values.get(fj) > values.get(fj + 1)) {
                        highlightBar(fj, Color.YELLOW);
                        highlightBar(fj + 1, Color.YELLOW);
                        swapBars(fj, fj + 1);
                    }
                });
                pt.play();
            }
        }
    }

    private void quickSort(int low, int high) {
        if (low < high) {
            int pi = partition(low, high);
            quickSort(low, pi - 1);
            quickSort(pi + 1, high);
        }
    }

    private int partition(int low, int high) {
        int pivot = values.get(high);
        highlightBar(high, Color.RED);
        int i = low - 1;

        for (int j = low; j < high; j++) {
            final int fj = j;
            final int fi = i;
            if (values.get(j) <= pivot) {
                i++;
                PauseTransition pt = new PauseTransition(Duration.seconds(0.5));
                pt.setOnFinished(e -> {
                    highlightBar(fj, Color.YELLOW);
                    highlightBar(fi + 1, Color.YELLOW);
                    swapBars(fi + 1, fj);
                });
                pt.play();
            }
        }
        final int finalI = i; // Create a final copy of i
        PauseTransition pt = new PauseTransition(Duration.seconds(0.5));
        pt.setOnFinished(e -> {
            highlightBar(finalI + 1, Color.YELLOW);
            highlightBar(high, Color.YELLOW);
            swapBars(finalI + 1, high);
        });
        pt.play();
        return finalI + 1;
    }

    private void swapBars(int i, int j) {
        int temp = values.get(i);
        values.set(i, values.get(j));
        values.set(j, temp);

        Rectangle bar1 = bars.get(i);
        Rectangle bar2 = bars.get(j);

        double x1 = bar1.getX();
        bar1.setX(bar2.getX());
        bar2.setX(x1);
    }

    private void highlightBar(int index, Color color) {
        Rectangle bar = bars.get(index);
        bar.setFill(color);
        PauseTransition pt = new PauseTransition(Duration.seconds(0.5));
        pt.setOnFinished(e -> bar.setFill(Color.LIGHTBLUE));
        pt.play();
    }

    private void showWarning(String msg) {
        Label warning = new Label(msg);
        warning.setStyle("-fx-background-color: #ffeb3b; -fx-text-fill: #d32f2f; -fx-padding: 5; -fx-border-radius: 5;");
        warning.setLayoutX(10);
        warning.setLayoutY(10);
        canvas.getChildren().add(warning);
        PauseTransition pt = new PauseTransition(Duration.seconds(2));
        pt.setOnFinished(e -> canvas.getChildren().remove(warning));
        pt.play();
    }
}