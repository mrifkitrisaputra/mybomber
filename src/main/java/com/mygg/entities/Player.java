package com.mygg.entities;

import java.util.HashMap;

import com.mygg.render.SpriteAnimation;

import javafx.scene.image.Image;

public class Player {

    public double x = 200, y = 200;
    public double speed = 120;

    public enum State { IDLE, WALK, PLACE, DEAD }
    public enum Direction { DOWN, UP, LEFT, RIGHT }

    public State state = State.IDLE;
    public Direction dir = Direction.DOWN;

    public HashMap<String, SpriteAnimation> anims = new HashMap<>();

    public void addAnim(String key, SpriteAnimation anim) {
        anims.put(key, anim);
    }

    public Image update(double dt) {
        String key = switch (state) {
            case IDLE -> "idle_" + dir.name().toLowerCase();
            case WALK -> "walk_" + dir.name().toLowerCase();
            case PLACE -> "place_bom";
            case DEAD -> "death";
        };

        return anims.get(key).update(dt);
    }
}
