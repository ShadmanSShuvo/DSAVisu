package com.example.ds;

import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
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
    private boolean isMaxHeap;
    private static final double RADIUS = 20;
    private static final double HORIZONTAL_SPACING = 60; // Default spacing
    private static final double VERTICAL_SPACING = 80;
    private static final double LEFT_MARGIN = 50;
    private static final double TOP_MARGIN = 50;
    private static final double ANIMATION_DURATION = 0.3;

    public Heap(Pane canvas, boolean isMaxHeap) {
        this.canvas = canvas;
        this.isMaxHeap = isMaxHeap;
        this.heap = new ArrayList<>();
        this.circles = new ArrayList<>();
        this.labels = new ArrayList<>();
    }

    public void insert(int value) {
        heap.add(value);
        Circle circle = new Circle(RADIUS);
        circle.setFill(Color.LIGHTGREEN);
        circle.setStroke(Color.BLACK);
        Label label = new Label(String.valueOf(value));
        label.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
        label.setTranslateX(-6);
        label.setTranslateY(-6);

        circles.add(circle);
        labels.add(label);

        canvas.getChildren().addAll(circle, label);

        // Heapify up to maintain heap property
        heapifyUp(heap.size() - 1);

        // Update positions and draw lines
        updatePositions();
    }

    private void heapifyUp(int index) {
        if (index <= 0)
            return;

        int parentIndex = (index - 1) / 2;
        boolean shouldSwap = isMaxHeap ? heap.get(index) > heap.get(parentIndex)
                : heap.get(index) < heap.get(parentIndex);

        if (shouldSwap) {
            // Swap values
            int temp = heap.get(index);
            heap.set(index, heap.get(parentIndex));
            heap.set(parentIndex, temp);

            // Animate the swap visually
            animateSwap(index, parentIndex);

            // Continue heapifying up
            PauseTransition pt = new PauseTransition(Duration.seconds(ANIMATION_DURATION));
            pt.setOnFinished(e -> heapifyUp(parentIndex));
            pt.play();
        }
    }

    public void removeRoot() {
        if (heap.isEmpty()) {
            showWarning(isMaxHeap ? "Max Heap is empty" : "Min Heap is empty");
            return;
        }

        if (heap.size() == 1) {
            Circle circle = circles.remove(0);
            Label label = labels.remove(0);
            heap.clear();
            highlightNode(circle, Color.RED);

            PauseTransition pt = new PauseTransition(Duration.seconds(0.5));
            pt.setOnFinished(e -> {
                canvas.getChildren().removeAll(circle, label);
                updatePositions();
            });
            pt.play();
            return;
        }

        // Replace root with last element
        int lastValue = heap.get(heap.size() - 1);
        heap.set(0, lastValue);
        heap.remove(heap.size() - 1);

        // Remove the last visual node
        Circle removedCircle = circles.remove(circles.size() - 1);
        Label removedLabel = labels.remove(labels.size() - 1);

        // Highlight the node being removed
        highlightNode(removedCircle, Color.RED);

        // Animate removal
        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
        pause.setOnFinished(e -> {
            canvas.getChildren().removeAll(removedCircle, removedLabel);
            updatePositions(); // Re-layout everything
            heapifyDown(0); // Restore heap property
        });
        pause.play();
    }

    private void heapifyDown(int index) {
        if (index >= heap.size())
            return;

        int extremeIndex = findExtremeIndex(index);

        if (extremeIndex != index) {
            // Swap values
            int temp = heap.get(index);
            heap.set(index, heap.get(extremeIndex));
            heap.set(extremeIndex, temp);

            // Animate the swap visually
            animateSwap(index, extremeIndex);

            // Recursive heapify down
            PauseTransition pt = new PauseTransition(Duration.seconds(ANIMATION_DURATION));
            pt.setOnFinished(e -> heapifyDown(extremeIndex));
            pt.play();
        }
    }

    private int findExtremeIndex(int index) {
        int extremeIndex = index;
        int left = 2 * index + 1;
        int right = 2 * index + 2;

        if (left < heap.size()) {
            boolean isLeftExtreme = isMaxHeap ? heap.get(left) > heap.get(extremeIndex)
                    : heap.get(left) < heap.get(extremeIndex);
            if (isLeftExtreme) {
                extremeIndex = left;
            }
        }

        if (right < heap.size()) {
            boolean isRightExtreme = isMaxHeap ? heap.get(right) > heap.get(extremeIndex)
                    : heap.get(right) < heap.get(extremeIndex);
            if (isRightExtreme) {
                extremeIndex = right;
            }
        }

        return extremeIndex;
    }

    private void animateSwap(int i, int j) {
        Circle c1 = circles.get(i);
        Circle c2 = circles.get(j);
        Label l1 = labels.get(i);
        Label l2 = labels.get(j);

        double x1 = c1.getCenterX(), y1 = c1.getCenterY();
        double x2 = c2.getCenterX(), y2 = c2.getCenterY();

        TranslateTransition tt1 = new TranslateTransition(Duration.seconds(ANIMATION_DURATION), c1);
        tt1.setByX(x2 - x1);
        tt1.setByY(y2 - y1);

        TranslateTransition tt2 = new TranslateTransition(Duration.seconds(ANIMATION_DURATION), c2);
        tt2.setByX(x1 - x2);
        tt2.setByY(y1 - y2);

        TranslateTransition lt1 = new TranslateTransition(Duration.seconds(ANIMATION_DURATION), l1);
        lt1.setByX(x2 - x1);
        lt1.setByY(y2 - y1);

        TranslateTransition lt2 = new TranslateTransition(Duration.seconds(ANIMATION_DURATION), l2);
        lt2.setByX(x1 - x2);
        lt2.setByY(y1 - y2);

        tt1.play();
        tt2.play();
        lt1.play();
        lt2.play();

        PauseTransition pt = new PauseTransition(Duration.seconds(ANIMATION_DURATION));
        pt.setOnFinished(e -> {
            circles.set(i, c2);
            circles.set(j, c1);
            labels.set(i, l2);
            labels.set(j, l1);

            // Update positions after swap
            updatePositions();
        });
        pt.play();
    }

    private void updatePositions() {
        // Remove all lines
        canvas.getChildren().removeIf(node -> node instanceof Line);

        if (heap.isEmpty())
            return;

        // Calculate proper spacing to prevent overflow
        int height = (int) (Math.log(heap.size()) / Math.log(2)) + 1; // Max levels
        double width = Math.max(canvas.getWidth(), 400); // Minimum width
        double levelWidth = width - 2 * LEFT_MARGIN;

        // Adjust horizontal spacing based on number of nodes in the deepest level
        int maxNodesInLastLevel = 1 << (height - 1); // 2^(height-1)
        double nodeSpacing = Math.min(levelWidth / maxNodesInLastLevel, 100); // Cap maximum spacing

        for (int i = 0; i < heap.size(); i++) {
            // Calculate level and position in level
            int level = (int) (Math.log(i + 1) / Math.log(2));
            int levelIndex = i - (1 << level) + 1; // position in current level (0-indexed)
            int nodesInLevel = 1 << level; // 2^level

            // Calculate x position with proper spacing
            double totalLevelWidth = nodeSpacing * (nodesInLevel - 1);
            double startX = (width - totalLevelWidth) / 2; // Center the level
            double x = startX + levelIndex * nodeSpacing;
            double y = TOP_MARGIN + level * VERTICAL_SPACING;

            Circle circle = circles.get(i);
            Label label = labels.get(i);

            // Reset translate if previously moved
            circle.setTranslateX(0);
            circle.setTranslateY(0);
            label.setTranslateX(0);
            label.setTranslateY(0);

            circle.setCenterX(x);
            circle.setCenterY(y);
            label.setLayoutX(x - 6);
            label.setLayoutY(y - 6);
        }

        drawLines();
    }

    private void drawLines() {
        // Draw lines first (behind circles)
        List<Line> lines = new ArrayList<>();

        for (int i = 0; i < heap.size(); i++) {
            int left = 2 * i + 1;
            int right = 2 * i + 2;

            Circle parent = circles.get(i);

            if (left < heap.size()) {
                Circle child = circles.get(left);
                Line line = new Line(
                        parent.getCenterX(), parent.getCenterY(),
                        child.getCenterX(), child.getCenterY());
                line.setStroke(Color.GRAY);
                line.setStrokeWidth(1.5);
                lines.add(line);
            }

            if (right < heap.size()) {
                Circle child = circles.get(right);
                Line line = new Line(
                        parent.getCenterX(), parent.getCenterY(),
                        child.getCenterX(), child.getCenterY());
                line.setStroke(Color.GRAY);
                line.setStrokeWidth(1.5);
                lines.add(line);
            }
        }

        // Add lines to canvas first (so they appear behind circles)
        canvas.getChildren().addAll(0, lines); // Add at the beginning
    }

    private void highlightNode(Circle circle, Color color) {
        Color original = (Color) circle.getFill();
        circle.setFill(color);

        PauseTransition pt = new PauseTransition(Duration.seconds(0.5));
        pt.setOnFinished(e -> circle.setFill(original));
        pt.play();
    }

    private void showWarning(String msg) {
        Label warning = new Label(msg);
        warning.setStyle(
                "-fx-background-color: #ffeb3b; " +
                        "-fx-text-fill: #d32f2f; " +
                        "-fx-padding: 8; " +
                        "-fx-border-radius: 4; " +
                        "-fx-background-radius: 4;");
        warning.setLayoutX(10);
        warning.setLayoutY(10);
        canvas.getChildren().add(warning);

        PauseTransition pt = new PauseTransition(Duration.seconds(2));
        pt.setOnFinished(e -> canvas.getChildren().remove(warning));
        pt.play();
    }

    public void peekRoot() {
        if (heap.isEmpty()) {
            showWarning(isMaxHeap ? "Max Heap is empty" : "Min Heap is empty");
            return;
        }
        highlightNode(circles.get(0), Color.CYAN);
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }

    public void isEmptyVisual() {
        showWarning(isEmpty() ? (isMaxHeap ? "Max Heap is empty" : "Min Heap is empty")
                : (isMaxHeap ? "Max Heap is not empty" : "Min Heap is not empty"));
    }

    public String getHeapType() {
        return isMaxHeap ? "Max Heap" : "Min Heap";
    }

    public List<Integer> getHeapArray() {
        return new ArrayList<>(heap); // Return copy
    }
}