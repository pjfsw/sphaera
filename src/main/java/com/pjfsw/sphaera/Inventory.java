package com.pjfsw.sphaera;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.pjfsw.sphaera.gameobject.GameObject;

public class Inventory {
    private final List<GameObject> inventory;
    private int selectedItem;

    public Inventory() {
        inventory = new ArrayList<>();
        selectedItem = 0;
    }

    public void add(GameObject gameObject) {
        inventory.add(gameObject);
    }

    public Collection<GameObject> getItems() {
        return inventory;
    }

    public int getSelectedIndex() {
        return selectedItem;
    }

    public GameObject getSelectedObject() {
        if (selectedItem < 0 || selectedItem >= inventory.size()) {
            return null;
        }
        return inventory.get(selectedItem);
    }

    public GameObject removeSelectedObject() {
        if (selectedItem < 0 || selectedItem >= inventory.size()) {
            return null;
        }
        GameObject go = inventory.remove(selectedItem);
        if (selectedItem >= inventory.size()) {
            selectedItem = inventory.size() - 1;
        }
        return go;
    }

    public boolean handleKeyEvent(KeyEvent event) {
        if (Input.isLeft(event)) {
            int newSelectedItem = selectedItem - 1;
            if (newSelectedItem < 0) {
                newSelectedItem = inventory.size() - 1;
            }
            selectedItem = newSelectedItem;
            return true;
        }
        if (Input.isRight(event)) {
            int newSelectedItem = selectedItem + 1;
            if (newSelectedItem >= inventory.size()) {
                newSelectedItem = 0;
            }
            selectedItem = newSelectedItem;
            return true;
        }
        return false;
    }
}
