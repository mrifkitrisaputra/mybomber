package com.mygg.render;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class SpriteLoader {

    public static class FrameDef {
        String name;
        int x;
        int y;
        int width;
        int height;
    }

    private final HashMap<String, WritableImage> frames = new HashMap<>();

    public void load(String jsonPath, String imagePath) {

        try {
            // Load spritesheet image
            Image sheet = new Image(getClass().getResourceAsStream(imagePath));
            if (sheet.isError()) throw new RuntimeException("Image load error");

            // Load JSON
            Type listType = new TypeToken<List<FrameDef>>() {}.getType();
            InputStreamReader reader = new InputStreamReader(
                    getClass().getResourceAsStream(jsonPath)
            );

            List<FrameDef> defs = new Gson().fromJson(reader, listType);

            // SORT BERDASARKAN NAMA
            defs.sort((a, b) -> a.name.compareToIgnoreCase(b.name));

            // === Loop frames + RESIZE ke 32x32 ===
            for (FrameDef def : defs) {

                PixelReader pr = sheet.getPixelReader();
                WritableImage raw = new WritableImage(pr, def.x, def.y, def.width, def.height);

                int target = 32; // ukuran sprite final
                WritableImage resized = new WritableImage(target, target);

                PixelWriter pw = resized.getPixelWriter();
                PixelReader rawPR = raw.getPixelReader();

                // nearest-neighbor scale
                for (int y = 0; y < target; y++) {
                    for (int x = 0; x < target; x++) {

                        int srcX = x * def.width / target;
                        int srcY = y * def.height / target;

                        pw.setArgb(x, y, rawPR.getArgb(srcX, srcY));
                    }
                }

                frames.put(def.name, resized);
            }

            System.out.println("Loaded frames (resized to 32x32): " + frames.size());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public WritableImage get(String name) {
        return frames.get(name);
    }
}
