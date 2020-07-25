package com.pjfsw.sphaera;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import com.pjfsw.sphaera.gameobject.FoodObjectFactory;
import com.pjfsw.sphaera.gameobject.GameObject;
import com.pjfsw.sphaera.gameobject.ImmovableObjectFactory;

public class World {
    private static final int IMMOVABLE_OBJECTS = 100000;
    private static final int FOOD_OBJECTS = 1000;
    private static final int WORLD_SPAN = 1000;
    public static final int TIME_PER_DAY = 500;
    private final Player player;
    private final Map<Integer, Map<Integer, GameObject>> gameObjects = new HashMap<>();
    private int day = 1;
    private int time = 0;

    public World(Player player) {
        this.player = player;
        createImmovableObjects();
        createFoodObjects();
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

    private void move(int x, int y) {
        int newX = player.x() + x;
        int newY = player.y() + y;
        if (getObjectAt(newX, newY) == null) {
            player.moveTo(player.x() + x, player.y() + y);
            player.consumeEnergy(1);
            nextTurn();
        }
    }

    public int getDay() {
        return day;
    }

    public int getTime() {
        return time;
    }

    public void moveWest() {
        move(-1,0);
    }

    public void moveEast() {
        move(1,0);
    }

    public void moveNorth() {
        move(0,-1);
    }

    public void moveSouth() {
        move(0,1);
    }
}
