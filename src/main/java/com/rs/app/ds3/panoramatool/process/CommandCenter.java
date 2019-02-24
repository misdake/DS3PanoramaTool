package com.rs.app.ds3.panoramatool.process;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CommandCenter {

    private String prompt;
    public CommandCenter() {

    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    private boolean exit = false;
    public void start() {
        Scanner sc = new Scanner(System.in);
        prompt = "Command:";
        System.out.println(prompt);
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            execute(line);
            if (exit) break;
            System.out.println(prompt);
        }
    }
    public void execute(String line) {
        String[] split = line.split(" ");
        if (split.length > 0) {
            String command = split[0];
            Command c = commands.get(command);
            String[] params = Arrays.copyOfRange(split, 1, split.length);
            if (c != null) {
                String r = null;
                try {
                    r = c.method.execute(params);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if ("exit_commandcenter".equals(r)) {
                    exit = true;
                    return;
                }
                if (r != null && !r.isEmpty()) System.out.println(r);
            }
        }
    }

    private Map<String, Command> commands = new HashMap<>();

    public void register(Command command) {
        commands.put(command.name, command);
    }

}
