package com.mygg.render;

import com.mygg.core.Arena;
import com.mygg.core.GameTimer;
import com.mygg.managers.BombManager;
import com.mygg.managers.ExplosionManager;
import com.mygg.managers.PlayerController;

public class GameUpdater {

    private final PlayerController playerController;
    private final BombManager bombManager;
    private final ExplosionManager explosionManager;
    private final CameraScaler scaler;
    private final GameTimer timer;
    private final Arena arena;

    public GameUpdater(PlayerController player, BombManager bombs,
                       ExplosionManager explosions, CameraScaler scaler,
                       GameTimer timer, Arena arena) {

        this.playerController = player;
        this.bombManager = bombs;
        this.explosionManager = explosions;
        this.scaler = scaler;
        this.timer = timer;
        this.arena = arena;
    }

    public void update(double dt) {
        playerController.update(dt);
        bombManager.update(dt, explosionManager);
        explosionManager.update(dt);
        timer.update(dt);
        scaler.update();

        double currentTime = timer.getTime();

        arena.update(dt, currentTime, playerController.getPlayer());
    }
}