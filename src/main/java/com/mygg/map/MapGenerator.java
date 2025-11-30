package com.mygg.map;

import java.util.Random;

public class MapGenerator {

    public static int[][] generate(int w, int h, int[] spawnPos) {
        int[][] map = new int[w][h];
        Random rand = new Random();

        // 1. Generate struktur dasar
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                // Boundary (Tembok Keliling)
                if (x == 0 || y == 0 || x == w - 1 || y == h - 1) {
                    map[x][y] = 1; // Unbreakable (Merah)
                    continue;
                }

                // Grid Tiang (Hard Block) - Sesuai request awal
                if (x % 2 == 0 && y % 2 == 0) {
                    map[x][y] = 1; // Unbreakable (Merah)
                    continue;
                }
                
                // ✅ RANDOM GENERATION UNTUK SISA RUANG
                // Jika bukan tembok & bukan tiang, acak apakah jadi Breakable (2) atau Ground (0)
                // Angka 0.45 berarti 45% kemungkinan muncul kotak hancur.
                if (rand.nextDouble() < 0.45) {
                    map[x][y] = 2; // Breakable (Kuning)
                } else {
                    map[x][y] = 0; // Ground (Biru/Kosong)
                }
            }
        }

        // 2. ✅ SAFE ZONE (Area Spawn)
        // Request: "3 blok vertikal dari tempat spawn player itu ground"
        // Spawn di (1,1). Maka (1,1), (1,2), dan (1,3) harus 0.
        
        if (w > 2 && h > 3) {
            map[1][1] = 0; // Posisi Player
            map[1][2] = 0; // Bawah 1 langkah
            map[1][3] = 0; // Bawah 2 langkah (Total 3 vertikal)
            

            // Opsional: Saya tetap beri 1 jalan ke kanan agar tidak terlalu sempit
            map[2][1] = 0; 
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