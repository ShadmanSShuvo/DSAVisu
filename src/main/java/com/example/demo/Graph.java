package com.example.demo;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Graph {
    public static class GraphNode {
        public Circle circle;
        public Label label;
        public int id;
        public Polygon arrow;

        public GraphNode(int id, Circle circle, Label label) {
            this.id = id;
            this.circle = circle;
            this.label = label;
            this.arrow = new Polygon();
            arrow.getPoints().addAll(0.0, 0.0, 10.0, 20.0, -10.0, 20.0);
            arrow.setFill(Color.DARKBLUE);
            arrow.setVisible(false);
        }
    }

    public static class GraphEdge {
        public GraphNode from;
        public GraphNode to;
        public Line line;
        public Label weightLabel;

        public GraphEdge(GraphNode from, GraphNode to, Line line) {
            this.from = from;
            this.to = to;
            this.line = line;
            this.weightLabel = new Label();
            this.weightLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
            this.weightLabel.setTextFill(Color.BLACK);
            this.weightLabel.setVisible(false);
        }
    }
}