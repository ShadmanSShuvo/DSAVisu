package com.example.demo;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.List;

public class Heap {
    private List<LinkedListNode> elements = new ArrayList<>();
    private Pane canvas;

    public Heap(Pane canvas) {
        this.canvas = canvas;
    }

    public void insert(int value) {
        Circle circle = new Circle(0, 0, 20);
        circle.setFill(Color.LIGHTCORAL);
        circle.setStroke(Color.BLACK);
        Label label = new Label(String.valueOf(value));
        label.setLayoutX(0);
        label.setLayoutY(0);

        LinkedListNode node = new LinkedListNode(String.valueOf(value), circle, label);
        elements.add(node);
        canvas.getChildren().addAll(circle, label);
        heapifyUp(elements.size() - 1);
        updatePositions();
    }

    public void removeMax() {
        if (elements.isEmpty()) return;
        LinkedListNode max = elements.get(0);
        canvas.getChildren().removeAll(max.circle, max.label, max.nextArrow);
        if (elements.size() > 1) {
            elements.set(0, elements.remove(elements.size() - 1));
            heapifyDown(0);
        } else {
            elements.clear();
        }
        updatePositions();
    }

    public boolean isEmpty() {
        return elements.isEmpty();
    }

    private void heapifyUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            int currentValue = Integer.parseInt(elements.get(index).value);
            int parentValue = Integer.parseInt(elements.get(parent).value);
            if (currentValue > parentValue) {
                LinkedListNode temp = elements.get(index);
                elements.set(index, elements.get(parent));
                elements.set(parent, temp);
                index = parent;
            } else {
                break;
            }
        }
    }

    private void heapifyDown(int index) {
        int maxIndex = index;
        int size = elements.size();

        while (true) {
            int left = 2 * index + 1;
            int right = 2 * index + 2;

            if (left < size && Integer.parseInt(elements.get(left).value) > Integer.parseInt(elements.get(maxIndex).value)) {
                maxIndex = left;
            }
            if (right < size && Integer.parseInt(elements.get(right).value) > Integer.parseInt(elements.get(maxIndex).value)) {
                maxIndex = right;
            }
            if (index != maxIndex) {
                LinkedListNode temp = elements.get(index);
                elements.set(index, elements.get(maxIndex));
                elements.set(maxIndex, temp);
                index = maxIndex;
            } else {
                break;
            }
        }
    }

    private void updatePositions() {
        if (elements.isEmpty()) return;
        double centerX = canvas.getWidth() / 2;
        double startY = 50;
        double levelHeight = 80;
        double baseWidth = 200;

        for (int i = 0; i < elements.size(); i++) {
            LinkedListNode node = elements.get(i);
            int level = (int) (Math.log(i + 1) / Math.log(2));
            int nodesInLevel = (int) Math.pow(2, level);
            int indexInLevel = i - ((int) Math.pow(2, level) - 1);
            double xOffset = (indexInLevel - (nodesInLevel - 1) / 2.0) * (baseWidth / Math.pow(2, level));
            node.circle.setCenterX(centerX + xOffset);
            node.circle.setCenterY(startY + level * levelHeight);
            node.label.setLayoutX(centerX + xOffset - 10);
            node.label.setLayoutY(startY + level * levelHeight - 10);

            node.nextArrow.setVisible(false);
            int parent = (i - 1) / 2;
            if (i > 0) {
                LinkedListNode parentNode = elements.get(parent);
                node.nextArrow.setStartX(node.circle.getCenterX());
                node.nextArrow.setStartY(node.circle.getCenterY() - 20);
                node.nextArrow.setEndX(parentNode.circle.getCenterX());
                node.nextArrow.setEndY(parentNode.circle.getCenterY() + 20);
                node.nextArrow.setVisible(true);
                if (!canvas.getChildren().contains(node.nextArrow)) {
                    canvas.getChildren().add(node.nextArrow);
                }
            }
        }
    }
}