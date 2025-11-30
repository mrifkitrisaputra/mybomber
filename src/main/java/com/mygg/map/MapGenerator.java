package com.mygg.map;

import java.util.Random;

public class MapGenerator {

    public static int[][] generate(int w, int h, int[] spawnPos) {
        int[][] map = new int[w][h];
        Random rand = new Random();

        // 1. Generate struktur dasar
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (x == 0 || y == 0 || x == w - 1 || y == h - 1) {
                    map[x][y] = 1; // boundary
                    continue;
                }
                if (x % 2 == 0 && y % 2 == 0) {
                    map[x][y] = 1; // grid titik
                    continue;
                }
                
            }
        }

        // 2. ✅ ✅ ✅ PAKSA AREA SPAWN 2×2 JADI KOSONG (ground)
        // Pastikan 4 tile berikut = 0:
        // (1,1), (2,1), (1,2), (2,2)
        if (w > 2 && h > 2) {
            map[1][1] = 0; // spawn
            map[2][1] = 0; // kanan
            map[1][2] = 0; // bawah
        }

        // 3. Set spawn fix di (1,1)
        spawnPos[0] = 1; // x
        spawnPos[1] = 1; // y

        return map;
    }

    // Overload
    public static int[][] generate(int w, int h) {
        return generate(w, h, new int[2]);
    }
}