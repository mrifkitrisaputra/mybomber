package com.mygg;

import com.mygg.core.InputHandler;
import com.mygg.entities.Player;
import com.mygg.render.GameCanvas;
import com.mygg.render.SpriteLoader;
import com.mygg.ui.GameOverPopup;
import com.mygg.ui.SceneManager;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class App extends Application {
    
    // Kita simpan root agar bisa menumpuk Popup Game Over nanti
    private static StackPane gameRoot; 

    @Override
    public void start(Stage stage) {
        // 1. Inisialisasi SceneManager dengan Stage utama
        stage.setMaximized(true);
        SceneManager.setStage(stage);

        // 2. Setting Window
        stage.setTitle("Bomberman Modern");
        stage.setFullScreenExitKeyCombination(null);

        // 3. Masuk ke LOBBY dulu (Bukan langsung game)
        SceneManager.toLobby();
        
        stage.show();
    }

    /**
     * Method ini dipanggil dari RoomScene saat tombol START ditekan.
     */
    public static void startGame(Stage stage) {
        // --- SETUP CORE GAME (Logic lama dipindah ke sini) ---
        
        InputHandler input = new InputHandler();
        SpriteLoader loader = new SpriteLoader();
        
        // Load assets
        loader.loadFolder("/com/mygg/assets/player/");

        // Init Player & Canvas
        Player player = new Player(loader);
        GameCanvas canvas = new GameCanvas(player, input);

        // --- SETUP LAYOUT ---
        // Gunakan StackPane agar kita bisa menumpuk UI (GameOver) di atas Canvas
        gameRoot = new StackPane(canvas);
        
        // Buat Scene baru untuk Gameplay
        Scene gameScene = new Scene(gameRoot);

        // Input Handling (Sambungkan keyboard ke scene game)
        gameScene.setOnKeyPressed(e -> input.keyDown(e));
        gameScene.setOnKeyReleased(e -> input.keyUp(e));

        // Pindah layar ke Game
        stage.setScene(gameScene);
        
        // Pastikan canvas mengikuti ukuran layar
        canvas.widthProperty().bind(gameScene.widthProperty());
        canvas.heightProperty().bind(gameScene.heightProperty());
        
        // Fokus ke game agar input keyboard terbaca
        canvas.requestFocus();
    }

    /**
     * Method untuk memunculkan Popup Game Over / Victory
     */
    public static void showGameOver(boolean isWin) {
        // Kita jalankan di JavaFX Thread agar aman
        javafx.application.Platform.runLater(() -> {
            GameOverPopup popup = new GameOverPopup(isWin, () -> {
                // Aksi tombol "Back to Lobby"
                SceneManager.toLobby();
            });
            
            // Tumpuk popup di atas game canvas
            if (gameRoot != null) {
                gameRoot.getChildren().add(popup);
            }
        });
    }

    public static void main(String[] args) {
        launch();
    }
}