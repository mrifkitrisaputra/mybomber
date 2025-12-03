package com.mygg.render;

import com.mygg.core.Arena;
import com.mygg.core.CollisionHandler;
import com.mygg.core.GameTimer;
import com.mygg.core.InputHandler;
import com.mygg.entities.Player;
import com.mygg.managers.BombManager;
import com.mygg.managers.ExplosionManager;
import com.mygg.managers.ItemManager; 
import com.mygg.managers.PlayerController;
import com.mygg.map.MapGenerator;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;

public class GameCanvas extends Canvas {
    private final GameUpdater updater;
    private final GameRenderer renderer;

    private double renderScale = 1.8;
    private static final double MIN_SCALE = 0.5;
    private static final double MAX_SCALE = 4.0;
    private static final double SCALE_STEP = 0.1;

    private final int tile = 32;
    private final int[][] map;
    private final Arena arena;

    // Loop game
    private final AnimationTimer loop = new AnimationTimer() {
        long last = System.nanoTime();

        @Override
        public void handle(long now) {
            double dt = (now - last) / 1e9;
            last = now;
            updater.update(dt);
            renderer.render();
        }
    };
    
    // Method untuk Stop Loop saat keluar room
    public void stop() {
        loop.stop();
    }

    // Method untuk Resume Game dari Pause Menu
    public void resume() {
        updater.resumeGame();
    }

    public GameCanvas(Player player, InputHandler input) {
        int[] spawnPos = new int[2];
        this.map = MapGenerator.generate(13, 13, spawnPos);
        this.arena = new Arena(map);

        ItemManager itemManager = new ItemManager(tile);
        BombManager bombManager = new BombManager(this);
        ExplosionManager explosionManager = new ExplosionManager(map, itemManager); 
        CollisionHandler collider = new CollisionHandler(map, tile);

        PlayerController playerController = new PlayerController(
                player, input, collider, bombManager, explosionManager, spawnPos
        );

        CameraScaler scaler = new CameraScaler(this);
        GameTimer timer = new GameTimer(60); 

        renderer = new GameRenderer(this, player, map, bombManager, explosionManager, itemManager, scaler, timer, arena);
        updater = new GameUpdater(playerController, bombManager, explosionManager, itemManager, scaler, timer, arena);

        bindToScene();
        loop.start();
    }
    
    // ... (kode bindToScene, getters tetap sama) ...
    private void bindToScene() {
        sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                widthProperty().bind(newScene.widthProperty());
                heightProperty().bind(newScene.heightProperty());
                newScene.setOnKeyPressed(e -> {
                    switch (e.getCode()) {
                        case PLUS, ADD, EQUALS -> setRenderScale(renderScale + SCALE_STEP);
                        case MINUS, SUBTRACT -> setRenderScale(renderScale - SCALE_STEP);
                        default -> {}
                    }
                });
            }
        });
    }
    public void setRenderScale(double s) { renderScale = Math.max(MIN_SCALE, Math.min(MAX_SCALE, s)); }
    public double getRenderScale() { return renderScale; }
    public double getOffsetX() { return (getWidth() - map.length * tile * renderScale) / 2.0; }
    public double getOffsetY() { return (getHeight() - map[0].length * tile * renderScale) / 2.0; }
    public int getTileSize() { return tile; }
}