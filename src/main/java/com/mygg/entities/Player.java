package com.mygg.entities;

import com.mygg.render.SpriteAnimation;
import com.mygg.render.SpriteLoader;

import javafx.scene.image.Image;

/**
 * Player entity: posisi, speed, state, and animations.
 * Compatible with:
 *  - SpriteLoader.get(name) -> Image (WritableImage)
 *  - SpriteAnimation(Image... frames)
 */
public class Player {

    public double x = 32;   // start position (pixel)
    public double y = 32;
    public double speed = 120; // pixels per second

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

    // --- Constructor: build animations from SpriteLoader ---
    public Player(SpriteLoader loader) {
        // Idle (single frame each direction) — using first idle frame if sprites had multiple
        animIdleDown = new SpriteAnimation(loadFrames(loader, "idle_down", 1));
        animIdleUp   = new SpriteAnimation(loadFrames(loader, "idle_up", 1));
        animIdleLeft = new SpriteAnimation(loadFrames(loader, "idle_left", 1));
        animIdleRight= new SpriteAnimation(loadFrames(loader, "idle_right", 1));

        // Walk (multiple frames)
        animWalkDown = new SpriteAnimation(loadFrames(loader, "walk_down", 4));
        animWalkUp   = new SpriteAnimation(loadFrames(loader, "walk_up", 4));
        animWalkLeft = new SpriteAnimation(loadFrames(loader, "walk_left", 3));
        animWalkRight= new SpriteAnimation(loadFrames(loader, "walk_right", 4));
    }

    /**
     * Helper: load frames from loader using naming pattern base1, base2, ... baseN.
     * If a frame not found (null), it's skipped (keeps array length consistent with found frames).
     */
    private Image[] loadFrames(SpriteLoader loader, String base, int count) {
        // Collect into temporary array, allow missing frames
        Image[] tmp = new Image[count];
        int found = 0;
        for (int i = 1; i <= count; i++) {
            String key = base + i;       // e.g. "walk_down1"
            Image img = loader.get(key); // loader.get returns WritableImage (Image subclass)
            if (img != null) {
                tmp[found++] = img;
            } else {
                // If a numbered frame missing, try without number (for single-frame idle names like "idle_down")
                if (i == 1) {
                    Image alt = loader.get(base); // try "idle_down"
                    if (alt != null) {
                        tmp[found++] = alt;
                    }
                }
                // otherwise just skip
            }
        }

        // If none found, return a 1-length array with a blank 1x1 image to avoid NPE
        if (found == 0) {
            return new Image[] { new javafx.scene.image.WritableImage(1,1) };
        }

        // shrink array to found length
        Image[] frames = new Image[found];
        System.arraycopy(tmp, 0, frames, 0, found);
        return frames;
    }

    /**
     * Update animation state and return the current Image frame to draw.
     * GameCanvas will call this each render tick.
     *
     * @param dt delta time in seconds (unused for idle frame but passed to animation)
     * @return Image current frame
     */
    public Image update(double dt) {

        // choose animation based on state + direction
        if (state == State.IDLE) {
            switch (dir) {
                case DOWN:  return animIdleDown.update(dt);
                case UP:    return animIdleUp.update(dt);
                case LEFT:  return animIdleLeft.update(dt);
                case RIGHT: return animIdleRight.update(dt);
                default:    return animIdleDown.update(dt);
            }
        } else if (state == State.WALK) {
            switch (dir) {
                case DOWN:  return animWalkDown.update(dt);
                case UP:    return animWalkUp.update(dt);
                case LEFT:  return animWalkLeft.update(dt);
                case RIGHT: return animWalkRight.update(dt);
                default:    return animWalkDown.update(dt);
            }
        } else if (state == State.PLACE) {
            // fallback: show idle down frame if no place animation
            return animIdleDown.update(dt);
        } else { // DEAD or others
            return animIdleDown.update(dt);
        }
    }
}
