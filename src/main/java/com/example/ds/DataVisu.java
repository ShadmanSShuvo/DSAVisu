// DataVisu.java
package com.example.ds;

import java.util.NoSuchElementException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Main application class for the Data Structure Visualizer.
 * Acts as the controller, managing the UI and delegating operations
 * to specific data structure implementations.
 */
public class DataVisu extends Application {

    private enum Mode {
        LINKED_LIST, STACK, QUEUE, HEAP, BST, QUIT
    }

    private Mode currentMode;
    private ToggleButton activeButton;
    private Stack stack;
    private Queue queue;
    private Heap heap;
    private BinarySearchTree bst;
    // Use the external LinkedList class
    private LinkedList linkedList;
    private Stage stage;
    private String initialMode;
    private VBox leftPanel;
    private MenuButton createDataStructureMenu;
    private Pane canvas;

    /**
     * Constructor to set initial mode based on a string.
     * 
     * @param mode The name of the initial data structure mode.
     */
    public DataVisu(String mode) {
        this.initialMode = mode;
        switch (mode) {
            case "Linked List":
                this.currentMode = Mode.LINKED_LIST;
                break;
            case "Stack":
                this.currentMode = Mode.STACK;
                break;
            case "Queue":
                this.currentMode = Mode.QUEUE;
                break;
            case "Heap":
                this.currentMode = Mode.HEAP;
                break;
            case "Binary Search Tree":
                this.currentMode = Mode.BST;
                break;
            case "Quit Program":
                this.currentMode = Mode.QUIT;
                break;
            default:
                this.currentMode = Mode.LINKED_LIST;
        }
    }

    /**
     * Default constructor, defaults to Linked List mode.
     */
    public DataVisu() {
        this("Linked List"); // Default to Linked List
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        leftPanel = new VBox(10);
        leftPanel.setPrefWidth(150);
        leftPanel.setPadding(new Insets(10));
        leftPanel.setStyle("-fx-background-color: #ffffff; -fx-border-color: #dcdcdc; -fx-border-width: 1;");

        createDataStructureMenu = new MenuButton("Select Data Structure");
        MenuItem linkedListItem = new MenuItem("Linked List");
        MenuItem stackItem = new MenuItem("Stack");
        MenuItem queueItem = new MenuItem("Queue");
        MenuItem heapItem = new MenuItem("Heap");
        MenuItem bstItem = new MenuItem("Binary Search Tree");

        createDataStructureMenu.getItems().addAll(linkedListItem, stackItem, queueItem, heapItem, bstItem);

        leftPanel.getChildren().add(createDataStructureMenu);

        canvas = new Pane();
        canvas.setStyle("-fx-background-color: #e6f7ff; -fx-border-color: #dcdcdc;");

        Button clearBtn = new Button("Clear");
        clearBtn.setOnAction(e -> clearCurrentStructure());

        Button backBtn = new Button("Back to Main");
        backBtn.setOnAction(e -> {
            try {
                new Program().start(stage); // Assumes Program class exists
            } catch (Exception ex) {
                // Handle potential exception if Program.start throws one or if Program doesn't
                // exist
                System.err.println("Could not navigate back to main program: " + ex.getMessage());
                // Optionally, just close the stage: stage.close();
            }
        });

        Button quitBtn = new Button("Quit Program");
        quitBtn.setOnAction(e -> Platform.exit());

        HBox controlBar = new HBox(10);
        controlBar.setPadding(new Insets(10));
        controlBar.getChildren().addAll(clearBtn, backBtn, quitBtn);

        VBox rightPanel = new VBox(controlBar, canvas);
        VBox.setVgrow(canvas, Priority.ALWAYS);

        // Setup data structure menu handlers
        linkedListItem.setOnAction(e -> switchToMode(Mode.LINKED_LIST));
        stackItem.setOnAction(e -> switchToMode(Mode.STACK));
        queueItem.setOnAction(e -> switchToMode(Mode.QUEUE));
        heapItem.setOnAction(e -> switchToMode(Mode.HEAP));
        bstItem.setOnAction(e -> switchToMode(Mode.BST));

        HBox root = new HBox(leftPanel, rightPanel);
        HBox.setHgrow(rightPanel, Priority.ALWAYS);

        Scene scene = new Scene(root, 1000, 600);
        scene.getStylesheets().add("styles.css"); // Optional: Uncomment if using a
        // stylesheet

        stage.setTitle("Data Structure Visualizer - " + initialMode);
        stage.setScene(scene);
        stage.show();

        // Initialize data structures (assuming these classes exist and manage their own
        // visualization)
        stack = new Stack(canvas);
        queue = new Queue(canvas);
        heap = new Heap(canvas, true); // Assuming true for max heap, adjust if needed
        bst = new BinarySearchTree(canvas);
        // Initialize the LinkedList controller
        linkedList = new LinkedList(canvas);

        switchToMode(currentMode);
    }

