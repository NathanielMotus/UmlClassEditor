package com.nathaniel.motus.umlclasseditor.model;

import android.content.Context;
import android.util.Log;

import com.nathaniel.motus.umlclasseditor.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UmlProject {

    private String mName;
    private ArrayList<UmlClass> mUmlClasses;
    private ArrayList<UmlType> mUmlTypes;
    private ArrayList<UmlRelation> mUmlRelations;

    public static final String JSON_PROJECT_NAME = "ProjectName";
    public static final String JSON_PROJECT_CLASSES = "ProjectClasses";
    public static final String JSON_PROJECT_RELATIONS = "ProjectRelations";

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

    public UmlClass getUmlClass(String className) {
        for (UmlClass c:mUmlClasses)
            if (c.mName.equals(className)) return c;

        return null;
    }

    public void setUmlRelations(ArrayList<UmlRelation> UmlRelations) {
        this.mUmlRelations = UmlRelations;
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
        mUmlTypes.add(umlClass);
    }

    public void removeUmlClass(UmlClass umlClass) {
        mUmlClasses.remove(umlClass);
        mUmlTypes.remove(umlClass);
        removeRelationsInvolving(umlClass);
        //todo : remove attributes and methods of UmlClass type
    }

    public void addUmlRelation(UmlRelation umlRelation) {
        mUmlRelations.add(umlRelation);
    }

    public void removeUmlRelation(UmlRelation umlRelation) {
        mUmlRelations.remove(umlRelation);
    }

    public void removeRelationsInvolving(UmlClass umlClass) {
        ArrayList<UmlRelation> umlRelations=new ArrayList<>();
        for (UmlRelation r : mUmlRelations) {
            if (umlClass.isInvolvedInRelation(r)) umlRelations.add(r);
        }
        for (UmlRelation r : umlRelations) {
            removeUmlRelation(r);
        }
    }

//    **********************************************************************************************
//    Test methods
//    **********************************************************************************************

    public boolean relationAlreadyExistsBetween(UmlClass firstClass, UmlClass secondClass) {
        //check whether there already is a relation between two classes
        //this test is not oriented
        boolean test=false;

        for (UmlRelation r : this.getUmlRelations())
            if ((r.getRelationOriginClass()==firstClass && r.getRelationEndClass()==secondClass)
                    || (r.getRelationOriginClass()==secondClass && r.getRelationEndClass()==firstClass))
                test=true;
        return test;
    }

//    **********************************************************************************************
//    JSON methods
//    **********************************************************************************************

    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(JSON_PROJECT_NAME, mName);
            jsonObject.put(JSON_PROJECT_CLASSES, getClassesToJSONArray());
            jsonObject.put(JSON_PROJECT_RELATIONS, getRelationsToJSONArray());
            return jsonObject;
        } catch (JSONException e) {
            return null;
        }
    }

    public static UmlProject fromJSONObject(JSONObject jsonObject,Context context) {
        try {
            UmlProject project = new UmlProject(jsonObject.getString(JSON_PROJECT_NAME), context);

            //copy jsonObject because it is cleared in getClassesFromJSONArray
            JSONObject jsonObjectCopy=new JSONObject(jsonObject.toString());
            for (UmlClass c : getClassesFromJSONArray((JSONArray)jsonObjectCopy.get(JSON_PROJECT_CLASSES)))
                project.addUmlClass(c);
            project.populateClassesFromJSONArray((JSONArray) jsonObject.get(JSON_PROJECT_CLASSES));
            project.setUmlRelations(UmlProject.getRelationsFromJSONArray((JSONArray) jsonObject.get(JSON_PROJECT_RELATIONS),project));
            return project;
        } catch (JSONException e) {
            return null;
        }
    }

    private JSONArray getClassesToJSONArray() {
        JSONArray jsonArray = new JSONArray();

        for (UmlClass c:mUmlClasses) jsonArray.put(c.toJSONObject());

        return jsonArray;
    }

    private static ArrayList<UmlClass> getClassesFromJSONArray(JSONArray jsonArray) {
        //first get classes and their names, to be populated later
        //otherwise, attributes with custom types can't be created

        ArrayList<UmlClass> classes = new ArrayList<>();

        JSONObject jsonObject=(JSONObject)jsonArray.remove(0);
        while (jsonObject != null) {
            classes.add(UmlClass.fromJSONObject(jsonObject));
            jsonObject=(JSONObject)jsonArray.remove(0);
        }
        return classes;
    }

    private void populateClassesFromJSONArray(JSONArray jsonArray) {
        JSONObject jsonObject=(JSONObject)jsonArray.remove(0);
        while (jsonObject != null) {
            UmlClass.populateUmlClassFromJSONObject(jsonObject, this);
            jsonObject = (JSONObject) jsonArray.remove(0);
        }
    }

    private JSONArray getRelationsToJSONArray() {
        JSONArray jsonArray = new JSONArray();

        for (UmlRelation r:mUmlRelations) jsonArray.put(r.toJSONObject());

        return jsonArray;
    }

    private static ArrayList<UmlRelation> getRelationsFromJSONArray(JSONArray jsonArray, UmlProject project) {
        ArrayList<UmlRelation> relations = new ArrayList<>();

        JSONObject jsonObject = (JSONObject) jsonArray.remove(0);
        while (jsonObject != null) {
            relations.add(UmlRelation.fromJSONObject(jsonObject,project));
            jsonObject = (JSONObject) jsonArray.remove(0);
        }
        return relations;
    }

}
