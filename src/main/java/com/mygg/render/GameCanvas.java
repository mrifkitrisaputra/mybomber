package com.mygg.render;

import com.mygg.core.InputHandler;
import com.mygg.entities.Player;
import com.mygg.map.MapGenerator;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class GameCanvas extends Canvas {

    private final Player player;
    private final InputHandler input;

    private final int[][] map;

    private final Image ground;
    private final Image breakable;
    private final Image unbreak;

    private final int tile = 32;

    public GameCanvas(Player player, InputHandler input) {
        super(800, 600);

        this.player = player;
        this.input = input;

        // Load map 13x13
        this.map = MapGenerator.generate(13, 13);

        // Load textures
        ground = new Image(getClass().getResourceAsStream("/com/mygg/assets/tiles/ground.png"), 32, 32, false, false);
        breakable = new Image(getClass().getResourceAsStream("/com/mygg/assets/tiles/break.png"), 32, 32, false, false);
        unbreak = new Image(getClass().getResourceAsStream("/com/mygg/assets/tiles/unbreak.png"), 32, 32, false, false);

        player.x = tile * 1;
        player.y = tile * 1;

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

        if (input.up) {
            nextY -= speed;
            player.dir = Player.Direction.UP;
        }
        if (input.down) {
            nextY += speed;
            player.dir = Player.Direction.DOWN;
        }
        if (input.left) {
            nextX -= speed;
            player.dir = Player.Direction.LEFT;
        }
        if (input.right) {
            nextX += speed;
            player.dir = Player.Direction.RIGHT;
        }

        // Collision check
        if (!collides(nextX, player.y)) {
            player.x = nextX;
        }
        if (!collides(player.x, nextY)) {
            player.y = nextY;
        }

        // animation state
        player.state = (input.up || input.down || input.left || input.right)
                ? Player.State.WALK
                : Player.State.IDLE;
    }

    private boolean collides(double px, double py) {

        int gridX = (int) (px / tile);
        int gridY = (int) (py / tile);

        if (map[gridY][gridX] == 1) return true; // unbreakable
        if (map[gridY][gridX] == 2) return true; // breakable

        return false;
    }

    private void render() {
        GraphicsContext g = getGraphicsContext2D();

        g.clearRect(0, 0, getWidth(), getHeight());
        g.setImageSmoothing(false);

        // Draw map
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {

                int t = map[y][x];

                switch (t) {
                    case 0 -> g.drawImage(ground, x * tile, y * tile);
                    case 1 -> g.drawImage(unbreak, x * tile, y * tile);
                    case 2 -> g.drawImage(breakable, x * tile, y * tile);
                }
            }
        }

        // Draw player
        g.drawImage(player.update(1 / 60.0), player.x, player.y);
    }
}
