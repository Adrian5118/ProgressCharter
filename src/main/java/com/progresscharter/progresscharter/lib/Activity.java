package com.progresscharter.progresscharter.lib;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class Activity {
    private String name;
    private String description;

    private LocalDate startDate;
    private LocalDate endDate;

    private Activity parentActivity;
    private ArrayList<Activity> childActivities;
    private HashMap<Activity, Dependency> dependencies;

    public Activity(String name, String description) {
        this.name = name;
        this.description = description;

        startDate = LocalDate.now();
        endDate = startDate.plusDays(1);

        parentActivity = null;
        childActivities = new ArrayList<>();
        dependencies = new HashMap<>();
    }


    enum Dependency {
        FINISH_TO_START,
        START_TO_START,
        FINISH_TO_FINISH,
        START_TO_FINISH
    }
}
