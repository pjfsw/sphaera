package com.pjfsw.cd2048;

import static java.awt.event.KeyEvent.KEY_PRESSED;

import java.awt.event.KeyEvent;

public class GameManager {
    private Game game;
    private final StartScreen startScreen;

    private enum State {
        START,
        PLAYING
    } ;

    private State state;

    GameManager() {
        state = State.START;
        startScreen = new StartScreen();
    }

    public Drawable getDrawable() {
        switch (state) {
            case START:
                return startScreen;
            case PLAYING:
                return game;
        }
        return startScreen;
    }

    public boolean handleKeyEvent(final KeyEvent keyEvent) {
        switch (state) {
            case START:
                if (keyEvent.getID() == KEY_PRESSED) {
                    game = new Game();
                    state = State.PLAYING;
                    return true;
                }
                return false;
            case PLAYING:
                // Override some buttons here
                return game.handleKeyEvent(keyEvent);
        }
        return false;
    }
}
