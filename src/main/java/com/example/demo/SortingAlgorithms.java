package com.example.demo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class SortingAlgorithms extends Application {
    public static final int NUM_BARS = 32;
    private List<Integer> array = new ArrayList<>();
    private List<Group> bars = new ArrayList<>();
    private VBox root;
    private HBox barContainer;
    private boolean isSorting = false;
    private static final int MAX_HEIGHT = 100;
    private static final int BAR_WIDTH = 20;
    private static final int BAR_SPACING = 10;

    @Override
    public void start(Stage primaryStage) {
        root = new VBox(10);
        barContainer = new HBox(BAR_SPACING);
        barContainer.setPrefHeight(MAX_HEIGHT);
        root.getStyleClass().add("root");
        barContainer.getStyleClass().add("bar-container");

        root.getChildren().add(barContainer);

        Button generateBtn = new Button("Generate");
        Button insertionSortBtn = new Button("Insertion Sort");
        Button mergeSortBtn = new Button("Merge Sort");
        Button bubbleSortBtn = new Button("Bubble Sort");
        Button selectionSortBtn = new Button("Selection Sort");
        Button quickSortBtn = new Button("Quick Sort");
        Button backBtn = new Button("Back to Main");
        Button quitBtn = new Button("Quit Program");
        generateBtn.getStyleClass().add("button");
        insertionSortBtn.getStyleClass().add("button");
        mergeSortBtn.getStyleClass().add("button");
        bubbleSortBtn.getStyleClass().add("button");
        selectionSortBtn.getStyleClass().add("button");
        quickSortBtn.getStyleClass().add("button");

        generateBtn.setOnAction(e -> generateArray());
        insertionSortBtn.setOnAction(e -> insertionSort());
        mergeSortBtn.setOnAction(e -> mergeSort());
        bubbleSortBtn.setOnAction(e -> bubbleSort());
        selectionSortBtn.setOnAction(e -> selectionSort());
        quickSortBtn.setOnAction(e -> quickSort());
        backBtn.setOnAction(e -> {
            new Program().start(primaryStage);
        });
        quitBtn.setOnAction(e -> Platform.exit());

        HBox buttons = new HBox(10, generateBtn, insertionSortBtn, mergeSortBtn, bubbleSortBtn, selectionSortBtn,
                quickSortBtn, backBtn, quitBtn);
        root.getChildren().add(buttons);

        Scene scene = new Scene(root, 900, 600);
        scene.getStylesheets().add("style.css");
        // scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Sorting Visualization");
        primaryStage.show();

        generateArray();
    }

    private void generateArray() {
        if (isSorting)
            return;
        array.clear();
        bars.clear();
        barContainer.getChildren().clear();

        for (int i = 0; i < NUM_BARS; i++) {
            int value = (int) (Math.random() * MAX_HEIGHT) + 20;
            array.add(value);

            Rectangle bar = new Rectangle(BAR_WIDTH, value);
            bar.setFill(Color.BLUE);
            Text text = new Text(String.valueOf(value));
            text.setFill(Color.BLACK);
            text.getStyleClass().add("bar-label");

            text.setY(-10); // text above the bar
            text.setX((BAR_WIDTH - text.getLayoutBounds().getWidth()) / 2);

            Group group = new Group(bar, text);
            group.setTranslateY(0);// - value);
            bars.add(group);
            barContainer.getChildren().add(group);

            // Center label on bar
            bar.widthProperty().addListener((obs, oldVal, newVal) -> {
                text.setX((newVal.doubleValue() - text.getLayoutBounds().getWidth()) / 2);
            });
        }
    }

    private void updateVisualization() {
        Platform.runLater(() -> {
            for (int i = 0; i < array.size(); i++) {
                int value = array.get(i);
                Group group = bars.get(i);
                Rectangle bar = (Rectangle) group.getChildren().get(0);
                Text text = (Text) group.getChildren().get(1);

                bar.setHeight(value);
                group.setTranslateY(0); // - value);
                text.setText(String.valueOf(value));
                text.setX((BAR_WIDTH - text.getLayoutBounds().getWidth()) / 2);
            }
        });
    }

    private void refreshBarContainer() {
        Platform.runLater(() -> barContainer.getChildren().setAll(bars));
    }

    private void highlightBars(int i, int j, Color color) {
        Platform.runLater(() -> {
            if (i >= 0 && i < bars.size())
                ((Rectangle) bars.get(i).getChildren().get(0)).setFill(color);
            if (j >= 0 && j < bars.size())
                ((Rectangle) bars.get(j).getChildren().get(0)).setFill(color);
        });
    }

    private void resetBarColors() {
        Platform.runLater(() -> {
            for (Group group : bars) {
                ((Rectangle) group.getChildren().get(0)).setFill(Color.BLUE);
            }
        });
    }

    private void swap(int i, int j) {
        Collections.swap(array, i, j);
        Collections.swap(bars, i, j);
        updateVisualization();
        refreshBarContainer();
    }

    private void pause() throws InterruptedException {
        Thread.sleep(500);
    }

    private void insertionSort() {
        if (isSorting)
            return;
        isSorting = true;
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                for (int i = 1; i < array.size(); i++) {
                    int key = array.get(i);
                    Group keyBar = bars.get(i);
                    int j = i - 1;

                    while (j >= 0 && array.get(j) > key) {
                        highlightBars(j, j + 1, Color.YELLOW);
                        array.set(j + 1, array.get(j));
                        bars.set(j + 1, bars.get(j));
                        updateVisualization();
                        refreshBarContainer();
                        pause();
                        j--;
                    }

                    array.set(j + 1, key);
                    bars.set(j + 1, keyBar);
                    updateVisualization();
                    refreshBarContainer();
                    highlightBars(j + 1, -1, Color.GREEN);
                    pause();
                }
                resetBarColors();
                return null;
            }
        };
        task.setOnSucceeded(e -> isSorting = false);
        new Thread(task).start();
    }

    private void mergeSort() {
        if (isSorting)
            return;
        isSorting = true;
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                mergeSort(0, array.size() - 1);
                resetBarColors();
                return null;
            }
        };
        task.setOnSucceeded(e -> isSorting = false);
        new Thread(task).start();
    }

    private void mergeSort(int left, int right) throws InterruptedException {
        if (left < right) {
            int mid = (left + right) / 2;
            mergeSort(left, mid);
            mergeSort(mid + 1, right);
            merge(left, mid, right);
        }
    }

    private void merge(int left, int mid, int right) throws InterruptedException {
        List<Integer> temp = new ArrayList<>();
        List<Group> tempBars = new ArrayList<>();

        int i = left, j = mid + 1;

        while (i <= mid && j <= right) {
            highlightBars(i, j, Color.YELLOW);
            if (array.get(i) <= array.get(j)) {
                temp.add(array.get(i));
                tempBars.add(bars.get(i));
                i++;
            } else {
                temp.add(array.get(j));
                tempBars.add(bars.get(j));
                j++;
            }
            updateVisualization();
            pause();
        }

        while (i <= mid) {
            temp.add(array.get(i));
            tempBars.add(bars.get(i));
            i++;
        }

        while (j <= right) {
            temp.add(array.get(j));
            tempBars.add(bars.get(j));
            j++;
        }

        for (int k = 0; k < temp.size(); k++) {
            array.set(left + k, temp.get(k));
            bars.set(left + k, tempBars.get(k));
            highlightBars(left + k, -1, Color.GREEN);
        }
        updateVisualization();
        refreshBarContainer();
        pause();
    }

    private void bubbleSort() {
        if (isSorting)
            return;
        isSorting = true;
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                for (int i = 0; i < array.size() - 1; i++) {
                    for (int j = 0; j < array.size() - i - 1; j++) {
                        highlightBars(j, j + 1, Color.YELLOW);
                        if (array.get(j) > array.get(j + 1)) {
                            swap(j, j + 1);
                            pause();
                        }
                        resetBarColors();
                    }
                }
                resetBarColors();
                return null;
            }
        };
        task.setOnSucceeded(e -> isSorting = false);
        new Thread(task).start();
    }

    private void selectionSort() {
        if (isSorting)
            return;
        isSorting = true;
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                for (int i = 0; i < array.size() - 1; i++) {
                    int minIdx = i;
                    for (int j = i + 1; j < array.size(); j++) {
                        highlightBars(minIdx, j, Color.YELLOW);
                        if (array.get(j) < array.get(minIdx)) {
                            minIdx = j;
                        }
                        pause();
                        resetBarColors();
                    }
                    if (minIdx != i) {
                        swap(i, minIdx);
                        pause();
                    }
                }
                resetBarColors();
                return null;
            }
        };
        task.setOnSucceeded(e -> isSorting = false);
        new Thread(task).start();
    }

    private void quickSort() {
        if (isSorting)
            return;
        isSorting = true;
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                quickSort(0, array.size() - 1);
                resetBarColors();
                return null;
            }
        };
        task.setOnSucceeded(e -> isSorting = false);
        new Thread(task).start();
    }

    private void quickSort(int low, int high) throws InterruptedException {
        if (low < high) {
            int pivotIndex = partition(low, high);
            quickSort(low, pivotIndex - 1);
            quickSort(pivotIndex + 1, high);
        }
    }

    private int partition(int low, int high) throws InterruptedException {
        int pivot = array.get(high);
        int i = low - 1;

        for (int j = low; j < high; j++) {
            highlightBars(j, high, Color.YELLOW);
            if (array.get(j) < pivot) {
                i++;
                swap(i, j);
                pause();
            }
            resetBarColors();
        }
        swap(i + 1, high);
        pause();
        return i + 1;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
