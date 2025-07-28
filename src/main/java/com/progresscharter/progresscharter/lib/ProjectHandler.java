package com.progresscharter.progresscharter.lib;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class ProjectHandler {
    private static String projectName = "";
    private static String projectDescription = "";
    private static String creatorName = "";

    private static Activity currentActivity = null;

    public static String getProjectName() {
        return projectName;
    }
    public static String getProjectDescription() {
        return projectDescription;
    }
    public static String getCreatorName() {
        return creatorName;
    }
    public static Activity getCurrentActivity() {
        return currentActivity;
    }

    public static void setProjectName(String projectName) {
        ProjectHandler.projectName = projectName;
    }
    public static void setProjectDescription(String projectDescription) {
        ProjectHandler.projectDescription = projectDescription;
    }
    public static void setCreatorName(String creatorName) {
        ProjectHandler.creatorName = creatorName;
    }
    public static void setCurrentActivity(Activity currentActivity) {
        ProjectHandler.currentActivity = currentActivity;
    }

    public static JSONObject toJSONObject() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("projectName", projectName);
        object.put("projectDescription", projectDescription);
        object.put("creatorName", creatorName);
        object.put("activities", currentActivity.toJSONObject());

        return object;
    }

    public static void fromJSONObject(JSONObject object) throws JSONException {
        projectName = object.getString("projectName");
        projectDescription = object.getString("projectDescription");
        creatorName = object.getString("creatorName");
        currentActivity = new Activity(object.getJSONObject("activities"));
    }

}
