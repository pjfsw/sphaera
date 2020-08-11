package com.pjfsw.cd2048;

import static java.awt.event.KeyEvent.KEY_PRESSED;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.ImmutableList;

public class Game implements Drawable {
    private static final int LINE_HEIGHT = 20;
    private static final int LINE_OFFSET = 100;
    private static final int X = 10;
    private Room room;
    private Level level;
    private final Font font;
    private int selectedItem = 0;
    private final Set<Item> items = new HashSet<>();

    private final Color highlightColor = Color.WHITE;
    private final Color normalColor = Color.LIGHT_GRAY;
    private final Color dialogFrame = Color.WHITE;
    private final Color dialogBackground = Color.DARK_GRAY;
    private final Color dialogColor = Color.LIGHT_GRAY;

    private String message;
    private State messageState;

    private enum State {
        IDLE,
        TAKE,
        DROP,
        USE,
        MESSAGE
    }

    private State state;

    Game() {
        level = new Level();
        room = level.getRoom();
        this.font = new Font("Courier New", Font.PLAIN, 18);
        this.state = State.IDLE;
    }


    private Collection<String> getActions() {
        return ImmutableList.of("North", "East", "South", "West");
    }

    private void print(Graphics2D g, String string, int y) {
        g.drawString(string, X, LINE_OFFSET + y * LINE_HEIGHT);
    }

    @Override
    public void draw(final Graphics2D g) {
        g.setColor(highlightColor);
        g.setFont(font);
        print(g, room.getName(), 0);
        g.setColor(normalColor);
        int y = 2;
        for (String description : room.getDescription()) {
            print(g, description, y);
            y++;
        }

        Collection<Item> selectableItems = room.items();
        if (state == State.DROP) {
            selectableItems = this.items;
        }

        if (!selectableItems.isEmpty()) {
            y++;
            if (state == State.IDLE) {
                print(g, "You see", y);
            } else if (state == State.TAKE) {
                print(g, "Take", y);
            } else if (state == State.DROP) {
                print(g, "Drop", y);
            }
            int index = 0;
            for (Item item : selectableItems) {
                if ((state == State.TAKE || state == State.DROP) && selectedItem == index) {
                    g.setColor(highlightColor);
                } else {
                    g.setColor(normalColor);
                }

                y++;
                print(g, item.getId(), y);
            }
        }

        if (state == State.MESSAGE) {
            g.setColor(dialogBackground);
            g.fillRect(0,100, 800, 100);
            g.setColor(dialogFrame);
            g.drawRect(0, 100, 800,100);
            g.setColor(dialogColor);
            g.drawString(message, 10, 130);
        }
    }

    private void go(Room room) {
        if (room != null) {
            this.room = room;
        }
    }

    private void message(String message, State messageState) {
        this.message = message;
        state = State.MESSAGE;
        this.messageState = messageState;
    }

    private void takeItem() {
        Iterator<Item> it = room.items().iterator();
        Item item = it.next();
        while (it.hasNext() && selectedItem > 0) {
            it.next();
            selectedItem--;
        }
        room.removeItem(item);
        items.add(item);
        message("You put " + item.getId() + " in your pocket!", State.IDLE);
    }

    private void dropItem() {
        Iterator<Item> it = items.iterator();
        Item item = it.next();
        while (it.hasNext() && selectedItem > 0) {
            it.next();
            selectedItem--;
        }
        items.remove(item);
        room.putItem(item);
        message("You dropped " + item.getId() + "!", State.IDLE);
    }

    private boolean handleIdleKeyEvent(KeyEvent keyEvent) {
        if (KeyManager.isEast(keyEvent)) {
            Room room = this.room.east();
            if (room != null) {
                go(room);
            }
            return true;
        } else if (KeyManager.isWest(keyEvent)) {
            Room room = this.room.west();
            if (room != null) {
                go(room);
            }
            return true;
        } else if (KeyManager.isNorth(keyEvent)) {
            Room room = this.room.north();
            if (room != null) {
                go(room);
            }
            return true;
        } else if (KeyManager.isSouth(keyEvent)) {
            Room room = this.room.south();
            if (room != null) {
                go(room);
            }
            return true;
        } else if (KeyManager.isTake(keyEvent)) {
            if (!room.items().isEmpty()) {
                selectedItem = 0;
                state = State.TAKE;
            }
            return true;
        } else if (KeyManager.isDrop(keyEvent)) {
            if (!items.isEmpty()) {
                selectedItem = 0;
                state = State.DROP;
            }
        }
        return false;
    }

    private boolean handleSelectorKeyEvent(KeyEvent keyEvent, Collection<Item> items, Runnable selectionHandler) {
        if (KeyManager.isDown(keyEvent)) {
            selectedItem++;
            if (selectedItem >= items.size()) {
                selectedItem = 0;
            }
            return true;
        } else if (KeyManager.isUp(keyEvent)) {
            selectedItem--;
            if (selectedItem < 0) {
                selectedItem = items.size()-1;
            }
            return true;
        } else if (KeyManager.isCancel(keyEvent)) {
            state = State.IDLE;
            return true;
        } else if (KeyManager.isSelect(keyEvent)) {
            selectionHandler.run();
            return true;
        }
        return false;
    }

    public boolean handleKeyEvent(final KeyEvent keyEvent) {
        if (state == State.IDLE) {
            return handleIdleKeyEvent(keyEvent);
        } else if (state == State.TAKE) {
            return handleSelectorKeyEvent(keyEvent, room.items(), this::takeItem);
        } else if (state == State.DROP) {
            return handleSelectorKeyEvent(keyEvent, items, this::dropItem);
        } else if (state == State.MESSAGE) {
            if (keyEvent.getID() == KEY_PRESSED) {
                state = messageState;
            }
        }
        return false;
    }
}