    /**
     * Switches the visualization to the specified mode.
     * Clears the canvas and rebuilds the control panel.
     * 
     * @param mode The target Mode enum.
     */
    private void switchToMode(Mode mode) {
        currentMode = mode;
        clearCurrentStructure(); // Clear visualization for the previous mode
        leftPanel.getChildren().clear();
        leftPanel.getChildren().add(createDataStructureMenu); // Re-add the menu

        // Clear existing control bar and rebuild
        VBox rightPanel = (VBox) canvas.getParent();
        if (rightPanel.getChildren().size() > 0) {
            rightPanel.getChildren().remove(0); // Remove old control bar (index 0)
        }

        HBox controlBar = new HBox(10);
        controlBar.setPadding(new Insets(10));

        Button clearBtn = new Button("Clear");
        clearBtn.setOnAction(e -> clearCurrentStructure());

        Button backBtn = new Button("Back to Main");
        backBtn.setOnAction(e -> {
            try {
                new Program().start(stage); // Assumes Program class exists
            } catch (Exception ex) {
                System.err.println("Could not navigate back to main program: " + ex.getMessage());
            }
        });

        Button quitBtn = new Button("Quit Program");
        quitBtn.setOnAction(e -> Platform.exit());

        switch (mode) {
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
            case QUIT:
                createDataStructureMenu.setText("Quit Program");
                controlBar.getChildren().addAll(quitBtn);
                break;
        }
        rightPanel.getChildren().add(0, controlBar); // Add the new control bar at index 0
    }

