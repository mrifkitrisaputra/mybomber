package com.mygg.core;

import com.mygg.entities.Player;

public class Arena {

    private final int[][] map;
    private final int mapCols;
    private final int mapRows;

    // Batas arena
    private int left, right, top, bottom;

    // --- CONFIG JADWAL ---
    // Di detik keberapa (SISA WAKTU) arena harus mengecil?
    // Index 0 (30.0) -> Trigger untuk pattern pertama (LR)
    // Index 1 (20.0) -> Trigger untuk pattern kedua (LR)
    // Index 2 (10.0) -> Trigger untuk pattern ketiga (TB)
    // Index 3 (5.0)  -> Trigger untuk pattern keempat (LR)
    // Silakan ganti angka ini sesuai selera kamu.
    private final double[] shrinkTimes = {30.0, 20.0, 10.0, 5.0};
    
    // Pattern shrink sesuai request: LR, LR, TB, LR
    private final String[] shrinkPattern = {"LR", "LR", "TB", "LR"};

    private int currentStep = 0; // Menunjuk ke index array di atas

    // Status Animasi
    private boolean isShrinking = false; 
    private double shrinkAnimTimer = 0;
    private static final double SHRINK_ANIM_DURATION = 5.0;

    public Arena(int[][] map) {
        this.map = map;
        this.mapCols = map.length;
        this.mapRows = map[0].length;

        this.left = 1;
        this.right = mapCols - 1;
        this.top = 1;
        this.bottom = mapRows - 1;
    }

    /** * Update dipanggil setiap frame.
     * PARAMETER WAJIB: gameTime (Waktu timer saat ini, misal 59.9, 59.8...)
     */
public void update(double dt, double gameTime, Player player) {
        
    // 1. CEK TRIGGER BERDASARKAN WAKTU TIMER
    // Logic: Trigger jika waktu timer sekarang <= target waktu shrink
    if (!isShrinking && currentStep < shrinkTimes.length) {
        double targetTime = shrinkTimes[currentStep];
        
        // Asumsi timer mundur (Countdown 60 -> 0)
        if (gameTime <= targetTime) {
            startShrinkSequence();
        }
    }

    // 2. LOGIC ANIMASI & KILL
    if (isShrinking) {
        shrinkAnimTimer += dt;

        // FASE PERINGATAN (0 sampai 2 detik)
        if (shrinkAnimTimer < SHRINK_ANIM_DURATION) {
            // KOSONGKAN BAGIAN INI!
            // Jangan bunuh player di sini. 
            // Biarkan player melihat zona merah dan lari menyelamatkan diri.
        } 
        // FASE EKSEKUSI (Setelah 2 detik lewat)
        else {
            // 1. Cek dulu: Apakah player masih bodoh berdiri di situ?
            // Kita cek SEBELUM temboknya digeser/dibuat permanen.
            if (isPlayerInShrinkingZone(player)) {
                player.state = Player.State.DEAD;
                System.out.println("Player mati tergencet tembok!");
            }

            // 2. Jadikan tembok permanen & geser batas arena
            applyShrinkPermanently();

            // 3. Selesai
            isShrinking = false; 
        }
    }
}

    private void startShrinkSequence() {
        isShrinking = true;
        shrinkAnimTimer = 0;
        System.out.println("TRIGGER! Time hit: " + shrinkTimes[currentStep]);
    }

    private void applyShrinkPermanently() {
        if (currentStep >= shrinkPattern.length) return;

        String step = shrinkPattern[currentStep];
        switch (step) {
            case "LR" -> {
                for (int y = 0; y < mapRows; y++) {
                    map[left][y] = 1;  // Jadi tembok
                    map[right][y] = 1; 
                }
                left++;
                right--;
            }
            case "TB" -> {
                for (int x = 0; x < mapCols; x++) {
                    map[x][top] = 1;
                    map[x][bottom] = 1;
                }
                top++;
                bottom--;
            }
        }
        currentStep++; // Lanjut ke jadwal berikutnya
    }

    private boolean isPlayerInShrinkingZone(Player player) {
        if (currentStep >= shrinkPattern.length) return false;
        
        int px = (int) ((player.x + 16) / 32);
        int py = (int) ((player.y + 16) / 32);
        String step = shrinkPattern[currentStep];

        switch (step) {
            case "LR": return px == left || px == right;
            case "TB": return py == top || py == bottom;
        }
        return false;
    }

    // Getters
    public boolean isShrinking() { return isShrinking; }
    public double getShrinkAnimTimer() { return shrinkAnimTimer; }
    public int getLeft() { return left; }
    public int getRight() { return right; }
    public int getTop() { return top; }
    public int getBottom() { return bottom; }
    
    public String getCurrentStepPattern() {
        if (currentStep < shrinkPattern.length) return shrinkPattern[currentStep];
        return "";
    }
}