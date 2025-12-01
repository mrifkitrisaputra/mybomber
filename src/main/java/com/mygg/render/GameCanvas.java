package com.mygg.render;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.mygg.core.CollisionHandler;
import com.mygg.core.InputHandler;
import com.mygg.core.SoundHandler; // Import SoundHandler
import com.mygg.entities.Bomb;
import com.mygg.entities.Explosion;
import com.mygg.entities.Player;
import com.mygg.map.MapGenerator;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;

public class GameCanvas extends Canvas {

    private final Player player;
    private final InputHandler input;
    private final CollisionHandler collider;
    private final int[][] map;

    // List Entity
    private final List<Bomb> bombs = new ArrayList<>();
    private final List<Explosion> explosions = new ArrayList<>();

    private boolean isSpaceHeld = false;

    private final Image ground;
    private final Image breakable;
    private final Image unbreak;

    private final int tile = 32;

    // RENDER SCALE (zoom visual) -> 1.0 = normal, >1 = zoom in
    private double renderScale = 1.8; // ubah nilai default ini sesuai preferensi

    // batas zoom
    private final double MIN_SCALE = 0.5;
    private final double MAX_SCALE = 4.0;
    private final double SCALE_STEP = 0.1;

    public GameCanvas(Player player, InputHandler input) {
        // Init Sound System
        SoundHandler.init();

        int[] spawnPos = new int[2];

        // Generate map
        this.map = MapGenerator.generate(13, 13, spawnPos);
        this.collider = new CollisionHandler(this.map, this.tile);

        // Jangan set width/height tetap di sini — biarkan mengikuti scene/window
        // int width = map.length * tile;
        // int height = map[0].length * tile;
        // super.setWidth(width);
        // super.setHeight(height);

        this.player = player;
        this.input = input;

        // Load textures
        ground = new Image(getClass().getResourceAsStream("/com/mygg/assets/tiles/ground.png"), 32, 32, false, false);
        breakable = new Image(getClass().getResourceAsStream("/com/mygg/assets/tiles/break.png"), 32, 32, false, false);
        unbreak = new Image(getClass().getResourceAsStream("/com/mygg/assets/tiles/unbreak.png"), 32, 32, false, false);

        player.x = spawnPos[0] * tile;
        player.y = spawnPos[1] * tile;

        // jika scene tersedia nanti, bind canvas ke ukuran scene sehingga canvas membesar mengikuti window
        this.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                bindToScene(newScene);
            } else {
                // jika scene hilang, unbind
                widthProperty().unbind();
                heightProperty().unbind();
            }
        });

        loop.start();
    }

    private void bindToScene(Scene scene) {
        // bind ke scene (isi window) agar canvas memperbesar – sehingga kotak map bisa tampak besar
        widthProperty().bind(scene.widthProperty());
        heightProperty().bind(scene.heightProperty());

        // juga pasang key listener untuk zoom +/- (hanya sekali)
        scene.setOnKeyPressed(e -> {
            KeyCode kc = e.getCode();
            if (kc == KeyCode.PLUS || kc == KeyCode.ADD || kc == KeyCode.EQUALS) {
                // note: EQUALS sering untuk tombol '=' yang tidak perlu shift (tergantung layout)
                setRenderScale(renderScale + SCALE_STEP);
            } else if (kc == KeyCode.MINUS || kc == KeyCode.SUBTRACT) {
                setRenderScale(renderScale - SCALE_STEP);
            } else {
                // tetap teruskan ke input handler game (movement, bomb)
                // jika InputHandler dipasang di scene di App.java, jangan override; 
                // di proyekmu InputHandler sudah menangani key events via scene.setOnKeyPressed
            }
        });
    }

    // setter yang memastikan clamp
    public void setRenderScale(double s) {
        renderScale = Math.max(MIN_SCALE, Math.min(MAX_SCALE, s));
        // langsung render ulang supaya perubahan terlihat cepat
        render();
    }

    public double getRenderScale() {
        return renderScale;
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

private boolean isBombBlocking(double px, double py) {

    double playerLeft   = px;
    double playerRight  = px + tile;
    double playerTop    = py;
    double playerBottom = py + tile;

    for (Bomb b : bombs) {
        if (!b.isSolid) continue;

        double bombLeft   = b.tileX * tile;
        double bombRight  = bombLeft + tile;
        double bombTop    = b.tileY * tile;
        double bombBottom = bombTop + tile;

        boolean overlap = playerRight > bombLeft &&
                          playerLeft < bombRight &&
                          playerBottom > bombTop &&
                          playerTop < bombBottom;

        if (overlap) return true;
    }

    return false;
}


    private void update(double dt) {
        // 1. Player Movement
        double speed = player.speed * dt;
        double nextX = player.x;
        double nextY = player.y;

        if (input.isUp()) {
            nextY -= speed;
            player.dir = Player.Direction.UP;
        }
        if (input.isDown()) {
            nextY += speed;
            player.dir = Player.Direction.DOWN;
        }
        if (input.isLeft()) {
            nextX -= speed;
            player.dir = Player.Direction.LEFT;
        }
        if (input.isRight()) {
            nextX += speed;
            player.dir = Player.Direction.RIGHT;
        }

if (!collider.checkCollision(nextX, player.y) && !isBombBlocking(nextX, player.y))
    player.x = nextX;

if (!collider.checkCollision(player.x, nextY) && !isBombBlocking(player.x, nextY))
    player.y = nextY;



        player.state = (input.isUp() || input.isDown() || input.isLeft() || input.isRight()) ? Player.State.WALK
                : Player.State.IDLE;

        // 2. Place Bomb
        if (input.isPlace()) {
            if (!isSpaceHeld) {
                placeBomb();
                isSpaceHeld = true;
            }
        } else {
            isSpaceHeld = false;
        }

        // 3. Update Bombs
        Iterator<Bomb> bIt = bombs.iterator();
        while (bIt.hasNext()) {
            Bomb b = bIt.next();
            b.update(dt);
            if (b.isExploded) {
                // Pass MAP ke Explosion agar bisa hancurkan blok
                explosions.add(new Explosion(b.tileX, b.tileY, b.range, this.map));
                bIt.remove();
            }
        }

        // 4. Update Explosions
        Iterator<Explosion> eIt = explosions.iterator();
        while (eIt.hasNext()) {
            Explosion e = eIt.next();
            e.update(dt);
            if (e.isFinished)
                eIt.remove();
        }
    }

    private void placeBomb() {
        // Hitung posisi grid dengan offset agar pas tengah
        int gx = (int) ((player.x + collider.offset) / tile);
        int gy = (int) ((player.y + collider.offset) / tile);

        // Cek duplicate
        for (Bomb b : bombs) {
            if (b.tileX == gx && b.tileY == gy)
                return;
        }

        bombs.add(new Bomb(gx, gy, 1));

        // Play SFX Bom ditaruh
        SoundHandler.playBombPlace();
    }

    private void render() {
        GraphicsContext g = getGraphicsContext2D();
        // Bersihkan area canvas penuh (gunakan getWidth/getHeight karena canvas ter-bind ke scene)
        g.clearRect(0, 0, getWidth(), getHeight());
        g.setImageSmoothing(false);

        double mapWidth = map.length * tile;
        double mapHeight = map[0].length * tile;

        double scale = renderScale;

        // hitung offset supaya kotak map berada di tengah layar setelah di-scale
        double scaledMapW = mapWidth * scale;
        double scaledMapH = mapHeight * scale;

        double offsetX = (getWidth() - scaledMapW) / 2.0;
        double offsetY = (getHeight() - scaledMapH) / 2.0;

        g.save();
        // terjemahkan ke offset (dalam pixel layar), lalu scale lalu gambar (semua koordinat world tetap pakai tile=32)
        g.translate(offsetX, offsetY);
        g.scale(scale, scale);

        // Render Map (world coordinates)
        for (int y = 0; y < map[0].length; y++) {
            for (int x = 0; x < map.length; x++) {
                double px = x * tile;
                double py = y * tile;
                switch (map[x][y]) {
                    case 0 -> g.drawImage(ground, px, py);
                    case 1 -> g.drawImage(unbreak, px, py);
                    case 2 -> g.drawImage(breakable, px, py);
                }
            }
        }

        // Render Bombs (menggunakan world coords)
        for (Bomb b : bombs)
            b.render(g);

        // Render Explosions
        for (Explosion e : explosions)
            e.render(g);

        

        // Render Player
        g.drawImage(player.update(1 / 60.0), player.x, player.y);
        // g.setStroke(javafx.scene.paint.Color.LIME);
        // g.setLineWidth(2);
        // g.strokeRect(player.x, player.y, tile, tile);

        // g.setFill(javafx.scene.paint.Color.rgb(0, 255, 0, 0.25));
        // g.fillRect(player.x, player.y, tile, tile);

        g.restore();
    }
}
