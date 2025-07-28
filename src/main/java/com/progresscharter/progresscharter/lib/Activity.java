package com.progresscharter.progresscharter.lib;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class Activity implements JSONSerializable {
    private String name;
    private String description;
    private long cost = 0;
    private boolean isFinished = false;

    private LocalDate startDate;
    private LocalDate endDate;

    private Activity parentActivity;
    private final HashMap<String, Activity> childActivities;
    private final HashMap<String, Dependency> dependencies;

    public Activity(String name, String description) {
        this.name = name;
        this.description = description;

        startDate = LocalDate.now();
        endDate = startDate.plusDays(1);

        parentActivity = null;
        childActivities = new HashMap<>();
        dependencies = new HashMap<>();
    }

    public Activity(JSONObject object) throws JSONException {
        this("", "");
        fromJSONObject(object);
    }

    // Parent and child activity handling

    /**
     * Add parent activity. This will put current activity to parent's child activities
     * @param parentActivity becomes parent of this activity
     */
    public void addParentActivity(Activity parentActivity) {
        this.parentActivity = parentActivity;
        parentActivity.childActivities.put(name, this);
    }

    /**
     * Remove parent activity. This will remove current activity from parent's child activities, while clearing current activity's dependencies.
     */
    public void removeParentActivity() {
        parentActivity.childActivities.remove(this.name);
        this.parentActivity = null;
        dependencies.clear(); // Activity dependencies is dependent on if activity has parent
    }

    /**
     * Add child activity. This will set parent activity of child activity to this.
     * @param childActivity becomes child of this activity
     */
    public void addChildActivity(Activity childActivity) {
        childActivities.put(childActivity.name, childActivity);
        childActivity.parentActivity = this;
    }

    /**
     * Remove child activity. This will remove parent activity from child activity, while clearing child activity's dependencies.
     * @param childActivity gets removed as child
     */
    public void removeChildActivity(Activity childActivity) {
        childActivities.remove(childActivity.name);
        childActivity.parentActivity = null;
        childActivity.dependencies.clear(); // Activity dependencies is dependent on if activity has parent
    }

    /**
     * Remove child activity. This will remove parent activity from child activity, while clearing child activity's dependencies.
     * @param activityName name of activity to be removed as child
     */
    public void removeChildActivity(String activityName) {
        childActivities.get(activityName).parentActivity = null;
        childActivities.get(activityName).dependencies.clear();
        childActivities.remove(activityName);
    }

    public boolean contains(Activity childActivity) {
        return childActivities.containsKey(childActivity.getName());
    }

    public int activitiesCount() {
        return childActivities.size();
    }

    // Dependency handling with parent's family

    /**
     * Add dependency to fellow activity.
     * @param fellowActivity activity that has same parent as current activity
     * @param mode dependency mode
     */
    public void addDependency(Activity fellowActivity, Dependency mode) {
        if(fellowActivity == this) {
            throw new RuntimeException("Fellow activity cannot be itself.");
        }

        if(parentActivity == null) {
            throw new RuntimeException("Activity " + name + " has no parent. Cannot add new dependency.");
        }
        if(!parentActivity.childActivities.containsKey(fellowActivity.getName())) {
            throw new RuntimeException("Activity " + name + " has different parent from " + fellowActivity.name + ". Cannot link as dependency.");
        }

        dependencies.put(fellowActivity.name, mode);
    }

    /**
     * Remove dependency from fellow activity.
     * @param fellowActivity activity that has same parent as current activity
     */
    public void removeDependency(Activity fellowActivity) {
        if(fellowActivity == this) {
            throw new RuntimeException("Fellow activity cannot be itself.");
        }

        if(parentActivity == null) {
            throw new RuntimeException("Activity " + name + " has no parent. Cannot add new dependency!");
        }
        if(!parentActivity.childActivities.containsKey(fellowActivity.getName())) {
            throw new RuntimeException("Activity " + name + " has different parent from " + fellowActivity.name + ". Cannot link as dependency");
        }

        dependencies.remove(fellowActivity.getName());
    }

    // Synchronizations with child activities

    public void synchronizeCost(boolean lazySync) {
        if(childActivities.isEmpty()) return;
        cost = 0;

        for(Activity activity: childActivities.values()) {
            if(!lazySync) activity.synchronizeCost(true);

            cost += activity.cost;
        }
    }

    /**
     * Synchronize current activity's start and end date with
     * @param lazySync whether if synchronization will be done lazily, as in will not try synchronizing child activity with child activities of child activity.
     */
    public void synchronizeDate(boolean lazySync) {
        if(childActivities.isEmpty()) return;

        LocalDate newStartDate = null;
        LocalDate newEndDate = null;

        for(Activity activity: childActivities.values()) {
            if(!lazySync) activity.synchronizeDate(true);

            if(newStartDate == null) {
                newStartDate = activity.startDate;
            } else {
                if(ChronoUnit.DAYS.between(activity.startDate, newStartDate) > 0) {
                    newStartDate = activity.startDate;
                }
            }

            if(newEndDate == null) {
                newEndDate = activity.endDate;
            } else {
                if(ChronoUnit.DAYS.between(newEndDate, activity.endDate) > 0) {
                    newEndDate = activity.endDate;
                }
            }
        }

        startDate = newStartDate;
        endDate = newEndDate;
    }

    // Miscellaneous
    public void markAsFinished() {
        this.isFinished = true;
    }

    // Setters and getters
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public LocalDate getStartDate() {
        return startDate;
    }
    public LocalDate getEndDate() {
        return endDate;
    }
    public Collection<Activity> getChildActivities() {
        return childActivities.values();
    }
    public HashMap<String, Dependency> getDependencies() {
        try {
            return (HashMap<String, Dependency>) dependencies.clone();
        } catch(ClassCastException cce) {
            System.err.println("A fatal error has occurred when trying to get dependency of " + name + ". You should report this to developer directly.\nReason: " + cce.getMessage());
        }

        return null;
    }
    public long getCost() {
        return cost;
    }
    public boolean getIsFinished() {
        return isFinished;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setCost(long cost) {
        this.cost = cost;
    }

    /**
     * Set start date. Ignored if activity has child activities.
     * @param startDate new start date
     */
    public void setStartDate(LocalDate startDate) {
        if(!childActivities.isEmpty()) {
            throw new RuntimeException("Start date is bound to child's start date.");
        }

        this.startDate = startDate;
    }

    /**
     * Set end date. Ignored if activity has child activities.
     * @param endDate new end date
     */
    public void setEndDate(LocalDate endDate) {
        if(!childActivities.isEmpty()) {
            throw new RuntimeException("End date is bound to child's end date.");
        }

        this.endDate = endDate;
    }


    // JSON object handling
    @Override
    public JSONObject toJSONObject() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("name", name);
        object.put("description", description);
        object.put("startDate", startDate);
        object.put("endDate", endDate);
        object.put("cost", cost);
        object.put("isFinished", isFinished);

        JSONArray childActivitiesJSON = new JSONArray();
        for(Activity childActivity: childActivities.values()) {
            childActivitiesJSON.put(childActivity.toJSONObject());
        }
        object.put("activities", childActivitiesJSON);

        JSONArray dependenciesJSON = new JSONArray();
        for(String fellowActivity: dependencies.keySet()) {
            JSONObject dependency = new JSONObject();
            dependency.put("dependsOn", fellowActivity);
            dependency.put("mode", dependencies.get(fellowActivity).name());
            dependenciesJSON.put(dependency);
        }
        object.put("dependencies", dependenciesJSON);

        return object;
    }

    @Override
    public void fromJSONObject(JSONObject object) throws JSONException {
        name = object.getString("name");
        description = object.getString("description");
        startDate = LocalDate.parse(object.getString("startDate"));
        endDate = LocalDate.parse(object.getString("endDate"));
        cost = object.getLong("cost");
        isFinished = object.getBoolean("isFinished");

        JSONArray childActivitiesJSON = object.getJSONArray("activities");
        childActivities.clear();
        for(int i = 0; i < childActivitiesJSON.length(); i++) {
            addChildActivity(
                    new Activity(
                            childActivitiesJSON.getJSONObject(i)
                    )
            );
        }
        synchronizeDate(false);
        synchronizeCost(false);

        JSONArray dependenciesJSON = object.getJSONArray("dependencies");
        dependencies.clear();
        for(int i = 0; i < dependenciesJSON.length(); i++) {
            JSONObject dependency = dependenciesJSON.getJSONObject(i);
            dependencies.put(
                    dependency.getString("dependsOn"),
                    Dependency.valueOf(dependency.getString("mode"))
            );
        }
    }

    // Dependency
    public enum Dependency {
        FINISH_TO_START,
        START_TO_START,
        FINISH_TO_FINISH,
        START_TO_FINISH
    }
}
