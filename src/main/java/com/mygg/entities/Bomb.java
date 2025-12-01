package com.mygg.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Bomb {
    // Posisi Tile (Grid)
    public final int tileX;
    public final int tileY;
    
    // Konfigurasi
    public final int range; // Radius ledakan
    private float timer = 3.0f; // Waktu sebelum meledak (detik)
    public boolean isExploded = false;

    // Assets & Animasi
    private final Image[] sprites;
    private final float frameInterval = 0.2f; // Kecepatan animasi ganti gambar
    private float animTimer = 0;
    private int frameIndex = 0;

    private final int tileSize = 32; // Sesuaikan dengan ukuran tile game

    public Bomb(int tileX, int tileY, int range) {
        this.tileX = tileX;
        this.tileY = tileY;
        this.range = range;

        // Load 3 gambar bom untuk animasi ticking
        sprites = new Image[3];
        sprites[0] = new Image(getClass().getResourceAsStream("/com/mygg/assets/bomb/bom1.png"), tileSize, tileSize, false, false);
        sprites[1] = new Image(getClass().getResourceAsStream("/com/mygg/assets/bomb/bom2.png"), tileSize, tileSize, false, false);
        sprites[2] = new Image(getClass().getResourceAsStream("/com/mygg/assets/bomb/bom3.png"), tileSize, tileSize, false, false);
    }

    public void update(double dt) {
        if (isExploded) return;

        timer -= dt;
        
        // Update Animasi (bom1 -> bom2 -> bom3 -> bom1 ...)
        // Memberi efek bom "berdenyut" mau meledak
        animTimer += dt;
        if (animTimer >= frameInterval) {
            animTimer = 0;
            frameIndex = (frameIndex + 1) % sprites.length;
        }

        // Jika waktu habis, set status meledak
        if (timer <= 0) {
            isExploded = true;
        }
    }

    public void render(GraphicsContext g) {
        if (isExploded) return;
        
        // Render di posisi pixel (tile * 32)
        g.drawImage(sprites[frameIndex], tileX * tileSize, tileY * tileSize);
    }
}