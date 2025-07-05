package com.example.demo;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Program extends Application {

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f0f4f8;");

        Button graphBtn = new Button("Graph Visualization");
        Button linkedListBtn = new Button("Linked List Visualization");
        Button stackBtn = new Button("Stack Visualization");
        Button queueBtn = new Button("Queue Visualization");
        Button heapBtn = new Button("Heap Visualization");
        Button bstBtn = new Button("Binary Search Tree Visualization");
        Button sortingBtn = new Button("Sorting Algorithms");

        // Style buttons
        for (Button btn : new Button[]{graphBtn, linkedListBtn, stackBtn, queueBtn, heapBtn, bstBtn, sortingBtn}) {
            btn.setStyle("-fx-font-size: 16px; -fx-padding: 10px 20px; -fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 5;");
            btn.setMaxWidth(300);
        }

        // Navigate to respective visualizations
        graphBtn.setOnAction(e -> new DataVisu("Graph").start(primaryStage));
        linkedListBtn.setOnAction(e -> new DataVisu("Linked List").start(primaryStage));
        stackBtn.setOnAction(e -> new DataVisu("Stack").start(primaryStage));
        queueBtn.setOnAction(e -> new DataVisu("Queue").start(primaryStage));
        heapBtn.setOnAction(e -> new DataVisu("Heap").start(primaryStage));
        bstBtn.setOnAction(e -> new DataVisu("Binary Search Tree").start(primaryStage));
        sortingBtn.setOnAction(e -> new SortingAlgorithms().start(primaryStage));

        root.getChildren().addAll(graphBtn, linkedListBtn, stackBtn, queueBtn, heapBtn, bstBtn, sortingBtn);

        Scene scene = new Scene(root, 600, 600);
        scene.getStylesheets().add("styles.css");
        primaryStage.setTitle("Data Structure & Algorithm Visualizer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}