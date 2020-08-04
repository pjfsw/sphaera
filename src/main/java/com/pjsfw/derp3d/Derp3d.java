package com.pjsfw.derp3d;

import static java.awt.event.KeyEvent.KEY_RELEASED;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.KeyboardFocusManager;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.io.IOException;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import com.google.common.collect.ImmutableList;

public class Derp3d  {
    private final JFrame frame;
    private static final int TILE_SIZE = 32;
    private static final double MOVE_SPEED = 0.02;
    private static final double FOV = 75.0 * Math.PI / 180.0;
    private static final double HALF_FOV = FOV / 2.0;
    private static final int RAYS = 256;
    private static final Color rayColor = new Color(255,0,0,127);
    private final double[] rays = new double[RAYS];
    private final double[] distances = new double[RAYS];
    private final int[] sides = new int[RAYS];
    private final double[] rx = new double[RAYS];
    private final double[] ry = new double[RAYS];
    private static final int MAX_RAY = 200;
    private static final double MAX_DISTANCE = 20000;

    private boolean moveUp = false;
    private boolean moveDown = false;
    private boolean moveLeft = false;
    private boolean moveRight = false;

    private List<String> map = ImmutableList.of(
        "1111111111111111",
        "1000000000000001",
        "1000000000111101",
        "1000010000100001",
        "1000010000100001",
        "1000010000100001",
        "1000100011111001",
        "1000000000000001",
        "1000000000000001",
        "1000000000100001",
        "1000000000100001",
        "1111111111111111"
        );

    private int[][] grid;
    private double mx = 2;
    private double my = 2;
    private double lastX = 0;
    private double angle = 0;
    private double dx;
    private double dy;
    private double ndx;
    private double ndy;
    private int gridX;
    private int gridY;

    private Derp3d() throws IOException {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();
        GraphicsConfiguration gc = gs[0].getConfigurations()[0];


        frame = new JFrame(gc);
        frame.setPreferredSize(new Dimension(1024,384));
        frame.setTitle("Derp3d");
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

        createGrid();
    }

