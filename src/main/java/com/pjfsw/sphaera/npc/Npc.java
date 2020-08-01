package com.pjfsw.sphaera.npc;

import java.awt.image.BufferedImage;

import com.pjfsw.sphaera.Random;
import com.pjfsw.sphaera.World;
import com.pjfsw.sphaera.gameobject.GameObject;
import com.pjfsw.sphaera.gameobject.ImageFactory;

public class Npc implements GameObject {
    private final int movementSpeed = 2;

    public static final String NAME = "npc";
    private final BufferedImage image;
    private int x;
    private int y;
    private int direction = 0;
    private final int[] dx = {1,0,-1,0};
    private final int[] dy = {0,1,0,-1};
    private int cycle = 0;

    public Npc(ImageFactory imageFactory, int x, int y) {
        this.image = imageFactory.get(NAME);
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
        if (cycle == 0) {
            int newX = x+dx[direction];
            int newY = y+dy[direction];
            if (world.getTerrainAt(newX, newY) == 0 && world.getObjectAt(newX, newY) == null) {
                x = x + dx[direction];
                y = y + dy[direction];
            } else {
                direction = Random.random(4);
            }
        }
        cycle = (cycle + 1) % movementSpeed;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }
}
