package com.example.demo;

import javafx.animation.PauseTransition;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class Heap {
    private List<Integer> heap;
    private List<Circle> circles;
    private List<Label> labels;
    private Pane canvas;
    private static final double RADIUS = 20;
    private static final double PADDING = 50;
    private static final double LEVEL_HEIGHT = 100;

    public Heap(Pane canvas) {
        this.canvas = canvas;
        this.heap = new ArrayList<>();
        this.circles = new ArrayList<>();
        this.labels = new ArrayList<>();
    }

    public void insert(int value) {
        heap.add(value);
        Circle circle = new Circle(0, 0, RADIUS);
        circle.setFill(Color.LIGHTGREEN);
        circle.setStroke(Color.BLACK);
        Label label = new Label(String.valueOf(value));
        label.setLayoutX(-5);
        label.setLayoutY(-5);
        label.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
        circles.add(circle);
        labels.add(label);
        canvas.getChildren().addAll(circle, label);
        updatePositions();
        highlightNode(circle, Color.YELLOW);
    }

    public void removeMax() {
        if (heap.isEmpty()) {
            showWarning("Heap is empty");
            return;
        }
        int max = heap.get(0);
        heap.set(0, heap.get(heap.size() - 1));
        heap.remove(heap.size() - 1);
        Circle circle = circles.remove(0);
        Label label = labels.remove(0);
        highlightNode(circle, Color.RED);
        PauseTransition pt = new PauseTransition(Duration.seconds(0.5));
        pt.setOnFinished(e -> {
            canvas.getChildren().removeAll(circle, label);
            updatePositions();
            drawLines();
        });
        pt.play();
        heapify(0);
    }

    private void heapify(int index) {
        int largest = index;
        int left = 2 * index + 1;
        int right = 2 * index + 2;

        if (left < heap.size() && heap.get(left) > heap.get(largest)) largest = left;
        if (right < heap.size() && heap.get(right) > heap.get(largest)) largest = right;

        if (largest != index) {
            int temp = heap.get(index);
            heap.set(index, heap.get(largest));
            heap.set(largest, temp);
            updatePositions();
            drawLines();
            heapify(largest);
        }
    }

    private void updatePositions() {
        canvas.getChildren().removeIf(node -> node instanceof Line);
        int height = (int) (Math.log(heap.size() + 1) / Math.log(2));
        double maxWidth = canvas.getWidth() - 2 * PADDING;
        double nodeSpacing = maxWidth / (int) Math.pow(2, height);

        for (int i = 0; i < heap.size(); i++) {
            int level = (int) (Math.log(i + 1) / Math.log(2));
            int levelSize = (int) Math.pow(2, level);
            int levelPos = i - (int) Math.pow(2, level) + 1;
            double x = PADDING + (levelPos * 2 + 1) * nodeSpacing / 2;
            double y = PADDING + level * LEVEL_HEIGHT;
            Circle circle = circles.get(i);
            Label label = labels.get(i);
            circle.setCenterX(x);
            circle.setCenterY(y);
            label.setLayoutX(x - 5);
            label.setLayoutY(y - 5);
        }
        drawLines();
    }

    private void drawLines() {
        canvas.getChildren().removeIf(node -> node instanceof Line);
        for (int i = 0; i < heap.size() / 2; i++) {
            int left = 2 * i + 1;
            int right = 2 * i + 2;
            if (left < heap.size()) {
                Line line = new Line(
                        circles.get(i).getCenterX(), circles.get(i).getCenterY(),
                        circles.get(left).getCenterX(), circles.get(left).getCenterY()
                );
                line.setStroke(Color.GRAY);
                canvas.getChildren().add(line);
            }
            if (right < heap.size()) {
                Line line = new Line(
                        circles.get(i).getCenterX(), circles.get(i).getCenterY(),
                        circles.get(right).getCenterX(), circles.get(right).getCenterY()
                );
                line.setStroke(Color.GRAY);
                canvas.getChildren().add(line);
            }
        }
    }

    private void highlightNode(Circle circle, Color color) {
        circle.setFill(color);
        PauseTransition pt = new PauseTransition(Duration.seconds(0.5));
        pt.setOnFinished(e -> circle.setFill(Color.LIGHTGREEN));
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

    public void peekMax() {
        if (heap.isEmpty()) {
            showWarning("Heap is empty");
            return;
        }
        //highlightNode(0, Color.GREEN);
        highlightNode(circles.get(0), Color.GREEN);
    }
    public boolean isEmpty() {
        return heap.isEmpty();
    }
    public void isEmptyVisual() {
        String msg = isEmpty() ? "Heap is empty" : "Heap is not empty";
        showWarning(msg);
    }

}