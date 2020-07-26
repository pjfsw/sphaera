package com.pjfsw.sphaera;

import static com.pjfsw.sphaera.Tile.TILE_SIZE;
import static com.pjfsw.sphaera.Ui.SCALE;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.pjfsw.sphaera.gameobject.GameObject;

public class WorldUi implements Drawable {
    private static final int HORIZONTAL_TILES = 9;
    private static final int VERTICAL_TILES = 9;
    private static final int W = TILE_SIZE * HORIZONTAL_TILES;
    private static final int H = TILE_SIZE * VERTICAL_TILES;
    private static final int SCR_W = W * SCALE;
    private static final int SCR_H = H * SCALE;
    private static final int CAMERA_HIT_BOX = 2;

    private final BufferedImage tileImage;
    private final Player player;
    private final World world;
    private int worldX = -HORIZONTAL_TILES/2;
    private int worldY = -VERTICAL_TILES/2;
    private int highlightEnergy = 0;
    private int lastPlayerEnergy;
    private final Font font;

    public WorldUi(Player player, World world) {
        tileImage = new BufferedImage(W,H, BufferedImage.TYPE_INT_ARGB);
        font = new Font("Courier New", Font.PLAIN, 12);
        this.player = player;
        this.world = world;
    }


    private void updateCamera() {
        if (player.x() - CAMERA_HIT_BOX < worldX) {
            worldX = player.x() - CAMERA_HIT_BOX;
        }
        if (worldX + HORIZONTAL_TILES <= player.x() + CAMERA_HIT_BOX) {
            worldX = player.x() + CAMERA_HIT_BOX - HORIZONTAL_TILES + 1;
        }
        if (player.y() - CAMERA_HIT_BOX < worldY) {
            worldY = player.y() - CAMERA_HIT_BOX;
        }
        if (worldY + VERTICAL_TILES <= player.y() + CAMERA_HIT_BOX) {
            worldY = player.y() + CAMERA_HIT_BOX - VERTICAL_TILES + 1;
        }
    }

    private void drawWorld(Graphics2D g) {
        Color[] colors = new Color[] {
            new Color(0x444433),
            new Color(0x555544),
            new Color(0x666655)
        };
        for (int y = 0; y < VERTICAL_TILES; y++) {
            for (int x = 0; x < HORIZONTAL_TILES; x++) {
                g.setColor(colors[Math.abs(worldX+x+worldY+y)%colors.length]);
                g.fillRect(x*TILE_SIZE+1, y*TILE_SIZE+1, TILE_SIZE-2, TILE_SIZE-2);
            }
        }
    }

    private void drawObjects(Graphics2D g) {
        for (int y = 0; y < VERTICAL_TILES; y++) {
            for (int x = 0 ; x < HORIZONTAL_TILES; x++) {
                GameObject go = world.getObjectAt(x+worldX,y+worldY);
                if (go != null) {
                    g.drawImage(go.image(), TILE_SIZE*x, TILE_SIZE*y, null);
                }
            }
        }

        g.drawImage(player.image(), TILE_SIZE*(player.x()-worldX), TILE_SIZE*(player.y()-worldY), null);
    }

    private void drawGauge(Graphics2D g, int value, int max, String str, int xpos, int highlight) {
        final int HUD_OFFSET = 1;
        final int HUD_HEIGHT = 16;
        g.setColor(Color.DARK_GRAY);
        g.drawRect(HUD_OFFSET + xpos, SCR_H, 100, HUD_HEIGHT);
        if (highlight < 0) {
            g.setColor(Color.RED);
        } else if (highlight > 0) {
            g.setColor(Color.GREEN);
        }
        g.fillRect(HUD_OFFSET + xpos,  SCR_H, value * 100 / max, HUD_HEIGHT);
        g.setColor(Color.WHITE);
        g.drawString(str, 2+HUD_OFFSET+xpos, SCR_H + font.getSize());
    }

    private void drawZeroGauge(Graphics2D g, int value, int max, String str, int xpos) {
        final int HUD_OFFSET = 1;
        final int HUD_HEIGHT = 16;
        int percent = value * 50 / max;
        g.setColor(Color.DARK_GRAY);
        g.drawRect(HUD_OFFSET + xpos, SCR_H, 100, HUD_HEIGHT);
        if (value < 0) {
            g.fillRect(HUD_OFFSET + xpos + 50 + percent, SCR_H, -percent, HUD_HEIGHT);
        } else {
            g.fillRect(HUD_OFFSET + xpos + 50, SCR_H, percent, HUD_HEIGHT);
        }
        g.setColor(Color.WHITE);
        g.drawLine(HUD_OFFSET + xpos + 50, SCR_H, HUD_OFFSET + xpos + 50, SCR_H+HUD_HEIGHT-1);
        g.drawString(str, 2+HUD_OFFSET+xpos, SCR_H + font.getSize());
    }

    private void drawHud(Graphics2D g) {
        g.setFont(font);
        drawGauge(g, world.getTime(), World.TIME_PER_DAY, String.format("Day %d", world.getDay()), 0, 0);
        drawGauge(g, player.getLife(), Player.LIFE_MAX, "Life", 110,0 );
        drawGauge(g, player.getEnergy(), Player.ENERGY_MAX, "Energy", 220, highlightEnergy);
        drawZeroGauge(g, player.getHappiness(), Player.HAPPINESS_MAX, "Happiness", 330);
    }


    @Override
    public void draw(final Graphics2D g) {
        updateCamera();

        if (player.getEnergy() > lastPlayerEnergy) {
            highlightEnergy = 60;
        }
        lastPlayerEnergy = player.getEnergy();

        Graphics2D tg = (Graphics2D)tileImage.getGraphics();
        tg.setColor(Color.BLACK);
        tg.fillRect(0,0, W, H);
        drawWorld(tg);
        drawObjects(tg);
        g.drawImage(tileImage, 0,0, SCR_W, SCR_H, null);
        g.setColor(world.getDayNightCycle());
        g.fillRect(0,0, SCR_W, SCR_H);
        drawHud(g);
        if (highlightEnergy > 0) {
            highlightEnergy--;
        }
        if (highlightEnergy < 0) {
            highlightEnergy++;
        }
    }

}
