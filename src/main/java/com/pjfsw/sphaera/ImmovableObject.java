package com.pjfsw.sphaera;

import java.awt.image.BufferedImage;

public class ImmovableObject implements GameObject {
    private final int x;
    private final int y;
    private final BufferedImage image;

    public ImmovableObject(int x, int y, BufferedImage image) {
        this.x = x;
        this.y = y;
        this.image = image;
    }

    @Override
    public int x() {
        return x;
    }

    @Override
    public int y() {
        return y;
    }

    @Override
    public BufferedImage image() {
        return image;
    }
}
