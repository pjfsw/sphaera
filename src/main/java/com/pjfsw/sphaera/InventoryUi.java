package com.pjfsw.sphaera;

import static com.pjfsw.sphaera.Tile.TILE_SIZE;
import static com.pjfsw.sphaera.Ui.SCALE;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.Collection;

import com.pjfsw.sphaera.gameobject.GameObject;

public class InventoryUi implements Drawable {
    private final Inventory inventory;
    private final Font font;
    private int selectedIndex;

    public InventoryUi(Inventory inventory) {
        this.inventory = inventory;
        this.selectedIndex = 0;
        font = new Font("Courier New", Font.PLAIN, 18);

    }

    @Override
    public void draw(final Graphics2D g) {
        Collection<GameObject> items = inventory.getItems();
        selectedIndex = inventory.getSelectedIndex();

        g.setColor(Color.WHITE);
        g.setFont(font);
        g.drawString("Inventory", 0, font.getSize());

        int item = 0;
        for (GameObject go : items) {
            int x = (item % 9) * TILE_SIZE * SCALE;
            int y = item / 9 * TILE_SIZE * SCALE + 32;
            g.drawImage(go.image(),
                x, y,
                TILE_SIZE * SCALE, TILE_SIZE * SCALE,
                null
            );
            if (item == selectedIndex) {
                g.drawRect(x,y,TILE_SIZE * SCALE, TILE_SIZE * SCALE);
            }
            item++;
        }
    }
}
