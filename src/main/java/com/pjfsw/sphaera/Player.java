package com.pjfsw.sphaera;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Player implements GameObject {
    private final BufferedImage image;
    private int x;
    private int y;

    public Player() {
        image = Tile.createTile();
        Graphics2D g = (Graphics2D)image.getGraphics();
        g.setColor(Color.WHITE);
        g.drawOval(8,4,16,16);
        g.drawLine(16,20,16,24);
        g.drawLine(16,24,12,30);
        g.drawLine(16,24,20,30);
    }

    public void moveTo(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int x() {
        return x;
    }

    @Override
    public int y() {
        return y;
    }

    @Override
    public BufferedImage image() {
        return image;
    }
}
