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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import com.google.common.collect.ImmutableList;

public class Derp3d  {
    private final JFrame frame;
    private static final int TILE_SIZE = 16;
    private static final double MOVE_SPEED = 3;
    private static final double ROTATE_SPEED = 0.8;
    private static final double FOV = 85.0 * Math.PI / 180.0;
    private static final double HALF_FOV = FOV / 2.0;
    private static final int RAYS = 320;
    private static final int W = RAYS;
    private static final double PROJECTION_DIST = (double)RAYS/(2.0 * Math.tan(HALF_FOV));
    private static final int H = 200;
    private static final Color rayColor = new Color(255,0,0,127);
    private final double[] rays = new double[RAYS];
    private final double[] distances = new double[RAYS];
    private final double[] rx = new double[RAYS];
    private final double[] ry = new double[RAYS];
    private final double[] RAY_ANGLES = new double[RAYS];
    private static final int MAX_RAY = 200;
    private static final double CLOSE_DISTANCE = 0.3;

    private boolean moveUp = false;
    private boolean moveDown = false;
    private boolean moveLeft = false;
    private boolean moveRight = false;

    private List<String> map = ImmutableList.of(
        "2222222222222222222",
        "2000000000000000002",
        "2000000000002222002",
        "2000000200000012002",
        "2000000200000000002",
        "2222001200000200002",
        "2000002200022122002",
        "2000000000000000002",
        "2000002000022222002",
        "2000001000022112002",
        "2000000000000000002",
        "2000000000000000222",
        "2222222222222000002",
        "2000000000000020002",
        "2000000222200020002",
        "2000000000200000002",
        "2220010000222221122",
        "2112000000000000002",
        "2000000000000200002",
        "2000222222222100002",
        "2000000000000211112",
        "2222200222211222222",
        "2100200200000200002",
        "2000200200000200002",
        "2000200200000000002",
        "2000000000000000002",
        "2222222222222002222",
        "2000002000002002002",
        "2000002000002002002",
        "2000000000000000012",
        "2003010103030101012",
        "2222222222222222222"
        );

    private int[][] grid;
    private double mx = 2;
    private double my = 2;
    private double lastX = 0;
    private double angle = Math.PI/2;
    private double dx;
    private double dy;
    private double ndx;
    private double ndy;
    private int gridX;
    private int gridY;
    private final int[] textureX = new int[RAYS];

    private final Color[] opacity = new Color[256];
    private int side;
    private int textureIndex;
    private final int[] rayTextures = new int[RAYS];

    private final BufferedImage[][] textures = new BufferedImage[3][];
    private double deltaTime;

    private Derp3d() throws IOException {
        createRayAngles();
        loadTextures();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();
        GraphicsConfiguration gc = gs[0].getConfigurations()[0];

        for (int i = 0 ; i < 256; i++) {
            opacity[i] = new Color(0,0,0,255-i);
        }

        frame = new JFrame(gc);
        frame.setPreferredSize(new Dimension(1280,H*3+3));
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

    private void createRayAngles() {
        double halfRays = RAYS/2.0;

        for (int ray = 0; ray < RAYS; ray++) {
            RAY_ANGLES[ray] = Math.atan( ( (double)ray - halfRays ) / PROJECTION_DIST );

        }

    }

    private BufferedImage[] generateTexture(BufferedImage src) {
        BufferedImage[] dest = new BufferedImage[src.getWidth()];
        for (int x = 0; x < src.getWidth(); x++) {
            dest[x] = new BufferedImage(1, src.getHeight(), BufferedImage.TYPE_INT_ARGB);
            for (int y = 0; y < src.getHeight(); y++) {
                dest[x].setRGB(0,y, src.getRGB(x,y));
            }
        }
        return dest;
    }

    private void loadTextures() throws IOException {
        textures[0] = generateTexture(ImageIO.read(new File("src/main/resources/dystopia.jpg")));
        textures[1] = generateTexture(ImageIO.read(new File("src/main/resources/rock.png")));
        textures[2] = generateTexture(ImageIO.read(new File("src/main/resources/IMG_5392.jpg")));
    }

    private void createGrid() {
        grid = new int[map.size()][map.get(0).length()];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (j < map.get(i).length()) {
                    grid[i][j] = Integer.parseInt(map.get(i).substring(j,j+1));
                } else {
                    grid[i][j] = 0;
                }
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

        lastX = MouseInfo.getPointerInfo().getLocation().getX();
        long ticks = System.nanoTime();
        deltaTime = 0.0;
        long lastTime = System.currentTimeMillis();
        while (true) {
            do {
                // preparation for rendering ?
                do {
                    Graphics graphics = strategy.getDrawGraphics();
                    graphics.setColor(Color.BLACK);
                    graphics.fillRect(0,0,frame.getWidth(),frame.getHeight());
                    graphics.translate(0, frame.getInsets().top);
                    deltaTime = (double)(System.currentTimeMillis()-lastTime)/1000.0;
                    lastTime = System.currentTimeMillis();
                    draw((Graphics2D)graphics);
                    graphics.dispose();
                } while (strategy.contentsRestored());
                strategy.show();
                long wait = WAIT_PERIOD - (int)(System.nanoTime() - ticks);
                ticks = System.nanoTime();
                if (wait > 1) {
                    waitNs(wait);
                }
            } while (strategy.contentsLost());
        }
    }

    public void draw(Graphics2D g) {
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                g.setColor(grid[y][x] > 0 ? Color.WHITE : Color.BLACK);
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

        g.scale(3,3);
        g.translate(102,H/2);
        g.setColor(Color.GRAY);
        g.fillRect(0,-H/2,W,H/2);
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0,0,W,H/2);

        for (int i = 0; i < RAYS; i++) {
            int v = (int)((double)H / distances[i]);
            //if (v > H-1) {
//                v = H-1;
//            }
            int c = (int)(255-20.0*distances[i]);
            if (c > 255) {
                c = 255;
            }
            if (c < 0) {
                c = 0;
            }
            //Color cl = texture[rayTextures[i]][textureX[i]];
            g.drawImage(textures[rayTextures[i]][textureX[i]], i, -v/2, 1, v, null);
            g.setColor(opacity[c]);
            g.fillRect(i, -v/2, 1, v);
        }
        updateAngle(MouseInfo.getPointerInfo().getLocation());
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

        updateVectors();

    }

