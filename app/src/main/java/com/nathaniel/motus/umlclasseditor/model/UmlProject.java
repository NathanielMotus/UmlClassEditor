package com.nathaniel.motus.umlclasseditor.model;

import android.content.Context;
import android.net.Uri;

import com.nathaniel.motus.umlclasseditor.controller.IOUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class UmlProject {

    private String mName;
    private ArrayList<UmlClass> mUmlClasses;
    private ArrayList<UmlRelation> mUmlRelations;
    private int mAppVersionCode;
    private float mZoom=1;
    private float mXOffset=0;
    private float mYOffset=0;

    public static final String JSON_PROJECT_NAME = "ProjectName";
    public static final String JSON_PROJECT_CLASSES = "ProjectClasses";
    public static final String JSON_PROJECT_RELATIONS = "ProjectRelations";
    public static final String JSON_PROJECT_PACKAGE_VERSION_CODE ="ProjectPackageVersionCode";
    public static final String JSON_PROJECT_ZOOM="ProjectZoom";
    public static final String JSON_PROJECT_X_OFFSET="ProjectXOffset";
    public static final String JSON_PROJECT_Y_OFFSET="ProjectYOffset";

    public static final String PROJECT_DIRECTORY="projects";

//    **********************************************************************************************
//    Constructors
//    **********************************************************************************************

    public UmlProject(String name,Context context) {
        mName = name;
        mUmlClasses=new ArrayList<UmlClass>();
        mUmlRelations=new ArrayList<UmlRelation>();
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

    public void setAppVersionCode(int appVersionCode) {
        mAppVersionCode = appVersionCode;
    }

    public void setZoom(float zoom) {
        mZoom = zoom;
    }

    public void setXOffset(float XOffset) {
        mXOffset = XOffset;
    }

    public void setYOffset(float YOffset) {
        mYOffset = YOffset;
    }

    public float getZoom() {
        return mZoom;
    }

    public float getXOffset() {
        return mXOffset;
    }

    public float getYOffset() {
        return mYOffset;
    }

//    **********************************************************************************************
//    Initialization
//    **********************************************************************************************

//    **********************************************************************************************
//    Modifiers
//    **********************************************************************************************

    public void addUmlClass(UmlClass umlClass) {
        mUmlClasses.add(umlClass);
    }

    public void removeUmlClass(UmlClass umlClass) {
        mUmlClasses.remove(umlClass);
        UmlType.removeUmlType(umlClass);
        removeRelationsInvolving(umlClass);
        removeAttributesOfType((UmlType)umlClass);
        removeMethodsOfType((UmlType)umlClass);
        removeParametersOfType((UmlType)umlClass);
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

    public void removeAttributesOfType(UmlType umlType) {
        for (UmlClass c : this.getUmlClasses()) {
            for (UmlClassAttribute a : c.getAttributeList()) {
                if (a.getUmlType()==umlType) c.removeAttribute(a);
            }
        }
    }

    public void removeMethodsOfType(UmlType umlType) {
        for (UmlClass c : this.getUmlClasses()) {
            for (UmlClassMethod m : c.getMethodList()) {
                if (m.getUmlType()==umlType) c.removeMethod(m);
            }
        }
    }

    public void removeParametersOfType(UmlType umlType) {
        for (UmlClass c : this.getUmlClasses()) {
            for (UmlClassMethod m : c.getMethodList()) {
                for (MethodParameter p : m.getParameters()) {
                    if (p.getUmlType() == umlType) m.removeParameter(p);
                }
            }
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

    public boolean hasConflictNameWith(UmlClass umlClass) {
        boolean test=false;
        for (UmlClass c:mUmlClasses)
            if (c.getName().compareTo(umlClass.getName())==0) test=true;
        return test;
    }

    public boolean containsClassNamed(String className) {
        //check whether a class with className already exists in this project

        for (UmlClass c:this.getUmlClasses())
            if (c.getName().equals(className)) return true;

        return false;
    }

//    **********************************************************************************************
//    JSON methods
//    **********************************************************************************************

    public JSONObject toJSONObject(Context context) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(JSON_PROJECT_PACKAGE_VERSION_CODE, IOUtils.getAppVersionCode(context));
            jsonObject.put(JSON_PROJECT_ZOOM,mZoom);
            jsonObject.put(JSON_PROJECT_X_OFFSET,mXOffset);
            jsonObject.put(JSON_PROJECT_Y_OFFSET,mYOffset);
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

            project.setAppVersionCode(jsonObject.getInt(JSON_PROJECT_PACKAGE_VERSION_CODE));
            project.setZoom((float)(jsonObject.getDouble(JSON_PROJECT_ZOOM)));
            project.setXOffset((float)(jsonObject.getDouble(JSON_PROJECT_X_OFFSET)));
            project.setYOffset((float)(jsonObject.getDouble(JSON_PROJECT_Y_OFFSET)));

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

//    **********************************************************************************************
//    Save and load project methods
//    **********************************************************************************************

    public void save(Context context) {
        File destination=new File(context.getFilesDir(),PROJECT_DIRECTORY);
        if (!destination.exists()) destination.mkdir();
        IOUtils.saveFileToInternalStorage(this.toJSONObject(context).toString(),new File(destination,mName));
    }

    public static UmlProject load(Context context, String projectName) {
        File destination=new File(context.getFilesDir(),PROJECT_DIRECTORY);
        File source=new File(destination,projectName);
        try {
            return UmlProject.fromJSONObject(new JSONObject(IOUtils.getFileFromInternalStorage(source)),context);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void exportProject(Context context, Uri toDestination) {
        IOUtils.saveFileToExternalStorage(context,this.toJSONObject(context).toString(),toDestination);
    }

    public static UmlProject importProject(Context context, Uri fromFileUri) {
        try {
            return UmlProject.fromJSONObject(new JSONObject(IOUtils.readFileFromExternalStorage(context,fromFileUri)),context);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void mergeWith(UmlProject project) {
        for (UmlClass c:project.getUmlClasses()){

            while (UmlType.containsUmlTypeNamed(c.getName())){
                c.setName(c.getName()+"(1)");
            }

            this.addUmlClass(c);
        }

        for (UmlRelation r:project.getUmlRelations()) this.addUmlRelation(r);
    }
}