    private void createGrid() {
        grid = new int[map.size()][map.get(0).length()];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                grid[i][j] = (j < map.get(i).length() && map.get(i).charAt(j) == '1') ? 1 : 0;
            }
        }
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

    public void draw(Graphics2D g) {
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                g.setColor(grid[y][x] == 1 ? Color.WHITE : Color.BLACK);
                g.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE-1, TILE_SIZE-1);
            }
        }
        if (gridX >= 0 && gridY >= 0) {
            g.setColor(Color.DARK_GRAY);
            g.fillRect(gridX*TILE_SIZE, gridY * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }

        g.setColor(Color.RED);
        int playerX = (int)(mx * TILE_SIZE);
        int playerY = (int)(my * TILE_SIZE);
        g.drawOval(playerX-2,playerY-2, 4,4);
        g.setColor(rayColor);

        for (int i = 0; i < RAYS; i++) {
            double rsq = rays[i];
            g.drawLine(playerX, playerY, playerX + (int)(TILE_SIZE * rsq * rx[i]), playerY + (int)(TILE_SIZE * rsq * ry[i]));
        }

        g.setColor(Color.BLACK);
        g.translate(512,192);
        g.setColor(Color.GRAY);
        g.drawRect(0,-192,512,383);
        g.scale(2,2);
        for (int i = 0; i < RAYS; i++) {
            double d = distances[i] > 0 ? distances[i] : 0;
            if (d > MAX_DISTANCE || d < 0.1) {
                continue;
            }
            int v = (int)(96 / distances[i]);
            if (v > 191) {
                v = 191;
            }
            int c = (int)(255-20.0*distances[i]);
            if (c > 255) {
                c = 255;
            }
            if (c < 0) {
                c = 0;
            }
            g.setColor(Color.WHITE);
            if (sides[i] == 0) {
                g.setColor(new Color(c,c/2,c));
            } else {
                g.setColor(new Color(c,c,c));
            }
            g.drawLine(i, -v/2, i, v/2);

        }
        if (moveLeft) {
            moveLeft();
        } else if (moveRight) {
            moveRight();
        }
        if (moveUp) {
            moveForward();
        } else if (moveDown) {
            moveBackward();
        }

        updateAngle(MouseInfo.getPointerInfo().getLocation());
        updateVectors();

    }

    private double getRayAngleFromCentre(int ray) {
        return (double)ray * FOV / (double)RAYS - HALF_FOV;
    }

    private void updateAngle(final Point location) {
        double movement = (location.getX() - lastX)/20.0;
        lastX = location.getX();
        angle += movement;
        if (angle < 0) {
            angle += 2*Math.PI;
        }
        if (angle > 2*Math.PI) {
            angle -= 2*Math.PI;
        }
    }

    private void castRays() {
        for (int i = 0; i < RAYS; i++) {
            double rayAngleFromCentre = getRayAngleFromCentre(i);
            double rayAngle = rayAngleFromCentre + angle;
            rx[i] = Math.cos(rayAngle);
            ry[i] = Math.sin(rayAngle);
            rays[i] =  MAX_RAY;

            int mapX = (int)mx;
            int mapY = (int)my;


            double deltaDistX = Math.abs(1.0 / rx[i]);
            double deltaDistY = Math.abs(1.0 / ry[i]);

            int stepX;
            int stepY;
            double sideDistX;
            double sideDistY;

            if (rx[i] < 0) {
                stepX = -1;
                sideDistX = (mx-(double)mapX) * deltaDistX;
            } else {
                stepX = 1;
                sideDistX = ((double)mapX + 1.0 - mx) * deltaDistX;
            }
            if (ry[i] < 0) {
                stepY = -1;
                sideDistY = (my-(double)mapY) * deltaDistY;
            } else {
                stepY = 1;
                sideDistY = ((double)mapY + 1.0 - my) * deltaDistY;
            }

            boolean hit = false;
            int steps = 0;
            int side = 0;
            while (!hit && steps < MAX_RAY) {
                if (sideDistX < sideDistY) {
                    sideDistX += deltaDistX;
                    mapX += stepX;
                    side = 0;
                } else {
                    sideDistY += deltaDistY;
                    mapY += stepY;
                    side = 1;
                }
                hit = grid[mapY][mapX] > 0;
                steps++;
            }

            double distance;

            if (side == 0) {
                int d = (1 - stepX) / 2;
                distance = (mapX - mx + (double)d)/rx[i];
            } else {
                int d = (1 - stepY) / 2;
                distance = (mapY - my + (double)d)/ry[i];
            }


            rays[i] = distance;
            sides[i]= side;
            if (steps < MAX_RAY) {
                double correction = Math.cos(Math.abs(rayAngleFromCentre));
                distances[i] = distance * correction;
            } else {
                distances[i] = MAX_DISTANCE+1.0;

            }
        }
    }

    private void updateVectors() {
        //mx = 2.374047;
        //my = 2.121556;
        dx = Math.cos(angle);
        dy = Math.sin(angle);
        ndx = Math.cos(angle+Math.PI/2);
        ndy = Math.sin(angle+Math.PI/2);
        gridX = (int)mx;
        gridY = (int)my;
        castRays();
    }

    private void moveForward() {
        mx += (MOVE_SPEED*dx);
        my += (MOVE_SPEED*dy);
    }

    private void moveBackward() {
        mx -= (MOVE_SPEED*dx);
        my -= (MOVE_SPEED*dy);
    }

    private void moveLeft() {
        mx -= (MOVE_SPEED*ndx);
        my -= (MOVE_SPEED*ndy);
    }

    private void moveRight() {
        mx += (MOVE_SPEED*ndx);
        my += (MOVE_SPEED*ndy);
    }

    private boolean handleKeyEvent(KeyEvent event) {
        if (event.getKeyChar() == 'w') {
            moveUp = event.getID() != KEY_RELEASED;
            return true;
        }
        if (event.getKeyChar() == 's') {
            moveDown = event.getID() != KEY_RELEASED;
            return true;
        }
        if (event.getKeyChar() == 'a') {
            moveLeft = event.getID() != KEY_RELEASED;
            return true;
        }
        if (event.getKeyChar() == 'd') {
            moveRight = event.getID() != KEY_RELEASED;
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        try {
            new Derp3d().run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
