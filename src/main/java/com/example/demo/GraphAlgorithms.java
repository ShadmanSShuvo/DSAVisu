package com.example.demo;

import javafx.animation.PauseTransition;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import java.util.List;
import java.util.LinkedList;
import java.util.*;

public class GraphAlgorithms {
    private List<Graph.GraphNode> nodes;
    private List<Graph.GraphEdge> edges;
    private Pane canvas;

    public GraphAlgorithms(List<Graph.GraphNode> nodes, List<Graph.GraphEdge> edges, Pane canvas) {
        this.nodes = nodes;
        this.edges = edges;
        this.canvas = canvas;
    }

    public void bfs(int start) {
        if (nodes.isEmpty()) return;
        Queue queue = new com.example.demo.Queue(canvas); // Custom Queue class
        Set<Integer> visited = new HashSet<>();
        queue.enqueue(String.valueOf(start)); // Convert int to String
        visited.add(start);

        // Start the BFS visualization
        bfsStep(queue, visited);
    }

    private void bfsStep(Queue queue, Set<Integer> visited) {
        if (queue.isEmpty()) return;

        String currentStr = queue.front(); // Get head element (assuming front returns String)
        if (currentStr != null) {
            int current = Integer.parseInt(currentStr); // Convert String to int
            highlightNode(current, Color.YELLOW);

            // Process neighbors
            for (Graph.GraphEdge edge : edges) {
                if (edge.from.id == current && !visited.contains(edge.to.id)) {
                    queue.enqueue(String.valueOf(edge.to.id)); // Convert int to String
                    visited.add(edge.to.id);
                    highlightEdge(edge, Color.GREEN);
                }
            }

            queue.dequeue(); // Remove head element (assuming index 0)
        }

        // Schedule the next step with a delay
        PauseTransition pt = new PauseTransition(Duration.seconds(1));
        pt.setOnFinished(e -> bfsStep(queue, visited));
        pt.play();
    }

    public void dfs(int start) {
        if (nodes.isEmpty()) return;
        Set<Integer> visited = new HashSet<>();
        dfsRec(start, visited);
    }

    private void dfsRec(int current, Set<Integer> visited) {
        visited.add(current);
        highlightNode(current, Color.YELLOW);
        PauseTransition pt = new PauseTransition(Duration.seconds(1));
        pt.setOnFinished(e -> {
            for (Graph.GraphEdge edge : edges) {
                if (edge.from.id == current && !visited.contains(edge.to.id)) {
                    highlightEdge(edge, Color.GREEN);
                    dfsRec(edge.to.id, visited);
                }
            }
        });
        pt.play();
    }

    public void kruskal() {
        List<Graph.GraphEdge> mst = new ArrayList<>();
        Collections.sort(edges, (e1, e2) -> {
            int w1 = e1.weightLabel.getText().isEmpty() ? 0 : Integer.parseInt(e1.weightLabel.getText());
            int w2 = e2.weightLabel.getText().isEmpty() ? 0 : Integer.parseInt(e2.weightLabel.getText());
            return w1 - w2;
        });

        int[] parent = new int[nodes.size()];
        for (int i = 0; i < nodes.size(); i++) {
            parent[i] = i;
        }

        for (Graph.GraphEdge edge : edges) {
            int u = edge.from.id;
            int v = edge.to.id;
            if (find(parent, u) != find(parent, v)) {
                mst.add(edge);
                union(parent, u, v);
                highlightEdge(edge, Color.BLUE);
                PauseTransition pt = new PauseTransition(Duration.seconds(1));
                pt.play();
            }
        }
    }

    private int find(int[] parent, int i) {
        if (parent[i] != i) {
            parent[i] = find(parent, parent[i]);
        }
        return parent[i];
    }

    private void union(int[] parent, int u, int v) {
        parent[find(parent, u)] = find(parent, v);
    }

    public void prim() {
        if (nodes.isEmpty()) return;
        PriorityQueue<Graph.GraphEdge> pq = new PriorityQueue<>((e1, e2) -> {
            int w1 = e1.weightLabel.getText().isEmpty() ? 0 : Integer.parseInt(e1.weightLabel.getText());
            int w2 = e2.weightLabel.getText().isEmpty() ? 0 : Integer.parseInt(e2.weightLabel.getText());
            return w1 - w2;
        });
        Set<Integer> visited = new HashSet<>();
        visited.add(0);

        for (Graph.GraphEdge edge : edges) {
            if (edge.from.id == 0 || edge.to.id == 0) {
                pq.add(edge);
            }
        }

        PauseTransition pt = new PauseTransition(Duration.seconds(1));
        pt.setOnFinished(e -> {
            if (!pq.isEmpty()) {
                Graph.GraphEdge edge = pq.poll();
                int u = edge.from.id;
                int v = edge.to.id;
                if (!visited.contains(u) || !visited.contains(v)) {
                    highlightEdge(edge, Color.BLUE);
                    int newNode = visited.contains(u) ? v : u;
                    visited.add(newNode);
                    highlightNode(newNode, Color.YELLOW);
                    for (Graph.GraphEdge nextEdge : edges) {
                        if ((nextEdge.from.id == newNode && !visited.contains(nextEdge.to.id)) ||
                                (nextEdge.to.id == newNode && !visited.contains(nextEdge.from.id))) {
                            pq.add(nextEdge);
                        }
                    }
                    pt.play();
                } else {
                    pt.play();
                }
            }
        });
        pt.play();
    }

    private void highlightNode(int id, Color color) {
        Graph.GraphNode node = nodes.get(id);
        node.circle.setFill(color);
        PauseTransition pt = new PauseTransition(Duration.seconds(0.5));
        pt.setOnFinished(e -> node.circle.setFill(Color.WHITE));
        pt.play();
    }

    private void highlightEdge(Graph.GraphEdge edge, Color color) {
        edge.line.setStroke(color);
        PauseTransition pt = new PauseTransition(Duration.seconds(0.5));
        pt.setOnFinished(e -> edge.line.setStroke(Color.BLACK));
        pt.play();
    }
}