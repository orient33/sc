package com.sudoteam.securitycenter.entity;

import android.graphics.drawable.Drawable;

import java.io.InputStream;

/**
 * Created by huayang on 14-10-28.
 */
public class ScanProcess {

    private String label;

    private Drawable icon;

    private int currentAppIndex;

    private int allApps;

    private boolean virus;

    private String usedTime;

    public String getUsedTime() {
        return usedTime;
    }

    public void setUsedTime(String usedTime) {
        this.usedTime = usedTime;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public int getCurrentAppIndex() {
        return currentAppIndex;
    }

    public void setCurrentAppIndex(int currentAppIndex) {
        this.currentAppIndex = currentAppIndex;
    }

    public int getAllApps() {
        return allApps;
    }

    public void setAllApps(int allApps) {
        this.allApps = allApps;
    }

    public boolean isVirus() {
        return virus;
    }

    public void setVirus(boolean virus) {
        this.virus = virus;
    }

    @Override
    public String toString() {
        return "ScanProcess{" +
                "label='" + label + '\'' +
                ", icon=" + icon +
                ", currentAppIndex=" + currentAppIndex +
                ", allApps=" + allApps +
                ", virus=" + virus +
                ", usedTime='" + usedTime + '\'' +
                '}';
    }
}
