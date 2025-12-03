package com.mygg.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class GameOverPopup extends VBox {

    public GameOverPopup(boolean isWin, Runnable onBackToLobby) {
        // Style Box
        this.setAlignment(Pos.CENTER);
        this.setSpacing(20);
        this.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8); -fx-padding: 40; -fx-background-radius: 20;");
        this.setMaxSize(400, 300);

        // Title
        Label title = new Label(isWin ? "VICTORY!" : "YOU DIED");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 48));
        title.setTextFill(isWin ? Color.GOLD : Color.RED);

        // Subtitle
        Label sub = new Label(isWin ? "You are the last survivor!" : "Better luck next time.");
        sub.setFont(Font.font("Arial", 16));
        sub.setTextFill(Color.WHITE);

        // Button
        Button btnLobby = new Button("Back to Lobby");
        btnLobby.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-size: 18px;");
        btnLobby.setOnAction(e -> onBackToLobby.run());

        this.getChildren().addAll(title, sub, btnLobby);
    }
}