    // --- Control Setup Methods for Each Data Structure ---

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
                linkedList.addNode(value); // Delegate to LinkedList class
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
                    linkedList.insertAt(value, index); // Delegate to LinkedList class
                    inputField.clear();
                } catch (NumberFormatException ex) {
                    showWarning(canvas, "Invalid index format. Use: value,index");
                } catch (IndexOutOfBoundsException ex) {
                    showWarning(canvas, ex.getMessage());
                }
            } else {
                showWarning(canvas, "Enter value,index (e.g., 5,2)");
            }
        });

        removeBtn.setOnAction(e -> {
            // Delegate to LinkedList class
            if (!linkedList.isEmpty()) {
                linkedList.removeLast();
            } else {
                showWarning(canvas, "List is empty");
            }
        });

        removeAtBtn.setOnAction(e -> {
            try {
                int index = Integer.parseInt(inputField.getText().trim());
                linkedList.removeAt(index); // Delegate to LinkedList class
                inputField.clear();
            } catch (NumberFormatException ex) {
                showWarning(canvas, "Enter a valid index");
            } catch (IndexOutOfBoundsException ex) {
                showWarning(canvas, ex.getMessage());
            }
        });

        searchBtn.setOnAction(e -> {
            String value = inputField.getText().trim();
            if (!value.isEmpty()) {
                int index = linkedList.search(value); // Delegate to LinkedList class
                if (index == -1) {
                    showWarning(canvas, "Value not found");
                } else {
                    // Optional: Show index found? The highlight happens in LinkedList.search
                    // showWarning(canvas, "Value found at index: " + index);
                }
                inputField.clear();
            } else {
                showWarning(canvas, "Please enter a value");
            }
        });

        VBox controls = new VBox(10, new Label("Linked List Operations"), inputField, addBtn, insertAtBtn, removeBtn,
                removeAtBtn, searchBtn);
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
                stack.push(value); // Delegate to Stack class
                inputField.clear();
            } else {
                showWarning(canvas, "Please enter a value");
            }
        });

        popBtn.setOnAction(e -> {
            try {
                stack.pop(); // Delegate to Stack class
            } catch (Exception ex) { // Assuming Stack.pop might throw if empty
                showWarning(canvas, ex.getMessage());
            }
        });
        peekBtn.setOnAction(e -> stack.peek()); // Delegate to Stack class
        isEmptyBtn.setOnAction(e -> stack.isEmptyVisual()); // Delegate to Stack class

        VBox controls = new VBox(10, new Label("Stack Operations"), inputField, pushBtn, popBtn, peekBtn, isEmptyBtn);
        controls.setAlignment(Pos.CENTER);
        leftPanel.getChildren().add(controls);
    }

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
                queue.enqueue(value); // Delegate to Queue class
                inputField.clear();
            } else {
                showWarning(canvas, "Please enter a value");
            }
        });

        dequeueBtn.setOnAction(e -> {
            try {
                queue.dequeue(); // Delegate to Queue class
            } catch (Exception ex) { // Assuming Queue.dequeue might throw if empty
                showWarning(canvas, ex.getMessage());
            }
        });
        peekBtn.setOnAction(e -> queue.peek()); // Delegate to Queue class

        frontBtn.setOnAction(e -> {
            try {
                String frontValue = queue.front(); // Delegate to Queue class
                showWarning(canvas, "Front element: " + frontValue);
            } catch (NoSuchElementException ex) {
                showWarning(canvas, ex.getMessage());
            }
        });

        isEmptyBtn.setOnAction(e -> queue.isEmptyVisual()); // Delegate to Queue class

        VBox controls = new VBox(10, new Label("Queue Operations"), inputField, enqueueBtn, dequeueBtn, peekBtn,
                frontBtn, isEmptyBtn);
        controls.setAlignment(Pos.CENTER);
        leftPanel.getChildren().add(controls);
    }

    private void setupHeapControls() {
        TextField inputField = new TextField();
        inputField.setPromptText("Enter number");

        Button insertBtn = new Button("Insert");
        Button removeMaxBtn = new Button("Remove Max"); // Or Min, depending on heap type
        Button peekBtn = new Button("Peek Max"); // Or Min, depending on heap type
        Button isEmptyBtn = new Button("Is Empty");

        insertBtn.setOnAction(e -> {
            String value = inputField.getText().trim();
            try {
                int num = Integer.parseInt(value);
                heap.insert(num); // Delegate to Heap class
                inputField.clear();
            } catch (NumberFormatException ex) {
                showWarning(canvas, "Please enter a valid number");
            }
        });

        removeMaxBtn.setOnAction(e -> {
            try {
                heap.removeRoot(); // Delegate to Heap class
            } catch (Exception ex) { // Assuming Heap.removeRoot might throw if empty
                showWarning(canvas, ex.getMessage());
            }
        });
        peekBtn.setOnAction(e -> heap.peekRoot()); // Delegate to Heap class
        isEmptyBtn.setOnAction(e -> heap.isEmptyVisual()); // Delegate to Heap class

        VBox controls = new VBox(10, new Label("Heap Operations"), inputField, insertBtn, removeMaxBtn, peekBtn,
                isEmptyBtn);
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
                bst.insert(num); // Delegate to BST class
                inputField.clear();
            } catch (NumberFormatException ex) {
                showWarning(canvas, "Please enter a valid number");
            }
        });

        deleteBtn.setOnAction(e -> {
            String value = inputField.getText().trim();
            try {
                int num = Integer.parseInt(value);
                bst.delete(num); // Delegate to BST class
                inputField.clear();
            } catch (NumberFormatException ex) {
                showWarning(canvas, "Please enter a valid number");
            }
        });

        searchBtn.setOnAction(e -> {
            String value = inputField.getText().trim();
            try {
                int num = Integer.parseInt(value);
                bst.search(num); // Delegate to BST class
                inputField.clear();
            } catch (NumberFormatException ex) {
                showWarning(canvas, "Please enter a valid number");
            }
        });

        VBox controls = new VBox(10, new Label("BST Operations"), inputField, insertBtn, deleteBtn, searchBtn);
        controls.setAlignment(Pos.CENTER);
        leftPanel.getChildren().add(controls);
    }

    // --- General Utility Methods ---

    /**
     * Displays a temporary warning message on the canvas.
     * 
     * @param canvas The canvas to display the message on.
     * @param msg    The warning message text.
     */
    private void showWarning(Pane canvas, String msg) {
        Label warning = new Label(msg);
        warning.setStyle(
                "-fx-background-color: #ffeb3b; -fx-text-fill: #d32f2f; -fx-padding: 5; -fx-border-radius: 5;");
        warning.setLayoutX(10);
        warning.setLayoutY(10);
        // Ensure the warning is visible by bringing it to the front
        warning.setViewOrder(-1); // Lower view order means higher z-index
        canvas.getChildren().add(warning);

        // Use a PauseTransition to remove the warning after a delay
        javafx.animation.PauseTransition pt = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(2));
        pt.setOnFinished(e -> canvas.getChildren().remove(warning));
        pt.play();
    }

    /**
     * Clears the visualization for the currently selected data structure.
     */
    private void clearCurrentStructure() {
        canvas.getChildren().clear(); // Clear all visual elements first
        if (currentMode == Mode.LINKED_LIST) {
            // Delegate clearing to the LinkedList class
            linkedList.clear();
        } else if (currentMode == Mode.STACK) {
            // Delegate clearing to the Stack class
            while (!stack.isEmpty()) {
                try {
                    stack.pop(); // This should handle visualization removal
                } catch (Exception ex) {
                    break;
                } // In case pop throws after isEmpty check
            }
        } else if (currentMode == Mode.QUEUE) {
            // Delegate clearing to the Queue class
            while (!queue.isEmpty()) {
                try {
                    queue.dequeue(); // This should handle visualization removal
                } catch (Exception ex) {
                    break;
                }
            }
        } else if (currentMode == Mode.HEAP) {
            // Delegate clearing to the Heap class
            while (!heap.isEmpty()) {
                try {
                    heap.removeRoot(); // This should handle visualization removal
                } catch (Exception ex) {
                    break;
                }
            }
        } else if (currentMode == Mode.BST) {
            // Delegate clearing to the BST class
            bst.clear(); // This should handle visualization removal
        }
        // Add cases for other modes if they manage their own clearing logic differently
    }

    public static void main(String[] args) {
        launch(args);
    }
}