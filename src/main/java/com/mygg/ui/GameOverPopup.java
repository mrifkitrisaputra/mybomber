// src/main/java/com/mygg/ui/GameOverPopup.java
package com.mygg.ui;

import com.mygg.App;
import com.mygg.managers.RoomManager;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class GameOverPopup extends VBox {

    private int voteCount = 0;
    private final int MIN_VOTES = 3;
    private Label lblVotes;
    private Button btnRematch;

    public GameOverPopup(boolean isWin) {
        this.setAlignment(Pos.CENTER);
        this.setSpacing(20);
        this.setStyle("-fx-background-color: rgba(0, 0, 0, 0.9); -fx-padding: 40; -fx-background-radius: 20;");
        this.setMaxSize(500, 400);

        Label title = new Label(isWin ? "VICTORY!" : "YOU DIED");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 48));
        title.setTextFill(isWin ? Color.GOLD : Color.RED);

        // --- REMATCH SECTION ---
        lblVotes = new Label("Rematch Votes: 0/" + MIN_VOTES);
        lblVotes.setTextFill(Color.WHITE);
        lblVotes.setFont(Font.font(18));

        btnRematch = new Button("Vote Rematch");
        styleButton(btnRematch, "#2980b9"); // Biru

        btnRematch.setOnAction(e -> handleVote());

        // Tombol Host (Accept Rematch) - Awalnya hidden
        Button btnHostAccept = new Button("START REMATCH");
        styleButton(btnHostAccept, "#27ae60"); // Hijau
        btnHostAccept.setVisible(false); // Muncul jika vote cukup & user adalah host

        // Logic Simulasi jika vote terpenuhi
        btnHostAccept.setOnAction(e -> {
            App.startGame(App.currentStage); // Restart Game
        });

        // --- LEAVE BUTTON ---
        Button btnLeave = new Button("Leave Room");
        styleButton(btnLeave, "#c0392b"); // Merah
        btnLeave.setOnAction(e -> {
            RoomManager.playerLeft("Player 1 (You)"); // Trigger logic keluar
            SceneManager.toLobby();
        });

        HBox buttonBox = new HBox(20, btnLeave, btnRematch);
        buttonBox.setAlignment(Pos.CENTER);

        this.getChildren().addAll(title, lblVotes, buttonBox, btnHostAccept);

        // Simpan referensi button host biar bisa diupdate
        this.btnHostStart = btnHostAccept;
    }
    
    private Button btnHostStart;

    private void handleVote() {
        btnRematch.setDisable(true); // Disable biar gak spam vote
        btnRematch.setText("Voted!");
        
        // SIMULASI: Anggap player lain langsung setuju
        voteCount++; // Vote kita
        updateVoteUI();

        // Simulasi delay bot voting (pake thread sleep ala-ala atau langsung aja)
        voteCount += 2; // Simulasi 2 player lain vote
        updateVoteUI();
    }

    private void updateVoteUI() {
        lblVotes.setText("Rematch Votes: " + voteCount + "/" + MIN_VOTES);
        
        // Jika vote cukup DAN kita adalah HOST -> Tampilkan tombol Start
        if (voteCount >= MIN_VOTES && RoomManager.isHost()) {
            btnHostStart.setVisible(true);
        }
    }

    private void styleButton(Button btn, String color) {
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        btn.setPrefSize(160, 40);
    }
}