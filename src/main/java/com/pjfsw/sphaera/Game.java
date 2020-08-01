package com.pjfsw.sphaera;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.pjfsw.sphaera.gameobject.FoodObject;
import com.pjfsw.sphaera.gameobject.GameObject;
import com.pjfsw.sphaera.gameobject.ImageFactory;
import com.pjfsw.sphaera.gameobject.InventoryObject;
import com.pjfsw.sphaera.npc.Npc;

public class Game {
    private final Player player;
    private final World world;
    private final Inventory inventory;
    private final WorldUi worldUi;
    private final InventoryUi inventoryUi;
    private final HudUi hudUi;
    private GameState state;
    private final GameTime gameTime;
    private int inventoryIndex = 0;

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
        world.nextTurn();
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
            if (inventory.getItemTypes().size() > 0) {
                state = GameState.INVENTORY;
                if (inventoryIndex >= inventory.getItemTypes().size()) {
                    inventoryIndex = inventory.getItemTypes().size()-1;
                }
                inventoryUi.setSelectedIndex(inventoryIndex);
            }
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
                if (Input.isDown(event)) {
                    inventoryIndex = (inventoryIndex + 1) % inventory.getItemTypes().size();
                    inventoryUi.setSelectedIndex(inventoryIndex);
                    return true;
                }
                if (Input.isUp(event)) {
                    inventoryIndex--;
                    if (inventoryIndex < 0) {
                        inventoryIndex = inventory.getItemTypes().size()-1;
                    }
                    inventoryUi.setSelectedIndex(inventoryIndex);
                    return true;
                }
                if (Input.isInventory(event)) {
                    inventoryUi.setSelectedIndex(-1);
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
        g.setColor(Color.DARK_GRAY);
        g.drawRect(0,0, WorldUi.W+2, WorldUi.H+2);
        g.translate(1,1);
        worldUi.draw(g);
        int inventoryPos = WorldUi.W + 2;
        g.translate(inventoryPos, 0);
        inventoryUi.draw(g);
        g.translate(-inventoryPos, WorldUi.H+1);
        hudUi.draw(g);
    }
}
