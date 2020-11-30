package com.nathaniel.motus.umlclasseditor.model;

import org.json.JSONException;
import org.json.JSONObject;

public class UmlClassAttribute {

    private String mName;
    private Visibility mVisibility=Visibility.PRIVATE;
    private boolean mStatic=false;
    private boolean mFinal =false;
    private UmlType mUmlType;
    private TypeMultiplicity mTypeMultiplicity=TypeMultiplicity.SINGLE;
    private int mArrayDimension =1; //only used if it's a table

    public static final String JSON_CLASS_ATTRIBUTE_NAME="ClassAttributeName";
    public static final String JSON_CLASS_ATTRIBUTE_VISIBILITY ="ClassAttributeVisibility";
    public static final String JSON_CLASS_ATTRIBUTE_STATIC="ClassAttributeStatic";
    public static final String JSON_CLASS_ATTRIBUTE_FINAL="ClassAttributeFinal";
    public static final String JSON_CLASS_ATTRIBUTE_TYPE="ClassAttributeType";
    public static final String JSON_CLASS_ATTRIBUTE_TYPE_MULTIPLICITY="ClassAttributeTypeMultiplicity";
    public static final String JSON_CLASS_ATTRIBUTE_ARRAY_DIMENSION="ClassAttributeArrayDimension";


//    **********************************************************************************************
//    Constructors
//    **********************************************************************************************

    public UmlClassAttribute(String name, Visibility visibility, boolean aStatic, boolean aFinal, UmlType umlType, TypeMultiplicity typeMultiplicity, int arrayDimension) {
        mName = name;
        mVisibility = visibility;
        mStatic = aStatic;
        mFinal = aFinal;
        mUmlType = umlType;
        mTypeMultiplicity = typeMultiplicity;
        mArrayDimension = arrayDimension;
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

    public Visibility getVisibility() {
        return mVisibility;
    }

    public void setVisibility(Visibility visibility) {
        mVisibility = visibility;
    }

    public boolean isStatic() {
        return mStatic;
    }

    public void setStatic(boolean aStatic) {
        mStatic = aStatic;
    }

    public boolean isFinal() {
        return mFinal;
    }

    public void setFinal(boolean aFinal) {
        mFinal = aFinal;
    }

    public UmlType getUmlType() {
        return mUmlType;
    }

    public void setUmlType(UmlType umlType) {
        mUmlType = umlType;
    }

    public TypeMultiplicity getTypeMultiplicity() {
        return mTypeMultiplicity;
    }

    public void setTypeMultiplicity(TypeMultiplicity typeMultiplicity) {
        mTypeMultiplicity = typeMultiplicity;
    }

    public int getArrayDimension() {
        return mArrayDimension;
    }

    public void setArrayDimension(int arrayDimension) {
        mArrayDimension = arrayDimension;
    }

//    **********************************************************************************************
//    JSON methods
//    **********************************************************************************************

    public JSONObject toJSONObject() {
        JSONObject jsonObject=new JSONObject();

        try {
            jsonObject.put(JSON_CLASS_ATTRIBUTE_NAME, mName);
            jsonObject.put(JSON_CLASS_ATTRIBUTE_VISIBILITY, mVisibility.toString());
            jsonObject.put(JSON_CLASS_ATTRIBUTE_STATIC, mStatic);
            jsonObject.put(JSON_CLASS_ATTRIBUTE_FINAL, mFinal);
            jsonObject.put(JSON_CLASS_ATTRIBUTE_TYPE, mUmlType.getName());
            jsonObject.put(JSON_CLASS_ATTRIBUTE_TYPE_MULTIPLICITY, mTypeMultiplicity.toString());
            jsonObject.put(JSON_CLASS_ATTRIBUTE_ARRAY_DIMENSION, mArrayDimension);
            return jsonObject;
        } catch (JSONException jsonException) {
            return null;
        }
    }

    public static UmlClassAttribute fromJSONObject(JSONObject jsonObject, UmlProject project) {
        try {
            return new UmlClassAttribute(jsonObject.getString(JSON_CLASS_ATTRIBUTE_NAME),
                    Visibility.valueOf(jsonObject.getString(JSON_CLASS_ATTRIBUTE_VISIBILITY)),
                    jsonObject.getBoolean(JSON_CLASS_ATTRIBUTE_STATIC),
                    jsonObject.getBoolean(JSON_CLASS_ATTRIBUTE_FINAL),
                    UmlType.valueOf(jsonObject.getString(JSON_CLASS_ATTRIBUTE_TYPE), project.getUmlTypes()),
                    TypeMultiplicity.valueOf(jsonObject.getString(JSON_CLASS_ATTRIBUTE_TYPE_MULTIPLICITY)),
                    jsonObject.getInt(JSON_CLASS_ATTRIBUTE_ARRAY_DIMENSION));
        } catch (JSONException jsonException) {
            return null;
        }
    }
}
