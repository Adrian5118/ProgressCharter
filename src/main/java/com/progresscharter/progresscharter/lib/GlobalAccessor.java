package com.progresscharter.progresscharter.lib;

import java.util.HashMap;
import java.util.HashSet;

public class GlobalAccessor {
    private static final HashMap<String, Object> accessList;
    private static final HashMap<Object, HashSet<Class>> accessPerm;

    static {
        accessList = new HashMap<>();
        accessPerm = new HashMap<>();
    }

    public static Object access(String accessName, Object keyObject) {
        if(!accessList.containsKey(accessName)) return null;

        if(accessPerm.get(accessList.get(accessName)).contains(keyObject.getClass())) {
            return accessList.get(accessName);
        }

        throw new SecurityException("Access to " + accessName + " is denied. " + keyObject.getClass() + " does not have a permission to access " + accessName + ".");
    }

    public static void addToAccessList(String accessName, Object object) {
        accessList.put(accessName, object);
        accessPerm.put(object, new HashSet<>());
    }

    public static void addPerm(String accessName, Class to) {
        if(!accessList.containsKey(accessName)) throw new NullPointerException("Access name not found in list of global accessible object");

        accessPerm.get(accessList.get(accessName)).add(to);
    }

    public static void revokePerm(String accessName, Class to) {
        if(!accessList.containsKey(accessName)) throw new NullPointerException("Access name not found in list of global accessible object");

        accessPerm.get(accessList.get(accessName)).remove(to);
    }

    public static void removeFromAccessList(String accessName) {
        accessPerm.remove(accessList.get(accessName));
        accessList.remove(accessName);
    }

}
