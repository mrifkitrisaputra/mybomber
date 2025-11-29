package com.mygg.render;

import javafx.scene.image.Image;

public class SpriteAnimation {

    private final Image[] frames;
    private int index = 0;
    private double timer = 0;
    private double frameTime = 0.12; // detik per frame

    public SpriteAnimation(Image... frames) {
        this.frames = frames;
    }

    public Image update(double dt) {
        timer += dt;

        if (timer >= frameTime) {
            timer = 0;
            index = (index + 1) % frames.length;
        }

        return frames[index];
    }

    public void reset() {
        index = 0;
        timer = 0;
    }
}
