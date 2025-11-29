package com.mygg.map;

import java.util.Random;

public class MapGenerator {

    public static int[][] generate(int w, int h) {

        int[][] map = new int[w][h];
        Random rand = new Random();

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {

                // Boundary = unbreakable
                if (x == 0 || y == 0 || x == w - 1 || y == h - 1) {
                    map[x][y] = 1; // unbreakable
                    continue;
                }

                // Pattern: setiap kolom ganjil & baris ganjil → unbreakable
                if (x % 2 == 0 && y % 2 == 0) {
                    map[x][y] = 1;
                    continue;
                }

                // Random breakable
                map[x][y] = rand.nextDouble() < 0.35 ? 2 : 0;
            }
        }

        return map;
    }
}
