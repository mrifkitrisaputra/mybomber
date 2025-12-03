package com.mygg.render;

import javafx.scene.image.Image;

public class SpriteAnimation {

    private final Image[] frames;
    private int index = 0;
    private double timer = 0;
    private double frameTime = 0.12;
    private boolean loop = true;
    private boolean isFinished = false;

    public SpriteAnimation(Image... frames) {
        if (frames == null || frames.length == 0) {
            // placeholder 1px agar tidak crash
            this.frames = new Image[]{ new javafx.scene.image.WritableImage(1, 1) };
        } else {
            this.frames = frames;
        }
    }

    public Image update(double dt) {
        // Jika sudah selesai dan tidak looping, jangan update lagi, return frame terakhir
        if (!loop && isFinished) {
            return frames[frames.length - 1];
        }

        timer += dt;

        while (timer >= frameTime) {
            timer -= frameTime;

            if (loop) {
                // Modulus hanya dipakai jika looping
                index = (index + 1) % frames.length;
            } else {
                // Jika tidak looping
                if (index < frames.length - 1) {
                    index++;
                } else {
                    // Mentok di frame terakhir -> Tandai selesai
                    isFinished = true;
                }
            }
        }
        return frames[index];
    }

    public void reset() {
        index = 0;
        timer = 0;
        isFinished = false;
    }

    // Tambahkan getter untuk debugging
    public int getIndex() {
        return index;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    // Getter untuk dicek di Player/Renderer
    public boolean isFinished() {
        return isFinished;
    }
}
