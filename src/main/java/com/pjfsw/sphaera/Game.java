package com.pjfsw.sphaera;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.io.IOException;
import com.pjfsw.sphaera.gameobject.FoodObject;
import com.pjfsw.sphaera.gameobject.GameObject;
import com.pjfsw.sphaera.gameobject.ImageFactory;
import com.pjfsw.sphaera.gameobject.InventoryObject;

public class Game {
    private final Player player;
    private final World world;
    private final Inventory inventory;
    private final WorldUi worldUi;
    private final InventoryUi inventoryUi;
    private final HudUi hudUi;
    private GameState state;
    private final GameTime gameTime;

    public Game() throws IOException {
        state = GameState.IN_GAME;
        player = new Player();
        ImageFactory imageFactory = new ImageFactory();
        world = new World(imageFactory);
        worldUi = new WorldUi(player, world);
        inventory = new Inventory();
        inventoryUi = new InventoryUi(inventory);
        gameTime = new GameTime();
        hudUi = new HudUi(player, gameTime);
    }


    private void nextTurn() {
        gameTime.advanceTime();
        worldUi.createDayNightCycle(gameTime.getTime());
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
            nextTurn();
        } else if (object instanceof InventoryObject) {
            inventory.add(object);
            world.removeObject(newX, newY);
            player.moveTo(player.x() + x, player.y() +y);
            nextTurn();
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
                /*if (inventory.handleKeyEvent(event)) {
                    return true;
                }*/
                if (Input.isInventory(event)) {
                    state = GameState.IN_GAME;
                    return true;
                }
                /*if (Input.isConsume(event)) {
                    GameObject go = inventory.getSelectedObject();
                    if (go instanceof InventoryObject && ((InventoryObject)go).getEnergy() > 0) {
                        consume(inventory.removeSelectedObject());
                        state = GameState.IN_GAME;
                        return true;
                    }

                }*/
                break;
        }
        return false;
    }

    public void draw(Graphics2D g) {
        g.scale(2,2);
        worldUi.draw(g);
        g.translate(WorldUi.W, 0);
        inventoryUi.draw(g);
        g.translate(-WorldUi.W, WorldUi.H+1);
        hudUi.draw(g);
    }
}
