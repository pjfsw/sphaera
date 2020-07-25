package com.pjfsw.sphaera;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ImmovableObjectFactory {
    private final BufferedImage image;

    public ImmovableObjectFactory() {
        image = Tile.createTile();
        Graphics2D g = (Graphics2D)image.getGraphics();
        g.setColor(Color.LIGHT_GRAY);
        g.fillOval(4,4,24,24);
    }

    public GameObject create(int x, int y) {
        return new ImmovableObject(x,y, image);
    }

}
