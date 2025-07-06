package com.example.demo;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.PauseTransition;

import java.io.File;
import java.nio.file.Files;
import java.util.*;

public class DataVisu extends Application {
    private enum Mode { GRAPH, LINKED_LIST, STACK, QUEUE, HEAP, BST }

    private Mode currentMode;
    private ToggleButton activeButton;
    private Graph.GraphNode selectedNode = null;
    private final List<Graph.GraphNode> graphNodes = new ArrayList<>();
    private final List<Graph.GraphEdge> graphEdges = new ArrayList<>();
    private final Map<Circle, Graph.GraphNode> circleNodeMap = new HashMap<>();
    private final Map<Line, Graph.GraphEdge> lineEdgeMap = new HashMap<>();
    private Stack stack;
    private Queue queue;
    private Heap heap;
    private BinarySearchTree bst;
    private List<LinkedListNode> linkedList = new ArrayList<>();
    private Stage stage;
    private String initialMode;

    private VBox leftPanel;
    private MenuButton createDataStructureMenu;
    private Pane canvas;

    private ToggleButton addNodeBtn;
    private ToggleButton addEdgeBtn;
    private ToggleButton colorBtn;
    private ToggleButton assignWeightBtn;
    private ToggleButton removeNodeBtn;
    private ToggleButton removeEdgeBtn;

    public DataVisu(String mode) {
        this.initialMode = mode;
        switch (mode) {
            case "Graph": this.currentMode = Mode.GRAPH; break;
            case "Linked List": this.currentMode = Mode.LINKED_LIST; break;
            case "Stack": this.currentMode = Mode.STACK; break;
            case "Queue": this.currentMode = Mode.QUEUE; break;
            case "Heap": this.currentMode = Mode.HEAP; break;
            case "Binary Search Tree": this.currentMode = Mode.BST; break;
            default: this.currentMode = Mode.GRAPH;
        }
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        leftPanel = new VBox(10);
        leftPanel.setPrefWidth(300);
        leftPanel.setPadding(new Insets(10));
        leftPanel.setStyle("-fx-background-color: #ffffff; -fx-border-color: #dcdcdc; -fx-border-width: 1;");

        createDataStructureMenu = new MenuButton("Select Data Structure");
        MenuItem graphItem = new MenuItem("Graph");
        MenuItem linkedListItem = new MenuItem("Linked List");
        MenuItem stackItem = new MenuItem("Stack");
        MenuItem queueItem = new MenuItem("Queue");
        MenuItem heapItem = new MenuItem("Heap");
        MenuItem bstItem = new MenuItem("Binary Search Tree");
        MenuItem sortingItem = new MenuItem("Sorting");
        createDataStructureMenu.getItems().addAll(graphItem, linkedListItem, stackItem, queueItem, heapItem, bstItem, sortingItem);

        leftPanel.getChildren().add(createDataStructureMenu);

        canvas = new Pane();
        canvas.setStyle("-fx-background-color: #e6f7ff; -fx-border-color: #dcdcdc;");

        addNodeBtn = new ToggleButton("Add Node");
        addEdgeBtn = new ToggleButton("Add Edge");
        colorBtn = new ToggleButton("Color Node");
        assignWeightBtn = new ToggleButton("Assign Weight");
        removeNodeBtn = new ToggleButton("Remove Node");
        removeEdgeBtn = new ToggleButton("Remove Edge");

        List<ToggleButton> graphButtons = List.of(
                addNodeBtn, addEdgeBtn, colorBtn, assignWeightBtn, removeNodeBtn, removeEdgeBtn
        );

        for (ToggleButton btn : graphButtons) {
            btn.setOnAction(e -> activateMode(btn));
            btn.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-background-radius: 5;");
        }

        HBox graphControlBar = new HBox(10);
        graphControlBar.setPadding(new Insets(10));
        graphControlBar.getChildren().addAll(graphButtons);

        Button clearBtn = new Button("Clear");
        clearBtn.setOnAction(e -> clearCurrentStructure());
        clearBtn.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-background-radius: 5;");

        MenuButton algorithmMenu = new MenuButton("Choose Algorithm");
        List<String> algorithms = List.of("BFS", "DFS", "Kruskal", "Prim");
        for (String algo : algorithms) {
            MenuItem item = new MenuItem(algo);
            item.setOnAction(e -> {
                algorithmMenu.setText(algo);
                runAlgorithm(algo);
            });
            algorithmMenu.getItems().add(item);
        }
        algorithmMenu.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-background-radius: 5;");

        Button backBtn = new Button("Back to Main");
        backBtn.setOnAction(e -> new Program().start(stage));
        backBtn.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-background-radius: 5;");

        Button quitBtn = new Button("Quit Program");
        quitBtn.setOnAction(e -> Platform.exit());
        quitBtn.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-background-radius: 5;");

        HBox controlBar = new HBox(10);
        controlBar.setPadding(new Insets(10));
        controlBar.getChildren().addAll(clearBtn, algorithmMenu, backBtn, quitBtn);

        VBox rightPanel = new VBox(controlBar, canvas);
        VBox.setVgrow(canvas, Priority.ALWAYS);

        // Setup data structure menu handlers
        graphItem.setOnAction(e -> switchToMode(Mode.GRAPH));
        linkedListItem.setOnAction(e -> switchToMode(Mode.LINKED_LIST));
        stackItem.setOnAction(e -> switchToMode(Mode.STACK));
        queueItem.setOnAction(e -> switchToMode(Mode.QUEUE));
        heapItem.setOnAction(e -> switchToMode(Mode.HEAP));
        bstItem.setOnAction(e -> switchToMode(Mode.BST));
        sortingItem.setOnAction(e -> new SortingAlgorithms().start(stage));

        // Graph-specific menu
        MenuButton createGraphMenu = new MenuButton("Create Graph");
        MenuItem adjacencyMatrixItem = new MenuItem("Adjacency Matrix");
        MenuItem adjacencyListItem = new MenuItem("Adjacency List");
        Menu menuUsingEdges = new Menu("Using Edges");
        MenuItem manualEdgesItem = new MenuItem("Manual");
        MenuItem fileEdgesItem = new MenuItem("From File");
        menuUsingEdges.getItems().addAll(manualEdgesItem, fileEdgesItem);
        createGraphMenu.getItems().addAll(adjacencyMatrixItem, adjacencyListItem, menuUsingEdges);
        createGraphMenu.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-background-radius: 5;");

        adjacencyMatrixItem.setOnAction(e -> {
            createDataStructureMenu.setText("Graph: Adjacency Matrix");
            showTextInputPane("Adjacency Matrix Input", this::parseAdjacencyMatrix);
        });
        adjacencyListItem.setOnAction(e -> {
            createDataStructureMenu.setText("Graph: Adjacency List");
            showTextInputPane("Adjacency List Input", this::parseAdjacencyList);
        });
        manualEdgesItem.setOnAction(e -> {
            createDataStructureMenu.setText("Graph: Manual Edges");
            showTextInputPane("Manual Edges Input\n(Formats: u v  or  u->v  or  u<->v)", this::parseManualEdges);
        });
        fileEdgesItem.setOnAction(e -> {
            createDataStructureMenu.setText("Graph: From File");
            openFileAndParseEdges();
        });

        canvas.setOnMouseClicked(event -> {
            if (currentMode == Mode.GRAPH && activeButton == addNodeBtn) {
                double x = event.getX();
                double y = event.getY();
                addGraphNodeAtPosition(x, y);
            }
        });

        HBox root = new HBox(leftPanel, rightPanel);
        HBox.setHgrow(rightPanel, Priority.ALWAYS);

        Scene scene = new Scene(root, 1200, 700);
        scene.getStylesheets().add("styles.css");
        stage.setTitle("Data Structure Visualizer - " + initialMode);
        stage.setScene(scene);
        stage.show();

        // Initialize data structures
        stack = new Stack(canvas);
        queue = new Queue(canvas);
        heap = new Heap(canvas);
        bst = new BinarySearchTree(canvas);
        switchToMode(currentMode);
    }

