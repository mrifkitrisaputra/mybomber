package com.mygg.render;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List; // Import Logic Bom

import com.mygg.core.CollisionHandler; // Import Logic Explosion
import com.mygg.core.InputHandler;
import com.mygg.entities.Bomb;
import com.mygg.entities.Explosion;
import com.mygg.entities.Player;
import com.mygg.map.MapGenerator;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class GameCanvas extends Canvas {

    private final Player player;
    private final InputHandler input;
    private final CollisionHandler collider;
    private final int[][] map;

    // List untuk menampung Entity dinamis
    private final List<Bomb> bombs = new ArrayList<>();
    private final List<Explosion> explosions = new ArrayList<>();

    // Helper agar tidak spam bom (tombol harus dilepas dulu)
    private boolean isSpaceHeld = false;

    private final Image ground;
    private final Image breakable;
    private final Image unbreak;

    private final int tile = 32;

    public GameCanvas(Player player, InputHandler input) {
        int[] spawnPos = new int[2]; // [x, y]
        
        // Generate map
        this.map = MapGenerator.generate(13, 13, spawnPos);

        // Init Collision Handler
        this.collider = new CollisionHandler(this.map, this.tile);

        // Ukuran canvas mengikuti map
        int width = map.length * tile;
        int height = map[0].length * tile;
        super.setWidth(width);
        super.setHeight(height);

        this.player = player;
        this.input = input;

        // Load textures
        ground = new Image(getClass().getResourceAsStream("/com/mygg/assets/tiles/ground.png"), 32, 32, false, false);
        breakable = new Image(getClass().getResourceAsStream("/com/mygg/assets/tiles/break.png"), 32, 32, false, false);
        unbreak = new Image(getClass().getResourceAsStream("/com/mygg/assets/tiles/unbreak.png"), 32, 32, false, false);

        // Spawn di lokasi aman
        player.x = spawnPos[0] * tile;
        player.y = spawnPos[1] * tile;

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
        // --- 1. PLAYER MOVEMENT ---
        double speed = player.speed * dt;
        double nextX = player.x;
        double nextY = player.y;

        if (input.isUp()) { nextY -= speed; player.dir = Player.Direction.UP; }
        if (input.isDown()) { nextY += speed; player.dir = Player.Direction.DOWN; }
        if (input.isLeft()) { nextX -= speed; player.dir = Player.Direction.LEFT; }
        if (input.isRight()) { nextX += speed; player.dir = Player.Direction.RIGHT; }

        // Collision Check
        if (!collider.checkCollision(nextX, player.y)) player.x = nextX;
        if (!collider.checkCollision(player.x, nextY)) player.y = nextY;

        player.state = (input.isUp() || input.isDown() || input.isLeft() || input.isRight())
                ? Player.State.WALK : Player.State.IDLE;


        // --- 2. BOMB PLACEMENT ---
        if (input.isPlace()) {
            if (!isSpaceHeld) { // Cek agar tidak spam bom jika spasi ditahan
                placeBomb();
                isSpaceHeld = true;
            }
        } else {
            isSpaceHeld = false; // Reset jika spasi dilepas
        }


        // --- 3. UPDATE BOMBS ---
        Iterator<Bomb> bIt = bombs.iterator();
        while (bIt.hasNext()) {
            Bomb b = bIt.next();
            b.update(dt);
            
            if (b.isExploded) {
                // Saat meledak, create Explosion di posisi bom
                // Range bom diset di sini (misal: 2)
                explosions.add(new Explosion(b.tileX, b.tileY, b.range, this.map));
                
                bIt.remove(); // Hapus bom dari list
            }
        }


        // --- 4. UPDATE EXPLOSIONS ---
        Iterator<Explosion> eIt = explosions.iterator();
        while (eIt.hasNext()) {
            Explosion e = eIt.next();
            e.update(dt);
            
            if (e.isFinished) {
                eIt.remove(); // Hapus api jika animasi selesai
            }
        }
    }

    private void placeBomb() {
        // Hitung posisi grid player (tambah offset agar akurat di tengah)
        int gx = (int) ((player.x + collider.offset) / tile);
        int gy = (int) ((player.y + collider.offset) / tile);

        // Cek apakah sudah ada bom di koordinat ini (cegah tumpuk)
        for (Bomb b : bombs) {
            if (b.tileX == gx && b.tileY == gy) return;
        }

        // Cek apakah tembok (opsional, tapi logic bom biasanya bisa ditaruh di mana player berdiri)
        
        // Tambah bom baru (Range 2)
        bombs.add(new Bomb(gx, gy, 2));
    }

    private void render() {
        GraphicsContext g = getGraphicsContext2D();

        g.clearRect(0, 0, getWidth(), getHeight());
        g.setImageSmoothing(false);

        // 1. Gambar tile (Map)
        for (int y = 0; y < map[0].length; y++) {
            for (int x = 0; x < map.length; x++) {
                double posX = x * tile;
                double posY = y * tile;

                switch (map[x][y]) {
                    case 0 -> g.drawImage(ground, posX, posY);
                    case 1 -> g.drawImage(unbreak, posX, posY);
                    case 2 -> g.drawImage(breakable, posX, posY);
                }
            }
        }

        // 2. Render Bombs
        for (Bomb b : bombs) {
            b.render(g);
        }

        // 3. Render Explosions (Api)
        for (Explosion e : explosions) {
            e.render(g);
        }

        // 4. Gambar Sprite Player (Player di atas bom & api)
        g.drawImage(player.update(1 / 60.0), player.x, player.y);

        // 5. DEBUG: Hitbox Player
        // g.setStroke(Color.MAGENTA);
        // g.setLineWidth(2.0);
        // g.strokeRect(player.x + collider.offset, player.y + collider.offset, collider.hitboxSize, collider.hitboxSize);
    }
}