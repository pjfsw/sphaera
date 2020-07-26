package com.pjfsw.sphaera;

import java.awt.Color;
import java.awt.Point;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.pjfsw.sphaera.gameobject.FoodObjectFactory;
import com.pjfsw.sphaera.gameobject.GameObject;
import com.pjfsw.sphaera.gameobject.ImmovableObjectFactory;

public class World {
    private static final int IMMOVABLE_OBJECTS = 50000;
    private static final int FOOD_OBJECTS = 5000;
    private static final int WORLD_SPAN = 1000;
    public static final int TIME_PER_DAY = 500;
    private final Player player;
    private final Map<Integer, Map<Integer, GameObject>> gameObjects = new HashMap<>();
    private int day = 1;
    private int time = 0;
    private Color dayNightCycle;
    private final int[][] terrain;

    public World(Player player) throws IOException {
        this.player = player;
        terrain = new int[WORLD_SPAN*2][WORLD_SPAN*2];
        generateTerrain();
        createImmovableObjects();
        createFoodObjects();
        createDayNightCycle();
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

    private void createDayNightCycle() {
        int scale = 400;
        int darkness =  (int)(scale * Math.cos(2 * getTime() * Math.PI / World.TIME_PER_DAY)) - scale/2;
        if (darkness < 0) {
            darkness = 0;
        }
        if (darkness > 200) {
            darkness = 200;
        }
        //dayNightCycle = new Color(0,0,0,darkness);
        dayNightCycle = new Color(0,0,0,0);
    }

    public Color getDayNightCycle() {
        return dayNightCycle;
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


    private void createImmovableObjects() throws IOException {
        ImmovableObjectFactory factory = new ImmovableObjectFactory();
        for (int i = 0; i < IMMOVABLE_OBJECTS; i++) {
            Point spot = findEmptySpot();
            addObject(factory.create(), spot.x, spot.y);
        }
    }

    private void createFoodObjects() {
        FoodObjectFactory factory = new FoodObjectFactory();
        for (int i = 0; i < FOOD_OBJECTS; i++) {
            Point spot = findEmptySpot();
            addObject(factory.create(), spot.x, spot.y);
        }
    }


    public void nextTurn() {
        time++;
        if (time >= TIME_PER_DAY) {
            time = 0;
            day++;
        }
        createDayNightCycle();
    }
    public void addObject(GameObject go, int x, int y) {
        gameObjects
            .computeIfAbsent(x, (xcoord) -> new HashMap<>())
            .put(y, go);
    }


//    public Collection<GameObject> getGameObjects() {
//        return gameObjects.values().stream()
//            .flatMap(map -> map.values().stream())
//            .collect(Collectors.toList());
//    }

    public GameObject getObjectAt(int x, int y) {
        if (gameObjects.containsKey(x) && gameObjects.get(x).containsKey(y)) {
            return gameObjects.get(x).get(y);
        }
        return null;
    }

    public int getDay() {
        return day;
    }

    public int getTime() {
        return time;
    }

    public void removeObject(final int newX, final int newY) {
        gameObjects.get(newX).remove(newY);
    }
}
