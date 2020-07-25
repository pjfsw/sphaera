package com.pjfsw.sphaera;

import static com.pjfsw.sphaera.Tile.TILE_SIZE;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import com.pjfsw.sphaera.gameobject.GameObject;

public class Sphaera {
    private static final int SCALE = 2;
    private static final int HUD_H = 32;
    private static final int VERTICAL_TILES = 9;
    private static final int HORIZONTAL_TILES = 9;
    private static final int W = HORIZONTAL_TILES * TILE_SIZE;
    private static final int H = VERTICAL_TILES * TILE_SIZE;
    private static final int SCR_W = W * SCALE;
    private static final int SCR_H = H * SCALE;
    private static final int CAMERA_HIT_BOX = 2;
    private final JFrame frame;
    private final BufferedImage tileImage;
    private final World world;
    private final Player player;
    private final Font font;
    private int worldX = -HORIZONTAL_TILES/2;
    private int worldY = -VERTICAL_TILES/2;
    private Color dayNightCycle;

    private Sphaera() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();
        GraphicsConfiguration gc = gs[0].getConfigurations()[0];

        tileImage = new BufferedImage(W, H, BufferedImage.TYPE_INT_ARGB);

        frame = new JFrame(gc);
        frame.setPreferredSize(new Dimension(SCR_W,SCR_H+HUD_H));
        frame.setTitle("Sphaera");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setSize(new Dimension(
            frame.getPreferredSize().width
                + frame.getInsets().left + frame.getInsets().right,
            frame.getPreferredSize().height
                + frame.getInsets().top + frame.getInsets().bottom));
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        font = new Font("Courier New", Font.PLAIN, 12);

        player = new Player();
        world = new World(player);
        createDayNightCycle();
    }

    private static void waitNs(long ns) {
        if (ns > 0) {
            try {
                Thread.sleep(ns / 1000000, (int)(ns % 1000000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean handleKeyEvent(KeyEvent event) {
        if (event.getID() != KeyEvent.KEY_PRESSED) {
            return false;
        }
        createDayNightCycle();
        if (event.getKeyChar() == 'w' || event.getKeyCode() == KeyEvent.VK_UP) {
            world.moveNorth();
            return true;
        } else if (event.getKeyChar() == 's' || event.getKeyCode() == KeyEvent.VK_DOWN) {
            world.moveSouth();
            return true;
        } else if (event.getKeyChar() == 'a' || event.getKeyCode() == KeyEvent.VK_LEFT) {
            world.moveWest();
            return true;
        } else if (event.getKeyChar() == 'd' || event.getKeyCode() == KeyEvent.VK_RIGHT) {
            world.moveEast();
            return true;
        }

        return false;
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

    private void drawGauge(Graphics2D g, int value, int max, String str, int xpos) {
        final int HUD_OFFSET = 1;
        final int HUD_HEIGHT = 16;
        g.setColor(Color.DARK_GRAY);
        g.drawRect(HUD_OFFSET + xpos, SCR_H, 100, HUD_HEIGHT);
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
        drawGauge(g, world.getTime(), World.TIME_PER_DAY, String.format("Day %d", world.getDay()), 0);
        drawGauge(g, player.getLife(), Player.LIFE_MAX, "Life", 110);
        drawGauge(g, player.getEnergy(), Player.ENERGY_MAX, "Energy", 220);
        drawZeroGauge(g, player.getHappiness(), Player.HAPPINESS_MAX, "Happiness", 330);
    }

    private void createDayNightCycle() {
        int scale = 400;
        int darkness =  (int)(scale * Math.cos(2 * world.getTime() * Math.PI / World.TIME_PER_DAY)) - scale/2;
        if (darkness < 0) {
            darkness = 0;
        }
        if (darkness > 200) {
            darkness = 200;
        }
        dayNightCycle = new Color(0,0,0,darkness);
    }

    private void draw(Graphics2D g) {
        updateCamera();

        Graphics2D tg = (Graphics2D)tileImage.getGraphics();
        tg.setColor(Color.BLACK);
        tg.fillRect(0,0, W, H);
        drawWorld(tg);
        drawObjects(tg);
        g.drawImage(tileImage, 0,0, SCR_W, SCR_H, null);
        g.setColor(dayNightCycle);
        g.fillRect(0,0, SCR_W, SCR_H);
        drawHud(g);
    }

    public void run() {
        frame.setAlwaysOnTop(true);
        frame.setAlwaysOnTop(false);
        frame.createBufferStrategy(2);
        BufferStrategy strategy = frame.getBufferStrategy();

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this::handleKeyEvent);

        long WAIT_PERIOD = 1_000_000_000 / 60; // TODO get monitor refresh rate

        long ticks = System.nanoTime();
        while (true) {
            do {
                // preparation for rendering ?
                do {
                    Graphics graphics = strategy.getDrawGraphics();
                    graphics.setColor(Color.BLACK);
                    graphics.fillRect(0,0,frame.getWidth(),frame.getHeight());
                    graphics.translate(0, frame.getInsets().top);
                    draw((Graphics2D)graphics);
                    graphics.dispose();
                } while (strategy.contentsRestored());
                strategy.show();
                long wait = WAIT_PERIOD - (int)(System.nanoTime() - ticks);
                ticks = System.nanoTime();
                waitNs(wait);
            } while (strategy.contentsLost());
        }
    }

    public static void main(String[] args) {
        Sphaera sphaera = new Sphaera();
        sphaera.run();
    }

}
