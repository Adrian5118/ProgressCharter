package com.progresscharter.progresscharter.lib;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class FileHandler {
    private static String currentFilePath = "";
    private static String currentFileName = "";

    public static void open(File file) throws JSONException, IOException, IllegalArgumentException{
        StringBuilder fileContent = new StringBuilder();
        Scanner fileScanner = new Scanner(file);

        while(fileScanner.hasNextLine()) {
            fileContent.append(fileScanner.nextLine());
        }
        fileScanner.close();

        ProjectHandler.readFromJSON(
                new JSONObject(
                        fileContent.toString()
                )
        );

        syncFilePathName(file);
    }

    public static void save(String pathToFile) throws JSONException, IOException, IllegalArgumentException{
        if(pathToFile == null || pathToFile.isBlank()) throw new IllegalArgumentException("Path to file cannot be empty.");

        File file = new File(pathToFile);

        try(Writer writer = new FileWriter(file, false)) {
            writer.write(ProjectHandler.saveToJSON().toString(4));
        }

        syncFilePathName(file);
    }

    public static void save(File file) throws JSONException, IOException, IllegalArgumentException{
        try(Writer writer = new FileWriter(file, false)) {
            writer.write(ProjectHandler.saveToJSON().toString(4));
        }

        syncFilePathName(file);
    }

    public static String getCurrentFilePath() {
        return currentFilePath;
    }

    public static String getCurrentFileName() {
        return currentFileName;
    }

    public static boolean checkCurrentProjectIntegrity() {
        try {
            byte[] currentFilePathBytes = Files.readAllBytes(Paths.get(currentFilePath));
            byte[] currentFilePathHash = MessageDigest.getInstance("MD5").digest(currentFilePathBytes);
            String currentFilePathHashString = new BigInteger(1, currentFilePathHash).toString(16);
            System.out.println(currentFilePathHashString);

            byte[] currentProjectBytes = ProjectHandler.saveToJSON().toString(4).getBytes();
            byte[] currentProjectHash = MessageDigest.getInstance("MD5").digest(currentProjectBytes);
            String currentProjectHashString = new BigInteger(1, currentProjectHash).toString(16);
            System.out.println(currentProjectHashString);

            return currentFilePathHashString.equals(currentProjectHashString);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void syncFilePathName(File file) {
        currentFilePath = file.getAbsolutePath();
        currentFileName = file.getName();
    }
}