    private void switchToMode(Mode mode) {
        currentMode = mode;
        clearCurrentStructure();
        leftPanel.getChildren().clear();
        leftPanel.getChildren().add(createDataStructureMenu);

        // Clear existing control bar and rebuild
        VBox rightPanel = (VBox) canvas.getParent();
        rightPanel.getChildren().remove(0); // Remove old control bar
        HBox controlBar = new HBox(10);
        controlBar.setPadding(new Insets(10));
        Button clearBtn = new Button("Clear");
        clearBtn.setOnAction(e -> clearCurrentStructure());
        clearBtn.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-background-radius: 5;");
        Button backBtn = new Button("Back to Main");
        backBtn.setOnAction(e -> new Program().start(stage));
        backBtn.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-background-radius: 5;");
        Button quitBtn = new Button("Quit Program");
        quitBtn.setOnAction(e -> Platform.exit());
        quitBtn.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-background-radius: 5;");

        MenuButton algorithmMenu = new MenuButton("Choose Algorithm");
        algorithmMenu.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-background-radius: 5;");

        switch (mode) {
            case GRAPH:
                createDataStructureMenu.setText("Graph");
                MenuButton createGraphMenu = new MenuButton("Create Graph");
                MenuItem adjacencyMatrixItem = new MenuItem("Adjacency Matrix");
                MenuItem adjacencyListItem = new MenuItem("Adjacency List");
                Menu menuUsingEdges = new Menu("Using Edges");
                MenuItem manualEdgesItem = new MenuItem("Manual");
                MenuItem fileEdgesItem = new MenuItem("From File");
                menuUsingEdges.getItems().addAll(manualEdgesItem, fileEdgesItem);
                createGraphMenu.getItems().addAll(adjacencyMatrixItem, adjacencyListItem, menuUsingEdges);
                createGraphMenu.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-background-radius: 5;");
                leftPanel.getChildren().add(createGraphMenu);

                adjacencyMatrixItem.setOnAction(e -> {
                    createDataStructureMenu.setText("Graph:\nAdjacency Matrix");
                    showTextInputPane("Adjacency Matrix Input", this::parseAdjacencyMatrix);
                });
                adjacencyListItem.setOnAction(e -> {
                    createDataStructureMenu.setText("Graph:\nAdjacency List");
                    showTextInputPane("Adjacency List Input", this::parseAdjacencyList);
                });
                manualEdgesItem.setOnAction(e -> {
                    createDataStructureMenu.setText("Graph:\nManual Edges");
                    showTextInputPane("Manual Edges Input\n(Formats: u v  or  u->v  or  u<->v)", this::parseManualEdges);
                });
                fileEdgesItem.setOnAction(e -> {
                    createDataStructureMenu.setText("Graph: From File");
                    openFileAndParseEdges();
                });

                List<String> algorithms = List.of("BFS", "DFS", "Kruskal", "Prim");
                for (String algo : algorithms) {
                    MenuItem item = new MenuItem(algo);
                    item.setOnAction(e -> {
                        algorithmMenu.setText(algo);
                        runAlgorithm(algo);
                    });
                    algorithmMenu.getItems().add(item);
                }

                HBox graphControlBar = new HBox(10);
                graphControlBar.setPadding(new Insets(10));
                graphControlBar.getChildren().addAll(addNodeBtn, addEdgeBtn, colorBtn, assignWeightBtn, removeNodeBtn, removeEdgeBtn);
                controlBar.getChildren().addAll(clearBtn, algorithmMenu, graphControlBar, backBtn, quitBtn);
                break;
            case LINKED_LIST:
                createDataStructureMenu.setText("Linked List");
                setupLinkedListControls();
                controlBar.getChildren().addAll(clearBtn, backBtn, quitBtn);
                break;
            case STACK:
                createDataStructureMenu.setText("Stack");
                setupStackControls();
                controlBar.getChildren().addAll(clearBtn, backBtn, quitBtn);
                break;
            case QUEUE:
                createDataStructureMenu.setText("Queue");
                setupQueueControls();
                controlBar.getChildren().addAll(clearBtn, backBtn, quitBtn);
                break;
            case HEAP:
                createDataStructureMenu.setText("Heap");
                setupHeapControls();
                controlBar.getChildren().addAll(clearBtn, backBtn, quitBtn);
                break;
            case BST:
                createDataStructureMenu.setText("Binary Search Tree");
                setupBSTControls();
                controlBar.getChildren().addAll(clearBtn, backBtn, quitBtn);
                break;
        }
        rightPanel.getChildren().add(0, controlBar);
    }

