package com.progresscharter.progresscharter.lib;

import org.codehaus.jettison.json.JSONObject;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
class ActivityTest {
    // TEST 1
    // Init test
    @Test
    @Order(0)
    public void testInit() {
        assertDoesNotThrow(() -> {
            Activity activity = new Activity("Test activity", "Lorem ipsum");
        });
    }

    // TEST 2
    // Parent handling test
    @Test
    @Order(1)
    public void testParentHandling() {
        Activity parentActivity = new Activity("Parent", "");
        Activity childActivity1 = new Activity("Child 1", "");
        Activity childActivity2 = new Activity("Child 2", "");
        Activity childActivity3 = new Activity("Child 3", "");

        assertDoesNotThrow(() -> {
            parentActivity.addChildActivity(childActivity1);
            parentActivity.addChildActivity(childActivity2);
            parentActivity.addChildActivity(childActivity3);
        });

        assertTrue(parentActivity.contains(childActivity1));
        assertTrue(parentActivity.contains(childActivity2));
        assertTrue(parentActivity.contains(childActivity3));
        assertEquals(3, parentActivity.activitiesCount());

        assertDoesNotThrow(() -> {
            childActivity1.removeParentActivity();
            childActivity2.removeParentActivity();
            childActivity3.removeParentActivity();
        });

        assertFalse(parentActivity.contains(childActivity1));
        assertFalse(parentActivity.contains(childActivity2));
        assertFalse(parentActivity.contains(childActivity3));
    }

    // TEST 3
    // Time handling test
    @Test
    @Order(2)
    public void testTimeHandling() {
        Activity parentActivity = new Activity("Parent", "");
        Activity childActivity1 = new Activity("Child 1", "");
        Activity childActivity2 = new Activity("Child 2", "");
        Activity childActivity3 = new Activity("Child 3", "");

        childActivity1.setStartDate(LocalDate.now().minusDays(30));
        childActivity1.setEndDate(LocalDate.now().plusDays(10));
        childActivity2.setStartDate(childActivity1.getEndDate().minusDays(3));
        childActivity2.setEndDate(childActivity1.getEndDate().plusDays(10));
        childActivity3.setStartDate(childActivity2.getEndDate());
        childActivity3.setEndDate(childActivity2.getEndDate().plusDays(21));

        assertDoesNotThrow(() -> {
            parentActivity.addChildActivity(childActivity1);
            parentActivity.addChildActivity(childActivity2);
            parentActivity.addChildActivity(childActivity3);

            parentActivity.synchronizeDate(false);
        });

        assertThrows(RuntimeException.class, () -> {
            parentActivity.setStartDate(LocalDate.now());
        });

        assertThrows(RuntimeException.class, () -> {
            parentActivity.setEndDate(LocalDate.now().plusDays(30));
        });

        System.out.println("Parent start date  : " + parentActivity.getStartDate().toString());
        System.out.println("Child 1 start date : " + childActivity1.getStartDate().toString());
        System.out.println("Parent end date    : " + parentActivity.getEndDate().toString());
        System.out.println("Child 3 end date   : " + childActivity3.getEndDate().toString());
        assertEquals(0, ChronoUnit.DAYS.between(childActivity1.getStartDate(), parentActivity.getStartDate()));
        assertEquals(0, ChronoUnit.DAYS.between(childActivity3.getEndDate(), parentActivity.getEndDate()));


        assertDoesNotThrow(() -> {
            childActivity1.removeParentActivity();
            childActivity2.removeParentActivity();
            childActivity3.removeParentActivity();
        });
    }

    // TEST 4
    // Dependency handling test
    @Test
    @Order(3)
    public void testDependencyHandling() {
        Activity parentActivity = new Activity("Parent", "");
        Activity childActivity1 = new Activity("Child 1", "");
        Activity childActivity2 = new Activity("Child 2", "");
        Activity childActivity3 = new Activity("Child 3", "");

        childActivity1.setStartDate(LocalDate.now().minusDays(30));
        childActivity1.setEndDate(LocalDate.now().plusDays(10));
        childActivity2.setStartDate(childActivity1.getEndDate().minusDays(3));
        childActivity2.setEndDate(childActivity1.getEndDate().plusDays(10));
        childActivity3.setStartDate(childActivity2.getEndDate());
        childActivity3.setEndDate(childActivity2.getEndDate().plusDays(21));

        assertThrows(RuntimeException.class, () -> {
            childActivity1.addDependency(childActivity2, Activity.Dependency.FINISH_TO_START);
            parentActivity.addDependency(parentActivity, Activity.Dependency.START_TO_START);
        });

        assertDoesNotThrow(() -> {
            parentActivity.addChildActivity(childActivity1);
            parentActivity.addChildActivity(childActivity2);
            parentActivity.addChildActivity(childActivity3);

            childActivity1.addDependency(childActivity2, Activity.Dependency.START_TO_FINISH);
            childActivity2.addDependency(childActivity1, Activity.Dependency.START_TO_START);

            HashMap<String, Activity.Dependency> dependencies1 = childActivity1.getDependencies();
            assertTrue(dependencies1.containsKey(childActivity2.getName()));
            assertFalse(dependencies1.containsKey(childActivity3.getName()));
        });

        assertDoesNotThrow(() -> {
            childActivity1.removeParentActivity();
            childActivity2.removeParentActivity();
            childActivity3.removeParentActivity();
        });

    }

    // TEST 5
    // JSON Test
    @Test
    @Order(4)
    public void testJSON() {
        Activity parentActivity = new Activity("Parent", "");
        Activity childActivity1 = new Activity("Child 1", "");
        Activity childActivity1_1 = new Activity("Child 1.1", ""); childActivity1_1.setCost(3000000);
        Activity childActivity1_2 = new Activity("Child 1.2", ""); childActivity1_2.setCost(5000000);
        Activity childActivity2 = new Activity("Child 2", ""); childActivity2.setCost(9600000);
        Activity childActivity3 = new Activity("Child 3", ""); childActivity3.setCost(14200000);

        childActivity1_1.setStartDate(LocalDate.now().minusDays(13));
        childActivity1_1.setEndDate(LocalDate.now().plusDays(3));
        childActivity1_2.setStartDate(LocalDate.now().plusDays(5));
        childActivity1_2.setEndDate(LocalDate.now().plusDays(11));
        childActivity2.setStartDate(LocalDate.now().plusDays(3));
        childActivity2.setEndDate(LocalDate.now().plusDays(20));
        childActivity3.setStartDate(childActivity2.getStartDate().minusDays(1));
        childActivity3.setEndDate(childActivity2.getStartDate().plusDays(15));

        assertDoesNotThrow(() -> {
            parentActivity.addChildActivity(childActivity1);
            childActivity1.addChildActivity(childActivity1_1);
            childActivity1.addChildActivity(childActivity1_2);
            parentActivity.addChildActivity(childActivity2);
            parentActivity.addChildActivity(childActivity3);

            childActivity1_2.addDependency(childActivity1_1, Activity.Dependency.FINISH_TO_START);
            childActivity3.addDependency(childActivity2, Activity.Dependency.START_TO_START);

            parentActivity.synchronizeDate(false);
            parentActivity.synchronizeCost(false);
        });

        assertDoesNotThrow(() -> {
            JSONObject object = parentActivity.toJSONObject();
            System.out.println(object.toString(4));

            Activity newParent = new Activity(object);
            JSONObject newObject = newParent.toJSONObject();
            System.out.println(newObject.toString(4));
        });
    }
}