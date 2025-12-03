// src/main/java/com/mygg/ui/PauseMenu.java
package com.mygg.ui;

import com.mygg.managers.RoomManager;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class PauseMenu extends VBox {

    public PauseMenu(Runnable onResume, Runnable onLeave) {
        this.setAlignment(Pos.CENTER);
        this.setSpacing(20);
        this.setStyle("-fx-background-color: rgba(0, 0, 0, 0.85); -fx-padding: 30; -fx-background-radius: 15;");
        this.setMaxSize(300, 300);

        Label title = new Label("PAUSED");
        title.setTextFill(Color.WHITE);
        title.setFont(Font.font("Arial", 32));

        Button btnResume = new Button("Resume Game");
        Button btnLeave = new Button("Leave Room");

        styleButton(btnResume, "#27ae60");
        styleButton(btnLeave, "#c0392b");

        btnResume.setOnAction(e -> onResume.run());
        
        btnLeave.setOnAction(e -> {
            RoomManager.playerLeft("Player 1 (You)"); // Logic host migration
            onLeave.run();
        });

        this.getChildren().addAll(title, btnResume, btnLeave);
    }

    private void styleButton(Button btn, String color) {
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 16px;");
        btn.setPrefWidth(200);
    }
}