    private void setupLinkedListControls() {
        TextField inputField = new TextField();
        inputField.setPromptText("Enter value");
        Button addBtn = new Button("Add Node");
        Button insertAtBtn = new Button("Insert At");
        Button removeBtn = new Button("Remove Node");
        Button removeAtBtn = new Button("Remove At");
        Button searchBtn = new Button("Search");

        addBtn.setOnAction(e -> {
            String value = inputField.getText().trim();
            if (!value.isEmpty()) {
                addLinkedListNode(value);
                inputField.clear();
            } else {
                showWarning(canvas, "Please enter a value");
            }
        });

        insertAtBtn.setOnAction(e -> {
            String[] inputs = inputField.getText().trim().split(",");
            if (inputs.length == 2) {
                try {
                    String value = inputs[0].trim();
                    int index = Integer.parseInt(inputs[1].trim());
                    if (index >= 0 && index <= linkedList.size()) { // Allow insertion at end
                        linkedListInsertAt(value, index);
                        inputField.clear();
                    } else {
                        showWarning(canvas, "Index out of bounds");
                    }
                } catch (NumberFormatException ex) {
                    showWarning(canvas, "Invalid index");
                }
            } else {
                showWarning(canvas, "Enter value,index (e.g., 5,2)");
            }
        });

        removeBtn.setOnAction(e -> removeLinkedListNode());

        removeAtBtn.setOnAction(e -> {
            try {
                int index = Integer.parseInt(inputField.getText().trim());
                if (index >= 0 && index < linkedList.size()) {
                    linkedListRemoveAt(index);
                    inputField.clear();
                } else {
                    showWarning(canvas, "Invalid index");
                }
            } catch (NumberFormatException ex) {
                showWarning(canvas, "Enter a valid index");
            }
        });

        searchBtn.setOnAction(e -> {
            String value = inputField.getText().trim();
            if (!value.isEmpty()) {
                linkedListSearch(value);
                inputField.clear();
            } else {
                showWarning(canvas, "Please enter a value");
            }
        });

        VBox controls = new VBox(10, new Label("Linked List Operations"), inputField, addBtn, insertAtBtn, removeBtn, removeAtBtn, searchBtn);
        controls.setAlignment(Pos.CENTER);
        leftPanel.getChildren().add(controls);
    }

    private void setupStackControls() {
        TextField inputField = new TextField();
        inputField.setPromptText("Enter value");
        Button pushBtn = new Button("Push");
        Button popBtn = new Button("Pop");
        Button peekBtn = new Button("Peek");
        Button isEmptyBtn = new Button("Is Empty");

        pushBtn.setOnAction(e -> {
            String value = inputField.getText().trim();
            if (!value.isEmpty()) {
                stack.push(value);
                inputField.clear();
            } else {
                showWarning(canvas, "Please enter a value");
            }
        });

        popBtn.setOnAction(e -> stack.pop());

        peekBtn.setOnAction(e -> stack.peek());

        isEmptyBtn.setOnAction(e -> stack.isEmptyVisual());

        VBox controls = new VBox(10, new Label("Stack Operations"), inputField, pushBtn, popBtn, peekBtn, isEmptyBtn);
        controls.setAlignment(Pos.CENTER);
        leftPanel.getChildren().add(controls);
    }

//    private void setupQueueControls() {
//        TextField inputField = new TextField();
//        inputField.setPromptText("Enter value");
//        Button enqueueBtn = new Button("Enqueue");
//        Button dequeueBtn = new Button("Dequeue");
//        Button peekBtn = new Button("Peek");
//        Button isEmptyBtn = new Button("Is Empty");
//
//        enqueueBtn.setOnAction(e -> {
//            String value = inputField.getText().trim();
//            if (!value.isEmpty()) {
//                queue.enqueue(value);
//                inputField.clear();
//            } else {
//                showWarning(canvas, "Please enter a value");
//            }
//        });
//
//        dequeueBtn.setOnAction(e -> queue.dequeue());
//
//        peekBtn.setOnAction(e -> queue.peek());
//
//        isEmptyBtn.setOnAction(e -> queue.isEmptyVisual());
//
//        VBox controls = new VBox(10, new Label("Queue Operations"), inputField, enqueueBtn, dequeueBtn, peekBtn, isEmptyBtn);
//        controls.setAlignment(Pos.CENTER);
//        leftPanel.getChildren().add(controls);
//    }

