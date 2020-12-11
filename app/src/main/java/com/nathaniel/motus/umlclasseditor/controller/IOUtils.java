package com.nathaniel.motus.umlclasseditor.controller;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class IOUtils {

    private IOUtils() {

    }

    public static void saveFileToInternalStorage(String data,File file) {
        try {
            FileWriter fileWriter=new FileWriter(file);
            fileWriter.append(data);
            fileWriter.flush();
            fileWriter.close();
            Log.i("TEST","Project saved");
            } catch (IOException e) {
            Log.i("TEST","Failed saving project");
        }
    }

    public static String getFileFromInternalStorage(File file) {
        String projectString="";
        if (file.exists()) {
            BufferedReader bufferedReader;
            try {
                bufferedReader = new BufferedReader(new FileReader(file));
                try {
                    String readString = bufferedReader.readLine();
                    while (readString != null) {
                        projectString = projectString + readString;
                        readString = bufferedReader.readLine();
                    }
                } finally {
                    bufferedReader.close();
                    Log.i("TEST", "Project loaded");
                }
            } catch (IOException e) {
                Log.i("TEST","Failed loading project");
            }
        }
        return projectString;
    }

    public static void saveFileToExternalStorage(String data, Uri externalStorageUri) {

    }

    public static String readFileFromExternalStorage(Uri externalStorageUri) {
        return null;

    }
}
