package com.pjfsw.sphaera.gameobject;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.pjfsw.sphaera.Tile;

public class ImmovableObjectFactory {
    private final BufferedImage image;

    public ImmovableObjectFactory() {
        image = Tile.createTile();
        Graphics2D g = (Graphics2D)image.getGraphics();
        g.setColor(Color.LIGHT_GRAY);
        g.fillOval(4,4,24,24);
    }

    public GameObject create() {
        return new ImmovableObject(image);
    }

}
