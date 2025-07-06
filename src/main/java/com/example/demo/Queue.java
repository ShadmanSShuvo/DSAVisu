package com.example.demo;

import javafx.animation.PauseTransition;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class Queue {
    private List<String> queue;
    private List<Rectangle> rectangles;
    private List<Label> labels;
    private Pane canvas;

    public Queue(Pane canvas) {
        this.canvas = canvas;
        this.queue = new ArrayList<>();
        this.rectangles = new ArrayList<>();
        this.labels = new ArrayList<>();
    }

    public void enqueue(String value) {
        Rectangle rect = new Rectangle(0, 0, 100, 40);
        rect.setFill(Color.LIGHTPINK);
        rect.setStroke(Color.BLACK);
        Label label = new Label(value);
        label.setLayoutX(30);
        label.setLayoutY(10);
        queue.add(value);
        rectangles.add(rect);
        labels.add(label);
        canvas.getChildren().addAll(rect, label);
        highlightElement(rect, Color.YELLOW);
        updatePositions();
    }

    public void dequeue() {
        if (queue.isEmpty()) {
            showWarning("Queue is empty");
            return;
        }
        String value = queue.remove(0);
        Rectangle rect = rectangles.remove(0);
        Label label = labels.remove(0);
        highlightElement(rect, Color.RED);
        PauseTransition pt = new PauseTransition(Duration.seconds(0.5));
        pt.setOnFinished(e -> {
            canvas.getChildren().removeAll(rect, label);
            updatePositions();
        });
        pt.play();
    }
//    public String front() {
//        if (queue.isEmpty()) {
//            throw new NoSuchElementException("Queue is empty");
//        }
//        return queue.get(0);
//    }

//    public String front() {
//        if (queue.isEmpty()) {
//            showWarning("Queue is empty");
//        }
//        return queue.get(0);
//    }



    public String front() {
        if (queue == null) {
            throw new IllegalStateException("Queue is not initialized");
        }
        if (queue.isEmpty()) {
            throw new NoSuchElementException("Queue is empty");
        }
        return queue.get(0);
    }

    public void peek() {
        if (queue.isEmpty()) {
            showWarning("Queue is empty");
            return;
        }
        Rectangle rect = rectangles.get(0);
        highlightElement(rect, Color.GREEN);
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public void isEmptyVisual() {
        String msg = isEmpty() ? "Queue is empty" : "Queue is not empty";
        showWarning(msg);
    }

    private void highlightElement(Rectangle rect, Color color) {
        rect.setFill(color);
        PauseTransition pt = new PauseTransition(Duration.seconds(0.5));
        pt.setOnFinished(e -> rect.setFill(Color.LIGHTPINK));
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

    private void updatePositions() {
        double startX = 50;
        double startY = canvas.getHeight() - 50;
        double maxWidth = 900; // Approximate canvas width
        int itemsPerRow = (int) (maxWidth / 110); // Each item is ~110px wide
        for (int i = 0; i < queue.size(); i++) {
            int row = i / itemsPerRow;
            int col = i % itemsPerRow;
            Rectangle rect = rectangles.get(i);
            Label label = labels.get(i);
            rect.setX(startX + col * 110);
            rect.setY(startY - row * 60); // New row every itemsPerRow elements
            label.setLayoutX(startX + col * 110 + 30);
            label.setLayoutY(startY - row * 60 + 10);
        }
    }
}