    private void setupQueueControls() {
        TextField inputField = new TextField();
        inputField.setPromptText("Enter value");
        Button enqueueBtn = new Button("Enqueue");
        Button dequeueBtn = new Button("Dequeue");
        Button peekBtn = new Button("Peek");
        Button frontBtn = new Button("Front");
        Button isEmptyBtn = new Button("Is Empty");

        enqueueBtn.setOnAction(e -> {
            String value = inputField.getText().trim();
            if (!value.isEmpty()) {
                queue.enqueue(value);
                inputField.clear();
            } else {
                showWarning(canvas, "Please enter a value");
            }
        });

        dequeueBtn.setOnAction(e -> queue.dequeue());

        peekBtn.setOnAction(e -> queue.peek());

        frontBtn.setOnAction(e -> {
            try {
                String frontValue = queue.front();
                showWarning(canvas, "Front element: " + frontValue);
            } catch (NoSuchElementException ex) {
                showWarning(canvas, ex.getMessage());
            }
        });

        isEmptyBtn.setOnAction(e -> queue.isEmptyVisual());

        VBox controls = new VBox(10, new Label("Queue Operations"), inputField, enqueueBtn, dequeueBtn, peekBtn, frontBtn, isEmptyBtn);
        controls.setAlignment(Pos.CENTER);
        leftPanel.getChildren().add(controls);
    }

    private void setupHeapControls() {
        TextField inputField = new TextField();
        inputField.setPromptText("Enter number");
        Button insertBtn = new Button("Insert");
        Button removeMaxBtn = new Button("Remove Max");
        Button peekBtn = new Button("Peek Max");
        Button isEmptyBtn = new Button("Is Empty");

        insertBtn.setOnAction(e -> {
            String value = inputField.getText().trim();
            try {
                int num = Integer.parseInt(value);
                heap.insert(num);
                inputField.clear();
            } catch (NumberFormatException ex) {
                showWarning(canvas, "Please enter a valid number");
            }
        });

        removeMaxBtn.setOnAction(e -> heap.removeMax());

        peekBtn.setOnAction(e -> heap.peekMax());

        isEmptyBtn.setOnAction(e -> heap.isEmptyVisual());

        VBox controls = new VBox(10, new Label("Heap Operations"), inputField, insertBtn, removeMaxBtn, peekBtn, isEmptyBtn);
        controls.setAlignment(Pos.CENTER);
        leftPanel.getChildren().add(controls);
    }

    private void setupBSTControls() {
        TextField inputField = new TextField();
        inputField.setPromptText("Enter number");
        Button insertBtn = new Button("Insert");
        Button deleteBtn = new Button("Delete");
        Button searchBtn = new Button("Search");

        insertBtn.setOnAction(e -> {
            String value = inputField.getText().trim();
            try {
                int num = Integer.parseInt(value);
                bst.insert(num);
                inputField.clear();
            } catch (NumberFormatException ex) {
                showWarning(canvas, "Please enter a valid number");
            }
        });

        deleteBtn.setOnAction(e -> {
            String value = inputField.getText().trim();
            try {
                int num = Integer.parseInt(value);
                bst.delete(num);
                inputField.clear();
            } catch (NumberFormatException ex) {
                showWarning(canvas, "Please enter a valid number");
            }
        });

        searchBtn.setOnAction(e -> {
            String value = inputField.getText().trim();
            try {
                int num = Integer.parseInt(value);
                bst.search(num);
                inputField.clear();
            } catch (NumberFormatException ex) {
                showWarning(canvas, "Please enter a valid number");
            }
        });

        VBox controls = new VBox(10, new Label("BST Operations"), inputField, insertBtn, deleteBtn, searchBtn);
        controls.setAlignment(Pos.CENTER);
        leftPanel.getChildren().add(controls);
    }

    private void activateMode(ToggleButton btn) {
        if (activeButton != null && activeButton != btn) {
            activeButton.setSelected(false);
            activeButton.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-background-radius: 5;");
        }
        activeButton = btn.isSelected() ? btn : null;
        btn.setStyle(btn.isSelected() ? "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px; -fx-background-radius: 5;" : "-fx-font-size: 14px; -fx-padding: 8px; -fx-background-radius: 5;");

        if (activeButton != null && selectedNode != null) {
            selectedNode.arrow.setVisible(false);
            selectedNode = null;
        }
    }

