package com.mygg.managers;

import com.mygg.entities.Item;
import com.mygg.entities.Player;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class ItemManager {

    private final List<Item> items = new ArrayList<>();
    private final Image imgBombUp, imgFireUp, imgSpeedUp;
    private final int tileSize;
    private final Random random = new Random();

    public ItemManager(int tileSize) {
        this.tileSize = tileSize;
        // Load asset images
        imgBombUp = new Image(getClass().getResourceAsStream("/com/mygg/assets/item/BombUp.png"), tileSize, tileSize, false, false);
        imgFireUp = new Image(getClass().getResourceAsStream("/com/mygg/assets/item/FireUp.png"), tileSize, tileSize, false, false);
        imgSpeedUp = new Image(getClass().getResourceAsStream("/com/mygg/assets/item/SpeedUp.png"), tileSize, tileSize, false, false);
    }

    public void trySpawnItem(int gridX, int gridY) {
        // Chance 30% muncul item
        if (random.nextDouble() < 0.3) {
            
            // Panggil Enum lewat class Item
            Item.Type type; 
            double r = random.nextDouble();

            if (r < 0.33) {
                type = Item.Type.BOMB_UP;
            } else if (r < 0.66) {
                type = Item.Type.FIRE_UP;
            } else {
                type = Item.Type.SPEED_UP;
            }

            items.add(new Item(gridX, gridY, type));
            System.out.println("Spawned Item: " + type + " at " + gridX + "," + gridY);
        }
    }

    public void update(Player player) {
        Iterator<Item> it = items.iterator();
        while (it.hasNext()) {
            Item item = it.next();

            // Hitung grid player (tambah 16/setengah tile biar deteksi titik tengah)
            int pGridX = (int) ((player.x + tileSize / 2) / tileSize);
            int pGridY = (int) ((player.y + tileSize / 2) / tileSize);

            if (item.x == pGridX && item.y == pGridY) {
                applyEffect(player, item.type);
                it.remove();
                System.out.println("Picked up: " + item.type);
            }
        }
    }

    private void applyEffect(Player player, Item.Type type) {
        switch (type) {
            case BOMB_UP -> player.addBombLimit();
            case FIRE_UP -> player.addFirePower();
            case SPEED_UP -> player.activateSpeedUp(5.0); // Durasi 5 detik
        }
    }

    public void render(GraphicsContext g) {
        for (Item item : items) {
            Image img = switch (item.type) {
                case BOMB_UP -> imgBombUp;
                case FIRE_UP -> imgFireUp;
                case SPEED_UP -> imgSpeedUp;
            };
            if (img != null) {
                g.drawImage(img, item.x * tileSize, item.y * tileSize);
            }
        }
    }
}