package com.mygg.entities;

import java.util.ArrayList;
import java.util.List;

import com.mygg.core.SoundHandler;
import com.mygg.managers.ItemManager;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Explosion {
    // Ubah centerX & centerY ke public
    public final int centerX, centerY;
    private float duration = 0.5f; 
    public boolean isFinished = false;

    // Ubah list parts jadi public
    public final List<ExplosionPart> parts = new ArrayList<>();

    // Assets
    private final Image centerImg;
    private final Image[] hAnimation; 
    private final Image[] vAnimation; 

    private float animTimer = 0;
    private int frameIndex = 0;
    private final int totalFrames = 4; 
    private final int tileSize = 32;

    // Simpan referensi ItemManager
    private final ItemManager itemManager;

    // Ubah record ExplosionPart jadi public static
    public static record ExplosionPart(int x, int y, boolean isVertical) {}

    // UPDATE CONSTRUCTOR: Tambahkan parameter ItemManager
    public Explosion(int startX, int startY, int range, int[][] map, ItemManager itemManager) {
        this.centerX = startX;
        this.centerY = startY;
        this.itemManager = itemManager; // Simpan

        SoundHandler.playExplosion();

        centerImg = new Image(getClass().getResourceAsStream("/com/mygg/assets/explosion/e_largeexplosion1.png"), tileSize, tileSize, false, false);
        
        hAnimation = new Image[4];
        vAnimation = new Image[4];
        for (int i = 0; i < 4; i++) {
            hAnimation[i] = new Image(getClass().getResourceAsStream("/com/mygg/assets/explosion/e_horizontal" + (i+1) + ".png"), tileSize, tileSize, false, false);
            vAnimation[i] = new Image(getClass().getResourceAsStream("/com/mygg/assets/explosion/e_vertical" + (i+1) + ".png"), tileSize, tileSize, false, false);
        }

        calculateSpread(map, range);
    }

    private void calculateSpread(int[][] map, int range) {
        // Logika Loop: Jika checkTile return FALSE (mentok), loop langsung BREAK.
        
        // 1. KANAN
        for (int i = 1; i <= range; i++) {
            if (!checkTile(centerX + i, centerY, map, false)) break;
        }
        // 2. KIRI
        for (int i = 1; i <= range; i++) {
            if (!checkTile(centerX - i, centerY, map, false)) break;
        }
        // 3. BAWAH
        for (int i = 1; i <= range; i++) {
            if (!checkTile(centerX, centerY + i, map, true)) break;
        }
        // 4. ATAS
        for (int i = 1; i <= range; i++) {
            if (!checkTile(centerX, centerY - i, map, true)) break;
        }
    }

    /**
     * Logic inti:
     * - Return TRUE: Api lanjut ke tile berikutnya (Ground).
     * - Return FALSE: Api berhenti (Tembok Keras atau Tembok Hancur).
     */
    private boolean checkTile(int x, int y, int[][] map, boolean isVertical) {
        // Boundary Check
        if (x < 0 || y < 0 || x >= map.length || y >= map[0].length) return false;

        int tileType = map[x][y];

        // 1. KENA TEMBOK KERAS (Unbreakable)
        if (tileType == 1) {
            return false; // Api berhenti, TIDAK digambar
        } 
        
        // 2. KENA TEMBOK HANCUR (Breakable)
        if (tileType == 2) {
            map[x][y] = 0; // Hancurkan tembok (jadi Ground)
            
            // --- UPDATE LOGIC ITEM DROP ---
            if (itemManager != null) {
                itemManager.trySpawnItem(x, y);
            }
            
            parts.add(new ExplosionPart(x, y, isVertical)); // Gambar api di sini
            return false; // PENTING: Return false agar loop berhenti (tidak tembus ke belakangnya)
        }

        // 3. TANAH KOSONG (Ground)
        if (tileType == 0) {
            parts.add(new ExplosionPart(x, y, isVertical));
            return true; // Lanjut ke tile berikutnya
        }

        return false;
    }

    public void update(double dt) {
        duration -= dt;
        float timePerFrame = 0.8f / totalFrames; 
        animTimer += dt;
        
        if (animTimer >= timePerFrame) {
            animTimer = 0;
            frameIndex++;
            if (frameIndex >= totalFrames) frameIndex = totalFrames - 1; 
        }

        if (duration <= 0) {
            isFinished = true;
        }
    }

    public void render(GraphicsContext g) {
        // Render Pusat
        g.drawImage(centerImg, centerX * tileSize, centerY * tileSize);

        // Render Bagian Api
        for (ExplosionPart part : parts) {
            Image sprite = part.isVertical ? vAnimation[frameIndex] : hAnimation[frameIndex];
            g.drawImage(sprite, part.x * tileSize, part.y * tileSize);
        }
    }
}