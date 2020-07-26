package com.pjfsw.sphaera.gameobject;

import java.awt.image.BufferedImage;

public class FoodObject implements GameObject, InventoryObject {
    private final BufferedImage image;
    private final int energy;

    public FoodObject(BufferedImage image, int energy) {
        this.image = image;
        this.energy = energy;
    }

    @Override
    public BufferedImage image() {
        return image;
    }

    @Override
    public int getEnergy() {
        return energy;
    }
}
