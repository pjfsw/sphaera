package com.pjfsw.sphaera.gameobject;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.pjfsw.sphaera.Tile;
import com.pjfsw.sphaera.npc.Npc;

public class ImageFactory {
    private final Map<String, BufferedImage> images = new HashMap<>();

    public ImageFactory() throws IOException {
        images.put(RockObject.NAME, ImageIO.read(new File("src/main/resources/smallrock.png")));
        images.put(ImmovableObject.NAME, ImageIO.read(new File("src/main/resources/rock.png")));
        images.put(FoodObject.NAME, createFoodImage());
        images.put(Npc.NAME, createNpcImage());
    }

    private BufferedImage createFoodImage() {
        BufferedImage image = Tile.createTile();
        Graphics2D g = (Graphics2D)image.getGraphics();
        g.setColor(Color.GREEN);
        g.drawLine(8,4,16,14);
        g.setColor(Color.RED);
        g.fillOval(8,14,16,16);
        return image;
    }

    private BufferedImage createNpcImage() {
        BufferedImage image = Tile.createTile();
        Graphics2D g = (Graphics2D)image.getGraphics();
        g.setColor(Color.PINK);
        g.fillOval(8,4,16,16);
        g.drawLine(16,20,16,31);
        return image;
    }

    public BufferedImage get(String name) {
        return images.get(name);
    }

}
