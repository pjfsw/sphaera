package com.pjfsw.sphaera;

import java.awt.image.BufferedImage;

public final class Tile {
    public static final int TILE_SIZE = 32;

    public static BufferedImage createTile() {
        return new BufferedImage(TILE_SIZE, TILE_SIZE, BufferedImage.TYPE_INT_ARGB);
    }
}
