package com.mygg.render;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javafx.scene.image.Image;
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

        // === SORT BERDASARKAN NAMA ===
        defs.sort((a, b) -> a.name.compareToIgnoreCase(b.name));

        for (FrameDef def : defs) {
            WritableImage frame = new WritableImage(
                    sheet.getPixelReader(),
                    def.x, def.y,
                    def.width, def.height
            );
            frames.put(def.name, frame);
        }

        System.out.println("Loaded frames: " + frames.size());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public WritableImage get(String name) {
        return frames.get(name);
    }
}
