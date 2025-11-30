package com.mygg.render;

import java.io.File;
import java.net.URL;
import java.util.HashMap;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class SpriteLoader {

    private final HashMap<String, WritableImage> frames = new HashMap<>();
    private final int TARGET_SIZE = 32;

    public void loadFolder(String folderPath) {
        try {
            URL url = getClass().getResource(folderPath);
            if (url == null) {
                throw new RuntimeException("Folder not found: " + folderPath);
            }

            File folder = new File(url.toURI());
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".png"));
            if (files == null) return;

            for (File file : files) {
                String fileName = file.getName(); // contoh: sP2Down_0.png

                Image raw = new Image(file.toURI().toString());
                WritableImage resized = resizeTo32(raw);

                // remove extension
                String key = fileName.substring(0, fileName.lastIndexOf("."));

                frames.put(key, resized);
            }

            System.out.println("Loaded PNG frames: " + frames.size());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private WritableImage resizeTo32(Image raw) {
        WritableImage out = new WritableImage(TARGET_SIZE, TARGET_SIZE);

        PixelWriter pw = out.getPixelWriter();
        PixelReader pr = raw.getPixelReader();

        int w = (int) raw.getWidth();
        int h = (int) raw.getHeight();

        // nearest neighbor scaling
        for (int y = 0; y < TARGET_SIZE; y++) {
            for (int x = 0; x < TARGET_SIZE; x++) {
                int srcX = x * w / TARGET_SIZE;
                int srcY = y * h / TARGET_SIZE;
                pw.setArgb(x, y, pr.getArgb(srcX, srcY));
            }
        }
        return out;
    }

    public WritableImage get(String name) {
        return frames.get(name);
    }
}
