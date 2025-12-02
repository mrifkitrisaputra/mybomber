package com.mygg.managers;

import com.mygg.core.CollisionHandler;
import com.mygg.core.InputHandler;
import com.mygg.entities.Player;

public class PlayerController {

    private final Player player;
    private final InputHandler input;
    private final CollisionHandler collider;
    private final BombManager bombs;
    private final ExplosionManager explosions;
    private final int spawnX, spawnY;
    private double deathTimer = 0;

    public PlayerController(Player player, InputHandler input, CollisionHandler collider,
                            BombManager bombs, ExplosionManager explosions,
                            int[] spawnPos) {

        this.player = player;
        this.input = input;
        this.collider = collider;
        this.bombs = bombs;
        this.explosions = explosions;

        this.spawnX = spawnPos[0] * 32;
        this.spawnY = spawnPos[1] * 32;

        player.x = spawnX;
        player.y = spawnY;
    }

    public void update(double dt) {

        if (player.state != Player.State.DEAD && explosions.hitsPlayer(player, 32)) {
            killPlayer();
        }

        if (player.state == Player.State.DEAD) {
            deathTimer += dt;
            if (deathTimer >= 0.8) respawn();
            return;
        }

        // 1. UPDATE BUFF TIMER (Penting! Agar durasi speedup jalan)
        player.updateBuffs(dt);

        movePlayer(dt);

        if (input.isPlace()) placeBomb();
    }

    private void movePlayer(double dt) {
        // 2. GUNAKAN SPEED DINAMIS (Bisa berubah karena item)
        double speed = player.getCurrentSpeed() * dt;
        
        double nextX = player.x;
        double nextY = player.y;

        if (input.isUp()) { nextY -= speed; player.dir = Player.Direction.UP; }
        if (input.isDown()) { nextY += speed; player.dir = Player.Direction.DOWN; }
        if (input.isLeft()) { nextX -= speed; player.dir = Player.Direction.LEFT; }
        if (input.isRight()) { nextX += speed; player.dir = Player.Direction.RIGHT; }

        if (!collider.checkCollision(nextX, player.y) && !bombs.isBombBlocking(nextX, player.y))
            player.x = nextX;

        if (!collider.checkCollision(player.x, nextY) && !bombs.isBombBlocking(player.x, nextY))
            player.y = nextY;

        player.state =
            (input.isUp() || input.isDown() || input.isLeft() || input.isRight())
            ? Player.State.WALK
            : Player.State.IDLE;
    }

    private void placeBomb() {
        // Hitung koordinat Grid
        int gx = ((int) (player.x + collider.offset)) / 32;
        int gy = ((int) (player.y + collider.offset)) / 32;

        // 3. PASS PLAYER KE BOMB MANAGER
        // Agar bisa cek: Jumlah Bom Max & Radius Api
        bombs.placeBomb(gx, gy, player);
    }

    private void killPlayer() {
        player.state = Player.State.DEAD;
        deathTimer = 0;
    }

    private void respawn() {
        player.x = spawnX;
        player.y = spawnY;
        player.state = Player.State.IDLE;
        deathTimer = 0;
        
        // Opsional: Reset buff saat mati?
        // player.speed = player.baseSpeed;
    }

    public Player getPlayer() {
        return player;
    }
}