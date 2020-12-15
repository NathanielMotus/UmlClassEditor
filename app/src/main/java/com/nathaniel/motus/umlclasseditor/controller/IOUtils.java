package com.nathaniel.motus.umlclasseditor.controller;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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

    public static void saveFileToExternalStorage(Context context, String data, Uri externalStorageUri) {
        try {
            OutputStream outputStream=context.getContentResolver().openOutputStream(externalStorageUri);
            outputStream.write(data.getBytes());
            outputStream.flush();
            outputStream.close();
            Log.i("TEST", "Project saved");
        } catch (IOException e) {
            Log.i("TEST","Failed saving project");
            Log.i("TEST",e.getMessage());
        }
    }

    public static String readFileFromExternalStorage(Context context, Uri externalStorageUri) {
        String data="";
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(externalStorageUri);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            data = bufferedReader.readLine();
            Log.i("TEST","Project loaded");
        } catch (IOException e) {
            Log.i("TEST","Failed loading project");
        }

        return data;
    }

    public static ArrayList<String> sortedFiles(File file) {
        File[] files=file.listFiles();
        ArrayList<String> fileList=new ArrayList<>();

        for (File f:files) fileList.add(f.getName());

        Collections.sort(fileList);
        return fileList;
    }
}
