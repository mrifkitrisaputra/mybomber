package com.mygg;

import com.mygg.core.InputHandler;
import com.mygg.entities.Player;
import com.mygg.render.GameCanvas;
import com.mygg.render.SpriteLoader;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) {

        InputHandler input = new InputHandler();

        SpriteLoader loader = new SpriteLoader();

        loader.loadFolder("/com/mygg/assets/player/");

        Player player = new Player(loader);
        GameCanvas canvas = new GameCanvas(player, input);

        Scene scene = new Scene(new StackPane(canvas));
        scene.setOnKeyPressed(e -> input.keyDown(e));
        scene.setOnKeyReleased(e -> input.keyUp(e));

        stage.setScene(scene);
        stage.setTitle("Bomberman Modern");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
