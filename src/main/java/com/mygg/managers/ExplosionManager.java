package com.mygg.managers;

import java.util.ArrayList;
import java.util.List;

import com.mygg.entities.Explosion;
import com.mygg.entities.Player;

import javafx.scene.canvas.GraphicsContext;

public class ExplosionManager {

    private final int[][] map;
    private final ItemManager itemManager; // 1. Simpan referensi ItemManager
    
    public final List<Explosion> explosions = new ArrayList<>();

    // 2. Constructor terima ItemManager dari GameCanvas
    public ExplosionManager(int[][] map, ItemManager itemManager) {
        this.map = map;
        this.itemManager = itemManager;
    }

    // BombManager memanggil method ini
    public void add(int x, int y, int range) {
        // 3. Pass itemManager ke Explosion
        // PENTING: Kamu harus update constructor di file Explosion.java juga
        // supaya menerima parameter ke-5 ini.
        explosions.add(new Explosion(x, y, range, map, itemManager)); 
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
        double px = p.x + tile / 2.0;
        double py = p.y + tile / 2.0;
        double hitRadius = tile / 2.5; // Radius hitbox dikit lebih kecil biar fair

        for (Explosion e : explosions) {
            // Cek pusat ledakan
            double centerX = e.centerX * tile + tile / 2.0;
            double centerY = e.centerY * tile + tile / 2.0;
            
            if (Math.abs(px - centerX) < hitRadius && Math.abs(py - centerY) < hitRadius)
                return true;

            // Cek bagian-bagian ledakan (lidah api)
            for (Explosion.ExplosionPart part : e.parts) {
                double ex = part.x() * tile + tile / 2.0;
                double ey = part.y() * tile + tile / 2.0;
                
                if (Math.abs(px - ex) < hitRadius && Math.abs(py - ey) < hitRadius)
                    return true;
            }
        }

        return false;
    }
}