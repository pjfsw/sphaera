package com.pjfsw.cd2048;

import static java.awt.event.KeyEvent.KEY_PRESSED;

import java.awt.event.KeyEvent;

public class KeyManager {
    private KeyManager() {
    }

    static boolean isNorth(KeyEvent event) {
        return event.getID() == KEY_PRESSED && (event.getKeyChar() == 'w' || event.getKeyCode() == KeyEvent.VK_UP);
    }

    static boolean isSouth(KeyEvent event) {
        return event.getID() == KEY_PRESSED && (event.getKeyChar() == 's' || event.getKeyCode() == KeyEvent.VK_DOWN);
    }

    static boolean isWest(KeyEvent event) {
        return event.getID() == KEY_PRESSED && (event.getKeyChar() == 'a' || event.getKeyCode() == KeyEvent.VK_LEFT);
    }

    static boolean isEast(KeyEvent event) {
        return event.getID() == KEY_PRESSED && (event.getKeyChar() == 'd' || event.getKeyCode() == KeyEvent.VK_RIGHT);
    }

    static boolean isTake(KeyEvent event) {
        return event.getID() == KEY_PRESSED && (event.getKeyChar() == 't' || event.getKeyCode() == KeyEvent.VK_DELETE);
    }

    static boolean isDrop(KeyEvent event) {
        return event.getID() == KEY_PRESSED && (event.getKeyChar() == 'p' || event.getKeyCode() == KeyEvent.VK_INSERT);
    }

    static boolean isUp(KeyEvent event) {
        return event.getID() == KEY_PRESSED && event.getKeyCode() == KeyEvent.VK_UP;
    }

    static boolean isDown(KeyEvent event) {
        return event.getID() == KEY_PRESSED && event.getKeyCode() == KeyEvent.VK_DOWN;
    }

    static boolean isCancel(KeyEvent event) {
        return event.getID() == KEY_PRESSED && event.getKeyCode() == KeyEvent.VK_ESCAPE;
    }

    static boolean isSelect(KeyEvent event) {
        return event.getID() == KEY_PRESSED && (event.getKeyCode() == KeyEvent.VK_ENTER || event.getKeyChar() == ' ');
    }

}
