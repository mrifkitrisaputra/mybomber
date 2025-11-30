package com.mygg.entities;

import com.mygg.render.SpriteAnimation;
import com.mygg.render.SpriteLoader;

import javafx.scene.image.Image;

public class Player {

    public double x = 32;
    public double y = 32;
    public double speed = 120;

    public enum State { IDLE, WALK, PLACE, DEAD }
    public enum Direction { DOWN, UP, LEFT, RIGHT }

    public State state = State.IDLE;
    public Direction dir = Direction.DOWN;

    // Animations
    private final SpriteAnimation animIdleDown;
    private final SpriteAnimation animIdleUp;
    private final SpriteAnimation animIdleLeft;
    private final SpriteAnimation animIdleRight;

    private final SpriteAnimation animWalkDown;
    private final SpriteAnimation animWalkUp;
    private final SpriteAnimation animWalkLeft;
    private final SpriteAnimation animWalkRight;

    private final SpriteAnimation animDeath; // jika ingin pakai


    // ================================================================
    // CONSTRUCTOR — Sesuaikan dengan PNG milikmu
    // ================================================================
    public Player(SpriteLoader loader) {

        // --- IDLE ---
        animIdleDown  = new SpriteAnimation(loadFrames(loader, "sP2DownIdle_", 1));
        animIdleUp    = new SpriteAnimation(loadFrames(loader, "sP2UpIdle_", 1));
        animIdleLeft  = new SpriteAnimation(loadFrames(loader, "sP2LeftIdle_", 1));
        animIdleRight = new SpriteAnimation(loadFrames(loader, "sP2RightIdle_", 1));

        // --- WALK ---
        animWalkDown  = new SpriteAnimation(loadFrames(loader, "sP2Down_", 2));  // 0,1
        animWalkUp    = new SpriteAnimation(loadFrames(loader, "sP2Up_", 2));    // 0,1
        animWalkLeft  = new SpriteAnimation(loadFrames(loader, "sP2Left_", 3));  // 0,1,2
        animWalkRight = new SpriteAnimation(loadFrames(loader, "sP2Right_", 3)); // 0,1,2

        // --- DEATH ---
        animDeath = new SpriteAnimation(loadFrames(loader, "sP2Death_", 7)); // 0..6
    }


    // ================================================================
    // LOAD FRAMES — cocok untuk format file: base + index
    // ex: loadFrames(loader, "sP2Down_", 2) → sP2Down_0 + sP2Down_1
    // ================================================================
    private Image[] loadFrames(SpriteLoader loader, String base, int count) {
        Image[] temp = new Image[count];
        int idx = 0;

        for (int i = 0; i < count; i++) {
            Image img = loader.get(base + i);
            if (img != null) {
                temp[idx++] = img;
            }
        }

        // jika gagal semua → kembalikan 1px kosong agar tidak NPE
        if (idx == 0) {
            return new Image[]{ new javafx.scene.image.WritableImage(1, 1) };
        }

        Image[] frames = new Image[idx];
        System.arraycopy(temp, 0, frames, 0, idx);
        return frames;
    }


    // ================================================================
    // UPDATE — pilih animasi berdasarkan state + direction
    // ================================================================
    public Image update(double dt) {

        switch (state) {

            case IDLE:
                switch (dir) {
                    case DOWN:  return animIdleDown.update(dt);
                    case UP:    return animIdleUp.update(dt);
                    case LEFT:  return animIdleLeft.update(dt);
                    case RIGHT: return animIdleRight.update(dt);
                }
                break;

            case WALK:
                switch (dir) {
                    case DOWN:  return animWalkDown.update(dt);
                    case UP:    return animWalkUp.update(dt);
                    case LEFT:  return animWalkLeft.update(dt);
                    case RIGHT: return animWalkRight.update(dt);
                }
                break;

            case DEAD:
                return animDeath.update(dt);

            case PLACE:
                // fallback pakai idle down
                return animIdleDown.update(dt);
        }

        return animIdleDown.update(dt);
    }
}
