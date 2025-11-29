package com.mygg.core;

import javafx.scene.Scene;

public class InputHandler {

    public boolean up, down, left, right, place;

    public void attach(Scene scene) {

        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case W -> up = true;
                case S -> down = true;
                case A -> left = true;
                case D -> right = true;
                case SPACE -> place = true;
            }
        });

        scene.setOnKeyReleased(e -> {
            switch (e.getCode()) {
                case W -> up = false;
                case S -> down = false;
                case A -> left = false;
                case D -> right = false;
                case SPACE -> place = false;
            }
        });
    }
}
