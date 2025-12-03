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
    private final double DEATH_ANIM_DURATION = 1.2; // Waktu tunggu animasi mati

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

        // 1. Cek Kena Ledakan
        if (player.state != Player.State.DEAD && explosions.hitsPlayer(player, 32)) {
            killPlayer();
        }

        // 2. Logic Saat Mati
        if (player.state == Player.State.DEAD) {
            deathTimer += dt;
            // HAPUS AUTO RESPAWN DISINI
            // Biarkan timer berjalan supaya GameUpdater tahu kapan harus munculin popup
            return; 
        }

        // 3. Update Buffs & Move
        player.updateBuffs(dt);
        movePlayer(dt);

        if (input.isPlace()) placeBomb();
    }

    private void movePlayer(double dt) {
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

        player.state = (input.isUp() || input.isDown() || input.isLeft() || input.isRight())
            ? Player.State.WALK : Player.State.IDLE;
    }

    private void placeBomb() {
        int gx = ((int) (player.x + collider.offset)) / 32;
        int gy = ((int) (player.y + collider.offset)) / 32;
        bombs.placeBomb(gx, gy, player);
    }

    private void killPlayer() {
        player.state = Player.State.DEAD;
        deathTimer = 0;
    }

    // Method Helper untuk GameUpdater
    public boolean isDeathAnimationFinished() {
        return player.state == Player.State.DEAD && deathTimer >= DEATH_ANIM_DURATION;
    }

    public InputHandler getInput() {
        return input;
    }

    public Player getPlayer() {
        return player;
    }
}