package com.pjfsw.cd2048;

import com.google.common.collect.ImmutableList;

public class Level {
    private Room room;

    Level() {
        Room bar = new Room("The bar",
            ImmutableList.of("You are at the bar. You see a bartender looking suspiciously at you."));
        bar.putItem(new Item("A keycard"));
        room = new Room();
        room.setNorth(bar);
        bar.setSouth(room);
    }

    Room getRoom() {
        return room;
    }

}
