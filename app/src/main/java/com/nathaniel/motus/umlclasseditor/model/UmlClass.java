package com.nathaniel.motus.umlclasseditor.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UmlClass extends UmlType {

    public enum UmlClassType{JAVA_CLASS,ABSTRACT_CLASS,INTERFACE,ENUM}

    private UmlClassType mUmlClassType=UmlClassType.JAVA_CLASS;
    private ArrayList<UmlClassAttribute> mAttributeList;
    private ArrayList<UmlClassMethod> mMethodList;
    private ArrayList<String> mValueList; //in case of an Enum

    private float mUmlClassNormalXPos;
    private float mUmlClassNormalYPos;
    private float mUmlClassNormalWidth;
    private float mUmlClassNormalHeight;

    private static final String JSON_CLASS_NAME="ClassName";
    private static final String JSON_CLASS_CLASS_TYPE="ClassClassType";
    private static final String JSON_CLASS_ATTRIBUTES="ClassAttributes";
    private static final String JSON_CLASS_METHODS="ClassMethods";
    private static final String JSON_CLASS_VALUES="ClassValues";
    private static final String JSON_CLASS_NORMAL_XPOS="ClassNormalXPos";
    private static final String JSON_CLASS_NORMAL_YPOS="ClassNormalYPos";

//    **********************************************************************************************
//    Constructors
//    **********************************************************************************************

    public UmlClass(String name) {
        this(name,UmlClassType.JAVA_CLASS);
    }

    public UmlClass(String name, UmlClassType umlClassType) {
        super(name,TypeLevel.PROJECT);
        mUmlClassType = umlClassType;
        mAttributeList= new ArrayList<>();
        mMethodList=new ArrayList<>();
        mValueList=new ArrayList<>();
    }

    public UmlClass(String name, UmlClassType umlClassType, ArrayList<UmlClassAttribute> attributeList, ArrayList<UmlClassMethod> methodList, ArrayList<String> valueList, float umlClassNormalXPos, float umlClassNormalYPos) {
        super(name,TypeLevel.PROJECT);
        mUmlClassType = umlClassType;
        mAttributeList = attributeList;
        mMethodList = methodList;
        mValueList = valueList;
        mUmlClassNormalXPos = umlClassNormalXPos;
        mUmlClassNormalYPos = umlClassNormalYPos;
    }

//    **********************************************************************************************
//    Getters and setters
//    **********************************************************************************************

    public UmlClassType getUmlClassType() {
        return mUmlClassType;
    }

    public void setUmlClassType(UmlClassType umlClassType) {
        mUmlClassType = umlClassType;
    }

    public float getUmlClassNormalXPos() {
        return mUmlClassNormalXPos;
    }

    public void setUmlClassNormalXPos(float umlClassNormalXPos) {
        mUmlClassNormalXPos = umlClassNormalXPos;
    }

    public float getUmlClassNormalYPos() {
        return mUmlClassNormalYPos;
    }

    public void setUmlClassNormalYPos(float umlClassNormalYPos) {
        mUmlClassNormalYPos = umlClassNormalYPos;
    }

    public float getUmlClassNormalWidth() {
        return mUmlClassNormalWidth;
    }

    public void setUmlClassNormalWidth(float umlClassNormalWidth) {
        mUmlClassNormalWidth = umlClassNormalWidth;
    }

    public float getUmlClassNormalHeight() {
        return mUmlClassNormalHeight;
    }

    public void setUmlClassNormalHeight(float umlClassNormalHeight) {
        mUmlClassNormalHeight = umlClassNormalHeight;
    }

    public ArrayList<UmlClassAttribute> getAttributeList() {
        return mAttributeList;
    }

    public ArrayList<UmlClassMethod> getMethodList() {
        return mMethodList;
    }

    public ArrayList<String> getValueList() {
        return mValueList;
    }

    public float getNormalRightEnd() {
        return mUmlClassNormalXPos+mUmlClassNormalWidth;
    }

    public float getNormalBottomEnd() {
        return mUmlClassNormalYPos+mUmlClassNormalHeight;
    }

    public void setAttributeList(ArrayList<UmlClassAttribute> attributeList) {
        mAttributeList = attributeList;
    }

    public void setMethodList(ArrayList<UmlClassMethod> methodList) {
        mMethodList = methodList;
    }

    public void setValueList(ArrayList<String> valueList) {
        mValueList = valueList;
    }

    @Override
    public void setName(String name) {
        super.setName(name);
    }

    @Override
    public String getName() {
        return super.getName();
    }

//    **********************************************************************************************
//    Modifiers
//    **********************************************************************************************

    public void addMethod(UmlClassMethod method) {
        mMethodList.add(method);
    }

    public void removeMethod(UmlClassMethod method) {
        mMethodList.remove(method);
    }

    public void addAttribute(UmlClassAttribute attribute) {
        mAttributeList.add(attribute);
    }

    public void removeAttribute(UmlClassAttribute attribute) {
        mAttributeList.remove(attribute);
    }

    public void addValue(String value) {
        mValueList.add(value);
    }

    public void removeValue(String value) {
        mValueList.remove(value);
    }

//    **********************************************************************************************
//    Test methods
//    **********************************************************************************************

    public boolean containsPoint(float absoluteX, float absoluteY) {
        return  (absoluteX<=mUmlClassNormalXPos+mUmlClassNormalWidth &&
                absoluteX >= mUmlClassNormalXPos &&
                absoluteY<=mUmlClassNormalYPos+mUmlClassNormalHeight &&
                absoluteY>=mUmlClassNormalYPos);
    }

    public boolean isSouthOf(UmlClass umlClass) {
        //is this in South quarter of umlClass ?
        return (this.getUmlClassNormalYPos()>=umlClass.getNormalBottomEnd() &&
                this.getNormalRightEnd()>=umlClass.getUmlClassNormalXPos()-this.getUmlClassNormalYPos()+umlClass.getNormalBottomEnd() &&
                this.getUmlClassNormalXPos()<=umlClass.getNormalRightEnd()+this.getUmlClassNormalYPos()-umlClass.getNormalBottomEnd());
    }

    public boolean isNorthOf(UmlClass umlClass) {
        //is this in North quarter of umlClass ?
        return (this.getNormalBottomEnd()<=umlClass.getUmlClassNormalYPos() &&
                this.getNormalRightEnd()>=umlClass.getUmlClassNormalXPos()-umlClass.getUmlClassNormalYPos()+this.getNormalBottomEnd() &&
                this.getUmlClassNormalXPos()<=umlClass.getNormalRightEnd()+umlClass.getUmlClassNormalYPos()-this.getNormalBottomEnd());
    }

    public boolean isWestOf(UmlClass umlClass) {
        //is this in West quarter of umlClass ?
        return (this.getNormalRightEnd()<=umlClass.getUmlClassNormalXPos() &&
                this.getNormalBottomEnd()>=umlClass.getUmlClassNormalYPos()-umlClass.getUmlClassNormalXPos()+this.getNormalRightEnd() &&
                this.getUmlClassNormalYPos()<=umlClass.getNormalBottomEnd()+umlClass.getUmlClassNormalXPos()-this.getNormalRightEnd());
    }

    public boolean isEastOf(UmlClass umlClass) {
        //is this in East Quarter of umlClass ?
        return (this.getUmlClassNormalXPos()>=umlClass.getNormalRightEnd() &&
                this.getNormalBottomEnd()>=umlClass.getUmlClassNormalYPos()-this.getUmlClassNormalXPos()+umlClass.getNormalRightEnd() &&
                this.getUmlClassNormalYPos()<=umlClass.getNormalBottomEnd()+this.getUmlClassNormalXPos()-umlClass.getNormalRightEnd());
    }

    public boolean isInvolvedInRelation(UmlRelation umlRelation) {
        return (this==umlRelation.getRelationOriginClass()||this==umlRelation.getRelationEndClass());
    }

    public boolean alreadyExists(UmlProject inProject) {
        //check whether class name already exists

        for (UmlClass c:inProject.getUmlClasses())
            if (this.getName().equals(c.getName())) return true;

        return false;
    }

//    **********************************************************************************************
//    JSON methods
//    **********************************************************************************************

    public JSONObject toJSONObject() {
        JSONObject jsonObject =new JSONObject();

        try {
            jsonObject.put(JSON_CLASS_NAME, this.getName().toString());
            jsonObject.put(JSON_CLASS_CLASS_TYPE, mUmlClassType);
            jsonObject.put(JSON_CLASS_ATTRIBUTES, getAttributesToJSONArray());
            jsonObject.put(JSON_CLASS_METHODS, getMethodsToJSONArray());
            jsonObject.put(JSON_CLASS_VALUES, getValuesToJSONArray());
            jsonObject.put(JSON_CLASS_NORMAL_XPOS, mUmlClassNormalXPos);
            jsonObject.put(JSON_CLASS_NORMAL_YPOS, mUmlClassNormalYPos);
            return jsonObject;
        } catch (JSONException e) {
            return null;
        }
    }

    //we need to first create classes with their names
    //in order to have them usable to create UmlTyped objects

    public static UmlClass fromJSONObject(JSONObject jsonObject) {
        try {
            return new UmlClass(jsonObject.getString(JSON_CLASS_NAME));
        } catch (JSONException e) {
            return null;
        }
    }

    //and then populate them with their attributes

    public static void populateUmlClassFromJSONObject(JSONObject jsonObject, UmlProject project) {
        //read a class JSONObject and populate the already created class

        try {
            UmlClass umlClass=project.getUmlClass(jsonObject.getString(JSON_CLASS_NAME));

            umlClass.setUmlClassType(UmlClassType.valueOf(jsonObject.getString(JSON_CLASS_CLASS_TYPE)));
            umlClass.setAttributeList(getAttributesFromJSONArray(jsonObject.getJSONArray(JSON_CLASS_ATTRIBUTES)));
            umlClass.setMethodList(getMethodsFromJSONArray(jsonObject.getJSONArray(JSON_CLASS_METHODS)));
            umlClass.setValueList(getValuesFromJSONArray(jsonObject.getJSONArray(JSON_CLASS_VALUES)));
            umlClass.setUmlClassNormalXPos(jsonObject.getInt(JSON_CLASS_NORMAL_XPOS));
            umlClass.setUmlClassNormalYPos(jsonObject.getInt(JSON_CLASS_NORMAL_YPOS));

        } catch (JSONException ignored) {

        }
    }

    private JSONArray getAttributesToJSONArray() {
        JSONArray jsonArray = new JSONArray();

        for (UmlClassAttribute a:mAttributeList) jsonArray.put(a.toJSONObject());
        return jsonArray;
    }

    private static ArrayList<UmlClassAttribute> getAttributesFromJSONArray(JSONArray jsonArray) {
        ArrayList<UmlClassAttribute> umlClassAttributes = new ArrayList<>();

        JSONObject jsonAttribute=(JSONObject) jsonArray.remove(0);
        while (jsonAttribute != null) {
            umlClassAttributes.add(UmlClassAttribute.fromJSONObject(jsonAttribute));
            jsonAttribute = (JSONObject) jsonArray.remove(0);
        }
        return umlClassAttributes;
    }

    private JSONArray getMethodsToJSONArray() {
        JSONArray jsonArray=new JSONArray();

        for (UmlClassMethod m:mMethodList) jsonArray.put(m.toJSONObject());
        return jsonArray;
    }

    private static ArrayList<UmlClassMethod> getMethodsFromJSONArray(JSONArray jsonArray) {
        ArrayList<UmlClassMethod> umlClassMethods = new ArrayList<>();

        JSONObject jsonMethod=(JSONObject)jsonArray.remove(0);
        while (jsonMethod != null) {
            umlClassMethods.add(UmlClassMethod.fromJSONObject(jsonMethod));
            jsonMethod=(JSONObject)jsonArray.remove(0);
        }
        return umlClassMethods;
    }

    private JSONArray getValuesToJSONArray() {
        JSONArray jsonArray=new JSONArray();

        for (String s:mValueList) jsonArray.put(s);
        return jsonArray;
    }

    private static ArrayList<String> getValuesFromJSONArray(JSONArray jsonArray) {
        ArrayList<String> values = new ArrayList<>();

        String jsonValue = (String)jsonArray.remove(0);
        while (jsonValue != null) {
            values.add(jsonValue);
            jsonValue=(String)jsonArray.remove(0);
        }
        return values;
    }


}
