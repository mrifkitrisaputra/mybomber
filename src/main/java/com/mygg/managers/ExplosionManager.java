package com.mygg.managers;

import com.mygg.entities.Explosion;
import com.mygg.entities.Player;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.List;

public class ExplosionManager {

    private final int[][] map;              // ✔ map disimpan di sini
    public final List<Explosion> explosions = new ArrayList<>();

    public ExplosionManager(int[][] map) {  // ✔ map diterima dari GameCanvas
        this.map = map;
    }

    // ✔ BombManager memanggil method ini
    public void add(int x, int y, int range) {
        explosions.add(new Explosion(x, y, range, map));  // ✔ cocok dengan constructor baru
    }

    public void update(double dt) {
        explosions.removeIf(e -> {
            e.update(dt);
            return e.isFinished;
        });
    }

    public void render(GraphicsContext g) {
        for (Explosion e : explosions)
            e.render(g);
    }

    public boolean hitsPlayer(Player p, int tile) {
        double px = p.x + tile/2;
        double py = p.y + tile/2;

        for (Explosion e : explosions) {
            if (Math.abs(px - (e.centerX * tile + tile/2)) < tile &&
                Math.abs(py - (e.centerY * tile + tile/2)) < tile)
                return true;

            for (Explosion.ExplosionPart part : e.parts) {
                double ex = part.x() * tile + tile/2;
                double ey = part.y() * tile + tile/2;
                if (Math.abs(px - ex) < tile/2 &&
                    Math.abs(py - ey) < tile/2)
                    return true;
            }
        }

        return false;
    }
}
