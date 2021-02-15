package com.nathaniel.motus.umlclasseditor.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UmlClassMethod implements AdapterItem{

    private String mName;
    private int mMethodIndex;
    private Visibility mVisibility=Visibility.PRIVATE;
    private boolean mStatic =false;
    private UmlType mUmlType;
    private TypeMultiplicity mTypeMultiplicity=TypeMultiplicity.SINGLE;
    private int mArrayDimension =1;
    private ArrayList<MethodParameter> mParameters;
    private int mParameterCount;

    public static final String JSON_CLASS_METHOD_NAME="ClassMethodName";
    public static final String JSON_CLASS_METHOD_VISIBILITY="ClassMethodVisibility";
    public static final String JSON_CLASS_METHOD_STATIC="ClassMethodStatic";
    public static final String JSON_CLASS_METHOD_TYPE="ClassMethodType";
    public static final String JSON_CLASS_METHOD_TYPE_MULTIPLICITY="ClassMethodTypeMultiplicity";
    public static final String JSON_CLASS_METHOD_ARRAY_DIMENSION="ClassMethodArrayDimension";
    public static final String JSON_CLASS_METHOD_PARAMETERS="ClassMethodParameters";
    public static final String JSON_CLASS_METHOD_PARAMETER_COUNT="ClassMethodParameterCount";
    public static final String JSON_CLASS_METHOD_INDEX="ClassMethodIndex";

//    **********************************************************************************************
//    Constructors
//    **********************************************************************************************

    public UmlClassMethod(String name,int methodIndex, Visibility visibility, boolean aStatic, UmlType umlType, TypeMultiplicity typeMultiplicity, int arrayDimension) {
        mName = name;
        mMethodIndex=methodIndex;
        mVisibility = visibility;
        mStatic = aStatic;
        mUmlType = umlType;
        mTypeMultiplicity = typeMultiplicity;
        mArrayDimension = arrayDimension;
        mParameters=new ArrayList<>();
        mParameterCount=0;
    }

    public UmlClassMethod(String mName,int methodIndex, Visibility mVisibility, boolean mStatic, UmlType mUmlType, TypeMultiplicity mTypeMultiplicity, int mArrayDimension, ArrayList<MethodParameter> mParameters, int parameterCount) {
        this.mName = mName;
        this.mMethodIndex=methodIndex;
        this.mVisibility = mVisibility;
        this.mStatic = mStatic;
        this.mUmlType = mUmlType;
        this.mTypeMultiplicity = mTypeMultiplicity;
        this.mArrayDimension = mArrayDimension;
        this.mParameters = mParameters;
        this.mParameterCount=parameterCount;
    }

    public UmlClassMethod(int methodIndex) {
        mMethodIndex=methodIndex;
        mParameterCount=0;
        mParameters=new ArrayList<>();
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

    public int getMethodIndex() {
        return mMethodIndex;
    }

    public void setMethodIndex(int methodIndex) {
        mMethodIndex = methodIndex;
    }

    public int getParameterCount() {
        return mParameterCount;
    }

    public void setParameterCount(int parameterCount) {
        mParameterCount = parameterCount;
    }

    public String getMethodCompleteString() {
        //return method name with conventional modifiers

        String completeString=new String();

        switch (mVisibility) {
            case PUBLIC:
                completeString="+";
                break;
            case PROTECTED:
                completeString="~";
                break;
            default:
                completeString="-";
                break;
        }

        completeString=completeString+mName+"(";

        for (MethodParameter p:mParameters) {
            completeString = completeString + p.getName();
            if (mParameters.indexOf(p)!=mParameters.size()-1)
                completeString=completeString+", ";
        }

        completeString=completeString+") : ";

        switch (mTypeMultiplicity) {
            case COLLECTION:
                completeString=completeString+"<"+mUmlType.getName()+">";
                break;
            case ARRAY:
                completeString=completeString+"["+mUmlType.getName()+"]^"+mArrayDimension;
                break;
            default:
                completeString=completeString+mUmlType.getName();
        }

        return completeString;
    }

    public static int indexOf(String methodName, ArrayList<UmlClassMethod> methods) {
        for (UmlClassMethod m:methods)
            if (methodName.equals(m.mName)) return methods.indexOf(m);

        return -1;
    }

//    **********************************************************************************************
//    Modifiers
//    **********************************************************************************************

    public void addParameter(MethodParameter parameter) {
        mParameters.add(parameter);
    }

    public void removeParameter(MethodParameter parameter) {
        mParameters.remove(parameter);
    }

    public void incrementParameterCount() {
        mParameterCount++;
    }

//    **********************************************************************************************
//    JSON methods
//    **********************************************************************************************

    public JSONObject toJSONObject() {
        JSONObject jsonObject=new JSONObject();

        try {
            jsonObject.put(JSON_CLASS_METHOD_NAME, mName);
            jsonObject.put(JSON_CLASS_METHOD_INDEX,mMethodIndex);
            jsonObject.put(JSON_CLASS_METHOD_VISIBILITY, mVisibility);
            jsonObject.put(JSON_CLASS_METHOD_STATIC, mStatic);
            jsonObject.put(JSON_CLASS_METHOD_TYPE, mUmlType.getName());
            jsonObject.put(JSON_CLASS_METHOD_TYPE_MULTIPLICITY, mTypeMultiplicity);
            jsonObject.put(JSON_CLASS_METHOD_ARRAY_DIMENSION, mArrayDimension);
            jsonObject.put(JSON_CLASS_METHOD_PARAMETERS, getParametersToJSONArray());
            jsonObject.put(JSON_CLASS_METHOD_PARAMETER_COUNT,mParameterCount);
            return jsonObject;
        } catch (JSONException jsonException) {
            return null;
        }
    }

    public static UmlClassMethod fromJSONObject(JSONObject jsonObject) {
        try {
            if (UmlType.valueOf(jsonObject.getString(JSON_CLASS_METHOD_TYPE),UmlType.getUmlTypes())==null)
                UmlType.createUmlType(jsonObject.getString(JSON_CLASS_METHOD_TYPE), UmlType.TypeLevel.CUSTOM);

            return new UmlClassMethod(jsonObject.getString(JSON_CLASS_METHOD_NAME),
                    jsonObject.getInt(JSON_CLASS_METHOD_INDEX),
                    Visibility.valueOf(jsonObject.getString(JSON_CLASS_METHOD_VISIBILITY)),
                    jsonObject.getBoolean(JSON_CLASS_METHOD_STATIC),
                    UmlType.valueOf(jsonObject.getString(JSON_CLASS_METHOD_TYPE), UmlType.getUmlTypes()),
                    TypeMultiplicity.valueOf(jsonObject.getString(JSON_CLASS_METHOD_TYPE_MULTIPLICITY)),
                    jsonObject.getInt(JSON_CLASS_METHOD_ARRAY_DIMENSION),
                    getParametersFromJSONArray(jsonObject.getJSONArray(JSON_CLASS_METHOD_PARAMETERS)),
                    jsonObject.getInt(JSON_CLASS_METHOD_PARAMETER_COUNT));
        } catch (JSONException jsonException) {
            return null;
        }
    }

    private JSONArray getParametersToJSONArray() {
        JSONArray jsonArray =new JSONArray();

        for (MethodParameter p : this.mParameters) jsonArray.put(p.toJSONObject());
        return jsonArray;
    }

    private static ArrayList<MethodParameter> getParametersFromJSONArray(JSONArray jsonArray) {
        ArrayList<MethodParameter> methodParameters=new ArrayList<>();

        JSONObject jsonParameter=(JSONObject)(jsonArray.remove(0));
        while (jsonParameter != null) {
            methodParameters.add(MethodParameter.fromJSONObject(jsonParameter));
            jsonParameter=(JSONObject)(jsonArray.remove(0));
        }
        return methodParameters;
    }
}
