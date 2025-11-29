package com.mygg.core;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class InputHandler {

    public boolean up, down, left, right, place;

    public void keyDown(KeyEvent e) {
        KeyCode code = e.getCode();

        switch (code) {
            case W:
            case UP:
                up = true;
                break;

            case S:
            case DOWN:
                down = true;
                break;

            case A:
            case LEFT:
                left = true;
                break;

            case D:
            case RIGHT:
                right = true;
                break;

            case SPACE:
                place = true;
                break;
        }
    }

    public void keyUp(KeyEvent e) {
        KeyCode code = e.getCode();

        switch (code) {
            case W:
            case UP:
                up = false;
                break;

            case S:
            case DOWN:
                down = false;
                break;

            case A:
            case LEFT:
                left = false;
                break;

            case D:
            case RIGHT:
                right = false;
                break;

            case SPACE:
                place = false;
                break;
        }
    }
}
