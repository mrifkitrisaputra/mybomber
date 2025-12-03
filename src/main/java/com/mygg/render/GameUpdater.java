package com.mygg.render;

import com.mygg.App; 
import com.mygg.core.Arena;
import com.mygg.core.GameTimer;
import com.mygg.managers.BombManager;
import com.mygg.managers.ExplosionManager;
import com.mygg.managers.ItemManager;
import com.mygg.managers.PlayerController;

public class GameUpdater {

    private final PlayerController playerController;
    private final BombManager bombManager;
    private final ExplosionManager explosionManager;
    private final ItemManager itemManager;
    
    private final CameraScaler scaler;
    private final GameTimer timer;
    private final Arena arena;
    
    private boolean isGameOver = false;
    private boolean isPaused = false;

    public GameUpdater(PlayerController player, BombManager bombs,
                       ExplosionManager explosions, 
                       ItemManager itemManager,
                       CameraScaler scaler,
                       GameTimer timer, Arena arena) {

        this.playerController = player;
        this.bombManager = bombs;
        this.explosionManager = explosions;
        this.itemManager = itemManager;
        this.scaler = scaler;
        this.timer = timer;
        this.arena = arena;
    }

    public void update(double dt) {
        // Jika Game Over atau Paused, hentikan update logic game
        if (isGameOver || isPaused) return;

        // 1. CEK PAUSE (ESC)
        if (playerController.getInput().isESC()) { 
             // Konsumsi key agar tidak spamming (opsional, tapi bagus)
             // playerController.getInput().consumeEsc(); 
             
             isPaused = true; 
             App.showPauseMenu(); 
             return;
        }

        // 2. UPDATE CORE LOGIC
        playerController.update(dt);
        bombManager.update(dt, explosionManager);
        explosionManager.update(dt);
        itemManager.update(playerController.getPlayer());
        timer.update(dt);
        scaler.update();
        
        double currentTime = timer.getTime();
        arena.update(dt, currentTime, playerController.getPlayer());

        // 3. CEK KONDISI KALAH (Mati & Animasi Selesai)
        if (playerController.isDeathAnimationFinished()) {
            triggerGameOver(false); 
        }

        // 4. CEK KONDISI MENANG (Waktu Habis)
        if (timer.getTime() <= 0) {
            triggerGameOver(true); 
        }
    }

    private void triggerGameOver(boolean isWin) {
        if (!isGameOver) {
            isGameOver = true;
            App.showGameOver(isWin); 
        }
    }
    
    // Method PENTING untuk Resume Game
    public void resumeGame() {
        this.isPaused = false;
    }
}