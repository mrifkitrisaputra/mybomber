package com.mygg.managers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.mygg.core.SoundHandler;
import com.mygg.entities.Bomb;

import javafx.scene.canvas.GraphicsContext;

public class BombManager {
    

    private final List<Bomb> bombs = new ArrayList<>();

    public void placeBomb(int x, int y) {
        SoundHandler.init();

        for (Bomb b : bombs)
            if (b.tileX == x && b.tileY == y) return;

        bombs.add(new Bomb(x, y, 1));
        SoundHandler.playBombPlace();
    }

    public void update(double dt, ExplosionManager explosions) {
        Iterator<Bomb> it = bombs.iterator();
        while (it.hasNext()) {
            Bomb b = it.next();
            b.update(dt);
            if (b.isExploded) {
                explosions.add(b.tileX, b.tileY, b.range);
                it.remove();
            }
        }
    }

    public void render(GraphicsContext g) {
        for (Bomb b : bombs) b.render(g);
    }

    public boolean isBlocking(int x, int y, int tile) {
        for (Bomb b : bombs) {
            if (!b.isSolid) continue;
            if (b.tileX == x && b.tileY == y) return true;
        }
        return false;
    }
}
