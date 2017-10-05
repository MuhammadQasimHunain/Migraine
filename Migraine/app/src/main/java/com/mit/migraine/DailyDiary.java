package com.mit.migraine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cs on 8/16/2017.
 */

public class DailyDiary implements Serializable {
    int hours;
    int minutes;
    int sleep_quality;
    int stress_level;
    String headache;
    String lurking;
    String migraine_start;
    String migraine_end;
    int severity;
    List<String> symptoms;
    List<String> triggers;
    List<String> helpers;

    public DailyDiary() {
        this.hours = 12;
        this.minutes = 30;
        this.sleep_quality = 0;
        this.stress_level = 0;
        this.headache = "No";
        this.lurking = "No";
        this.migraine_start = "";
        this.migraine_end = "";
        this.severity = 0;
        this.symptoms = new ArrayList<>();
        this.triggers = new ArrayList<>();
        this.helpers = new ArrayList<>();
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getSleep_quality() {
        return sleep_quality;
    }

    public void setSleep_quality(int sleep_quality) {
        this.sleep_quality = sleep_quality;
    }

    public int getStress_level() {
        return stress_level;
    }

    public void setStress_level(int stress_level) {
        this.stress_level = stress_level;
    }

    public String getHeadache() {
        return headache;
    }

    public void setHeadache(String headache) {
        this.headache = headache;
    }

    public String getLurking() {
        return lurking;
    }

    public void setLurking(String lurking) {
        lurking = lurking;
    }

    public String getMigraine_start() {
        return migraine_start;
    }

    public void setMigraine_start(String migraine_start) {
        this.migraine_start = migraine_start;
    }

    public String getMigraine_end() {
        return migraine_end;
    }

    public void setMigraine_end(String migraine_end) {
        this.migraine_end = migraine_end;
    }

    public int getSeverity() {
        return severity;
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }

    public List<String> getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(List<String> symptoms) {
        this.symptoms = symptoms;
    }

    public List<String> getTriggers() {
        return triggers;
    }

    public void setTriggers(List<String> triggers) {
        this.triggers = triggers;
    }

    public List<String> getHelpers() {
        return helpers;
    }

    public void setHelpers(List<String> helpers) {
        this.helpers = helpers;
    }
}
