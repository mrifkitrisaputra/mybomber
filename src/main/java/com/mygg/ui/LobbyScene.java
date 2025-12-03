package com.mygg.ui;

import java.util.Optional;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;

public class LobbyScene {
    private final Scene scene;

    public LobbyScene() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #2c3e50;"); 

        // --- HEADER ---
        Label title = new Label("GAME LOBBY");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
        title.setTextFill(Color.WHITE);
        BorderPane.setAlignment(title, Pos.CENTER);
        root.setTop(title);

        // --- ROOM LIST (CENTER) ---
        ListView<String> roomList = new ListView<>();
        // Note: String "(Private)" ini jadi penanda kunci
        roomList.getItems().addAll("Room Santai (Public)", "Pro Player Only (Private)", "Test Room");
        roomList.setStyle("-fx-control-inner-background: #34495e; -fx-text-fill: white;");
        
        VBox centerBox = new VBox(10, new Label("Available Rooms:") {{ setTextFill(Color.WHITE); }}, roomList);
        centerBox.setPadding(new Insets(20, 0, 20, 0));
        root.setCenter(centerBox);

        // --- BUTTONS (BOTTOM) ---
        Button btnJoin = new Button("Join Selected Room");
        Button btnCreate = new Button("Create New Room");
        styleButton(btnJoin);
        styleButton(btnCreate);

        HBox bottomBox = new HBox(15, btnJoin, btnCreate);
        bottomBox.setAlignment(Pos.CENTER);
        root.setBottom(bottomBox);

        // --- LOGIC ---
        btnCreate.setOnAction(e -> showCreateRoomDialog());
        
        // UPDATE LOGIC JOIN: Cek Public/Private
        btnJoin.setOnAction(e -> {
            String selected = roomList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                // Cek apakah string mengandung kata "Private"
                if (selected.contains("(Private)")) {
                    showPasswordDialog(selected);
                } else {
                    // Kalau Public langsung masuk
                    SceneManager.toRoom(selected, false);
                }
            }
        });

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        this.scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
    }

    // Method baru untuk validasi password
    private void showPasswordDialog(String roomName) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Private Room");
        dialog.setHeaderText("Room ini dikunci (Private)");
        dialog.setContentText("Masukkan Password (Hint: 1234):");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(password -> {
            // Simulasi validasi password sederhana
            if (password.equals("1234")) {
                SceneManager.toRoom(roomName, false);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Access Denied");
                alert.setHeaderText("Password Salah!");
                alert.setContentText("Kamu tidak bisa masuk ke room ini.");
                alert.showAndWait();
            }
        });
    }

    private void showCreateRoomDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Create Room");
        dialog.setHeaderText("Room Settings");

        ButtonType createBtnType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createBtnType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField roomName = new TextField();
        roomName.setPromptText("Room Name");
        PasswordField password = new PasswordField();
        password.setPromptText("Password (Optional)");
        
        CheckBox isPrivate = new CheckBox("Private Room");
        isPrivate.setOnAction(e -> password.setDisable(!isPrivate.isSelected()));
        password.setDisable(true); 

        grid.add(new Label("Name:"), 0, 0);
        grid.add(roomName, 1, 0);
        grid.add(isPrivate, 0, 1);
        grid.add(password, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createBtnType) {
                // Return format nama room (Simulasi)
                String name = roomName.getText();
                if (isPrivate.isSelected()) {
                    return name + " (Private)";
                }
                return name + " (Public)";
            }
            return null;
        });

        dialog.showAndWait().ifPresent(finalName -> {
            if (!finalName.isEmpty()) SceneManager.toRoom(finalName, true); 
        });
    }

    private void styleButton(Button btn) {
        btn.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        btn.setPrefWidth(150);
        btn.setPrefHeight(40);
    }

    public Scene getScene() { return scene; }
}