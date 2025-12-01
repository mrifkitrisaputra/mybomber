package com.mygg.map;

import java.util.Random;

public class MapGenerator {

    public static int[][] generate(int w, int h, int[] spawnPos) {
        int[][] map = new int[w][h];
        Random rand = new Random();

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {

                if (x == 0 || y == 0 || x == w - 1 || y == h - 1) {
                    map[x][y] = 1;
                    continue;
                }

                if (x % 2 == 0 && y % 2 == 0) {
                    map[x][y] = 1;
                    continue;
                }

                if (rand.nextDouble() < 0.70) {
                    map[x][y] = 2;
                } else {
                    map[x][y] = 0;
                }
            }
        }

        if (w > 2 && h > 3) {
            map[1][1] = 0;
            map[1][2] = 0;
            map[1][3] = 0;

            map[2][1] = 0;
        }

        spawnPos[0] = 1;
        spawnPos[1] = 1;

        return map;
    }

    public static int[][] generate(int w, int h) {
        return generate(w, h, new int[2]);
    }
}
