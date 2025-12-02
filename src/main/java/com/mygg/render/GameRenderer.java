package com.mygg.render;

import com.mygg.entities.Player;
import com.mygg.managers.BombManager;
import com.mygg.managers.ExplosionManager;
import com.mygg.core.GameTimer;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;

public class GameRenderer {

    private final GameCanvas canvas;
    private final Player player;
    private final int[][] map;
    private final BombManager bombs;
    private final ExplosionManager explosions;
    private final CameraScaler scaler;
    private final GameTimer timer;

    private double timerMarginX = 20;
    private double timerMarginY = 30;
    private int timerFontSize = 48;


    private final Image ground, breakable, unbreak;

    private final int tile;

    public GameRenderer(GameCanvas canvas, Player player, int[][] map,
                        BombManager bombs, ExplosionManager explosions,
                        CameraScaler scaler, GameTimer timer) {
        this.canvas = canvas;
        this.player = player;
        this.map = map;
        this.bombs = bombs;
        this.explosions = explosions;
        this.scaler = scaler;
        this.timer = timer;

        this.tile = canvas.getTileSize();

        ground = new Image(getClass().getResourceAsStream("/com/mygg/assets/tiles/ground.png"), 32, 32, false, false);
        breakable = new Image(getClass().getResourceAsStream("/com/mygg/assets/tiles/break.png"), 32, 32, false, false);
        unbreak = new Image(getClass().getResourceAsStream("/com/mygg/assets/tiles/unbreak.png"), 32, 32, false, false);
    }

    public void render() {
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        double offsetX = canvas.getOffsetX();
        double offsetY = canvas.getOffsetY();
        double scale = canvas.getRenderScale();

        g.save();
        g.translate(offsetX, offsetY);
        g.scale(scale, scale);

        renderMap(g);
        bombs.render(g);
        explosions.render(g);

        // Render player
        g.drawImage(player.update(1 / 60.0), player.x, player.y);

        g.restore();

        renderUI(g);
    }

    private void renderMap(GraphicsContext g) {
        for (int y = 0; y < map[0].length; y++) {
            for (int x = 0; x < map.length; x++) {
                Image img;
                switch (map[x][y]) {
                    case 1 -> img = unbreak;
                    case 2 -> img = breakable;
                    default -> img = ground;
                }
                g.drawImage(img, x * tile, y * tile);
            }
        }
    }

private void renderUI(GraphicsContext g) {
    g.save();

    g.setFont(new Font("Consolas", timerFontSize));
    g.setFill(Color.WHITE);
    g.setStroke(Color.BLACK);
    g.setLineWidth(3);

    String timeText = timer.toString();

    // Hitung lebar teks pakai Text
    Text text = new Text(timeText);
    text.setFont(g.getFont());
    double textWidth = text.getLayoutBounds().getWidth();

    // Posisi kanan atas, ambil ukuran dari canvas
    double x = canvas.getWidth() - textWidth - timerMarginX;
    double y = timerMarginY + timerFontSize;

    g.strokeText(timeText, x, y);
    g.fillText(timeText, x, y);

    g.restore();
}

}
