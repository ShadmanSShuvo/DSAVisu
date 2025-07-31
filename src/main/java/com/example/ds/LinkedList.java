// LinkedList.java
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

/**
 * Manages the visualization and logic for a singly linked list.
 * Encapsulates the list structure and its graphical representation.
 */
public class LinkedList {

    /**
     * Represents a node in the visualized linked list.
     * Encapsulates the visual elements (Circle, Label, Arrow) for a node.
     */
    public static class LinkedListNode {
        public String value;
        public Circle circle;
        public Label label;
        public Line nextArrow;

        /**
         * Constructor for LinkedListNode.
         *
         * @param value  The string value stored in the node.
         * @param circle The Circle shape representing the node visually.
         * @param label  The Label displaying the node's value.
         */
        public LinkedListNode(String value, Circle circle, Label label) {
            this.value = value;
            this.circle = circle;
            this.label = label;
            // Initialize the arrow pointing to the next node (initially invisible)
            this.nextArrow = new Line();
            this.nextArrow.setStroke(Color.BLACK);
            this.nextArrow.setVisible(false); // Initially hidden, shown when connected
        }
    }

    private List<LinkedListNode> nodes;
    private Pane canvas;
    private static final double NODE_RADIUS = 20.0;
    private static final double HORIZONTAL_SPACING = 60.0;
    private static final double START_X = 50.0;

    /**
     * Constructor for the LinkedList visualizer.
     *
     * @param canvas The JavaFX Pane where the list will be drawn.
     */
    public LinkedList(Pane canvas) {
        this.nodes = new ArrayList<>();
        this.canvas = canvas;
    }

    /**
     * Adds a new node with the given value to the end of the list.
     *
     * @param value The value for the new node.
     */
    public void addNode(String value) {
        Circle circle = new Circle(0, 0, NODE_RADIUS);
        circle.setFill(Color.LIGHTYELLOW);
        circle.setStroke(Color.BLACK);
        Label label = new Label(value);
        LinkedListNode node = new LinkedListNode(value, circle, label);
        nodes.add(node);
        canvas.getChildren().addAll(circle, label); // Add visual elements to the pane
        updatePositions();
    }

    /**
     * Inserts a new node at the specified index.
     *
     * @param value The value for the new node.
     * @param index The index where the node should be inserted.
     * @throws IndexOutOfBoundsException if the index is out of range (index < 0 ||
     *                                   index > size()).
     */
    public void insertAt(String value, int index) throws IndexOutOfBoundsException {
        if (index < 0 || index > nodes.size()) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + nodes.size());
        }
        Circle circle = new Circle(0, 0, NODE_RADIUS);
        circle.setFill(Color.LIGHTYELLOW);
        circle.setStroke(Color.BLACK);
        Label label = new Label(value);
        LinkedListNode node = new LinkedListNode(value, circle, label);
        nodes.add(index, node);
        canvas.getChildren().addAll(circle, label); // Add visual elements to the pane
        updatePositions();
        highlightNode(node, Color.GREEN); // Visually indicate insertion
    }

    /**
     * Removes the node at the specified index.
     *
     * @param index The index of the node to remove.
     * @return The value of the removed node.
     * @throws IndexOutOfBoundsException if the index is out of range (index < 0 ||
     *                                   index >= size()).
     */
    public String removeAt(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= nodes.size()) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + nodes.size());
        }
        LinkedListNode node = nodes.remove(index);
        canvas.getChildren().removeAll(node.circle, node.label, node.nextArrow); // Remove visuals
        updatePositions();
        return node.value;
    }

    /**
     * Removes the last node from the list.
     *
     * @return The value of the removed node, or null if the list is empty.
     */
    public String removeLast() {
        if (nodes.isEmpty()) {
            return null;
        }
        return removeAt(nodes.size() - 1);
    }

    /**
     * Searches for the first node with the given value and highlights it.
     *
     * @param value The value to search for.
     * @return The index of the first node with the value, or -1 if not found.
     */
    public int search(String value) {
        for (int i = 0; i < nodes.size(); i++) {
            LinkedListNode node = nodes.get(i);
            if (node.value.equals(value)) {
                highlightNode(node, Color.GREEN); // Highlight first match
                return i;
            }
        }
        // Value not found, could trigger a warning in the controller if needed
        return -1;
    }

    /**
     * Checks if the list is empty.
     *
     * @return true if the list is empty, false otherwise.
     */
    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    /**
     * Gets the current number of nodes in the list.
     *
     * @return The size of the list.
     */
    public int size() {
        return nodes.size();
    }

    /**
     * Clears all nodes from the list and removes their visuals from the canvas.
     */
    public void clear() {
        // Iterate backwards to avoid index shifting issues during removal
        for (int i = nodes.size() - 1; i >= 0; i--) {
            LinkedListNode node = nodes.get(i);
            canvas.getChildren().removeAll(node.circle, node.label, node.nextArrow);
        }
        nodes.clear();
        // No need to call updatePositions as the list is now empty
    }

    /**
     * Updates the positions of all nodes and arrows on the canvas.
     * Should be called after any structural change to the list.
     */
    private void updatePositions() {
        double startY = canvas.getHeight() / 2; // Position list vertically centered

        for (int i = 0; i < nodes.size(); i++) {
            LinkedListNode node = nodes.get(i);

            // Position Circle and Label
            double nodeCenterX = START_X + i * HORIZONTAL_SPACING;
            node.circle.setCenterX(nodeCenterX);
            node.circle.setCenterY(startY);
            node.label.setLayoutX(nodeCenterX - node.label.getWidth() / 2); // Center label
            node.label.setLayoutY(startY - node.label.getHeight() / 2);

            // Position and visibility of Arrow
            if (i < nodes.size() - 1) { // If not the last node
                // Connect arrow from current node to the next node
                node.nextArrow.setStartX(nodeCenterX + NODE_RADIUS); // Right edge of current circle
                node.nextArrow.setStartY(startY);
                node.nextArrow.setEndX(START_X + (i + 1) * HORIZONTAL_SPACING - NODE_RADIUS); // Left edge of next
                                                                                              // circle
                node.nextArrow.setEndY(startY);
                node.nextArrow.setVisible(true);
                // Ensure arrow is added to canvas (might be needed on first layout)
                if (!canvas.getChildren().contains(node.nextArrow)) {
                    canvas.getChildren().add(node.nextArrow);
                }
            } else {
                // Hide arrow for the last node
                node.nextArrow.setVisible(false);
            }
        }
    }

    /**
     * Temporarily changes a node's color to indicate an action.
     * 
     * @param node  The node to highlight.
     * @param color The color to highlight with.
     */
    private void highlightNode(LinkedListNode node, Color color) {
        Color originalColor = (Color) node.circle.getFill();
        node.circle.setFill(color);
        PauseTransition pt = new PauseTransition(Duration.seconds(1)); // Shorter duration
        pt.setOnFinished(e -> node.circle.setFill(originalColor)); // Reset color
        pt.play();
    }

}