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
    private class Node {
        int value;
        Circle circle;
        Label label;
        Line leftEdge;
        Line rightEdge;

        Node(int value, Circle circle, Label label) {
            this.value = value;
            this.circle = circle;
            this.label = label;
            this.leftEdge = new Line();
            this.rightEdge = new Line();
            this.leftEdge.setVisible(false);
            this.rightEdge.setVisible(false);
        }
    }

    private List<Node> heap;
    private Pane canvas;
    private List<Line> edges;

    public Heap(Pane canvas) {
        this.canvas = canvas;
        this.heap = new ArrayList<>();
        this.edges = new ArrayList<>();
    }

    public void insert(int value) {
        Circle circle = new Circle(0, 0, 20);
        circle.setFill(Color.LIGHTGREEN);
        circle.setStroke(Color.BLACK);
        Label label = new Label(String.valueOf(value));
        label.setLayoutX(0);
        label.setLayoutY(0);
        Node newNode = new Node(value, circle, label);
        heap.add(newNode);
        canvas.getChildren().addAll(circle, label);

        int index = heap.size() - 1;
        highlightNode(index, Color.YELLOW);
        heapifyUp(index);
        updatePositions();
    }

    private void heapifyUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            if (heap.get(index).value > heap.get(parent).value) {
                swap(index, parent);
                highlightNode(parent, Color.YELLOW);
                index = parent;
            } else {
                break;
            }
        }
    }

    public void removeMax() {
        if (heap.isEmpty()) {
            showWarning("Heap is empty");
            return;
        }
        Node max = heap.get(0);
        highlightNode(0, Color.RED);
        canvas.getChildren().removeAll(max.circle, max.label, max.leftEdge, max.rightEdge);
        heap.set(0, heap.get(heap.size() - 1));
        heap.remove(heap.size() - 1);
        if (!heap.isEmpty()) {
            heapifyDown(0);
        }
        updatePositions();
    }

    private void heapifyDown(int index) {
        int maxIndex = index;
        int leftChild = 2 * index + 1;
        int rightChild = 2 * index + 2;

        if (leftChild < heap.size() && heap.get(leftChild).value > heap.get(maxIndex).value) {
            maxIndex = leftChild;
        }
        if (rightChild < heap.size() && heap.get(rightChild).value > heap.get(maxIndex).value) {
            maxIndex = rightChild;
        }

        if (index != maxIndex) {
            swap(index, maxIndex);
            highlightNode(maxIndex, Color.YELLOW);
            heapifyDown(maxIndex);
        }
    }

    public void peekMax() {
        if (heap.isEmpty()) {
            showWarning("Heap is empty");
            return;
        }
        highlightNode(0, Color.GREEN);
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }

    public void isEmptyVisual() {
        String msg = isEmpty() ? "Heap is empty" : "Heap is not empty";
        showWarning(msg);
    }

    private void swap(int i, int j) {
        Node temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }

    private void highlightNode(int index, Color color) {
        Node node = heap.get(index);
        node.circle.setFill(color);
        PauseTransition pt = new PauseTransition(Duration.seconds(0.5));
        pt.setOnFinished(e -> node.circle.setFill(Color.LIGHTGREEN));
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
        canvas.getChildren().removeAll(edges);
        edges.clear();
        if (heap.isEmpty()) return;

        double centerX = canvas.getWidth() / 2;
        double startY = 50;
        double levelHeight = 80;
        double baseWidth = 600; // Widened for larger trees (supports height > 4)

        for (int i = 0; i < heap.size(); i++) {
            int level = (int) Math.floor(Math.log(i + 1) / Math.log(2));
            int nodesInLevel = (int) Math.pow(2, level);
            int indexInLevel = i - ((int) Math.pow(2, level) - 1);
            double x = centerX + (indexInLevel - nodesInLevel / 2.0 + 0.5) * (baseWidth / nodesInLevel);
            double y = startY + level * levelHeight;

            Node node = heap.get(i);
            node.circle.setCenterX(x);
            node.circle.setCenterY(y);
            node.label.setLayoutX(x - 10);
            node.label.setLayoutY(y - 10);

            int leftChild = 2 * i + 1;
            int rightChild = 2 * i + 2;

            if (leftChild < heap.size()) {
                node.leftEdge.setStartX(x);
                node.leftEdge.setStartY(y + 20);
                node.leftEdge.setEndX(heap.get(leftChild).circle.getCenterX());
                node.leftEdge.setEndY(heap.get(leftChild).circle.getCenterY() - 20);
                node.leftEdge.setVisible(true);
                if (!canvas.getChildren().contains(node.leftEdge)) {
                    canvas.getChildren().add(node.leftEdge);
                }
                edges.add(node.leftEdge);
            } else {
                node.leftEdge.setVisible(false);
            }

            if (rightChild < heap.size()) {
                node.rightEdge.setStartX(x);
                node.rightEdge.setStartY(y + 20);
                node.rightEdge.setEndX(heap.get(rightChild).circle.getCenterX());
                node.rightEdge.setEndY(heap.get(rightChild).circle.getCenterY() - 20);
                node.rightEdge.setVisible(true);
                if (!canvas.getChildren().contains(node.rightEdge)) {
                    canvas.getChildren().add(node.rightEdge);
                }
                edges.add(node.rightEdge);
            } else {
                node.rightEdge.setVisible(false);
            }
        }
    }
}