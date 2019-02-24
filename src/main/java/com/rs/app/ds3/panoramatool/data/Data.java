package com.rs.app.ds3.panoramatool.data;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class Data {

    private static Gson gson     = new Gson();
    private static File dataFile = new File("data.json");
    private static Data instance = new Data();

    public static Data instance() {
        return instance;
    }

    private static class Pack {
        private int       mapId;
        private int       pointId;
        private List<Map> maps;

        private void validate() {
            if (maps == null) maps = new ArrayList<>();
            mapId = 0;
            pointId = 0;
            for (Map map : maps) {
                mapId = Math.max(mapId, map.id);
                if (map.points == null) map.points = new ArrayList<>();
                for (Point point : map.points) {
                    point.mapId = map.id;
                    pointId = Math.max(pointId, point.id);
                }
            }
            mapId++;
            pointId++;
        }

        private Map newMap(String name) {
            Map map = new Map(mapId++, name, new ArrayList<>());
            this.maps.add(map);
            return map;
        }
        private boolean removeMap(Map map) {
            return maps.remove(map);
        }
        private Point newPoint(Map map, double x, double y, double z) {
            Point point = new Point(pointId++, map.id, x, y, z);
            map.points.add(point);
            return point;
        }
        private boolean removePoint(Map map, Point point) {
            return map.points.remove(point);
        }
    }

    private Pack pack = new Pack();

    public boolean load() {
        if (dataFile.exists()) {
            try {
                byte[] bytes = Files.readAllBytes(dataFile.toPath());
                String json = new String(bytes, StandardCharsets.UTF_8);
                Pack pack = gson.fromJson(json, Pack.class);
                if (pack != null) {
                    this.pack = pack;
                    this.pack.validate();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            this.pack = new Pack();
            this.pack.validate();
        }
        return true;
    }

    public boolean save() {
        String json = gson.toJson(this.pack);
        try {
            Files.write(dataFile.toPath(), json.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public Map addMap(String name) {
        return pack.newMap(name);
    }
    public boolean removeMap(int id) {
        return removeMap(findMap(id));
    }
    public boolean removeMap(String name) {
        return removeMap(findMap(name));
    }
    public boolean removeMap(Map map) {
        return pack.removeMap(map);
    }

    public Map findMap(int id) {
        for (Map map : pack.maps) {
            if (map.id == id) {
                return map;
            }
        }
        return null;
    }
    public Map findMap(String name) {
        for (Map map : pack.maps) {
            if (map.name.equals(name)) {
                return map;
            }
        }
        return null;
    }

    public Point newPoint(Map map, double x, double y, double z) {
        return pack.newPoint(map, x, y, z);
    }

    public List<Map> getMapAll() {
        return pack.maps;
    }

}
