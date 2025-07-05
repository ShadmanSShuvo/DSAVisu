package com.example.demo;

import javafx.scene.control.Label;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class LinkedListNode {
    public Circle circle;
    public Label label;
    public String value;
    public Line nextArrow;

    public LinkedListNode(String value, Circle circle, Label label) {
        this.value = value;
        this.circle = circle;
        this.label = label;
        this.nextArrow = new Line();
        this.nextArrow.setStrokeWidth(2);
        this.nextArrow.setVisible(false);
    }
}