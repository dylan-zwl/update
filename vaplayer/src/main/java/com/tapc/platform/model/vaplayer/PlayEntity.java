package com.tapc.platform.model.vaplayer;

import java.io.Serializable;
import java.util.List;

public class PlayEntity implements Serializable {
    private static final long serialVersionUID = 4553110282150816252L;
    public String name;
    public String description;
    public String location;
    public String version;
    public String still;
    public String uniqueid;
    public List<String> evtList;
    public String path;

    private double gradient;

    public double getGradient() {
        return gradient;
    }

    public void setGradient(double gradient) {
        this.gradient = gradient;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
