package com.nathaniel.motus.umlclasseditor.model;

import android.content.Context;
import android.net.Uri;

import com.nathaniel.motus.umlclasseditor.R;
import com.nathaniel.motus.umlclasseditor.controller.IOUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class UmlType {
    //type of attributes and parameters, such as int, String, etc.
    //for project types, i.e project classes, it will be extended by UmlClass
    //custom types will be added by user without any class definition
    //type list is at static level

    public enum TypeLevel{PRIMITIVE,CUSTOM,PROJECT}

    protected String mName;
    protected TypeLevel mTypeLevel;
    private static ArrayList<UmlType> sUmlTypes=new ArrayList<>();

    private static String CUSTOM_TYPES_FILENAME="custom_types";

    public static final String JSON_PACKAGE_VERSION_CODE="PackageVersionCode";
    public static final String JSON_CUSTOM_TYPES="CustomTypes";


//    **********************************************************************************************
//    Constructors
//    **********************************************************************************************

    public UmlType() {
        this.mTypeLevel=TypeLevel.PROJECT;
    }

    public UmlType(String name, TypeLevel typeLevel) {
        this.mName = name;
        this.mTypeLevel=typeLevel;
        sUmlTypes.add(this);
    }

    public static void createUmlType(String name, TypeLevel typeLevel) {
        UmlType t=new UmlType(name,typeLevel);
    }

//    **********************************************************************************************
//    Getters and setters
//    **********************************************************************************************

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public static UmlType valueOf(String name, ArrayList<UmlType> inUmlTypes) {
        for (UmlType t:inUmlTypes)
            if(t.getName().equals(name)) return t;
        return null;
    }

    public TypeLevel getTypeLevel() {
        return mTypeLevel;
    }

    public static ArrayList<UmlType> getUmlTypes() {
        return sUmlTypes;
    }

//    **********************************************************************************************
//    JSON methods
//    **********************************************************************************************

    public static JSONArray getCustomUmlTypesToJSONArray() {
        JSONArray jsonArray=new JSONArray();
        for (UmlType t:UmlType.sUmlTypes)
            if (t.isCustomUmlType()) jsonArray.put(t.mName);
        return jsonArray;
    }

    public static void createCustomUmlTypesFromJSONArray(JSONArray jsonArray) {
        String typeName=(String)jsonArray.remove(0);
        while (typeName != null) {
            while (UmlType.containsUmlTypeNamed(typeName))
                typeName=typeName+"(1)";
            UmlType.createUmlType(typeName,TypeLevel.CUSTOM);
            typeName=(String)jsonArray.remove(0);
        }
    }

//    **********************************************************************************************
//    Modifiers
//    **********************************************************************************************

    public static void clearProjectUmlTypes() {
        for (int i=sUmlTypes.size()-1;i>0;i--)
            if (sUmlTypes.get(i).isProjectUmlType()) sUmlTypes.remove(i);
    }

    public static void removeUmlType(UmlType umlType) {
        sUmlTypes.remove(umlType);
    }

    public static void initializePrimitiveUmlTypes(Context context) {
        String[] standardTypes=context.getResources().getStringArray(R.array.standard_types);

        for (int i=0;i< standardTypes.length;i++)
            createUmlType(standardTypes[i],TypeLevel.PRIMITIVE);
    }

    public void upgradeToProjectUmlType() {
        //upgrade a class created without type to Project UmlType
        this.mTypeLevel=TypeLevel.PROJECT;
        sUmlTypes.add(this);
    }

//    **********************************************************************************************
//    Test methods
//    **********************************************************************************************

    public boolean isPrimitiveUmlType() {
        return (this.mTypeLevel==TypeLevel.PRIMITIVE);
    }

    public boolean isCustomUmlType() {
        return (this.mTypeLevel==TypeLevel.CUSTOM);
    }

    public boolean isProjectUmlType() {
        return (this.mTypeLevel==TypeLevel.PROJECT);
    }

    public static boolean containsPrimitiveUmlTypeNamed(String name) {
        for (UmlType t:UmlType.sUmlTypes)
            if (t.mName.equals(name) && t.isPrimitiveUmlType()) return true;
        return false;
    }

    public static boolean containsCustomUmlTypeNamed(String name) {
        for (UmlType t:UmlType.sUmlTypes)
            if (t.mName.equals(name) && t.isCustomUmlType()) return true;
        return false;
    }

    public static boolean containsProjectUmlTypeNamed(String name) {
        for (UmlType t:UmlType.sUmlTypes)
            if (t.mName.equals(name) && t.isProjectUmlType()) return true;
        return false;
    }

    public static boolean containsUmlTypeNamed(String name) {
        for (UmlType t:sUmlTypes)
            if (t.mName.equals(name)) return true;
        return false;
    }

//    **********************************************************************************************
//    Save and load methods
//    **********************************************************************************************
    public static void saveCustomUmlTypes(Context context) {
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put(JSON_CUSTOM_TYPES,UmlType.getCustomUmlTypesToJSONArray());
            jsonObject.put(JSON_PACKAGE_VERSION_CODE, IOUtils.getAppVersionCode(context));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        IOUtils.saveFileToInternalStorage(jsonObject.toString(),new File(context.getFilesDir(),CUSTOM_TYPES_FILENAME));
    }

    public static void initializeCustomUmlTypes(Context context) {
        try {
            JSONObject jsonObject=new JSONObject(IOUtils.getFileFromInternalStorage(new File(context.getFilesDir(),CUSTOM_TYPES_FILENAME)));
            JSONArray jsonCustomTypes=jsonObject.getJSONArray(JSON_CUSTOM_TYPES);
            UmlType.createCustomUmlTypesFromJSONArray(jsonCustomTypes);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void exportCustomUmlTypes(Context context, Uri toDestination) {
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put(JSON_CUSTOM_TYPES,UmlType.getCustomUmlTypesToJSONArray());
            jsonObject.put(JSON_PACKAGE_VERSION_CODE,IOUtils.getAppVersionCode(context));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        IOUtils.saveFileToExternalStorage(context,jsonObject.toString(),toDestination);
    }

    public static void importCustomUmlTypes(Context context, Uri fromDestination) {
        try {
            JSONObject jsonObject=new JSONObject(IOUtils.readFileFromExternalStorage(context,fromDestination));
            UmlType.createCustomUmlTypesFromJSONArray(jsonObject.getJSONArray(JSON_CUSTOM_TYPES));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
