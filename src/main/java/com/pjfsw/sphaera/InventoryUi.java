package com.pjfsw.sphaera;

import static com.pjfsw.sphaera.Tile.TILE_SIZE;

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
        font = new Font("Courier New", Font.PLAIN, 9);

    }

    @Override
    public void draw(final Graphics2D g) {
        Collection<String> itemTypes = inventory.getItemTypes();
        //selectedIndex = inventory.getSelectedIndex();

        g.setColor(Color.WHITE);
        g.setFont(font);
        g.drawString("Inventory", 0, font.getSize());

        int item = 0;
        for (String itemType : itemTypes) {
            Collection<GameObject> items = inventory.getItems(itemType);
            if (items.size() > 0) {
                GameObject go = items.iterator().next();
                int x = 0;
                int y = item * TILE_SIZE + 32;
                g.drawImage(go.image(),
                    x, y,
                    TILE_SIZE, TILE_SIZE,
                    null
                );
                g.drawString(String.valueOf(items.size()), x + TILE_SIZE, y + TILE_SIZE);

                if (item == selectedIndex) {
                    g.drawRect(x,y,TILE_SIZE, TILE_SIZE);
                }
                item++;
            }
        }
    }
}
