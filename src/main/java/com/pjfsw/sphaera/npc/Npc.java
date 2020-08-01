package com.pjfsw.sphaera.npc;

import java.awt.image.BufferedImage;

import com.pjfsw.sphaera.World;
import com.pjfsw.sphaera.gameobject.GameObject;

public class Npc implements GameObject {
    public static final String NAME = "npc";
    private final BufferedImage image;
    private int x;
    private int y;

    public Npc(BufferedImage image, int x, int y) {
        this.image = image;
        this.x = x;
        this.y = y;
    }

    @Override
    public BufferedImage image() {
        return image;
    }

    @Override
    public String getName() {
        return NAME;
    }

    public void move(World world) {
        x = (x + 1) % 8;
        y = (y + 1) % 8;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }
}
