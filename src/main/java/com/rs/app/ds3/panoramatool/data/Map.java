package com.rs.app.ds3.panoramatool.data;

import java.util.List;

public class Map {

    public int         id;
    public String      name;
    public List<Point> points;

    public Map() {
    }
    public Map(int id, String name, List<Point> points) {
        this.id = id;
        this.name = name;
        this.points = points;
    }

}
