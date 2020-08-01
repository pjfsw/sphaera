package com.pjfsw.sphaera.gameobject;

import java.awt.image.BufferedImage;

public class ImmovableObject implements GameObject {
    public static final String NAME = "immovable object";

    private final BufferedImage image;

    public ImmovableObject(ImageFactory imageFactory) {
        this.image = imageFactory.get(NAME);
    }

    @Override
    public BufferedImage image() {
        return image;
    }

    @Override
    public String getName() {
        return NAME;
    }
}
