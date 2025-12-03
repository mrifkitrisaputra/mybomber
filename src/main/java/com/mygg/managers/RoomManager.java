// src/main/java/com/mygg/managers/RoomManager.java
package com.mygg.managers;

import java.util.ArrayList;
import java.util.List;

public class RoomManager {
    
    public static boolean isGameStarted = false; // Lock Room Status
    private static String hostName = "Player 1 (You)";
    private static List<String> players = new ArrayList<>();

    // Reset saat masuk room baru
    public static void initRoom() {
        players.clear();
        players.add("Player 1 (You)");
        players.add("Player 2"); // Simulasi bot
        players.add("Player 3");
        hostName = "Player 1 (You)";
        isGameStarted = false;
    }

    public static boolean isHost() {
        return hostName.equals("Player 1 (You)");
    }

    // Logic Host Migration
    public static void playerLeft(String name) {
        players.remove(name);
        
        // Jika Host keluar, pindahkan host ke player berikutnya
        if (hostName.equals(name)) {
            if (!players.isEmpty()) {
                hostName = players.get(0);
                System.out.println("Host Migrated to: " + hostName);
            } else {
                System.out.println("Room closed (Empty)");
            }
        }
    }

    // Cek apakah boleh masuk room
    public static boolean canJoin() {
        return !isGameStarted;
    }
}