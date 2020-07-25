package com.pjfsw.sphaera;

import static com.pjfsw.sphaera.Tile.TILE_SIZE;

import java.awt.Color;
import java.awt.Dimension;
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

public class Sphaera {
    private static final int SCALE = 2;
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
    private int worldX = -HORIZONTAL_TILES/2;
    private int worldY = -VERTICAL_TILES/2;

    private Sphaera() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();
        GraphicsConfiguration gc = gs[0].getConfigurations()[0];


        tileImage = new BufferedImage(W, H, BufferedImage.TYPE_INT_ARGB);

        frame = new JFrame(gc);
        frame.setPreferredSize(new Dimension(SCR_W,SCR_H));
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

        player = new Player();
        world = new World(player);
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
        if (event.getKeyChar() == 'w') {
            world.moveNorth();
            return true;
        } else if (event.getKeyChar() == 's') {
            world.moveSouth();
            return true;
        } else if (event.getKeyChar() == 'a') {
            world.moveWest();
            return true;
        } else if (event.getKeyChar() == 'd') {
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
        for (GameObject go : world.getGameObjects()) {
            if (go.x() >= worldX && go.x() < worldX + HORIZONTAL_TILES
                && go.y() >= worldY && go.y() < worldY + VERTICAL_TILES)
            {
                g.drawImage(go.image(), TILE_SIZE*(go.x()-worldX), TILE_SIZE*(go.y()-worldY), null);
            }
        }
    }

    private void draw(Graphics2D g) {
        updateCamera();
        Graphics2D tg = (Graphics2D)tileImage.getGraphics();
        tg.setColor(Color.BLACK);
        tg.fillRect(0,0, W, H);
        drawWorld(tg);
        drawObjects(tg);
        g.drawImage(tileImage, 0,0, SCR_W, SCR_H, null);
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
