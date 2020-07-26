package com.pjfsw.sphaera.gameobject;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImmovableObjectFactory {
    private final BufferedImage image;

    public ImmovableObjectFactory() throws IOException {
        image = ImageIO.read(new File("src/main/resources/rock.png"));
        //image = Tile.createTile();

        //Graphics2D g = (Graphics2D)image.getGraphics();
        //g.setColor(Color.LIGHT_GRAY);
        //g.fillOval(4,4,24,24);
    }

    public GameObject create() {
        return new ImmovableObject(image);
    }

}