    private double getRayAngleFromCentre(int ray) {
        return RAY_ANGLES[ray];
    }

    private void updateAngle(final Point location) {
        double movement = (location.getX() - lastX) * deltaTime * ROTATE_SPEED;
        lastX = location.getX();
        angle += movement;
        if (angle < 0) {
            angle += 2*Math.PI;
        }
        if (angle > 2*Math.PI) {
            angle -= 2*Math.PI;
        }
    }

    private double castRay(double rx, double ry) {

        int mapX = (int)mx;
        int mapY = (int)my;


        double deltaDistX = Math.abs(1.0 / rx);
        double deltaDistY = Math.abs(1.0 / ry);

        int stepX;
        int stepY;
        double sideDistX;
        double sideDistY;

        if (rx < 0) {
            stepX = -1;
            sideDistX = (mx-(double)mapX) * deltaDistX;
        } else {
            stepX = 1;
            sideDistX = ((double)mapX + 1.0 - mx) * deltaDistX;
        }
        if (ry < 0) {
            stepY = -1;
            sideDistY = (my-(double)mapY) * deltaDistY;
        } else {
            stepY = 1;
            sideDistY = ((double)mapY + 1.0 - my) * deltaDistY;
        }

        boolean hit = false;
        int steps = 0;
        side = 0;
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
            distance = (mapX - mx + (double)d)/rx;
        } else {
            int d = (1 - stepY) / 2;
            distance = (mapY - my + (double)d)/ry;
        }

        textureIndex = grid[mapY][mapX] -1;

        return distance;
    }

    private void castRays() {
        for (int i = 0; i < RAYS; i++) {
            double rayAngleFromCentre = getRayAngleFromCentre(i);
            double rayAngle = rayAngleFromCentre + angle;
            rx[i] = Math.cos(rayAngle);
            ry[i] = Math.sin(rayAngle);
            rays[i] =  MAX_RAY;

            double distance = castRay(rx[i], ry[i]);
            rayTextures[i] = textureIndex;
            double correction = Math.cos(Math.abs(rayAngleFromCentre));

            //calculate value of wallX
            double wallX; //where exactly the wall was hit
            if (side == 0) {
                wallX = my + distance * ry[i];
            } else {
                wallX = mx + distance * rx[i];
            }
            wallX -= Math.floor(wallX);

            int textureSize = textures[textureIndex].length;
            //x coordinate on the texture
            textureX[i] = (int)(wallX * (double)textureSize);

            if (side == 0 && rx[i] > 0) {
                textureX[i] = textureSize - textureX[i] - 1;
            }
            if (side == 1 && ry[i] < 0)  {
                textureX[i] = textureSize - textureX[i] - 1;
            }

            rays[i] = distance;

            distances[i] = distance * correction;

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

    private void maybeMove(double dirX, double dirY) {
        double movementX = Math.abs(castRay(dirX, 0));
        double movementY = Math.abs(castRay(0,dirY));
        if (movementX > CLOSE_DISTANCE && movementY > CLOSE_DISTANCE) {
            mx += deltaTime * MOVE_SPEED * dirX;
            my += deltaTime * MOVE_SPEED * dirY;
        }
    }

    private void moveForward() {
        maybeMove(dx,dy);
    }

    private void moveBackward() {
        maybeMove(-dx, -dy);
    }

    private void moveLeft() {
        maybeMove(-ndx, -ndy);
    }

    private void moveRight() {
        maybeMove(ndx, ndy);
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
