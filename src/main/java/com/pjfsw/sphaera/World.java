package com.pjfsw.sphaera;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import com.pjfsw.sphaera.gameobject.FoodObject;
import com.pjfsw.sphaera.gameobject.GameObject;
import com.pjfsw.sphaera.gameobject.ImageFactory;
import com.pjfsw.sphaera.gameobject.ImmovableObject;
import com.pjfsw.sphaera.gameobject.RockObject;

public class World {
    private static final int IMMOVABLE_OBJECTS = 50000;
    private static final int FOOD_OBJECTS = 5000;
    private static final int ROCKS = 1000;
    private static final int WORLD_SPAN = 1000;
    private final Map<Integer, Map<Integer, GameObject>> gameObjects = new HashMap<>();
    private final ImageFactory imageFactory;
    private final int[][] terrain;

    public World(ImageFactory imageFactory) {
        this.imageFactory = imageFactory;

        terrain = new int[WORLD_SPAN*2][WORLD_SPAN*2];
        generateTerrain();
        createImmovableObjects();
        createFoodObjects();
        createRocks();
    }

    private void generateTerrain() {
        double threshold = 0.51;
        for (int x = 0; x < WORLD_SPAN * 2; x++) {
            for (int y = 0; y < WORLD_SPAN *2; y++) {
                terrain[x][y] = (Math.random() > threshold) ? 1 : 0;
            }
        }
        // Smooth terrain
        for (int i = 0; i < 6; i++) {
            for (int x = 1; x < WORLD_SPAN * 2-1; x++) {
                for (int y = 1; y < WORLD_SPAN * 2-1; y++) {
                    int neighborCount = getNeighborCount(x,y);
                    if (neighborCount > 4) {
                        terrain[x][y] = 1;
                    } else if (neighborCount < 4) {
                        terrain[x][y] = 0;
                    }
                }
            }
        }
        int x = 0;
        while (terrain[WORLD_SPAN+x][WORLD_SPAN] != 0) {
            terrain[WORLD_SPAN+x][WORLD_SPAN] = 0;
            x++;
        }
    }

    private int getNeighborCount(int x, int y) {
        int count = 0;
        for (int nx = x-1; nx <= x+1; nx++) {
            for (int ny = y-1; ny <= y+1; ny++) {
                if (nx != x || ny != y) {
                    count += terrain[nx][ny];
                }
            }
        }
        return count;
    }

    public int getTerrainAt(int x, int y) {
        return terrain[x+WORLD_SPAN][y+WORLD_SPAN];
    }

    Point findEmptySpot() {
        int x;
        int y;
        do {
            x = Random.random(WORLD_SPAN) - WORLD_SPAN/2;
            y = Random.random(WORLD_SPAN) - WORLD_SPAN/2;
        } while (getTerrainAt(x,y) !=0 || getObjectAt(x,y) != null || (x == 0 && y == 0));
        return new Point(x,y);
    }


    private void createImmovableObjects() {
        for (int i = 0; i < IMMOVABLE_OBJECTS; i++) {
            Point spot = findEmptySpot();
            addObject(new ImmovableObject(imageFactory), spot.x, spot.y);
        }
    }

    private void createFoodObjects() {
        for (int i = 0; i < FOOD_OBJECTS; i++) {
            Point spot = findEmptySpot();
            addObject(new FoodObject(imageFactory, 100), spot.x, spot.y);
        }
    }

    private void createRocks() {
        for (int i = 0; i < ROCKS; i++) {
            Point spot = findEmptySpot();
            addObject(new RockObject(imageFactory), spot.x, spot.y);
        }
    }

    public void addObject(GameObject go, int x, int y) {
        gameObjects
            .computeIfAbsent(x, (xcoord) -> new HashMap<>())
            .put(y, go);
    }

    public GameObject getObjectAt(int x, int y) {
        if (gameObjects.containsKey(x) && gameObjects.get(x).containsKey(y)) {
            return gameObjects.get(x).get(y);
        }
        return null;
    }

    public void removeObject(final int newX, final int newY) {
        gameObjects.get(newX).remove(newY);
    }
}
