package com.mygg.managers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.mygg.core.SoundHandler;
import com.mygg.entities.Bomb;
import com.mygg.render.GameCanvas;

import javafx.scene.canvas.GraphicsContext;

public class BombManager {

    private final List<Bomb> bombs = new ArrayList<>();
    private final GameCanvas canvas;

    // Constructor menerima canvas agar bisa ambil tile size
    public BombManager(GameCanvas canvas) {
        this.canvas = canvas;
    }

    // Tambahkan bom baru di tile tertentu
    public void placeBomb(int x, int y) {
        SoundHandler.init();

        // Cek apakah sudah ada bom di tile itu
        for (Bomb b : bombs)
            if (b.tileX == x && b.tileY == y) return;

        bombs.add(new Bomb(x, y, 1));
        SoundHandler.playBombPlace();
    }

    // Update semua bom (timer, animasi, explode)
    public void update(double dt, ExplosionManager explosions) {
        Iterator<Bomb> it = bombs.iterator();
        while (it.hasNext()) {
            Bomb b = it.next();
            b.update(dt);

            // Jika meledak → tambahkan ke explosion manager & hapus dari list
            if (b.isExploded) {
                explosions.add(b.tileX, b.tileY, b.range);
                it.remove();
            }
        }
    }

    // Render semua bom
    public void render(GraphicsContext g) {
        for (Bomb b : bombs) b.render(g);
    }

    // Cek apakah koordinat player berbenturan dengan bom solid
    public boolean isBombBlocking(double px, double py) {
        int tileSize = canvas.getTileSize();

        double playerLeft = px;
        double playerRight = px + tileSize;
        double playerTop = py;
        double playerBottom = py + tileSize;

        for (Bomb b : bombs) {
            if (!b.isSolid) continue;

            double bombLeft = b.tileX * tileSize;
            double bombRight = bombLeft + tileSize;
            double bombTop = b.tileY * tileSize;
            double bombBottom = bombTop + tileSize;

            boolean overlap = playerRight > bombLeft &&
                              playerLeft < bombRight &&
                              playerBottom > bombTop &&
                              playerTop < bombBottom;

            if (overlap) return true;
        }

        return false;
    }

    public boolean hasActiveBomb(int playerX, int playerY) {
        for (Bomb b : bombs) {
            if (!b.isExploded) {
                return true;
            }
        }
        return false;
    }

    // Opsional: untuk cek di luar collision player
    public List<Bomb> getBombs() {
        return bombs;
    }
}
