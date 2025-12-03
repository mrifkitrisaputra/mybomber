package com.mygg.render;

import com.mygg.core.Arena;
import com.mygg.core.GameTimer;
import com.mygg.entities.Player;
import com.mygg.managers.BombManager; // IMPORT BARU
import com.mygg.managers.ExplosionManager;
import com.mygg.managers.ItemManager;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class GameRenderer {

    private final GameCanvas canvas;
    private final Player player;
    private final int[][] map;
    private final BombManager bombs;
    private final ExplosionManager explosions;
    private final ItemManager itemManager; // FIELD BARU
    
    private final CameraScaler scaler; // (Tambahkan field scaler yg sebelumnya lupa di-declare di snippet mu)
    private final GameTimer timer;
    private final Arena arena;

    private final Image ground, breakable, unbreak;
    private final int tile;

    public GameRenderer(GameCanvas canvas, Player player, int[][] map,
                        BombManager bombs, ExplosionManager explosions,
                        ItemManager itemManager, // PARAMETER BARU
                        CameraScaler scaler, GameTimer timer, Arena arena) {
        this.canvas = canvas;
        this.player = player;
        this.map = map;
        this.bombs = bombs;
        this.explosions = explosions;
        this.itemManager = itemManager; // SIMPAN
        this.scaler = scaler;
        this.timer = timer;
        this.arena = arena;
        this.tile = canvas.getTileSize();

        // Load images
        ground = new Image(getClass().getResourceAsStream("/com/mygg/assets/tiles/ground.png"), tile, tile, false, false);
        breakable = new Image(getClass().getResourceAsStream("/com/mygg/assets/tiles/break.png"), tile, tile, false, false);
        unbreak = new Image(getClass().getResourceAsStream("/com/mygg/assets/tiles/unbreak.png"), tile, tile, false, false);
    }

    public void render() {
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.setFill(Color.web("#2c3e50")); // Warna Dark Blue-Grey (Ala Discord/Modern UI)
    g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        

        double offsetX = canvas.getOffsetX();
        double offsetY = canvas.getOffsetY();
        double scale = canvas.getRenderScale();

        g.save();
        g.translate(offsetX, offsetY);
        g.scale(scale, scale);

        // 1. Map
        renderMap(g);
        
        // 2. Arena Highlight
        renderArenaShrink(g); 
        
        // 3. Items (Render item di atas tanah, di bawah player)
        itemManager.render(g); 
        
        // 4. Objects
        bombs.render(g);
        explosions.render(g);

        // 5. Player
        g.drawImage(player.update(1/60.0), player.x, player.y);

        g.restore();
        renderUI(g);
    }

    private void renderMap(GraphicsContext g) {
        for (int y = 0; y < map[0].length; y++) {
            for (int x = 0; x < map.length; x++) {
                Image img;
                if (map[x][y] == 1) img = unbreak;
                else if (map[x][y] == 2) img = breakable;
                else img = ground;
                
                g.drawImage(img, x * tile, y * tile);
            }
        }
    }

    private void renderArenaShrink(GraphicsContext g) {
        if (!arena.isShrinking()) return;

        double alpha = 0.4 + 0.4 * Math.sin(arena.getShrinkAnimTimer() * 20);
        g.setFill(Color.rgb(255, 0, 0, alpha));

        int left = arena.getLeft();
        int right = arena.getRight();
        int top = arena.getTop();
        int bottom = arena.getBottom();
        
        String step = arena.getCurrentStepPattern();

        if ("LR".equals(step)) {
            g.fillRect(left * 32, 0, 32, map[0].length * 32);
            g.fillRect(right * 32, 0, 32, map[0].length * 32);
        } else if ("TB".equals(step)) {
            g.fillRect(0, top * 32, map.length * 32, 32);
            g.fillRect(0, bottom * 32, map.length * 32, 32);
        }
    }

    private void renderUI(GraphicsContext g) {
        g.save();
        g.setFont(new Font("Consolas", 48));
        g.setFill(Color.WHITE);
        g.setStroke(Color.BLACK);
        g.setLineWidth(2);
        String t = timer.toString();
        g.strokeText(t, canvas.getWidth() - 150, 60);
        g.fillText(t, canvas.getWidth() - 150, 60);
        g.restore();
    }
}