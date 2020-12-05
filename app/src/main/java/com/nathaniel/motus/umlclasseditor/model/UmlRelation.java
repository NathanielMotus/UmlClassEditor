package com.nathaniel.motus.umlclasseditor.model;

import org.json.JSONException;
import org.json.JSONObject;

public class UmlRelation {

    public enum UmlRelationType {INHERITANCE, REALIZATION,AGGREGATION,COMPOSITION,ASSOCIATION,DEPENDENCY}

    private UmlClass mRelationOriginClass; //arrow starts from this
    private UmlClass mRelationEndClass; //to this
    private float mXOrigin;
    private float mYOrigin;
    private float mXEnd;
    private float mYEnd;
    private UmlRelationType mUmlRelationType;

    public static final String JSON_RELATION_TYPE="RelationType";
    public static final String JSON_RELATION_ORIGIN_CLASS="RelationOriginClass";
    public static final String JSON_RELATION_END_CLASS="RelationEndCLass";

//    **********************************************************************************************
//    Constructors
//    **********************************************************************************************

    public UmlRelation(UmlClass relationOriginClass, UmlClass relationEndClass, UmlRelationType umlRelationType) {
        mRelationOriginClass = relationOriginClass;
        mRelationEndClass = relationEndClass;
        mUmlRelationType = umlRelationType;
        mXOrigin =0;
        mYOrigin =0;
        mXEnd =0;
        mYEnd =0;
    }

//    **********************************************************************************************
//    Getters and setters
//    **********************************************************************************************

    public UmlClass getRelationOriginClass() {
        return mRelationOriginClass;
    }

    public void setRelationOriginClass(UmlClass relationOriginClass) {
        mRelationOriginClass = relationOriginClass;
    }

    public UmlClass getRelationEndClass() {
        return mRelationEndClass;
    }

    public void setRelationEndClass(UmlClass relationEndClass) {
        mRelationEndClass = relationEndClass;
    }

    public UmlRelationType getUmlRelationType() {
        return mUmlRelationType;
    }

    public void setUmlRelationType(UmlRelationType umlRelationType) {
        mUmlRelationType = umlRelationType;
    }

    public float getXOrigin() {
        return mXOrigin;
    }

    public void setXOrigin(float XOrigin) {
        mXOrigin = XOrigin;
    }

    public float getYOrigin() {
        return mYOrigin;
    }

    public void setYOrigin(float YOrigin) {
        mYOrigin = YOrigin;
    }

    public float getXEnd() {
        return mXEnd;
    }

    public void setXEnd(float XEnd) {
        mXEnd = XEnd;
    }

    public float getYEnd() {
        return mYEnd;
    }

    public void setYEnd(float YEnd) {
        mYEnd = YEnd;
    }

//    **********************************************************************************************
//    Test methods
//    **********************************************************************************************

//    **********************************************************************************************
//    Other methods
//    **********************************************************************************************

//    **********************************************************************************************
//    JSON methods
//    **********************************************************************************************

    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(JSON_RELATION_TYPE, mUmlRelationType.toString());
            jsonObject.put(JSON_RELATION_ORIGIN_CLASS, mRelationOriginClass.getName());
            jsonObject.put(JSON_RELATION_END_CLASS, mRelationEndClass.getName());
            return jsonObject;
        } catch (JSONException e) {
            return null;
        }
    }

    public static UmlRelation fromJSONObject(JSONObject jsonObject,UmlProject project) {
        try {
            return new UmlRelation(project.getUmlClass(jsonObject.getString(JSON_RELATION_ORIGIN_CLASS)),
                    project.getUmlClass(jsonObject.getString(JSON_RELATION_END_CLASS)),
                    UmlRelationType.valueOf(jsonObject.getString(JSON_RELATION_TYPE)));
        } catch (JSONException e) {
            return null;
        }
    }

}
