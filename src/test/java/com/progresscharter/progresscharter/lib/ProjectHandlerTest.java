package com.progresscharter.progresscharter.lib;

import org.codehaus.jettison.json.JSONObject;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProjectHandlerTest {
    // TEST 1
    // Test basic init
    @Test
    @Order(0)
    public void testInit() {
        assertDoesNotThrow(() -> {
            ProjectHandler.setProjectName("Ramirez' Project");
            ProjectHandler.setProjectDescription("Lorem ipsum");
            ProjectHandler.setCreatorName("Ramirez Timbuktu");

            Activity parentActivity = new Activity("Parent", "");
            Activity childActivity1 = new Activity("Child 1", "");
            Activity childActivity2 = new Activity("Child 2", "");
            Activity childActivity3 = new Activity("Child 3", "");

            parentActivity.addChildActivity(childActivity1);
            parentActivity.addChildActivity(childActivity2);
            parentActivity.addChildActivity(childActivity3);

            ProjectHandler.setCurrentActivity(parentActivity);
        });
    }

    // TEST 2
    // Test JSON handling
    @Test
    @Order(1)
    public void testJSONHandling() {
        assertDoesNotThrow(() -> {
            ProjectHandler.setProjectName("Ramirez' Project");
            ProjectHandler.setProjectDescription("Lorem ipsum");
            ProjectHandler.setCreatorName("Ramirez Timbuktu");

            Activity parentActivity = new Activity("Parent", "");
            Activity childActivity1 = new Activity("Child 1", "");
            Activity childActivity2 = new Activity("Child 2", "");
            Activity childActivity3 = new Activity("Child 3", "");

            parentActivity.addChildActivity(childActivity1);
            parentActivity.addChildActivity(childActivity2);
            parentActivity.addChildActivity(childActivity3);

            ProjectHandler.setCurrentActivity(parentActivity);

            JSONObject object = ProjectHandler.saveToJSON();
            System.out.println(object.toString(4));

            ProjectHandler.readFromJSON(object);
        });
    }
}