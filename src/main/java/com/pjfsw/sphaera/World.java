package com.pjfsw.sphaera;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class World {
    private static final int IMMOVABLE_OBJECTS = 10000;
    private static final int WORLD_SPAN = 1000;
    private final Player player;
    private final List<GameObject> gameObjects = new ArrayList<>();

    public World(Player player) {
        this.player = player;
        gameObjects.add(player);
        createImmovableObjects();
    }


    private void createImmovableObjects() {
        ImmovableObjectFactory factory = new ImmovableObjectFactory();
        for (int i = 0; i < IMMOVABLE_OBJECTS; i++) {
            int x = Random.random(WORLD_SPAN) - WORLD_SPAN/2;
            int y = Random.random(WORLD_SPAN) - WORLD_SPAN/2;
            gameObjects.add(factory.create(x,y));
        }
    }

    public Collection<GameObject> getGameObjects() {
        return gameObjects;
    }

    private void move(int x, int y) {
        player.moveTo(player.x() + x, player.y() + y);
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
