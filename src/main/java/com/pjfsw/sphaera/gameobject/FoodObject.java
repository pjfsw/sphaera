package com.pjfsw.sphaera.gameobject;

import java.awt.image.BufferedImage;

public class FoodObject implements GameObject, InventoryObject {
    private final BufferedImage image;
    private final int energy;
    public static final String NAME = "food";

    public FoodObject(ImageFactory imageFactory, int energy) {
        this.image = imageFactory.get(NAME);
        this.energy = energy;
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
        return energy;
    }
}
