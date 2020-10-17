package com.nathaniel.motus.umlclasseditor.model;

import android.content.Context;
import android.graphics.drawable.shapes.Shape;
import android.graphics.fonts.Font;
import android.util.Log;

import com.nathaniel.motus.umlclasseditor.R;

import java.util.ArrayList;

public class UmlProject {

    private String mName;
    private ArrayList<UmlClass> mUmlClasses;
    private ArrayList<UmlType> mUmlTypes;
    private ArrayList<UmlRelation> mUmlRelations;

//    **********************************************************************************************
//    Constructors
//    **********************************************************************************************

    public UmlProject(String name,Context context) {
        mName = name;
        mUmlClasses=new ArrayList<UmlClass>();
        mUmlTypes=new ArrayList<UmlType>();
        mUmlRelations=new ArrayList<UmlRelation>();

        initializeStandardTypes(context);
    }

//    **********************************************************************************************
//    Getters and setters
//    **********************************************************************************************

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public ArrayList<UmlClass> getUmlClasses() {
        return mUmlClasses;
    }

    public ArrayList<UmlType> getUmlTypes() {
        return mUmlTypes;
    }

    public ArrayList<UmlRelation> getUmlRelations() {
        return mUmlRelations;
    }

//    **********************************************************************************************
//    Initialization
//    **********************************************************************************************

    public void initializeStandardTypes(Context context) {
        //Initialize primitive types and their wrappers, among others

        String[] standardTypes=context.getResources().getStringArray(R.array.standard_types);

        for (int i=0;i< standardTypes.length;i++) {
            mUmlTypes.add(new UmlType(standardTypes[i]));
        }
    }

//    **********************************************************************************************
//    Modifiers
//    **********************************************************************************************

    public void addUmlClass(UmlClass umlClass) {
        mUmlClasses.add(umlClass);
    }

    public void removeUmlClass(UmlClass umlClass) {
        mUmlClasses.remove(umlClass);
    }

    public void addUmlRelation(UmlRelation umlRelation) {
        mUmlRelations.add(umlRelation);
    }

    public void removeUmlRelation(UmlRelation umlRelation) {
        mUmlRelations.remove(umlRelation);
    }
}
