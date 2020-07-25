package com.pjfsw.sphaera.gameobject;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.pjfsw.sphaera.Tile;

public class FoodObjectFactory {
    private final BufferedImage image;

    public FoodObjectFactory() {
        image = Tile.createTile();
        Graphics2D g = (Graphics2D)image.getGraphics();
        g.setColor(Color.GREEN);
        g.drawLine(8,4,16,14);
        g.setColor(Color.RED);
        g.fillOval(8,14,16,16);
    }

    public GameObject create() {
        return new FoodObject(image, 100);
    }

}
