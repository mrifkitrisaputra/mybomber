package com.mygg;

import com.mygg.map.MapGenerator;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class testing extends Application {

    @Override
    public void start(Stage stage) {

        // --- 1. Generate map ---
        int[][] map = MapGenerator.generate(13, 13);

        // --- 2. Load textures as 32x32 (NO BLUR, NO SMOOTH) ---
        Image ground = new Image(
                getClass().getResourceAsStream("/com/mygg/assets/tiles/ground.png"),
                32, 32,
                false, false
        );

        Image breakable = new Image(
                getClass().getResourceAsStream("/com/mygg/assets/tiles/break.png"),
                32, 32,
                false, false
        );

        Image unbreakable = new Image(
                getClass().getResourceAsStream("/com/mygg/assets/tiles/unbreak.png"),
                32, 32,
                false, false
        );

        int tileSize = 32;

        // Canvas width = jumlah kolom * tile, height = jumlah baris * tile
        int rows = map.length;        // Y
        int cols = map[0].length;     // X

        Canvas canvas = new Canvas(cols * tileSize, rows * tileSize);
        GraphicsContext g = canvas.getGraphicsContext2D();

        // --- 3. Draw MAP ONLY ---
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {

                int tile = map[y][x]; // yang benar: [baris][kolom]

                switch (tile) {
                    case 0 -> g.drawImage(ground, x * tileSize, y * tileSize);
                    case 1 -> g.drawImage(unbreakable, x * tileSize, y * tileSize);
                    case 2 -> g.drawImage(breakable, x * tileSize, y * tileSize);
                }
            }
        }

        // --- 4. WINDOW ---
        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("MAP TESTING");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
