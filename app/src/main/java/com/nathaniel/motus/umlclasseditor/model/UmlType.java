package com.nathaniel.motus.umlclasseditor.model;

import android.content.Context;

import com.nathaniel.motus.umlclasseditor.R;

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
//    Modifiers
//    **********************************************************************************************

    public static void clearClassUmlTypes() {
        for (UmlType t:sUmlTypes)
            if (t.mTypeLevel==TypeLevel.PROJECT) sUmlTypes.remove(t);
    }

    public static void removeUmlType(UmlType umlType) {
        sUmlTypes.remove(umlType);
    }

    public static void initializePrimitiveUmlTypes(Context context) {
        String[] standardTypes=context.getResources().getStringArray(R.array.standard_types);

        for (int i=0;i< standardTypes.length;i++)
            createUmlType(standardTypes[i],TypeLevel.PRIMITIVE);
    }

    public static void initializeCustomUmlTypes(Context context) {
        //todo : implement initializeCustomUmlTypes
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
}
