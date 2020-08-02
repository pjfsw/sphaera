package com.pjsfw.derp3d;

import static java.awt.event.KeyEvent.KEY_PRESSED;

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
    private static final double MOVE_SPEED = 3.0;
    private static final double FOV = 80.0 * Math.PI / 180.0;
    private static final double HALF_FOV = FOV / 2.0;
    private static final int RAYS = 320;
    private static  final Color rayColor = new Color(255,0,0,127);
    private int rays[] = new int[RAYS];
    private double distances[] = new double[RAYS];
    private double rx[] = new double[RAYS];
    private double ry[] = new double[RAYS];
    private int rgx[] = new int[RAYS];
    private int rgy[] = new int[RAYS];
    private static final int MAX_RAY = 300;
    private static final double MAX_DISTANCE = 20000;

    private List<String> map = ImmutableList.of(
        "1111111111111111",
        "1000000000000001",
        "1000000000000001",
        "1000010000000001",
        "1000010000000001",
        "1000010000000001",
        "1000100011110001",
        "1000000000000001",
        "1000000000000001",
        "1000000000100001",
        "1111111111111111"
        );

    private int[][] grid;
    private double mx = 50;
    private double my = 50;
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
        frame.setPreferredSize(new Dimension(1024,600));
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
        int playerX = (int)mx;
        int playerY = (int)my;
        g.drawOval(playerX-2,playerY-2, 4,4);

        //g.drawLine(playerX, playerY, playerX + (int)(dx * 25), playerY + (int)(dy * 25));

        g.setColor(rayColor);

        for (int i = 0; i < RAYS; i++) {
            //g.fillRect(rgx[i]*TILE_SIZE,rgy[i]*TILE_SIZE, TILE_SIZE, TILE_SIZE);
            double rsq = rays[i];
            g.drawLine(playerX, playerY, playerX + (int)(rsq * rx[i]), playerY + (int)(rsq * ry[i]));
        }

        g.translate(512,128);
        g.setColor(Color.WHITE);
        for (int i = 0; i < RAYS; i++) {
            double d = distances[i] > 0 ? distances[i] : 0;
            if (d > MAX_DISTANCE || d < 0.1) {
                continue;
            }
            int v = (int)(2500 / distances[i]);
            if (v > 255) {
                v = 255;
            }
            g.setColor(new Color(v, v, v));
            g.drawLine(i, -v/2, i, v/2);

        }

        updateAngle(MouseInfo.getPointerInfo().getLocation());

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
        updateVectors();
    }

    private void castRays() {
        for (int i = 0; i < RAYS; i++) {
            double rayAngleFromCentre = getRayAngleFromCentre(i);
            double rayAngle = rayAngleFromCentre + angle;
            rx[i] = Math.cos(rayAngle);
            ry[i] = Math.sin(rayAngle);
            double rxStep = rx[i];
            double ryStep = ry[i];
            rays[i] =  MAX_RAY;
            double px = mx;
            double py = my;

            int steps = 0;
            boolean run = true;
            do {
                int cx  = (int)(px / TILE_SIZE);
                int cy = (int)(py/TILE_SIZE);
                if (cx < 0
                    || cx >= grid[0].length
                    || cy < 0
                    || cy >= grid.length) {
                    run = false;
                } else if (grid[cy][cx] == 1) {
                    //if (Math.abs(rxStep) > Math.abs(ryStep)) {
                        if (rxStep > 0) {
                            px = cx * TILE_SIZE;
                        } else {
                            px = (cx+1) * TILE_SIZE-1;
                        }
                    /*} else {
                        if (ryStep > 0) {
                            py = cy * TILE_SIZE;
                        } else {
                            py =  (cy+1) * TILE_SIZE - 1;
                        }
                    }*/
                    run = false;
                } else {
                    steps++;
                    px += rxStep;
                    py += ryStep;
                    // TODO correct "holes"
                }
            } while (steps < MAX_RAY && run);
            double diffX = px-mx;
            double diffY = py-my;
            double distance = Math.sqrt(diffX*diffX + diffY*diffY);
            rays[i] = (int)distance;
            rgx[i] = (int)(px/TILE_SIZE);
            rgy[i] = (int)(py/TILE_SIZE);
            if (steps < MAX_RAY) {
                double correction = Math.cos(Math.abs(rayAngleFromCentre));
                //System.out.println(correction);
                distances[i] = distance * correction;
            } else {
                distances[i] = MAX_DISTANCE+1.0;

            }
        }
    }

    private void updateVectors() {
        dx = Math.cos(angle);
        dy = Math.sin(angle);
        ndx = Math.cos(angle+Math.PI/2);
        ndy = Math.sin(angle+Math.PI/2);
        gridX = (int)(mx / TILE_SIZE);
        gridY = (int)(my / TILE_SIZE);
        castRays();
    }

    private void moveForward() {
        mx += (MOVE_SPEED*dx);
        my += (MOVE_SPEED*dy);
        updateVectors();
    }

    private void moveBackward() {
        mx -= (MOVE_SPEED*dx);
        my -= (MOVE_SPEED*dy);
        updateVectors();
    }

    private void moveLeft() {
        mx -= (MOVE_SPEED*ndx);
        my -= (MOVE_SPEED*ndy);
        updateVectors();
    }

    private void moveRight() {
        mx += (MOVE_SPEED*ndx);
        my += (MOVE_SPEED*ndy);
        updateVectors();
    }

    private boolean handleKeyEvent(KeyEvent event) {
        if (event.getID() != KEY_PRESSED) {
            return false;
        }

        if (event.getKeyChar() == 'w') {
            moveForward();
            return true;
        }
        if (event.getKeyChar() == 's') {
            moveBackward();
            return true;
        }
        if (event.getKeyChar() == 'a') {
            moveLeft();
            return true;
        }
        if (event.getKeyChar() == 'd') {
            moveRight();
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
