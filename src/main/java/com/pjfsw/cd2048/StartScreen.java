package com.pjfsw.cd2048;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class StartScreen implements Drawable {
    private final Font font;

    StartScreen() {
        this.font = new Font("Courier New", Font.PLAIN, 16);
    }

    @Override
    public void draw(final Graphics2D g) {
        g.setFont(font);
        g.setColor(Color.WHITE);
        g.drawString("Welcome to Cyberderp 2048", 10, 100);
        g.drawString("Press any key to play", 10, 120);
    }
}
