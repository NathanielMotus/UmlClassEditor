package com.nathaniel.motus.umlclasseditor.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class MethodParameter implements AdapterItem{

    private String mName;
    private int mParameterIndex;
    private UmlType mUmlType;
    private TypeMultiplicity mTypeMultiplicity=TypeMultiplicity.SINGLE;
    private int mArrayDimension =1;

    public static final String JSON_METHOD_PARAMETER_NAME="MethodParameterName";
    public static final String JSON_METHOD_PARAMETER_TYPE="MethodParameterType";
    public static final String JSON_METHOD_PARAMETER_TYPE_MULTIPLICITY="MethodParameterTypeMultiplicity";
    public static final String JSON_METHOD_PARAMETER_ARRAY_DIMENSION="MethodParameterArrayDimension";
    public static final String JSON_METHOD_PARAMETER_INDEX="MethodParameterIndex";

//    **********************************************************************************************
//    Constructors
//    **********************************************************************************************

    public MethodParameter(String name,int parameterIndex, UmlType umlType, TypeMultiplicity typeMultiplicity, int arrayDimension) {
        mName = name;
        mParameterIndex=parameterIndex;
        mUmlType = umlType;
        mTypeMultiplicity = typeMultiplicity;
        mArrayDimension = arrayDimension;
    }

    public MethodParameter(int parameterIndex) {
        mParameterIndex=parameterIndex;
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

    public int getParameterIndex() {
        return mParameterIndex;
    }

    public void setParameterIndex(int parameterIndex) {
        mParameterIndex = parameterIndex;
    }

//    **********************************************************************************************
//    JSON methods
//    **********************************************************************************************

    public JSONObject toJSONObject(){
        JSONObject jsonObject=new JSONObject();

        try {
            jsonObject.put(JSON_METHOD_PARAMETER_NAME, mName);
            jsonObject.put(JSON_METHOD_PARAMETER_INDEX,mParameterIndex);
            jsonObject.put(JSON_METHOD_PARAMETER_TYPE, mUmlType.getName());
            jsonObject.put(JSON_METHOD_PARAMETER_TYPE_MULTIPLICITY, mTypeMultiplicity);
            jsonObject.put(JSON_METHOD_PARAMETER_ARRAY_DIMENSION, mArrayDimension);
            return jsonObject;
        } catch (JSONException jsonException) {
            return null;
        }
    }

    public static MethodParameter fromJSONObject(JSONObject jsonObject) {
        try {
            if (UmlType.valueOf(jsonObject.getString(JSON_METHOD_PARAMETER_TYPE),UmlType.getUmlTypes())==null)
                UmlType.createUmlType(jsonObject.getString(JSON_METHOD_PARAMETER_TYPE), UmlType.TypeLevel.CUSTOM);
            return new MethodParameter(jsonObject.getString(JSON_METHOD_PARAMETER_NAME),
                    jsonObject.getInt(JSON_METHOD_PARAMETER_INDEX),
                    UmlType.valueOf(jsonObject.getString(JSON_METHOD_PARAMETER_TYPE),UmlType.getUmlTypes()),
                    TypeMultiplicity.valueOf(jsonObject.getString(JSON_METHOD_PARAMETER_TYPE_MULTIPLICITY)),
                    jsonObject.getInt(JSON_METHOD_PARAMETER_ARRAY_DIMENSION));
        } catch (JSONException jsonException) {
            return null;
        }
    }
}
