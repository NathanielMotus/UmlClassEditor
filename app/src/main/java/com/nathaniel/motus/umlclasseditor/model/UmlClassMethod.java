package com.nathaniel.motus.umlclasseditor.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UmlClassMethod {

    private String mName;
    private Visibility mVisibility=Visibility.PRIVATE;
    private boolean mStatic =false;
    private UmlType mUmlType;
    private TypeMultiplicity mTypeMultiplicity=TypeMultiplicity.SINGLE;
    private int mArrayDimension =1;
    private ArrayList<MethodParameter> mParameters;

    public static final String JSON_CLASS_METHOD_NAME="ClassMethodName";
    public static final String JSON_CLASS_METHOD_VISIBILITY="ClassMethodVisibility";
    public static final String JSON_CLASS_METHOD_STATIC="ClassMethodStatic";
    public static final String JSON_CLASS_METHOD_TYPE="ClassMethodType";
    public static final String JSON_CLASS_METHOD_TYPE_MULTIPLICITY="ClassMethodTypeMultiplicity";
    public static final String JSON_CLASS_METHOD_ARRAY_DIMENSION="ClassMethodArrayDimension";
    public static final String JSON_CLASS_METHOD_PARAMETERS="ClassMethodParameters";

//    **********************************************************************************************
//    Constructors
//    **********************************************************************************************

    public UmlClassMethod(String name, Visibility visibility, boolean aStatic, UmlType umlType, TypeMultiplicity typeMultiplicity, int arrayDimension) {
        mName = name;
        mVisibility = visibility;
        mStatic = aStatic;
        mUmlType = umlType;
        mTypeMultiplicity = typeMultiplicity;
        mArrayDimension = arrayDimension;
        mParameters=new ArrayList<>();
    }

    public UmlClassMethod(String mName, Visibility mVisibility, boolean mStatic, UmlType mUmlType, TypeMultiplicity mTypeMultiplicity, int mArrayDimension, ArrayList<MethodParameter> mParameters) {
        this.mName = mName;
        this.mVisibility = mVisibility;
        this.mStatic = mStatic;
        this.mUmlType = mUmlType;
        this.mTypeMultiplicity = mTypeMultiplicity;
        this.mArrayDimension = mArrayDimension;
        this.mParameters = mParameters;
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

    public ArrayList<MethodParameter> getParameters() {
        return mParameters;
    }

//    **********************************************************************************************
//    Modifiers
//    **********************************************************************************************

    public void addParemeter(MethodParameter parameter) {
        mParameters.add(parameter);
    }

    public void removeParameter(MethodParameter parameter) {
        mParameters.remove(parameter);
    }

//    **********************************************************************************************
//    JSON methods
//    **********************************************************************************************

    public JSONObject toJSONObject() {
        JSONObject jsonObject=new JSONObject();

        try {
            jsonObject.put(JSON_CLASS_METHOD_NAME, mName);
            jsonObject.put(JSON_CLASS_METHOD_VISIBILITY, mVisibility);
            jsonObject.put(JSON_CLASS_METHOD_STATIC, mStatic);
            jsonObject.put(JSON_CLASS_METHOD_TYPE, mUmlType.getName());
            jsonObject.put(JSON_CLASS_METHOD_TYPE_MULTIPLICITY, mTypeMultiplicity);
            jsonObject.put(JSON_CLASS_METHOD_ARRAY_DIMENSION, mArrayDimension);
            jsonObject.put(JSON_CLASS_METHOD_PARAMETERS, getAttributesToJSONArray());
            return jsonObject;
        } catch (JSONException jsonException) {
            return null;
        }
    }

    public static UmlClassMethod fromJSONObject(JSONObject jsonObject, UmlProject project) {
        try {
            return new UmlClassMethod(jsonObject.getString(JSON_CLASS_METHOD_NAME),
                    Visibility.valueOf(jsonObject.getString(JSON_CLASS_METHOD_VISIBILITY)),
                    jsonObject.getBoolean(JSON_CLASS_METHOD_STATIC),
                    UmlType.valueOf(jsonObject.getString(JSON_CLASS_METHOD_TYPE), project.getUmlTypes()),
                    TypeMultiplicity.valueOf(jsonObject.getString(JSON_CLASS_METHOD_TYPE_MULTIPLICITY)),
                    jsonObject.getInt(JSON_CLASS_METHOD_ARRAY_DIMENSION),
                    getAttributesFromJSONArray(jsonObject.getJSONArray(JSON_CLASS_METHOD_PARAMETERS),project));
        } catch (JSONException jsonException) {
            return null;
        }
    }

    private JSONArray getAttributesToJSONArray() {
        JSONArray jsonArray =new JSONArray();

        for (MethodParameter p : this.mParameters) jsonArray.put(p.toJSONObject());
        return jsonArray;
    }

    private static ArrayList<MethodParameter> getAttributesFromJSONArray(JSONArray jsonArray,UmlProject project) {
        ArrayList<MethodParameter> methodParameters=new ArrayList<>();

        JSONObject jsonParameter=(JSONObject)(jsonArray.remove(0));
        while (jsonParameter != null) {
            methodParameters.add(MethodParameter.fromJSONObject(jsonParameter, project));
            jsonParameter=(JSONObject)(jsonArray.remove(0));
        }
        return methodParameters;
    }
}
