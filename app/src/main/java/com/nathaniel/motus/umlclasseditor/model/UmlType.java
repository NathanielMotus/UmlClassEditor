package com.nathaniel.motus.umlclasseditor.model;

import android.content.Context;

import com.nathaniel.motus.umlclasseditor.R;

import org.json.JSONArray;

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

//    **********************************************************************************************
//    Constructors
//    **********************************************************************************************

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

    public static void createCustomUmlTypesFromJSONArray(JSONArray jsonArrayTypes) {
        String jsonString=(String)jsonArrayTypes.remove(0);
        while (jsonString!=null) {
            createUmlType(jsonString, TypeLevel.CUSTOM);
            jsonString=(String)jsonArrayTypes.remove(0);
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
}
