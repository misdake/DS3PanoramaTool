package com.rs.app.ds3.panoramatool.process;

public class Command {

    public interface Method {
        String execute(String ...params) throws Exception;
    }

    public final String name;
    public final Method method;

    public Command(String name, Method method) {
        this.name = name;
        this.method = method;
    }

}
