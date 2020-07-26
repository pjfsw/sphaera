package com.pjfsw.sphaera;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.KeyEvent;
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

    public World(Player player) {
        this.player = player;
        createImmovableObjects();
        createFoodObjects();
        createDayNightCycle();
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
        dayNightCycle = new Color(0,0,0,darkness);
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
        } while (getObjectAt(x,y) != null || (x == 0 && y == 0));
        return new Point(x,y);
    }


    private void createImmovableObjects() {
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
