package com.pjfsw.cd2048;

public class Item {
    private final String id;

    public Item(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return  id.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Item) && (((Item)o).id.equals(id));
    }

}
