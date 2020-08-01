package com.pjfsw.sphaera;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class HudUi {
    private final Player player;
    private final GameTime gameTime;
    private int highlightEnergy = 0;
    private int lastPlayerEnergy;
    private final Font font;

    private static final int HUD_OFFSET = 2;
    private static final int HUD_HEIGHT = 10;
    private static final int HUD_WIDTH = 50;

    public HudUi(Player player, GameTime gameTime) {
        this.player = player;
        this.gameTime = gameTime;
        font = new Font("Courier New", Font.PLAIN, 7);
    }

    private void drawGauge(Graphics2D g, int value, int max, String str, int xpos, int highlight) {
        g.setColor(Color.DARK_GRAY);
        g.drawRect(HUD_OFFSET + xpos, 0, HUD_WIDTH, HUD_HEIGHT);
        if (highlight < 0) {
            g.setColor(Color.RED);
        } else if (highlight > 0) {
            g.setColor(Color.GREEN);
        }
        g.fillRect(HUD_OFFSET + xpos,  0, value * HUD_WIDTH / max, HUD_HEIGHT);
        g.setColor(Color.WHITE);
        g.drawString(str, 2+HUD_OFFSET+xpos,  font.getSize());
    }

    private void drawZeroGauge(Graphics2D g, int value, int max, String str, int xpos) {
        int percent = value * 50 / max;
        g.setColor(Color.DARK_GRAY);
        g.drawRect(HUD_OFFSET + xpos, 0, HUD_WIDTH, HUD_HEIGHT);
        if (value < 0) {
            g.fillRect(HUD_OFFSET + xpos + HUD_WIDTH/2 + percent, 0, -percent, HUD_HEIGHT);
        } else {
            g.fillRect(HUD_OFFSET + xpos + HUD_WIDTH/2, 0, percent, HUD_HEIGHT);
        }
        g.setColor(Color.WHITE);
        g.drawLine(HUD_OFFSET + xpos + HUD_WIDTH/2, 0, HUD_OFFSET + xpos + HUD_WIDTH/2, HUD_HEIGHT-1);
        g.drawString(str, 2+HUD_OFFSET+xpos,  font.getSize());
    }

    private void drawHud(Graphics2D g) {
        g.setFont(font);
        drawGauge(g, gameTime.getTime(), GameTime.TIME_PER_DAY, String.format("Day %d", gameTime.getDay()), 0, 0);
        drawGauge(g, player.getLife(), Player.LIFE_MAX, "Life", HUD_WIDTH+5,0 );
        drawGauge(g, player.getEnergy(), Player.ENERGY_MAX, "Energy", 2*(HUD_WIDTH+5), highlightEnergy);
        drawZeroGauge(g, player.getHappiness(), Player.HAPPINESS_MAX, "Happiness", 3*(HUD_WIDTH+5));
    }


    public void draw(Graphics2D g) {
        if (player.getEnergy() > lastPlayerEnergy) {
            highlightEnergy = 60;
        }
        lastPlayerEnergy = player.getEnergy();
        drawHud(g);
        if (highlightEnergy > 0) {
            highlightEnergy--;
        }
        if (highlightEnergy < 0) {
            highlightEnergy++;
        }

    }
}
