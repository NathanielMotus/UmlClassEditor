package com.nathaniel.motus.umlclasseditor.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class MethodParameter {

    private String mName;
    private UmlType mUmlType;
    private TypeMultiplicity mTypeMultiplicity=TypeMultiplicity.SINGLE;
    private int mArrayDimension =1;

    public static final String JSON_METHOD_PARAMETER_NAME="MethodParameterName";
    public static final String JSON_METHOD_PARAMETER_TYPE="MethodParameterType";
    public static final String JSON_METHOD_PARAMETER_TYPE_MULTIPLICITY="MethodParameterTypeMultiplicity";
    public static final String JSON_METHOD_PARAMETER_ARRAY_DIMENSION="MethodParameterArrayDimension";

//    **********************************************************************************************
//    Constructors
//    **********************************************************************************************

    public MethodParameter(String name, UmlType umlType, TypeMultiplicity typeMultiplicity, int arrayDimension) {
        mName = name;
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

    public JSONObject toJSONObject(){
        JSONObject jsonObject=new JSONObject();

        try {
            jsonObject.put(JSON_METHOD_PARAMETER_NAME, mName);
            jsonObject.put(JSON_METHOD_PARAMETER_TYPE, mUmlType.getName());
            jsonObject.put(JSON_METHOD_PARAMETER_TYPE_MULTIPLICITY, mTypeMultiplicity);
            jsonObject.put(JSON_METHOD_PARAMETER_ARRAY_DIMENSION, mArrayDimension);
            return jsonObject;
        } catch (JSONException jsonException) {
            return null;
        }
    }

    public static MethodParameter fromJSONObject(JSONObject jsonObject, UmlProject project) {
        try {
            return new MethodParameter(jsonObject.getString(JSON_METHOD_PARAMETER_NAME),
                    UmlType.valueOf(jsonObject.getString(JSON_METHOD_PARAMETER_TYPE),UmlType.getUmlTypes()),
                    TypeMultiplicity.valueOf(jsonObject.getString(JSON_METHOD_PARAMETER_TYPE_MULTIPLICITY)),
                    jsonObject.getInt(JSON_METHOD_PARAMETER_ARRAY_DIMENSION));
        } catch (JSONException jsonException) {
            return null;
        }
    }
}
