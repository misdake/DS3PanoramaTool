package com.rs.app.ds3.panoramatool;

import com.melloware.jintellitype.JIntellitype;
import com.rs.app.ds3.panoramatool.data.Data;
import com.rs.app.ds3.panoramatool.data.Map;
import com.rs.app.ds3.panoramatool.data.Point;
import com.rs.app.ds3.panoramatool.process.Command;
import com.rs.app.ds3.panoramatool.process.CommandCenter;
import com.rs.app.ds3.panoramatool.process.Processor;

import java.io.File;
import java.util.List;

public class App {

    public static void main(String[] args) {
        App app = new App();
        app.prepare();
        app.start();
        app.finish();
    }

    private final CommandCenter cmd;
    private final Data          data;
    private final Processor     processor;
    private final State         state;

    private static class State {
        private Map currentMap = null;
    }

    private App() {
        cmd = new CommandCenter();
        data = new Data();
        processor = new Processor(
                new File("C:\\Software\\Fraps\\Screenshots"),
                new File("C:\\Project_git\\DS3PanoramaViewer\\data"));
        state = new State();
    }

    private void prepare() {
        data.load();

        cmd.register(new Command("listmap", params -> {
            System.out.println("Maps:");
            List<Map> maps = data.getMapAll();
            for (Map map : maps) {
                System.out.printf("  id:%2d  name:%s\n", map.id, map.name);
            }
            return null;
        }));

        cmd.register(new Command("listpoint", params -> {
            Map map;
            if (state.currentMap != null && params.length == 0) {
                map = state.currentMap;
            } else if (params.length != 1) {
                return "listpoint MAP_ID";
            } else {
                map = data.findMap(Integer.parseInt(params[0]));
            }
            if (map == null) return "cannot find map.";
            System.out.println("Map: " + mapString(map));
            System.out.println("Points: ");
            for (Point point : map.points) {
                System.out.println("  " + pointString(point));
            }
            return null;
        }));

        cmd.register(new Command("addmap", params -> {
            if (params.length != 1) return "newmap MAP_NAME";
            Map map = data.addMap(params[0]);
            return "done. " + mapString(map);
        }));

        cmd.register(new Command("removemap", params -> {
            //TODO check currentMap
            if (params.length != 1) return "removemap MAP_ID";
            boolean succeed = data.removeMap(Integer.parseInt(params[0]));
            return succeed ? "removed" : "error";
        }));

        cmd.register(new Command("selectmap", params -> {
            if (params.length != 1) return "listpoint MAP_ID";
            Map map = data.findMap(Integer.parseInt(params[0]));
            if (map == null) return "cannot find map.";
            state.currentMap = map;
            cmd.setPrompt("Command: (current map: " + mapString(state.currentMap) + ")");
            return null;
        }));

        cmd.register(new Command("addpoint", params -> {
            double[] xyz = processor.readClipboardXYZ();
            if (state.currentMap == null) {
                return "execute selectmap first";
            }
            Point point = data.newPoint(state.currentMap, xyz[0], xyz[1], xyz[2]);
            processor.process(point);
            return "done.";
        }));

        cmd.register(new Command("save", params -> {
            boolean succeed = data.save();
            return succeed ? "saved" : "error";
        }));

        cmd.register(new Command("exit", params -> {
            return "exit_commandcenter";
        }));

        JIntellitype.getInstance().registerHotKey(1, "minus");
        JIntellitype.getInstance().registerHotKey(2, "equals");
        JIntellitype.getInstance().addHotKeyListener(bindingIndex -> {
            switch (bindingIndex) {
                case 1:
                case 2:
                    cmd.execute("addpoint");
                    break;
            }
        });
    }
    private String mapString(Map map) {
        return String.format("id:%2d  name:%s", map.id, map.name);
    }
    private String pointString(Point point) {
        return String.format("id:%2d  (%.0f, %.0f, %.0f)", point.id, point.x, point.y, point.z);
    }

    private void start() {
        cmd.start();
    }

    private void finish() {
        data.save();
    }


}
