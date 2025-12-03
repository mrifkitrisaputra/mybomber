package com.mygg;

import com.mygg.core.InputHandler;
import com.mygg.entities.Player;
import com.mygg.managers.RoomManager;
import com.mygg.render.GameCanvas;
import com.mygg.render.SpriteLoader;
import com.mygg.ui.GameOverPopup;
import com.mygg.ui.PauseMenu;
import com.mygg.ui.SceneManager;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class App extends Application {
    
    private static StackPane gameRoot; 
    public static Stage currentStage; 
    private static GameCanvas currentCanvas;

    @Override
    public void start(Stage stage) {
        currentStage = stage;
        stage.setMaximized(true);
        SceneManager.setStage(stage);

        stage.setTitle("Bomberman Modern");
        stage.setFullScreenExitKeyCombination(null);

        stage.setMaximized(true);
        SceneManager.toLobby();
        stage.show();
    }

    public static void startGame(Stage stage) {
        // Init Room State (Lock room, set host, dll)
        RoomManager.isGameStarted = true; 

        InputHandler input = new InputHandler();
        SpriteLoader loader = new SpriteLoader();
        loader.loadFolder("/com/mygg/assets/player/");

        Player player = new Player(loader);
        currentCanvas = new GameCanvas(player, input);

        gameRoot = new StackPane(currentCanvas);
        Scene gameScene = new Scene(gameRoot);

        gameScene.setOnKeyPressed(e -> input.keyDown(e));
        gameScene.setOnKeyReleased(e -> input.keyUp(e));

        stage.setMaximized(true);
        stage.setScene(gameScene);
        currentCanvas.widthProperty().bind(gameScene.widthProperty());
        currentCanvas.heightProperty().bind(gameScene.heightProperty());
        currentCanvas.requestFocus();
    }

    public static void showPauseMenu() {
        Platform.runLater(() -> {
            if (gameRoot.getChildren().stream().anyMatch(n -> n instanceof PauseMenu)) return;

            PauseMenu pause = new PauseMenu(
                () -> { // ON RESUME
                    gameRoot.getChildren().removeIf(n -> n instanceof PauseMenu);
                    
                    // PENTING: Panggil resume di canvas agar logic game jalan lagi
                    if (currentCanvas != null) currentCanvas.resume(); 
                    
                    currentCanvas.requestFocus();
                },
                () -> { // ON LEAVE ROOM
                    // Stop loop game agar tidak makan resource di background
                    if (currentCanvas != null) currentCanvas.stop(); 
                    
                    RoomManager.isGameStarted = false;
                    currentStage.setMaximized(true);
                    SceneManager.toLobby();
                }
            );
            gameRoot.getChildren().add(pause);
        });
    }

    public static void showGameOver(boolean isWin) {
        javafx.application.Platform.runLater(() -> {
            GameOverPopup popup = new GameOverPopup(isWin);
            
            // Cek biar gak numpuk
            if (gameRoot != null && gameRoot.getChildren().stream().noneMatch(n -> n instanceof GameOverPopup)) {
                gameRoot.getChildren().add(popup);
            }
        });
    }

    public static void main(String[] args) {
        launch();
    }
}