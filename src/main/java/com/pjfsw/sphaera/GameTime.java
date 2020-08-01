package com.pjfsw.sphaera;

import java.awt.Color;

public class GameTime {
    public static final int TIME_PER_DAY = 500;

    private int day = 1;
    private int time = 0;

    public int getTime() {
        return time;
    }

    public int getDay() {
        return day;
    }

    public void advanceTime() {
        time++;
        if (time >= TIME_PER_DAY) {
            time = 0;
            day++;
        }
    }



}
