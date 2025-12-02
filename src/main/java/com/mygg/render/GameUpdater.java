package com.mygg.render;

import com.mygg.core.GameTimer;
import com.mygg.managers.BombManager;
import com.mygg.managers.ExplosionManager;
import com.mygg.managers.PlayerController;

public class GameUpdater {

    private final PlayerController playerController;
    private final BombManager bombManager;
    private final ExplosionManager explosionManager;
    private final CameraScaler scaler;
    private final GameTimer timer;    // <-- ADD TIMER

    public GameUpdater(PlayerController player, BombManager bombs,
                       ExplosionManager explosions, CameraScaler scaler,
                       GameTimer timer) {   // <-- ADD TIMER PARAM

        this.playerController = player;
        this.bombManager = bombs;
        this.explosionManager = explosions;
        this.scaler = scaler;
        this.timer = timer;   // <-- SAVE TIMER
    }

    public void update(double dt) {
        playerController.update(dt);
        bombManager.update(dt, explosionManager);
        explosionManager.update(dt);
        timer.update(dt);   // <-- UPDATE TIMER EVERY FRAME
        scaler.update();
    }
}
