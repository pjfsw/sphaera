package com.pjfsw.sphaera;

import static com.pjfsw.sphaera.Tile.TILE_SIZE;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.pjfsw.sphaera.gameobject.GameObject;

public class WorldUi implements Drawable {
    private static final int HORIZONTAL_TILES = 9;
    private static final int VERTICAL_TILES = 9;
    public static final int W = TILE_SIZE * HORIZONTAL_TILES;
    public static final int H = TILE_SIZE * VERTICAL_TILES;
    private static final int CAMERA_HIT_BOX = 2;

    private final BufferedImage tileImage;
    private final Player player;
    private final World world;
    private int worldX = -HORIZONTAL_TILES/2;
    private int worldY = -VERTICAL_TILES/2;
    private Color dayNightCycle = Color.BLACK;

    public WorldUi(Player player, World world) {
        tileImage = new BufferedImage(W,H, BufferedImage.TYPE_INT_ARGB);
        this.player = player;
        this.world = world;
        createDayNightCycle(0);
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
            new Color(0x5f5840),
            new Color(0x3080b0)
        };
        for (int y = 0; y < VERTICAL_TILES; y++) {
            for (int x = 0; x < HORIZONTAL_TILES; x++) {
                g.setColor(colors[world.getTerrainAt(worldX+x,worldY+y)]);
                g.fillRect(x*TILE_SIZE, y*TILE_SIZE, TILE_SIZE, TILE_SIZE);
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

    public void createDayNightCycle(int time) {
        int scale = 400;
        int darkness =  (int)(scale * Math.cos(2 * time * Math.PI / GameTime.TIME_PER_DAY)) - scale/2;
        if (darkness < 0) {
            darkness = 0;
        }
        if (darkness > 200) {
            darkness = 200;
        }
        //dayNightCycle = new Color(0,0,0,darkness);
        dayNightCycle = new Color(0, 0, 0, 0);
    }

    @Override
    public void draw(final Graphics2D g) {
        updateCamera();

        Graphics2D tg = (Graphics2D)tileImage.getGraphics();
        tg.setColor(Color.BLACK);
        tg.fillRect(0,0, W, H);
        drawWorld(tg);
        drawObjects(tg);
        g.drawImage(tileImage, 0,0, W, H, null);
        g.setColor(dayNightCycle);
        g.fillRect(0,0, W, H);
    }

}
