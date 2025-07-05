package com.example.demo;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;

public class Stack {
    private List<LinkedListNode> elements = new ArrayList<>();
    private Pane canvas;

    public Stack(Pane canvas) {
        this.canvas = canvas;
    }

    public void push(String value) {
        Circle circle = new Circle(0, 0, 20);
        circle.setFill(Color.LIGHTGREEN);
        circle.setStroke(Color.BLACK);
        Label label = new Label(value);
        label.setLayoutX(0);
        label.setLayoutY(0);

        LinkedListNode node = new LinkedListNode(value, circle, label);
        elements.add(node);
        canvas.getChildren().addAll(circle, label);
        updatePositions();
    }

    public void pop() {
        if (!elements.isEmpty()) {
            LinkedListNode node = elements.remove(elements.size() - 1);
            canvas.getChildren().removeAll(node.circle, node.label, node.nextArrow);
            updatePositions();
        }
    }

    public boolean isEmpty() {
        return elements.isEmpty();
    }

    private void updatePositions() {
        double startX = canvas.getWidth() / 2;
        double startY = canvas.getHeight() - 50;
        for (int i = 0; i < elements.size(); i++) {
            LinkedListNode node = elements.get(i);
            node.circle.setCenterX(startX);
            node.circle.setCenterY(startY - i * 50);
            node.label.setLayoutX(startX - 10);
            node.label.setLayoutY(startY - i * 50 - 10);
            if (i < elements.size() - 1) {
                node.nextArrow.setStartX(startX);
                node.nextArrow.setStartY(startY - i * 50 - 20);
                node.nextArrow.setEndX(startX);
                node.nextArrow.setEndY(startY - (i + 1) * 50 + 20);
                node.nextArrow.setVisible(true);
                if (!canvas.getChildren().contains(node.nextArrow)) {
                    canvas.getChildren().add(node.nextArrow);
                }
            } else {
                node.nextArrow.setVisible(false);
            }
        }
    }
}