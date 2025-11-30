package com.mygg.core;

public class CollisionHandler {

    private final int[][] map;
    private final int tileSize;

    public final double hitboxSize;
    public final double offset;

    public CollisionHandler(int[][] map, int tileSize) {
        this.map = map;
        this.tileSize = tileSize;

        this.hitboxSize = 20.5;

        this.offset = (tileSize - hitboxSize) / 2.0;
    }

    /**
     * Mengecek apakah area kotak di posisi (x, y) bertabrakan dengan tembok.
     * Menggunakan 4 titik sudut (Bounding Box) untuk akurasi.
     *
     * * @param x Posisi X player (kiri atas sprite)
     * @param y Posisi Y player (kiri atas sprite)
     * @return true jika ada bagian hitbox player yang menabrak tembok.
     */
    public boolean checkCollision(double x, double y) {

        double left = x + offset;
        double top = y + offset;

        double right = left + hitboxSize;
        double bottom = top + hitboxSize;

        boolean topLeft = isSolid(left, top);
        boolean topRight = isSolid(right, top);
        boolean bottomLeft = isSolid(left, bottom);
        boolean bottomRight = isSolid(right, bottom);

        return topLeft || topRight || bottomLeft || bottomRight;
    }

    /**
     * Helper private untuk mengecek apakah satu titik pixel spesifik adalah
     * tembok.
     */
    private boolean isSolid(double px, double py) {
        int gridX = (int) (px / tileSize);
        int gridY = (int) (py / tileSize);

        if (gridX < 0 || gridY < 0 || gridX >= map.length || gridY >= map[0].length) {
            return true;
        }

        int t = map[gridX][gridY];
        return (t == 1 || t == 2);
    }
}
