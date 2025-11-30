package com.mygg.render;

import com.mygg.core.CollisionHandler;
import com.mygg.core.InputHandler;
import com.mygg.entities.Player;
import com.mygg.map.MapGenerator;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class GameCanvas extends Canvas {

    private final Player player;
    private final InputHandler input;
    private final CollisionHandler collider;
    private final int[][] map;

    private final Image ground;
    private final Image breakable;
    private final Image unbreak;

    private final int tile = 32;

    public GameCanvas(Player player, InputHandler input) {
        int[] spawnPos = new int[2]; // [x, y]
        
        // Generate map
        this.map = MapGenerator.generate(13, 13, spawnPos);

        // Init Collision Handler
        this.collider = new CollisionHandler(this.map, this.tile);

        // Ukuran canvas mengikuti map
        int width = map.length * tile;
        int height = map[0].length * tile;
        super.setWidth(width);
        super.setHeight(height);

        this.player = player;
        this.input = input;

        // Load textures
        ground = new Image(getClass().getResourceAsStream("/com/mygg/assets/tiles/ground.png"), 32, 32, false, false);
        breakable = new Image(getClass().getResourceAsStream("/com/mygg/assets/tiles/break.png"), 32, 32, false, false);
        unbreak = new Image(getClass().getResourceAsStream("/com/mygg/assets/tiles/unbreak.png"), 32, 32, false, false);

        // Spawn di lokasi aman
        player.x = spawnPos[0] * tile;
        player.y = spawnPos[1] * tile;

        loop.start();
    }

    private final AnimationTimer loop = new AnimationTimer() {
        long last = System.nanoTime();

        @Override
        public void handle(long now) {
            double dt = (now - last) / 1e9;
            last = now;

            update(dt);
            render();
        }
    };

    private void update(double dt) {
        double speed = player.speed * dt;

        double nextX = player.x;
        double nextY = player.y;

        if (input.isUp()) {
            nextY -= speed;
            player.dir = Player.Direction.UP;
        }
        if (input.isDown()) {
            nextY += speed;
            player.dir = Player.Direction.DOWN;
        }
        if (input.isLeft()) {
            nextX -= speed;
            player.dir = Player.Direction.LEFT;
        }
        if (input.isRight()) {
            nextX += speed;
            player.dir = Player.Direction.RIGHT;
        }

        // Collision Check
        if (!collider.checkCollision(nextX, player.y)) {
            player.x = nextX;
        }
        if (!collider.checkCollision(player.x, nextY)) {
            player.y = nextY;
        }

        player.state = (input.isUp() || input.isDown() || input.isLeft() || input.isRight())
                ? Player.State.WALK
                : Player.State.IDLE;
    }

    private void render() {
        GraphicsContext g = getGraphicsContext2D();

        g.clearRect(0, 0, getWidth(), getHeight());
        g.setImageSmoothing(false);

        // 1. Gambar tile (Visual Game Asli)
        for (int y = 0; y < map[0].length; y++) {
            for (int x = 0; x < map.length; x++) {
                double posX = x * tile;
                double posY = y * tile;

                switch (map[x][y]) {
                    case 0 -> g.drawImage(ground, posX, posY);
                    case 1 -> g.drawImage(unbreak, posX, posY);
                    case 2 -> g.drawImage(breakable, posX, posY);
                }
            }
        }

        // 2. DEBUG: Visualisasi Hitbox Blok & Koordinat
        // Menggunakan Fill (Isian) Transparan + Border + Text Coord
        g.setLineWidth(1.0);
        g.setFont(Font.font("Monospaced", 10)); // Font kecil untuk koordinat

        for (int y = 0; y < map[0].length; y++) {
            for (int x = 0; x < map.length; x++) {
                double posX = x * tile;
                double posY = y * tile;

                switch (map[x][y]) {
                    case 0 -> {
                        // Ground (Aman) - Biru Transparan
                        g.setFill(Color.rgb(33, 150, 243, 0.2)); 
                        g.fillRect(posX, posY, tile, tile);
                        g.setStroke(Color.rgb(33, 150, 243, 0.5));
                        g.strokeRect(posX, posY, tile, tile);
                    }
                    case 1 -> {
                        // Unbreakable (Solid) - Merah Transparan
                        g.setFill(Color.rgb(244, 67, 54, 0.4)); 
                        g.fillRect(posX, posY, tile, tile);
                        g.setStroke(Color.rgb(244, 67, 54, 0.8));
                        g.strokeRect(posX, posY, tile, tile);
                    }
                    case 2 -> {
                        // Breakable (Solid) - Kuning Transparan
                        g.setFill(Color.rgb(255, 193, 7, 0.4)); 
                        g.fillRect(posX, posY, tile, tile);
                        g.setStroke(Color.rgb(255, 193, 7, 0.8));
                        g.strokeRect(posX, posY, tile, tile);
                    }
                }

                // Debug: Tulis koordinat grid (x,y) di pojok kiri atas tile
                g.setFill(Color.WHITE);
                g.fillText(x + "," + y, posX + 2, posY + 10);
            }
        }

        // 3. Gambar Sprite Player
        g.drawImage(player.update(1 / 60.0), player.x, player.y);

        // 4. DEBUG: Hitbox Player (MAGENTA)
        // Kotak ini menunjukkan area sensitif player terhadap tabrakan
        g.setStroke(Color.MAGENTA);
        g.setLineWidth(2.0);
        g.strokeRect(
            player.x + collider.offset, 
            player.y + collider.offset, 
            collider.hitboxSize, 
            collider.hitboxSize
        );
    }
}