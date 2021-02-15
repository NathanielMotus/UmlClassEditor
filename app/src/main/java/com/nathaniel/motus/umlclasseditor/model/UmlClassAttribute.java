package com.nathaniel.motus.umlclasseditor.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UmlClassAttribute implements AdapterItem{

    private String mName;
    private int mAttributeIndex;
    private Visibility mVisibility=Visibility.PRIVATE;
    private boolean mStatic=false;
    private boolean mFinal =false;
    private UmlType mUmlType;
    private TypeMultiplicity mTypeMultiplicity=TypeMultiplicity.SINGLE;
    private int mArrayDimension =1; //only used if it's a table

    public static final String JSON_CLASS_ATTRIBUTE_NAME="ClassAttributeName";
    public static final String JSON_CLASS_ATTRIBUTE_INDEX="ClassAttributeIndex";
    public static final String JSON_CLASS_ATTRIBUTE_VISIBILITY ="ClassAttributeVisibility";
    public static final String JSON_CLASS_ATTRIBUTE_STATIC="ClassAttributeStatic";
    public static final String JSON_CLASS_ATTRIBUTE_FINAL="ClassAttributeFinal";
    public static final String JSON_CLASS_ATTRIBUTE_TYPE="ClassAttributeType";
    public static final String JSON_CLASS_ATTRIBUTE_TYPE_MULTIPLICITY="ClassAttributeTypeMultiplicity";
    public static final String JSON_CLASS_ATTRIBUTE_ARRAY_DIMENSION="ClassAttributeArrayDimension";


//    **********************************************************************************************
//    Constructors
//    **********************************************************************************************

    public UmlClassAttribute(String name,int attributeIndex, Visibility visibility, boolean aStatic, boolean aFinal, UmlType umlType, TypeMultiplicity typeMultiplicity, int arrayDimension) {
        mName = name;
        mAttributeIndex=attributeIndex;
        mVisibility = visibility;
        mStatic = aStatic;
        mFinal = aFinal;
        mUmlType = umlType;
        mTypeMultiplicity = typeMultiplicity;
        mArrayDimension = arrayDimension;
    }

    public UmlClassAttribute(int attributeIndex) {
        mAttributeIndex=attributeIndex;
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

    public String getAttributeCompleteString() {
        //return attribute name with conventional modifiers

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

        switch (mTypeMultiplicity) {
            case COLLECTION:
                completeString=completeString+mName+" : <"+mUmlType.getName()+">";
                break;
            case ARRAY:
                completeString=completeString+mName+" : ["+mUmlType.getName()+"]^"+ mArrayDimension;
                break;
            default:
                completeString=completeString+mName+" : "+mUmlType.getName();
                break;
        }
        return completeString;
    }

    public static int indexOf(String attributeName, ArrayList<UmlClassAttribute> attributes) {
        for (UmlClassAttribute a:attributes)
            if (attributeName.equals(a.mName)) return attributes.indexOf(a);

        return -1;
    }

    public int getAttributeIndex() {
        return mAttributeIndex;
    }

    public void setAttributeIndex(int attributeIndex) {
        mAttributeIndex = attributeIndex;
    }

    //todo : when creating new attribute, check if it already exists
    //todo : when creating new method, check if it already exists with same signature

//    **********************************************************************************************
//    JSON methods
//    **********************************************************************************************

    public JSONObject toJSONObject() {
        JSONObject jsonObject=new JSONObject();

        try {
            jsonObject.put(JSON_CLASS_ATTRIBUTE_NAME, mName);
            jsonObject.put(JSON_CLASS_ATTRIBUTE_INDEX,mAttributeIndex);
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

    public static UmlClassAttribute fromJSONObject(JSONObject jsonObject) {
        try {
            if (UmlType.valueOf(jsonObject.getString(JSON_CLASS_ATTRIBUTE_TYPE),UmlType.getUmlTypes())==null)
                UmlType.createUmlType(jsonObject.getString(JSON_CLASS_ATTRIBUTE_TYPE), UmlType.TypeLevel.CUSTOM);

            return new UmlClassAttribute(jsonObject.getString(JSON_CLASS_ATTRIBUTE_NAME),
                    jsonObject.getInt(JSON_CLASS_ATTRIBUTE_INDEX),
                    Visibility.valueOf(jsonObject.getString(JSON_CLASS_ATTRIBUTE_VISIBILITY)),
                    jsonObject.getBoolean(JSON_CLASS_ATTRIBUTE_STATIC),
                    jsonObject.getBoolean(JSON_CLASS_ATTRIBUTE_FINAL),
                    UmlType.valueOf(jsonObject.getString(JSON_CLASS_ATTRIBUTE_TYPE), UmlType.getUmlTypes()),
                    TypeMultiplicity.valueOf(jsonObject.getString(JSON_CLASS_ATTRIBUTE_TYPE_MULTIPLICITY)),
                    jsonObject.getInt(JSON_CLASS_ATTRIBUTE_ARRAY_DIMENSION));
        } catch (JSONException jsonException) {
            return null;
        }
    }
}
