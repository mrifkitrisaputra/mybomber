package com.mygg.entities;

public class Item {

    // Enum didefinisikan di dalam class Item
    public enum Type {
        BOMB_UP,
        FIRE_UP,
        SPEED_UP
    }

    public int x; // Koordinat Grid
    public int y;
    public Type type;

    public Item(int x, int y, Type type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }
}