package com.pjfsw.sphaera.gameobject;

import java.awt.image.BufferedImage;

public class RockObject implements GameObject, InventoryObject {
    private final BufferedImage image;

    public static final String NAME = "rock";

    public RockObject(ImageFactory imageFactory) {
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

    @Override
    public int getEnergy() {
        return 0;
    }



}
