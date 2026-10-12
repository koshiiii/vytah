package sk.koshi.elevator;

import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.LinkedList;
import java.util.Queue;

public class ElevatorController {

    @FXML
    private BorderPane root;

    @FXML
    private Rectangle elevator;

    @FXML
    private Label floorLabel;

    @FXML
    private Label statusLabel;

    private int currentFloor = 0;
    private int wantedFloor = 0;
    private boolean busy = false;

    private final Queue<Integer> requests = new LinkedList<>();

    @FXML
    private void initialize() {
        root.setOnKeyPressed(event -> {
            String key = event.getText();

            if (key.matches("[0-8]")) {
                callElevator(Integer.parseInt(key));
            }
        });
    }

    @FXML
    private void buttonPressed(ActionEvent event) {
        Button button = (Button) event.getSource();
        int floor = Integer.parseInt(button.getText());
        callElevator(floor);
    }

    private void callElevator(int floor) {
        if (busy) {
            requests.add(floor);
            statusLabel.setText("Poschodie " + floor + " je vo fronte.");
            return;
        }

        if (floor == currentFloor) {
            statusLabel.setText("Výťah je už na poschodí " + floor + ".");
            return;
        }

        wantedFloor = floor;
        busy = true;
        statusLabel.setText("Výťah ide na poschodie " + wantedFloor + ".");
        moveOneFloor();
    }

    private void moveOneFloor() {
        if (currentFloor == wantedFloor) {
            arrived();
            return;
        }

        int nextFloor;

        if (wantedFloor > currentFloor) {
            nextFloor = currentFloor + 1;
        } else {
            nextFloor = currentFloor - 1;
        }

        TranslateTransition move = new TranslateTransition(Duration.millis(500), elevator);
        move.setToY(-nextFloor * 50);
        move.setOnFinished(event -> {
            currentFloor = nextFloor;
            floorLabel.setText(String.valueOf(currentFloor));
            moveOneFloor();
        });
        move.play();
    }

    private void arrived() {
        statusLabel.setText("Výťah je na poschodí " + currentFloor + ". Čaká 2 sekundy.");

        PauseTransition wait = new PauseTransition(Duration.seconds(2));
        wait.setOnFinished(event -> {
            busy = false;
            nextRequest();
        });
        wait.play();
    }

    private void nextRequest() {
        while (!requests.isEmpty()) {
            int nextFloor = requests.poll();

            if (nextFloor != currentFloor) {
                callElevator(nextFloor);
                return;
            }
        }

        statusLabel.setText("Výťah čaká na poschodí " + currentFloor + ".");
    }
}
