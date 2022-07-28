package br.com.pdfreport.entidades;

import br.com.pdfreport.enuns.Location;

public class ItemTable {


    private String title;
    private Location location;

    public ItemTable(String title) {
        this.title = title;
        this.location = Location.LEFT;
    }

    public ItemTable(String title, Location location) {
        this.title = title;
        this.location = location;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