    private void addGraphNodeAtPosition(double x, double y) {
        int newId = graphNodes.size();
        Circle circle = new Circle(x, y, 20);
        circle.setFill(Color.WHITE);
        circle.setStroke(Color.BLACK);
        Label label = new Label(String.valueOf(newId));
        label.setLayoutX(x - 6);
        label.setLayoutY(y - 10);

        Graph.GraphNode node = new Graph.GraphNode(newId, circle, label);
        graphNodes.add(node);
        circleNodeMap.put(circle, node);
        canvas.getChildren().addAll(circle, label, node.arrow);

        circle.setOnMouseClicked(e -> {
            e.consume();
            if (activeButton == null) {
                if (selectedNode != null) selectedNode.arrow.setVisible(false);
                selectedNode = node;
                updateArrowPosition(node);
                node.arrow.setVisible(true);
                return;
            }

            if (activeButton == addEdgeBtn) {
                if (selectedNode == null) {
                    selectedNode = node;
                    circle.setStroke(Color.BLUE);
                } else if (selectedNode != node) {
                    Line line = new Line(
                            selectedNode.circle.getCenterX(), selectedNode.circle.getCenterY(),
                            node.circle.getCenterX(), node.circle.getCenterY()
                    );
                    line.setStrokeWidth(3);
                    Graph.GraphEdge edge = new Graph.GraphEdge(selectedNode, node, line);
                    graphEdges.add(edge);
                    canvas.getChildren().add(0, line);
                    canvas.getChildren().add(edge.weightLabel);
                    updateWeightLabelPosition(edge);
                    lineEdgeMap.put(line, edge);
                    selectedNode.circle.setStroke(Color.BLACK);
                    selectedNode = null;

                    line.setOnMouseClicked(ev -> {
                        ev.consume();
                        if (activeButton == assignWeightBtn) showWeightDialog(canvas, edge);
                        else if (activeButton == removeEdgeBtn) {
                            canvas.getChildren().removeAll(line, edge.weightLabel);
                            graphEdges.remove(edge);
                        }
                    });
                }
            } else if (activeButton == colorBtn) {
                ColorPicker picker = new ColorPicker((Color) node.circle.getFill());
                Popup popup = new Popup();
                popup.getContent().add(picker);
                popup.show(node.circle.getScene().getWindow(),
                        node.circle.localToScreen(0, 0).getX(),
                        node.circle.localToScreen(0, 0).getY() + 30);
                picker.setOnAction(ev -> {
                    node.circle.setFill(picker.getValue());
                    popup.hide();
                });
                picker.focusedProperty().addListener((obs, oldVal, newVal) -> {
                    if (!newVal) popup.hide();
                });
            } else if (activeButton == removeNodeBtn) {
                canvas.getChildren().removeAll(node.circle, node.label, node.arrow);
                List<Graph.GraphEdge> toRemove = new ArrayList<>();
                for (Graph.GraphEdge edge : graphEdges) {
                    if (edge.from == node || edge.to == node) {
                        canvas.getChildren().removeAll(edge.line, edge.weightLabel);
                        toRemove.add(edge);
                    }
                }
                graphEdges.removeAll(toRemove);
                graphNodes.remove(node);
                if (selectedNode == node) selectedNode = null;
            }
        });
    }

    private void addLinkedListNode(String value) {
        Circle circle = new Circle(0, 0, 20);
        circle.setFill(Color.LIGHTYELLOW);
        circle.setStroke(Color.BLACK);
        Label label = new Label(value);
        LinkedListNode node = new LinkedListNode(value, circle, label);
        linkedList.add(node);
        canvas.getChildren().addAll(circle, label);
        updateLinkedListPositions();
    }

    private void linkedListInsertAt(String value, int index) {
        Circle circle = new Circle(0, 0, 20);
        circle.setFill(Color.LIGHTYELLOW);
        circle.setStroke(Color.BLACK);
        Label label = new Label(value);
        LinkedListNode node = new LinkedListNode(value, circle, label);
        linkedList.add(index, node);
        canvas.getChildren().addAll(circle, label);
        updateLinkedListPositions();
        highlightNode(node, Color.GREEN);
    }

    private void linkedListRemoveAt(int index) {
        LinkedListNode node = linkedList.remove(index);
        canvas.getChildren().removeAll(node.circle, node.label, node.nextArrow);
        updateLinkedListPositions();
    }

    private void linkedListSearch(String value) {
        boolean found = false;
        for (LinkedListNode node : linkedList) {
            if (node.value.equals(value)) {
                highlightNode(node, Color.GREEN);
                found = true;
                break; // Highlight first match only
            }
        }
        if (!found) {
            showWarning(canvas, "Value not found");
        }
    }

    private void highlightNode(LinkedListNode node, Color color) {
        node.circle.setFill(color);
        PauseTransition pt = new PauseTransition(Duration.seconds(2));
        pt.setOnFinished(e -> node.circle.setFill(Color.LIGHTYELLOW));
        pt.play();
    }

    private void removeLinkedListNode() {
        if (!linkedList.isEmpty()) {
            LinkedListNode node = linkedList.remove(linkedList.size() - 1);
            canvas.getChildren().removeAll(node.circle, node.label, node.nextArrow);
            updateLinkedListPositions();
        }
    }

