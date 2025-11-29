package com.mygg.render;

import com.mygg.core.InputHandler;
import com.mygg.entities.Player;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class GameCanvas extends Canvas {

    private final Player player;
    private final InputHandler input;
    private long last = System.nanoTime();

    public GameCanvas(Player player, InputHandler input) {
        super(800, 600);
        this.player = player;
        this.input = input;

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                double dt = (now - last) / 1e9;
                last = now;

                update(dt);
                draw();
            }
        }.start();
    }

    private void update(double dt) {

        boolean move = false;

        if (input.up) {
            player.y -= player.speed * dt;
            player.dir = Player.Direction.UP;
            move = true;
        }
        if (input.down) {
            player.y += player.speed * dt;
            player.dir = Player.Direction.DOWN;
            move = true;
        }
        if (input.left) {
            player.x -= player.speed * dt;
            player.dir = Player.Direction.LEFT;
            move = true;
        }
        if (input.right) {
            player.x += player.speed * dt;
            player.dir = Player.Direction.RIGHT;
            move = true;
        }

        if (input.place) {
            player.state = Player.State.PLACE;
        } else {
            player.state = move ? Player.State.WALK : Player.State.IDLE;
        }
    }

    private void draw() {
        GraphicsContext g = getGraphicsContext2D();

        g.clearRect(0, 0, getWidth(), getHeight());

        Image frame = player.update(1 / 60.0);

        g.drawImage(frame, player.x, player.y);
    }
}
