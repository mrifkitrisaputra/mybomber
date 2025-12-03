package com.mygg.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Screen;

public class RoomScene {
    private final Scene scene;
    private int playerCount = 1; // Simulasi jumlah player (Host)

    public RoomScene(String roomName, boolean isHost) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #2c3e50;");

        // --- HEADER ---
        VBox header = new VBox(5);
        Label lblRoomName = new Label("ROOM: " + roomName);
        lblRoomName.setFont(new Font("Arial", 28));
        lblRoomName.setTextFill(Color.WHITE);
        
        Label lblCode = new Label("Room Code: 1234"); // Logic generate code nanti
        lblCode.setFont(new Font("Consolas", 18));
        lblCode.setTextFill(Color.YELLOW);
        
        header.getChildren().addAll(lblRoomName, lblCode);
        header.setAlignment(Pos.CENTER);
        root.setTop(header);

        // --- PLAYER LIST ---
        ListView<String> playerList = new ListView<>();
        playerList.getItems().add("Player 1 (You)"); // Mockup
        // Simulasi player lain masuk (nanti via network)
        // playerList.getItems().add("Player 2"); 
        
        playerList.setStyle("-fx-control-inner-background: #34495e; -fx-text-fill: white;");
        root.setCenter(playerList);

        // --- BUTTONS ---
        Button btnStart = new Button("START GAME");
        Button btnLeave = new Button("LEAVE ROOM");
        
        styleButton(btnStart, "#27ae60"); // Hijau
        styleButton(btnLeave, "#c0392b"); // Merah

        // Logic Start: Hanya Host & Min 2 Player (Disini kita mock dulu biar bisa dites sendiri)
        if (!isHost) {
            btnStart.setDisable(true);
            btnStart.setText("Waiting for Host...");
        }

        btnStart.setOnAction(e -> {
            // Cek jumlah player (Nanti aktifkan ini jika sudah ada network)
            /* if (playerList.getItems().size() < 2) {
                System.out.println("Need at least 2 players!");
                return;
            }
            */
            // Panggil Callback untuk memulai game di Main App
            // Untuk sementara kita print dulu, integrasi di App.java
             javafx.stage.Stage currentStage = (javafx.stage.Stage) btnStart.getScene().getWindow();
    com.mygg.App.startGame(currentStage);
             // Nanti panggil SceneManager.toGame() atau callback khusus
        });

        btnLeave.setOnAction(e -> SceneManager.toLobby());

        HBox bottomBox = new HBox(20, btnLeave, btnStart);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(20));
        root.setBottom(bottomBox);

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        this.scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
    }

    private void styleButton(Button btn, String color) {
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        btn.setPrefSize(180, 50);
    }

    public Scene getScene() { return scene; }
}