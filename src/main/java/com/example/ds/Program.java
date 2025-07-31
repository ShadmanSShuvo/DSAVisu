package com.example.ds;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Program extends Application {

    @Override
    public void start(Stage stage) {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f0f4f8;");

        
        Button linkedListBtn = new Button("Linked List Visualization");
        Button stackBtn = new Button("Stack Visualization");
        Button queueBtn = new Button("Queue Visualization");
        Button heapBtn = new Button("Heap Visualization");
        Button bstBtn = new Button("Binary Search Tree Visualization");
        Button quitBtn = new Button("Quit Program");

        
        
        linkedListBtn.setOnAction(e -> new DataVisu("Linked List").start(stage));
        stackBtn.setOnAction(e -> new DataVisu("Stack").start(stage));
        queueBtn.setOnAction(e -> new DataVisu("Queue").start(stage));
        heapBtn.setOnAction(e -> new DataVisu("Heap").start(stage));
        bstBtn.setOnAction(e -> new DataVisu("Binary Search Tree").start(stage));
        quitBtn.setOnAction(e -> System.exit(0));

        root.getChildren().addAll(linkedListBtn, stackBtn, queueBtn, heapBtn, bstBtn, quitBtn);

        Scene scene = new Scene(root, 600, 600);
        scene.getStylesheets().add("styles.css");
        stage.setTitle("Data Structure & Algorithm Visualizer");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}