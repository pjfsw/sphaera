package com.pjfsw.sphaera;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.pjfsw.sphaera.gameobject.GameObject;

public class Player implements GameObject {
    public static final int LIFE_MAX = 100;
    public static final int HAPPINESS_MAX = 100;
    public static final int ENERGY_MAX = 1000;

    private final BufferedImage image;
    private int energy;
    private int happiness;
    private int x;
    private int y;
    private int life;

    public Player() {
        image = Tile.createTile();
        Graphics2D g = (Graphics2D)image.getGraphics();
        g.setColor(Color.WHITE);
        g.drawOval(8,4,16,16);
        g.drawLine(16,20,16,24);
        g.drawLine(16,24,12,30);
        g.drawLine(16,24,20,30);
        life = LIFE_MAX;
        energy = ENERGY_MAX;
        happiness = -50;
    }

    public void moveTo(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void consumeLife(int amount) {
        life -= amount;
        if (life < 0) {
            life = 0;
        }
    }

    public void consumeEnergy(int amount) {
        energy -= amount;
        if (energy < 0) {
            energy = 0;
            consumeLife(1);
        }
    }

    public void increaseEnergy(int amount) {
        energy += amount;
        if (energy > ENERGY_MAX) {
            energy = ENERGY_MAX;
        }
    }

    public int getLife() {
        return life;
    }

    public int getEnergy() {
        return energy;
    }

    public int getHappiness() {
        return happiness;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    @Override
    public BufferedImage image() {
        return image;
    }

    @Override
    public String getName() {
        return "player";
    }
}
