package com.example.ds;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.PauseTransition;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;

public class BinarySearchTree {
    private Node root;
    private Pane canvas;
    private List<Node> allNodes;
    private static final double RADIUS = 20;
    //private static final double HORIZONTAL_SPACING = 60;
    private static final double VERTICAL_SPACING = 80;
    private static final double LEFT_MARGIN = 50;
    private static final double TOP_MARGIN = 50;

    public BinarySearchTree(Pane canvas) {
        this.canvas = canvas;
        this.allNodes = new ArrayList<>();
    }

    public void insert(int value) {
        root = insertRec(root, value);
        updatePositions();
    }

    private Node insertRec(Node node, int value) {
        if (node == null) {
            Node newNode = new Node(value);
            allNodes.add(newNode);
            canvas.getChildren().addAll(newNode.circle, newNode.label);
            return newNode;
        }

        if (value < node.value) {
            node.left = insertRec(node.left, value);
            node.left.parent = node;
        } else if (value > node.value) {
            node.right = insertRec(node.right, value);
            node.right.parent = node;
        }

        return node;
    }

    public void delete(int value) {
        root = deleteRec(root, value);
        updatePositions();
    }

    private Node deleteRec(Node node, int value) {
        if (node == null) {
            showWarning("Value " + value + " not found in BST");
            return node;
        }

        if (value < node.value) {
            node.left = deleteRec(node.left, value);
        } else if (value > node.value) {
            node.right = deleteRec(node.right, value);
        } else {
            // Node to be deleted found
            highlightNode(node.circle, Color.RED);

            PauseTransition pt = new PauseTransition(Duration.seconds(0.5));
            pt.setOnFinished(e -> {
                canvas.getChildren().removeAll(node.circle, node.label);
                if (node.lineToParent != null) {
                    canvas.getChildren().remove(node.lineToParent);
                }
                allNodes.remove(node);

                // Handle deletion cases
                if (node.left == null && node.right == null) {
                    // No children
                    if (node.parent != null) {
                        if (node.parent.left == node) {
                            node.parent.left = null;
                        } else {
                            node.parent.right = null;
                        }
                    } else {
                        root = null;
                    }
                } else if (node.left == null) {
                    // Only right child
                    if (node.parent != null) {
                        if (node.parent.left == node) {
                            node.parent.left = node.right;
                        } else {
                            node.parent.right = node.right;
                        }
                        node.right.parent = node.parent;
                    } else {
                        root = node.right;
                        node.right.parent = null;
                    }
                } else if (node.right == null) {
                    // Only left child
                    if (node.parent != null) {
                        if (node.parent.left == node) {
                            node.parent.left = node.left;
                        } else {
                            node.parent.right = node.left;
                        }
                        node.left.parent = node.parent;
                    } else {
                        root = node.left;
                        node.left.parent = null;
                    }
                } else {
                    // Two children
                    Node successor = getMinValueNode(node.right);
                    node.value = successor.value;
                    node.label.setText(String.valueOf(node.value));
                    node.right = deleteRec(node.right, successor.value);
                    //return node;
                }
            });
            pt.play();
        }
        return node;
    }

    private Node getMinValueNode(Node node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    public void search(int value) {
        Node found = searchRec(root, value);
        if (found != null) {
            highlightNode(found.circle, Color.GREEN);
        } else {
            showWarning("Value " + value + " not found in BST");
        }
    }

    private Node searchRec(Node node, int value) {
        if (node == null || node.value == value) {
            return node;
        }

        if (value < node.value) {
            return searchRec(node.left, value);
        }
        return searchRec(node.right, value);
    }

    public void clear() {
        for (Node node : allNodes) {
            canvas.getChildren().removeAll(node.circle, node.label);
            if (node.lineToParent != null) {
                canvas.getChildren().remove(node.lineToParent);
            }
        }
        allNodes.clear();
        root = null;
    }

    private void updatePositions() {
        if (root == null)
            return;

        // Remove all existing lines first
        for (Node node : allNodes) {
            if (node.lineToParent != null) {
                canvas.getChildren().remove(node.lineToParent);
            }
        }

        // Calculate positions using level-order traversal for better layout
        positionNodes(root, 0, 0, canvas.getWidth() - 2 * LEFT_MARGIN, canvas.getWidth() / 2, TOP_MARGIN);

        // Draw lines after positioning
        drawLines();
    }

    private void positionNodes(Node node, int level, int posInLevel, double levelWidth, double x, double y) {
        if (node == null)
            return;

        // Set position for current node
        node.circle.setCenterX(x);
        node.circle.setCenterY(y);
        node.label.setLayoutX(x - 6);
        node.label.setLayoutY(y - 6);

        // Calculate positions for children
        double childLevelWidth = levelWidth / 2;
        double childY = y + VERTICAL_SPACING;

        if (node.left != null) {
            double leftX = x - levelWidth / 4;
            positionNodes(node.left, level + 1, posInLevel * 2, childLevelWidth, leftX, childY);
        }

        if (node.right != null) {
            double rightX = x + levelWidth / 4;
            positionNodes(node.right, level + 1, posInLevel * 2 + 1, childLevelWidth, rightX, childY);
        }
    }

    private void drawLines() {
        for (Node node : allNodes) {
            if (node.parent != null) {
                // Create or update line to parent
                if (node.lineToParent == null) {
                    node.lineToParent = new Line();
                    node.lineToParent.setStroke(Color.GRAY);
                    node.lineToParent.setStrokeWidth(2);
                }

                // Update line coordinates
                node.lineToParent.setStartX(node.parent.circle.getCenterX());
                node.lineToParent.setStartY(node.parent.circle.getCenterY());
                node.lineToParent.setEndX(node.circle.getCenterX());
                node.lineToParent.setEndY(node.circle.getCenterY());

                // Add line to canvas if not already present
                if (!canvas.getChildren().contains(node.lineToParent)) {
                    canvas.getChildren().add(0, node.lineToParent); // Add at beginning so lines are behind nodes
                }
            }
        }
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

    // Inner Node class
    private class Node {
        int value;
        Node left, right, parent;
        Circle circle;
        Label label;
        Line lineToParent;

        Node(int value) {
            this.value = value;
            this.circle = new Circle(RADIUS);
            this.circle.setFill(Color.LIGHTBLUE);
            this.circle.setStroke(Color.BLACK);
            this.label = new Label(String.valueOf(value));
            this.label.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
            this.lineToParent = null; // Will be created when needed
        }
    }
}