    private void updateLinkedListPositions() {
        double startX = 50;
        double startY = canvas.getHeight() / 2;
        for (int i = 0; i < linkedList.size(); i++) {
            LinkedListNode node = linkedList.get(i);
            node.circle.setCenterX(startX + i * 60);
            node.circle.setCenterY(startY);
            node.label.setLayoutX(startX + i * 60 - 10);
            node.label.setLayoutY(startY - 10);
            if (i < linkedList.size() - 1) {
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

    private void updateWeightLabelPosition(Graph.GraphEdge edge) {
        double x1 = edge.line.getStartX();
        double y1 = edge.line.getStartY();
        double x2 = edge.line.getEndX();
        double y2 = edge.line.getEndY();

        double midX = (x1 + x2) / 2;
        double midY = (y1 + y2) / 2;

        double dx = x2 - x1;
        double dy = y2 - y1;
        double len = Math.hypot(dx, dy);
        if (len == 0) len = 1;
        double perpX = -dy / len;
        double perpY = dx / len;

        double offset = 12;

        Platform.runLater(() -> {
            edge.weightLabel.applyCss();
            edge.weightLabel.layout();
            double w = edge.weightLabel.getWidth();
            double h = edge.weightLabel.getHeight();
            double labelX = midX + perpX * offset - w / 2;
            double labelY = midY + perpY * offset - h / 2;
            edge.weightLabel.setLayoutX(labelX);
            edge.weightLabel.setLayoutY(labelY);
            edge.weightLabel.getTransforms().clear();
        });
    }

    private void updateArrowPosition(Graph.GraphNode node) {
        double cx = node.circle.getCenterX();
        double cy = node.circle.getCenterY();
        node.arrow.setLayoutX(cx);
        node.arrow.setLayoutY(cy - 35);
    }

    private void showWeightDialog(Pane canvas, Graph.GraphEdge edge) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Assign Weight");
        dialog.setHeaderText("Enter weight for edge " + edge.from.id + " - " + edge.to.id);
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(input -> {
            try {
                int w = Integer.parseInt(input.trim());
                if (w < 0) {
                    showWarning(canvas, "Negative weight not allowed");
                    return;
                }
                if (w == 0) {
                    edge.weightLabel.setVisible(false);
                    edge.weightLabel.setText("");
                } else {
                    edge.weightLabel.setText(String.valueOf(w));
                    edge.weightLabel.setVisible(true);
                    updateWeightLabelPosition(edge);
                }
            } catch (NumberFormatException e) {
                showWarning(canvas, "Invalid input");
            }
        });
    }

    private void showWarning(Pane canvas, String msg) {
        Label warning = new Label(msg);
        warning.setStyle("-fx-background-color: #ffeb3b; -fx-text-fill: #d32f2f; -fx-padding: 5; -fx-border-radius: 5;");
        warning.setLayoutX(10);
        warning.setLayoutY(10);
        canvas.getChildren().add(warning);
        PauseTransition pt = new PauseTransition(Duration.seconds(2));
        pt.setOnFinished(e -> canvas.getChildren().remove(warning));
        pt.play();
    }

    private void clearCurrentStructure() {
        canvas.getChildren().clear();
        if (currentMode == Mode.GRAPH) {
            for (Graph.GraphEdge edge : graphEdges) {
                canvas.getChildren().removeAll(edge.line, edge.weightLabel);
            }
            for (Graph.GraphNode node : graphNodes) {
                canvas.getChildren().removeAll(node.circle, node.label, node.arrow);
            }
            graphNodes.clear();
            graphEdges.clear();
            circleNodeMap.clear();
            lineEdgeMap.clear();
            selectedNode = null;
            activeButton = null;
        } else if (currentMode == Mode.LINKED_LIST) {
            for (LinkedListNode node : linkedList) {
                canvas.getChildren().removeAll(node.circle, node.label, node.nextArrow);
            }
            linkedList.clear();
        } else if (currentMode == Mode.STACK) {
            while (!stack.isEmpty()) {
                stack.pop();
            }
        } else if (currentMode == Mode.QUEUE) {
            while (!queue.isEmpty()) {
                queue.dequeue();
            }
        } else if (currentMode == Mode.HEAP) {
            while (!heap.isEmpty()) {
                heap.removeMax();
            }
        } else if (currentMode == Mode.BST) {
            bst.clear();
        }
    }

    private void positionNodesCircle(List<Graph.GraphNode> nodes, double centerX, double centerY, double radius) {
        int n = nodes.size();
        for (int i = 0; i < n; i++) {
            double angle = 2 * Math.PI * i / n;
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);
            Graph.GraphNode node = nodes.get(i);
            node.circle.setCenterX(x);
            node.circle.setCenterY(y);
            node.label.setLayoutX(x - 6);
            node.label.setLayoutY(y - 10);
            updateArrowPosition(node);
        }
    }

    private void runAlgorithm(String algorithm) {
        GraphAlgorithms algo = new GraphAlgorithms(graphNodes, graphEdges, canvas);
        switch (algorithm) {
            case "BFS": algo.bfs(0); break;
            case "DFS": algo.dfs(0); break;
            case "Kruskal": algo.kruskal(); break;
            case "Prim": algo.prim(); break;
        }
    }

    private void showTextInputPane(String title, GraphInputParser parser) {
        leftPanel.getChildren().clear();

        Label label = new Label(title);
        label.setWrapText(true);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        TextArea textArea = new TextArea();
        textArea.setPrefWidth(leftPanel.getPrefWidth());
        textArea.setPrefHeight(250);
        textArea.setWrapText(false);

        ScrollPane scrollPane = new ScrollPane(textArea);
        scrollPane.setPrefViewportWidth(leftPanel.getPrefWidth());
        scrollPane.setPrefViewportHeight(250);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        Runnable setDefaultContent = () -> {
            if (title.contains("Adjacency Matrix")) {
                textArea.setText(getDefaultAdjMatrixText(3));
            } else if (title.contains("Adjacency List")) {
                textArea.setText(getDefaultAdjListText(3));
            } else {
                textArea.clear();
            }
        };
        setDefaultContent.run();

        Button addNodeBtn = new Button("Add Node");
        Button removeNodeBtn = new Button("Remove Node");
        Button clearBtn = new Button("Clear");
        Button loadButton = new Button("Load");
        Button backButton = new Button("Back");

        addNodeBtn.setMaxWidth(Double.MAX_VALUE);
        removeNodeBtn.setMaxWidth(Double.MAX_VALUE);
        clearBtn.setMaxWidth(Double.MAX_VALUE);
        loadButton.setMaxWidth(Double.MAX_VALUE);
        backButton.setMaxWidth(Double.MAX_VALUE);

        VBox buttons = new VBox(10, addNodeBtn, removeNodeBtn, clearBtn, loadButton, backButton);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPrefWidth(leftPanel.getPrefWidth());

        HBox inputArea = new HBox(10, scrollPane, buttons);
        inputArea.setAlignment(Pos.CENTER_LEFT);

        addNodeBtn.setOnAction(e -> {
            if (title.contains("Adjacency Matrix")) {
                addNodeAdjMatrix(textArea);
            } else if (title.contains("Adjacency List")) {
                addNodeAdjList(textArea);
            }
        });

        removeNodeBtn.setOnAction(e -> {
            boolean success = false;
            if (title.contains("Adjacency Matrix")) {
                success = removeNodeAdjMatrix(textArea);
            } else if (title.contains("Adjacency List")) {
                success = removeNodeAdjList(textArea);
            }
            if (!success) showWarning(canvas, "Cannot reduce below 1 node");
        });

        clearBtn.setOnAction(e -> setDefaultContent.run());

        loadButton.setOnAction(e -> {
            String inputText = textArea.getText();
            if (inputText == null || inputText.isBlank()) {
                showWarning(canvas, "Input is empty");
                return;
            }
            clearCurrentStructure();
            boolean success = parser.parse(inputText);
            if (!success) {
                showWarning(canvas, "Parsing failed");
            }
        });

        backButton.setOnAction(e -> switchToMode(Mode.GRAPH));

        leftPanel.getChildren().addAll(label, inputArea);
    }

    private String getDefaultAdjMatrixText(int size) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                sb.append('0');
                if (j < size - 1) sb.append(' ');
            }
            if (i < size - 1) sb.append('\n');
        }
        return sb.toString();
    }

    private String getDefaultAdjListText(int size) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(i).append(":\n");
        }
        return sb.toString();
    }

    private void addNodeAdjMatrix(TextArea textArea) {
        String[] lines = textArea.getText().split("\n");
        int n = lines.length;

        List<List<String>> matrix = new ArrayList<>();
        for (String line : lines) {
            List<String> row = new ArrayList<>(Arrays.asList(line.trim().split("\\s+")));
            matrix.add(row);
        }

        for (List<String> row : matrix) {
            row.add("0");
        }

        List<String> newRow = new ArrayList<>();
        for (int i = 0; i < n + 1; i++) newRow.add("0");
        matrix.add(newRow);

        StringBuilder sb = new StringBuilder();
        for (List<String> row : matrix) {
            sb.append(String.join(" ", row)).append("\n");
        }
        textArea.setText(sb.toString().trim());
    }

    private boolean removeNodeAdjMatrix(TextArea textArea) {
        String[] lines = textArea.getText().split("\n");
        int n = lines.length;
        if (n <= 1) return false;

        List<List<String>> matrix = new ArrayList<>();
        for (String line : lines) {
            List<String> row = new ArrayList<>(Arrays.asList(line.trim().split("\\s+")));
            matrix.add(row);
        }

        for (List<String> row : matrix) {
            if (!row.isEmpty()) row.remove(row.size() - 1);
        }

        matrix.remove(matrix.size() - 1);

        StringBuilder sb = new StringBuilder();
        for (List<String> row : matrix) {
            sb.append(String.join(" ", row)).append("\n");
        }
        textArea.setText(sb.toString().trim());
        return true;
    }

    private void addNodeAdjList(TextArea textArea) {
        String[] lines = textArea.getText().split("\n");
        int n = lines.length;

        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append(line).append("\n");
        }
        sb.append(n).append(":\n");
        textArea.setText(sb.toString());
    }

    private boolean removeNodeAdjList(TextArea textArea) {
        String[] lines = textArea.getText().split("\n");
        int n = lines.length;
        if (n <= 1) return false;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n - 1; i++) {
            sb.append(lines[i]).append("\n");
        }
        textArea.setText(sb.toString());
        return true;
    }

    @FunctionalInterface
    interface GraphInputParser {
        boolean parse(String input);
    }

    private boolean parseAdjacencyMatrix(String input) {
        try {
            String[] lines = input.strip().split("\n");
            int n = lines.length;

            for (String line : lines) {
                String[] vals = line.trim().split("\\s+");
                if (vals.length != n) {
                    showWarning(canvas, "Matrix is not square");
                    return false;
                }
                for (String val : vals) {
                    int x = Integer.parseInt(val);
                    if (x != 0 && x != 1) {
                        showWarning(canvas, "Matrix values must be 0 or 1");
                        return false;
                    }
                }
            }

            for (int i = 0; i < n; i++) {
                Circle circle = new Circle(0, 0, 20);
                circle.setFill(Color.WHITE);
                circle.setStroke(Color.BLACK);
                Label label = new Label(String.valueOf(i));
                Graph.GraphNode node = new Graph.GraphNode(i, circle, label);
                graphNodes.add(node);
                circleNodeMap.put(circle, node);
                canvas.getChildren().addAll(circle, label, node.arrow);

                final int nodeIndex = i;
                circle.setOnMouseClicked(e -> {
                    e.consume();
                    handleGraphNodeClick(nodeIndex);
                });
            }

            positionNodesCircle(graphNodes, canvas.getWidth() / 2, canvas.getHeight() / 2, 200);

            for (int i = 0; i < n; i++) {
                String[] vals = lines[i].trim().split("\\s+");
                for (int j = 0; j < n; j++) {
                    int val = Integer.parseInt(vals[j]);
                    if (val == 1) {
                        addGraphEdge(graphNodes.get(i), graphNodes.get(j));
                    }
                }
            }

            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private boolean parseAdjacencyList(String input) {
        try {
            Map<Integer, List<Integer>> adj = new HashMap<>();
            String[] lines = input.strip().split("\n");
            int maxNodeId = -1;

            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(":", 2);
                if (parts.length != 2) {
                    showWarning(canvas, "Invalid adjacency list format");
                    return false;
                }
                int node = Integer.parseInt(parts[0].trim());
                maxNodeId = Math.max(maxNodeId, node);
                List<Integer> neighbors = new ArrayList<>();
                if (!parts[1].trim().isEmpty()) {
                    String[] nbrStrs = parts[1].trim().split("\\s+");
                    for (String nbr : nbrStrs) {
                        int n = Integer.parseInt(nbr);
                        neighbors.add(n);
                        maxNodeId = Math.max(maxNodeId, n);
                    }
                }
                adj.put(node, neighbors);
            }

            Set<Integer> allNodeIds = new HashSet<>();
            allNodeIds.addAll(adj.keySet());
            for (List<Integer> neighbors : adj.values()) {
                allNodeIds.addAll(neighbors);
            }

            List<Integer> sortedIds = new ArrayList<>(allNodeIds);
            Collections.sort(sortedIds);

            for (int id : sortedIds) {
                Circle circle = new Circle(0, 0, 20);
                circle.setFill(Color.WHITE);
                circle.setStroke(Color.BLACK);
                Label label = new Label(String.valueOf(id));
                Graph.GraphNode node = new Graph.GraphNode(id, circle, label);
                graphNodes.add(node);
                circleNodeMap.put(circle, node);
                canvas.getChildren().addAll(circle, label, node.arrow);

                final int nodeIndex = id;
                circle.setOnMouseClicked(e -> {
                    e.consume();
                    handleGraphNodeClick(nodeIndex);
                });
            }

            positionNodesCircle(graphNodes, canvas.getWidth() / 2, canvas.getHeight() / 2, 200);

            Map<Integer, Graph.GraphNode> nodeMap = new HashMap<>();
            for (Graph.GraphNode node : graphNodes) {
                nodeMap.put(node.id, node);
            }

            for (Map.Entry<Integer, List<Integer>> entry : adj.entrySet()) {
                int u = entry.getKey();
                for (int v : entry.getValue()) {
                    Graph.GraphNode from = nodeMap.get(u);
                    Graph.GraphNode to = nodeMap.get(v);
                    if (from != null && to != null) {
                        addGraphEdge(from, to);
                    }
                }
            }

            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private boolean parseManualEdges(String input) {
        try {
            String[] lines = input.strip().split("\n");
            List<int[]> edgesList = new ArrayList<>();

            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty()) continue;

                int u, v;
                if (line.contains("<->")) {
                    String[] parts = line.split("<->");
                    if (parts.length != 2) return false;
                    u = Integer.parseInt(parts[0].trim());
                    v = Integer.parseInt(parts[1].trim());
                    edgesList.add(new int[]{u, v});
                    edgesList.add(new int[]{v, u});
                } else if (line.contains("->")) {
                    String[] parts = line.split("->");
                    if (parts.length != 2) return false;
                    u = Integer.parseInt(parts[0].trim());
                    v = Integer.parseInt(parts[1].trim());
                    edgesList.add(new int[]{u, v});
                } else {
                    String[] parts = line.split("\\s+");
                    if (parts.length != 2) return false;
                    u = Integer.parseInt(parts[0].trim());
                    v = Integer.parseInt(parts[1].trim());
                    edgesList.add(new int[]{u, v});
                }
            }

            Set<Integer> uniqueNodeIds = new HashSet<>();
            for (int[] edge : edgesList) {
                uniqueNodeIds.add(edge[0]);
                uniqueNodeIds.add(edge[1]);
            }

            List<Integer> sortedNodeIds = new ArrayList<>(uniqueNodeIds);
            Collections.sort(sortedNodeIds);

            for (int id : sortedNodeIds) {
                Circle circle = new Circle(0, 0, 20);
                circle.setFill(Color.WHITE);
                circle.setStroke(Color.BLACK);
                Label label = new Label(String.valueOf(id));
                Graph.GraphNode node = new Graph.GraphNode(id, circle, label);
                graphNodes.add(node);
                circleNodeMap.put(circle, node);
                canvas.getChildren().addAll(circle, label, node.arrow);

                final int nodeIndex = id;
                circle.setOnMouseClicked(e -> {
                    e.consume();
                    handleGraphNodeClick(nodeIndex);
                });
            }

            positionNodesCircle(graphNodes, canvas.getWidth() / 2, canvas.getHeight() / 2, 200);

            Map<Integer, Graph.GraphNode> nodeMap = new HashMap<>();
            for (Graph.GraphNode node : graphNodes) {
                nodeMap.put(node.id, node);
            }
            for (int[] edge : edgesList) {
                addGraphEdge(nodeMap.get(edge[0]), nodeMap.get(edge[1]));
            }

            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private void openFileAndParseEdges() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Graph Edge File");
        File file = fileChooser.showOpenDialog(stage);
        if (file == null) return;

        try {
            List<String> lines = Files.readAllLines(file.toPath());
            StringBuilder sb = new StringBuilder();
            for (String line : lines) {
                sb.append(line).append("\n");
            }
            clearCurrentStructure();
            boolean success = parseManualEdges(sb.toString());
            if (!success) showWarning(canvas, "Failed to parse file edges");
        } catch (Exception e) {
            e.printStackTrace();
            showWarning(canvas, "Error reading file");
        }
    }

    private void addGraphEdge(Graph.GraphNode from, Graph.GraphNode to) {
        for (Graph.GraphEdge edge : graphEdges) {
            if (edge.from == from && edge.to == to) return;
        }

        Line line = new Line(
                from.circle.getCenterX(), from.circle.getCenterY(),
                to.circle.getCenterX(), to.circle.getCenterY()
        );
        line.setStrokeWidth(3);

        Graph.GraphEdge edge = new Graph.GraphEdge(from, to, line);
        graphEdges.add(edge);
        canvas.getChildren().add(0, line);
        canvas.getChildren().add(edge.weightLabel);
        updateWeightLabelPosition(edge);
        lineEdgeMap.put(line, edge);

        line.setOnMouseClicked(ev -> {
            ev.consume();
            if (activeButton == assignWeightBtn) showWeightDialog(canvas, edge);
            else if (activeButton == removeEdgeBtn) {
                canvas.getChildren().removeAll(line, edge.weightLabel);
                graphEdges.remove(edge);
            }
        });
    }

    private void handleGraphNodeClick(int nodeIndex) {
        Graph.GraphNode node = graphNodes.get(nodeIndex);
        if (activeButton == null) {
            if (selectedNode != null) selectedNode.arrow.setVisible(false);
            selectedNode = node;
            updateArrowPosition(node);
            node.arrow.setVisible(true);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}