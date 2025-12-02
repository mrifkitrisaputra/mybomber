package com.mygg.managers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.mygg.core.SoundHandler;
import com.mygg.entities.Bomb;
import com.mygg.entities.Player;
import com.mygg.render.GameCanvas;

import javafx.scene.canvas.GraphicsContext;

public class BombManager {

    private final List<Bomb> bombs = new ArrayList<>();
    private final GameCanvas canvas;

    public BombManager(GameCanvas canvas) {
        this.canvas = canvas;
    }

    /**
     * Logic menaruh bom.
     * Mengecek collision, limit total, dan mengurangi stok item jika perlu.
     */
    public void placeBomb(int x, int y, Player player) {
        
        // 1. CEK COLLISION: Jangan tumpuk bom di tile yang sama
        for (Bomb b : bombs) {
            if (b.tileX == x && b.tileY == y) return; 
        }

        // 2. CEK TOTAL LIMIT (Default + Extra)
        // Jika bom aktif sudah mencapai kapasitas maksimal player, stop.
        if (bombs.size() >= player.getMaxBombs()) {
            System.out.println("Gagal pasang: Limit bom habis!");
            return; 
        }

        // 3. LOGIC CONSUMABLE (Baru)
        // Jika jumlah bom aktif saat ini sudah sama atau lebih dari Default Limit (1),
        // berarti bom yang akan dipasang ini menggunakan jatah 'Extra'.
        // Maka, kurangi stok item extra player.
        if (bombs.size() >= player.getDefaultMaxBombs()) {
            player.consumeExtraBomb(); 
        }

        // 4. PASANG BOM
        SoundHandler.init();
        
        // Ambil radius api dari stats player (Item FireUp)
        int radius = player.getFirePower(); 

        if (radius > player.getDefaultFirePower()) {
            player.consumeFirePower();
        }

        bombs.add(new Bomb(x, y, radius));
        SoundHandler.playBombPlace();
        
        System.out.println("Bomb placed at [" + x + "," + y + "] Radius: " + radius);
    }

    public void update(double dt, ExplosionManager explosions) {
        Iterator<Bomb> it = bombs.iterator();
        while (it.hasNext()) {
            Bomb b = it.next();
            b.update(dt);

            // Jika meledak → pindahkan data ke explosion manager & hapus bom fisik
            if (b.isExploded) {
                // Pass Range/Radius dari bom ke ledakan
                explosions.add(b.tileX, b.tileY, b.range);
                it.remove(); // Bom hilang dari list, slot kembali kosong (Default terisi ulang otomatis)
            }
        }
    }

    public void render(GraphicsContext g) {
        for (Bomb b : bombs) b.render(g);
    }

    // Logic agar player tidak bisa menembus bom yang sudah solid (setelah beberapa detik pasang)
    public boolean isBombBlocking(double px, double py) {
        int tileSize = canvas.getTileSize();

        // Hitbox Player (sedikit lebih kecil dari tile biar enak beloknya)
        double pLeft   = px + 4;
        double pRight  = px + tileSize - 4;
        double pTop    = py + 4;
        double pBottom = py + tileSize - 4;

        for (Bomb b : bombs) {
            if (!b.isSolid) continue; // Bom baru pasang bisa ditembus sebentar agar player tidak terjebak

            double bLeft   = b.tileX * tileSize;
            double bRight  = bLeft + tileSize;
            double bTop    = b.tileY * tileSize;
            double bBottom = bTop + tileSize;

            // AABB Collision Detection
            if (pRight > bLeft && pLeft < bRight && pBottom > bTop && pTop < bBottom) {
                return true;
            }
        }
        return false;
    }

    // Cek apakah ada bom di koordinat grid tertentu (berguna untuk AI / Controller)
    public boolean hasActiveBomb(int tileX, int tileY) {
        for (Bomb b : bombs) {
            if (b.tileX == tileX && b.tileY == tileY) {
                return true;
            }
        }
        return false;
    }
    
    // Helper jika ingin cek bom spesifik (misal untuk chain reaction)
    public boolean hasBombAt(int tileX, int tileY) {
        return hasActiveBomb(tileX, tileY);
    }
}