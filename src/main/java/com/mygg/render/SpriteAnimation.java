package com.mygg.render;

import javafx.scene.image.Image;

public class SpriteAnimation {

    private final Image[] frames;
    private int index = 0;
    private double timer = 0;
    private double frameTime = 0.12; // detik per frame

    public SpriteAnimation(Image... frames) {
        if (frames == null || frames.length == 0) {
            // placeholder 1px agar tidak crash
            this.frames = new Image[]{ new javafx.scene.image.WritableImage(1, 1) };
        } else {
            this.frames = frames;
        }
    }

    public Image update(double dt) {
        timer += dt;

        while (timer >= frameTime) {
            timer -= frameTime; // lebih akurat
            index = (index + 1) % frames.length;

            // Debug: cetak info frame (optional)
            // System.out.println("Frame: " + index + " / " + frames.length);
        }

        return frames[index];
    }

    public void reset() {
        index = 0;
        timer = 0;
    }

    // Tambahkan getter untuk debugging
    public int getIndex() {
        return index;
    }
}
