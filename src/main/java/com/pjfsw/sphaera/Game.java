package com.pjfsw.sphaera;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.pjfsw.sphaera.gameobject.FoodObject;
import com.pjfsw.sphaera.gameobject.GameObject;
import com.pjfsw.sphaera.gameobject.InventoryObject;

public class Game {
    private final Player player;
    private final World world;
    private final Inventory inventory;
    private GameState state;
    private final Map<GameState, Drawable> drawables;

    public Game() throws IOException {
        state = GameState.IN_GAME;
        player = new Player();
        world = new World(player);
        inventory = new Inventory();

        drawables = ImmutableMap.<GameState, Drawable>builder()
            .put(GameState.IN_GAME, new WorldUi(player, world))
            .put(GameState.INVENTORY, new InventoryUi(inventory))
            .build();
    }

    public Drawable getDrawable() {
        return drawables.get(state);
    }

    private void move(int x, int y) {
        int newX = player.x() + x;
        int newY = player.y() + y;
        if (world.getTerrainAt(newX, newY) > 0) {
            return;
        }
        GameObject object = world.getObjectAt(newX, newY);
        if (object == null) {
            player.moveTo(player.x() + x, player.y() + y);
            player.consumeEnergy(1);
            world.nextTurn();
        } else if (object instanceof InventoryObject) {
            inventory.add(object);
            world.removeObject(newX, newY);
            player.moveTo(player.x() + x, player.y() +y);

            world.nextTurn();
        }
    }


    private void moveWest() {
        move(-1,0);
    }

    private void moveEast() {
        move(1,0);
    }

    private void moveNorth() {
        move(0,-1);
    }

    private void moveSouth() {
        move(0,1);
    }

    private boolean handleInGameKeyEvent(KeyEvent event) {
        if (Input.isUp(event)) {
            moveNorth();
            return true;
        } else if (Input.isDown(event)) {
            moveSouth();
            return true;
        } else if (Input.isLeft(event)) {
            moveWest();
            return true;
        } else if (Input.isRight(event)) {
            moveEast();
            return true;
        } else if (Input.isInventory(event)) {
            state = GameState.INVENTORY;
            return true;
        }
        return false;
    }

    public void consume(GameObject go) {
        if (go instanceof FoodObject) {
            FoodObject food = (FoodObject)go;
            player.increaseEnergy(food.getEnergy());
        }
    }

    public boolean handleKeyEvent(KeyEvent event) {
        if (event.getID() != KeyEvent.KEY_PRESSED) {
            return false;
        }

        switch (state) {
            case IN_GAME:
                return handleInGameKeyEvent(event);
            case INVENTORY:
                if (inventory.handleKeyEvent(event)) {
                    return true;
                }
                if (Input.isInventory(event)) {
                    state = GameState.IN_GAME;
                    return true;
                }
                if (Input.isConsume(event)) {
                    GameObject go = inventory.getSelectedObject();
                    if (go instanceof InventoryObject && ((InventoryObject)go).getEnergy() > 0) {
                        consume(inventory.removeSelectedObject());
                        state = GameState.IN_GAME;
                        return true;
                    }

                }
                break;
        }
        return false;
    }
}
