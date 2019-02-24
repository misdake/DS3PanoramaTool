package com.rs.app.ds3.panoramatool.data;

public class Point {

    public int    id;
    public int    mapId;
    public double x;
    public double y;
    public double z;

    public Point() {
    }
    public Point(int id, int mapId, double x, double y, double z) {
        this.id = id;
        this.mapId = mapId;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static final String[] TEXTURE_NAMES = {
            "px.jpg",
            "nx.jpg",
            "pz.jpg",
            "nz.jpg",
            "py.jpg",
            "ny.jpg",
    };

}
