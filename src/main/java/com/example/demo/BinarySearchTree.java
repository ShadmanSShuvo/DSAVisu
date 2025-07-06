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

public class BinarySearchTree {
    private class Node {
        int value;
        Circle circle;
        Label label;
        Line leftEdge;
        Line rightEdge;
        Node left;
        Node right;

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

    private Node root;
    private Pane canvas;
    private List<Node> nodes = new ArrayList<>();

    public BinarySearchTree(Pane canvas) {
        this.canvas = canvas;
    }

    public void insert(int value) {
        Circle circle = new Circle(0, 0, 20);
        circle.setFill(Color.LIGHTCORAL);
        circle.setStroke(Color.BLACK);
        Label label = new Label(String.valueOf(value));
        label.setLayoutX(0);
        label.setLayoutY(0);
        Node newNode = new Node(value, circle, label);
        nodes.add(newNode);
        canvas.getChildren().addAll(circle, label);

        if (root == null) {
            root = newNode;
            updatePositions();
            return;
        }

        Node current = root;
        Node parent;
        while (true) {
            parent = current;
            if (value < current.value) {
                if (current.left == null) {
                    current.left = newNode;
                    current.leftEdge.setVisible(true);
                    canvas.getChildren().add(current.leftEdge);
                    break;
                }
                current = current.left;
            } else {
                if (current.right == null) {
                    current.right = newNode;
                    current.rightEdge.setVisible(true);
                    canvas.getChildren().add(current.rightEdge);
                    break;
                }
                current = current.right;
            }
            highlightNode(current, Color.YELLOW);
        }
        updatePositions();
    }

    public void delete(int value) {
        root = deleteRec(root, value);
        updatePositions();
    }

    private Node deleteRec(Node root, int value) {
        if (root == null) {
            showWarning("Value not found");
            return null;
        }

        if (value < root.value) {
            highlightNode(root, Color.YELLOW);
            root.left = deleteRec(root.left, value);
        } else if (value > root.value) {
            highlightNode(root, Color.YELLOW);
            root.right = deleteRec(root.right, value);
        } else {
            highlightNode(root, Color.RED);
            if (root.left == null) {
                Node temp = root.right;
                canvas.getChildren().removeAll(root.circle, root.label, root.leftEdge, root.rightEdge);
                nodes.remove(root);
                return temp;
            } else if (root.right == null) {
                Node temp = root.left;
                canvas.getChildren().removeAll(root.circle, root.label, root.leftEdge, root.rightEdge);
                nodes.remove(root);
                return temp;
            }
            Node minNode = findMin(root.right);
            root.value = minNode.value;
            root.label.setText(String.valueOf(minNode.value));
            highlightNode(root, Color.GREEN);
            root.right = deleteRec(root.right, minNode.value);
        }
        return root;
    }

    private Node findMin(Node node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    public void search(int value) {
        Node current = root;
        while (current != null) {
            highlightNode(current, Color.YELLOW);
            if (current.value == value) {
                highlightNode(current, Color.GREEN);
                return;
            } else if (value < current.value) {
                current = current.left;
            } else {
                current = current.right;
            }
        }
        showWarning("Value not found");
    }

    public void clear() {
        for (Node node : nodes) {
            canvas.getChildren().removeAll(node.circle, node.label, node.leftEdge, node.rightEdge);
        }
        nodes.clear();
        root = null;
    }

    private void highlightNode(Node node, Color color) {
        node.circle.setFill(color);
        PauseTransition pt = new PauseTransition(Duration.seconds(0.5));
        pt.setOnFinished(e -> node.circle.setFill(Color.LIGHTCORAL));
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
        if (root == null) return;
        double centerX = canvas.getWidth() / 2;
        double startY = 50;
        double levelHeight = 100; // Increased for better spacing
        double baseWidth = 800; // Widened for deeper trees
        assignPositions(root, centerX, startY, baseWidth, 0);
        for (Node node : nodes) {
            if (node.left != null) {
                node.leftEdge.setStartX(node.circle.getCenterX());
                node.leftEdge.setStartY(node.circle.getCenterY() + 20);
                node.leftEdge.setEndX(node.left.circle.getCenterX());
                node.leftEdge.setEndY(node.left.circle.getCenterY() - 20);
                node.leftEdge.setVisible(true);
            } else {
                node.leftEdge.setVisible(false);
            }
            if (node.right != null) {
                node.rightEdge.setStartX(node.circle.getCenterX());
                node.rightEdge.setStartY(node.circle.getCenterY() + 20);
                node.rightEdge.setEndX(node.right.circle.getCenterX());
                node.rightEdge.setEndY(node.right.circle.getCenterY() - 20);
                node.rightEdge.setVisible(true);
            } else {
                node.rightEdge.setVisible(false);
            }
        }
    }

    private void assignPositions(Node node, double x, double y, double width, int level) {
        if (node == null) return;
        node.circle.setCenterX(x);
        node.circle.setCenterY(y);
        node.label.setLayoutX(x - 10);
        node.label.setLayoutY(y - 10);
        double levelHeight = 100; // Increased for better spacing
        double offset = width / 2.5; // More aggressive width reduction
        assignPositions(node.left, x - offset, y + levelHeight, width / 2.5, level + 1);
        assignPositions(node.right, x + offset, y + levelHeight, width / 2.5, level + 1);
    }
}