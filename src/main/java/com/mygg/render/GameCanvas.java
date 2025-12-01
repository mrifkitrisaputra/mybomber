package com.mygg.render;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.mygg.core.CollisionHandler;
import com.mygg.core.InputHandler;
import com.mygg.core.SoundHandler; // Import SoundHandler
import com.mygg.entities.Bomb;
import com.mygg.entities.Explosion;
import com.mygg.entities.Player;
import com.mygg.map.MapGenerator;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class GameCanvas extends Canvas {

    private final Player player;
    private final InputHandler input;
    private final CollisionHandler collider;
    private final int[][] map;

    // List Entity
    private final List<Bomb> bombs = new ArrayList<>();
    private final List<Explosion> explosions = new ArrayList<>();

    private boolean isSpaceHeld = false;

    private final Image ground;
    private final Image breakable;
    private final Image unbreak;

    private final int tile = 32;

    public GameCanvas(Player player, InputHandler input) {
        // Init Sound System
        SoundHandler.init();

        int[] spawnPos = new int[2]; 
        
        // Generate map
        this.map = MapGenerator.generate(13, 13, spawnPos);
        this.collider = new CollisionHandler(this.map, this.tile);

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
        // 1. Player Movement
        double speed = player.speed * dt;
        double nextX = player.x;
        double nextY = player.y;

        if (input.isUp()) { nextY -= speed; player.dir = Player.Direction.UP; }
        if (input.isDown()) { nextY += speed; player.dir = Player.Direction.DOWN; }
        if (input.isLeft()) { nextX -= speed; player.dir = Player.Direction.LEFT; }
        if (input.isRight()) { nextX += speed; player.dir = Player.Direction.RIGHT; }

        if (!collider.checkCollision(nextX, player.y)) player.x = nextX;
        if (!collider.checkCollision(player.x, nextY)) player.y = nextY;

        player.state = (input.isUp() || input.isDown() || input.isLeft() || input.isRight())
                ? Player.State.WALK : Player.State.IDLE;

        // 2. Place Bomb
        if (input.isPlace()) {
            if (!isSpaceHeld) {
                placeBomb();
                isSpaceHeld = true;
            }
        } else {
            isSpaceHeld = false;
        }

        // 3. Update Bombs
        Iterator<Bomb> bIt = bombs.iterator();
        while (bIt.hasNext()) {
            Bomb b = bIt.next();
            b.update(dt);
            if (b.isExploded) {
                // Pass MAP ke Explosion agar bisa hancurkan blok
                explosions.add(new Explosion(b.tileX, b.tileY, b.range, this.map));
                bIt.remove();
            }
        }

        // 4. Update Explosions
        Iterator<Explosion> eIt = explosions.iterator();
        while (eIt.hasNext()) {
            Explosion e = eIt.next();
            e.update(dt);
            if (e.isFinished) eIt.remove();
        }
    }

    private void placeBomb() {
        // Hitung posisi grid dengan offset agar pas tengah
        int gx = (int) ((player.x + collider.offset) / tile);
        int gy = (int) ((player.y + collider.offset) / tile);

        // Cek duplicate
        for (Bomb b : bombs) {
            if (b.tileX == gx && b.tileY == gy) return;
        }

        bombs.add(new Bomb(gx, gy, 1)); 
        
        // Play SFX Bom ditaruh
        SoundHandler.playBombPlace();
    }

    private void render() {
        GraphicsContext g = getGraphicsContext2D();
        g.clearRect(0, 0, getWidth(), getHeight());
        g.setImageSmoothing(false);

        // Render Map
        for (int y = 0; y < map[0].length; y++) {
            for (int x = 0; x < map.length; x++) {
                double px = x * tile;
                double py = y * tile;
                switch (map[x][y]) {
                    case 0 -> g.drawImage(ground, px, py);
                    case 1 -> g.drawImage(unbreak, px, py);
                    case 2 -> g.drawImage(breakable, px, py);
                }
            }
        }

        // Render Bombs
        for (Bomb b : bombs) b.render(g);

        // Render Explosions
        for (Explosion e : explosions) e.render(g);

        // Render Player
        g.drawImage(player.update(1 / 60.0), player.x, player.y);
    }
}