package com.pjfsw.cd2048;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ImmutableList;

public class Room {
    private final String name;
    private final Collection<String> description;
    private Room north;
    private Room south;
    private Room west;
    private Room east;
    private final Set<Item> items;

    public Room(String name, Collection<String> description) {
        this.items = new HashSet<>();
        this.name = name;
        this.description = description;
    }

    public Room() {
        this("A room", ImmutableList.of("You are in a room. You see nothing special."));
    }

    public void putItem(Item item) {
        this.items.add(item);
    }

    public boolean removeItem(Item item) {
        return this.items.remove(item);
    }

    public Collection<Item> items() {
        return this.items;
    }

    public void setWest(Room room) {
        this.west = room;
    }

    public void setEast(Room room) {
        this.east = room;
    }

    public void setNorth(Room room) {
        this.north = room;
    }

    public void setSouth(Room room) {
        this.south = room;
    }

    public String getName() {
        return name;
    }

    public Collection<String> getDescription() {
        return description;
    }

    public Room north() {
        return north;
    }

    public Room south() {
        return south;
    }

    public Room east() {
        return east;
    }

    public Room west() {
        return west;
    }

}
