package com.pjfsw.sphaera.gameobject;

import java.awt.image.BufferedImage;

public class ImmovableObject implements GameObject {
    private final BufferedImage image;

    public ImmovableObject(BufferedImage image) {
        this.image = image;
    }

    @Override
    public BufferedImage image() {
        return image;
    }
}
