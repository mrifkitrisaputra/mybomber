package com.mygg.ui;

import com.mygg.render.GameCanvas;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {
    private static Stage stage;
    
    public static void setStage(Stage s) {
        stage = s;
    }

    public static void toLobby() {
        LobbyScene lobby = new LobbyScene();
        stage.setScene(lobby.getScene());
        stage.setTitle("Bomber Game - Lobby");
    }

    public static void toRoom(String roomName, boolean isHost) {
        RoomScene room = new RoomScene(roomName, isHost);
        stage.setScene(room.getScene());
        stage.setTitle("Room: " + roomName);
    }

    public static void toGame() {
        // Disini kita akan inisialisasi GameCanvas (Gameplay dimulai)
        // Kita butuh dummy player/input handler di sini nantinya untuk integrasi penuh
        // Untuk sekarang, logic ini ada di App.java, nanti kita pindahkan.
    }
    
    // Method helper untuk mengganti scene secara custom (misal dari App.java)
    public static void setScene(Scene scene) {
        stage.setScene(scene);
    }
}