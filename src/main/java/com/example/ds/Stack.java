package com.example.ds;

import javafx.animation.PauseTransition;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class Stack {
    private List<String> stack;
    private List<Rectangle> rectangles;
    private List<Label> labels;
    private Pane canvas;

    public Stack(Pane canvas) {
        this.canvas = canvas;
        this.stack = new ArrayList<>();
        this.rectangles = new ArrayList<>();
        this.labels = new ArrayList<>();
    }

    public void push(String value) {
        Rectangle rect = new Rectangle(50, 0, 100, 40);
        rect.setFill(Color.LIGHTBLUE);
        rect.setStroke(Color.BLACK);
        Label label = new Label(value);
        label.setLayoutX(80);
        label.setLayoutY(10);
        stack.add(value);
        rectangles.add(rect);
        labels.add(label);
        canvas.getChildren().addAll(rect, label);
        highlightElement(rect, Color.YELLOW);
        updatePositions();
    }

    public void pop() {
        if (stack.isEmpty()) {
            showWarning("Stack is empty");
            return;
        }
        String value = stack.remove(stack.size() - 1);
        Rectangle rect = rectangles.remove(rectangles.size() - 1);
        Label label = labels.remove(labels.size() - 1);
        highlightElement(rect, Color.RED);
        PauseTransition pt = new PauseTransition(Duration.seconds(0.5));
        pt.setOnFinished(e -> {
            canvas.getChildren().removeAll(rect, label);
            updatePositions();
        });
        pt.play();
    }

    public void peek() {
        if (stack.isEmpty()) {
            showWarning("Stack is empty");
            return;
        }
        Rectangle rect = rectangles.get(rectangles.size() - 1);
        highlightElement(rect, Color.GREEN);
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }

    public void isEmptyVisual() {
        String msg = isEmpty() ? "Stack is empty" : "Stack is not empty";
        showWarning(msg);
    }

    private void highlightElement(Rectangle rect, Color color) {
        rect.setFill(color);
        PauseTransition pt = new PauseTransition(Duration.seconds(0.5));
        pt.setOnFinished(e -> rect.setFill(Color.LIGHTBLUE));
        pt.play();
    }

    private void showWarning(String msg) {
        Label warning = new Label(msg);
        warning.setStyle(
                "-fx-background-color: #ffeb3b; -fx-text-fill: #d32f2f; -fx-padding: 5; -fx-border-radius: 5;");
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
        for (int i = 0; i < stack.size(); i++) {
            Rectangle rect = rectangles.get(i);
            Label label = labels.get(i);
            rect.setX(startX);
            rect.setY(startY - (i + 1) * 50);
            label.setLayoutX(startX + 30);
            label.setLayoutY(startY - (i + 1) * 50 + 10);
        }
    }
}