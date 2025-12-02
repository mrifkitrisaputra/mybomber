package com.mygg.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Bomb {
    public final int tileX;
    public final int tileY;

    // Radius ledakan (diambil dari stats Player)
    public final int range;

    private float timer = 3.0f;     // UPDATE: Standar bomberman biasanya 3 detik (sebelumnya 1f)
    private float lifeTime = 0;     // Umur bom sejak dipasang
    private final float solidDelay = 0.5f; // UPDATE: Setelah 0.5 detik jadi solid (lebih responsif)

    public boolean isExploded = false;
    public boolean isSolid = false;

    private final Image[] sprites;
    private final float frameInterval = 0.2f;
    private float animTimer = 0;
    private int frameIndex = 0;

    private final int tileSize = 32;
    private final int bombSize = 28;

    // Debug
    private final boolean DEBUG = false; // Set false biar bersih

    public Bomb(int tileX, int tileY, int range) {
        this.tileX = tileX;
        this.tileY = tileY;
        this.range = range; // Simpan range yang dikirim dari Player

        sprites = new Image[3];
        sprites[0] = new Image(getClass().getResourceAsStream("/com/mygg/assets/bomb/bom1.png"), bombSize, bombSize, false, false);
        sprites[1] = new Image(getClass().getResourceAsStream("/com/mygg/assets/bomb/bom2.png"), bombSize, bombSize, false, false);
        sprites[2] = new Image(getClass().getResourceAsStream("/com/mygg/assets/bomb/bom3.png"), bombSize, bombSize, false, false);
    }

    public void update(double dt) {
        if (isExploded) return;

        // Hitung umur bom
        lifeTime += dt;

        // Setelah delay → solid (player tidak bisa tembus lagi)
        if (!isSolid && lifeTime >= solidDelay) {
            isSolid = true;
        }

        // Timer bom mundur
        timer -= dt;

        // Animasi ticking
        animTimer += dt;
        if (animTimer >= frameInterval) {
            animTimer = 0;
            frameIndex = (frameIndex + 1) % sprites.length;
        }

        // Meledak
        if (timer <= 0) {
            isExploded = true;
        }
    }

    public void render(GraphicsContext g) {
        if (isExploded) return;

        double px = tileX * tileSize;
        double py = tileY * tileSize;
        double offset = (tileSize - bombSize) / 2.0;

        // Gambar sprite bom
        g.drawImage(sprites[frameIndex], px + offset, py + offset, bombSize, bombSize);

        // // Debug Hitbox
        // if (DEBUG) {
        //     g.setStroke(javafx.scene.paint.Color.RED);
        //     g.strokeRect(px, py, tileSize, tileSize);
        // }
    }
}