package com.example.demo;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;

public class Queue {
    private List<LinkedListNode> elements = new ArrayList<>();
    private Pane canvas;

    public Queue(Pane canvas) {
        this.canvas = canvas;
    }

    public void enqueue(String value) {
        Circle circle = new Circle(0, 0, 20);
        circle.setFill(Color.LIGHTBLUE);
        circle.setStroke(Color.BLACK);
        Label label = new Label(value);
        label.setLayoutX(0);
        label.setLayoutY(0);

        LinkedListNode node = new LinkedListNode(value, circle, label);
        elements.add(node);
        canvas.getChildren().addAll(circle, label);
        updatePositions();
    }

    public void dequeue() {
        if (!elements.isEmpty()) {
            LinkedListNode node = elements.remove(0);
            canvas.getChildren().removeAll(node.circle, node.label, node.nextArrow);
            updatePositions();
        }
    }

    public boolean isEmpty() {
        return elements.isEmpty();
    }

    private void updatePositions() {
        double startX = 50;
        double startY = canvas.getHeight() / 2;
        for (int i = 0; i < elements.size(); i++) {
            LinkedListNode node = elements.get(i);
            node.circle.setCenterX(startX + i * 60);
            node.circle.setCenterY(startY);
            node.label.setLayoutX(startX + i * 60 - 10);
            node.label.setLayoutY(startY - 10);
            if (i < elements.size() - 1) {
                node.nextArrow.setStartX(startX + i * 60 + 20);
                node.nextArrow.setStartY(startY);
                node.nextArrow.setEndX(startX + (i + 1) * 60 - 20);
                node.nextArrow.setEndY(startY);
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