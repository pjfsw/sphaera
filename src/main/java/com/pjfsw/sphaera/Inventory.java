package com.pjfsw.sphaera;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.pjfsw.sphaera.gameobject.GameObject;

public class Inventory {
    private final Multimap<String,GameObject> inventory;

    public Inventory() {
        inventory = ArrayListMultimap.create();
    }

    public void add(GameObject gameObject) {
        inventory.put(gameObject.getName(), gameObject);
    }

    public Collection<String> getItemTypes() {
        return inventory.keySet();
    }

    public Collection<GameObject> getItems(String type) {
        return inventory.get(type);

    }
}
