package com.mygg.core;

public class GameTimer {
    private double totalTime; // total waktu dalam detik
    private double currentTime; // waktu tersisa dalam detik
    private boolean running = true;

    public GameTimer(double seconds) {
        this.totalTime = seconds;
        this.currentTime = seconds;
    }

    // Update timer setiap frame, dt = delta time dalam detik
    public void update(double dt) {
        if (!running) return;

        currentTime -= dt;
        if (currentTime < 0) {
            currentTime = 0;
            running = false;
        }
    }

    // Reset timer ke waktu tertentu (opsional)
    public void reset() {
        currentTime = totalTime;
        running = true;
    }

    // Set waktu baru
    public void setTime(double seconds) {
        this.totalTime = seconds;
        this.currentTime = seconds;
        this.running = true;
    }

    public boolean isFinished() {
        return !running;
    }

    public double getTime() {
        return currentTime;
    }

    @Override
    public String toString() {
        int minutes = (int) (currentTime / 60);
        int seconds = (int) (currentTime % 60);
        return String.format("%02d:%02d", minutes, seconds);
    }
}
