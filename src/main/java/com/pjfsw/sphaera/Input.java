package com.pjfsw.sphaera;

import java.awt.event.KeyEvent;

public class Input {
    public static final char CONSUME = 'c';

    public static boolean isLeft(KeyEvent event) {
        return event.getKeyChar() == 'a' || event.getKeyCode() == KeyEvent.VK_LEFT;
    }

    public static boolean isRight(KeyEvent event) {
        return event.getKeyChar() == 'd' || event.getKeyCode() == KeyEvent.VK_RIGHT;
    }

    public static boolean isUp(KeyEvent event) {
        return event.getKeyChar() == 'w' || event.getKeyCode() == KeyEvent.VK_UP;
    }


    public static boolean isDown(KeyEvent event) {
        return event.getKeyChar() == 's' || event.getKeyCode() == KeyEvent.VK_DOWN;
    }

    public static boolean isInventory(KeyEvent event) {
        return event.getKeyChar() == 'i' || event.getKeyCode() == KeyEvent.VK_ENTER;
    }

    public static boolean isConsume(KeyEvent event) {
        return event.getKeyChar() == CONSUME;
    }

}
