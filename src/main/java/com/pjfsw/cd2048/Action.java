package com.pjfsw.cd2048;

public enum Action {
    NORTH("North"),
    SOUTH("South"),
    EAST("East"),
    WEST("West");

    private final String text;

    Action(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
