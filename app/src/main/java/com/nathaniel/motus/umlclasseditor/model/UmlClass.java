package com.nathaniel.motus.umlclasseditor.model;

import android.graphics.Paint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UmlClass extends UmlType {

    public enum UmlClassType {JAVA_CLASS, ABSTRACT_CLASS, INTERFACE, ENUM}

    private UmlClassType mUmlClassType = UmlClassType.JAVA_CLASS;
    private ArrayList<UmlClassAttribute> mAttributes;
    private int mUmlClassAttributeCount;
    private ArrayList<UmlClassMethod> mMethods;
    private int mUmlClassMethodCount;
    private ArrayList<UmlEnumValue> mEnumValues;
    private int mValueCount;
    private int mClassOrder;

    private float mUmlClassNormalXPos;
    private float mUmlClassNormalYPos;
    private float mUmlClassNormalWidth;
    private float mUmlClassNormalHeight;

    private Paint mUmlClassHeaderPaint;
    private int colorPaint;

    private static final String JSON_CLASS_NAME = "ClassName";
    private static final String JSON_CLASS_INDEX = "ClassIndex";
    private static final String JSON_CLASS_CLASS_TYPE = "ClassClassType";
    private static final String JSON_CLASS_ATTRIBUTES = "ClassAttributes";
    private static final String JSON_CLASS_METHODS = "ClassMethods";
    private static final String JSON_CLASS_VALUES = "ClassValues";
    private static final String JSON_CLASS_NORMAL_XPOS = "ClassNormalXPos";
    private static final String JSON_CLASS_NORMAL_YPOS = "ClassNormalYPos";
    private static final String JSON_CLASS_ATTRIBUTE_COUNT = "ClassAttributeCount";
    private static final String JSON_CLASS_METHOD_COUNT = "ClassMethodCount";
    private static final String JSON_CLASS_VALUE_COUNT = "ClassValueCount";

//    **********************************************************************************************
//    Constructors
//    **********************************************************************************************

    public UmlClass(int classOrder) {
        mAttributes = new ArrayList<>();
        mMethods = new ArrayList<>();
        mEnumValues = new ArrayList<>();
        mUmlClassAttributeCount = 0;
        mUmlClassMethodCount = 0;
        mValueCount = 0;
        mClassOrder = classOrder;
    }

    public UmlClass(String name) {
        this(name, UmlClassType.JAVA_CLASS);
    }

    public UmlClass(String name, UmlClassType umlClassType) {
        super(name, TypeLevel.PROJECT);
        mUmlClassType = umlClassType;
        mAttributes = new ArrayList<>();
        mUmlClassAttributeCount = 0;
        mMethods = new ArrayList<>();
        mUmlClassMethodCount = 0;
        mEnumValues = new ArrayList<>();
        mValueCount = 0;
    }

    public UmlClass(String name, int classOrder, UmlClassType umlClassType,
                    ArrayList<UmlClassAttribute> attributes, int attributeCount,
                    ArrayList<UmlClassMethod> methods, int methodCount,
                    ArrayList<UmlEnumValue> values, int valueCount,
                    float umlClassNormalXPos, float umlClassNormalYPos) {
        super(name, TypeLevel.PROJECT);
        mClassOrder = classOrder;
        mUmlClassType = umlClassType;
        mAttributes = attributes;
        mUmlClassAttributeCount = attributeCount;
        mMethods = methods;
        mUmlClassMethodCount = methodCount;
        mEnumValues = values;
        mValueCount = valueCount;
        mUmlClassNormalXPos = umlClassNormalXPos;
        mUmlClassNormalYPos = umlClassNormalYPos;
    }

    public UmlClass(String name, UmlClassType umlClassType,
                    ArrayList<UmlClassAttribute> attributes,
                    ArrayList<UmlClassMethod> methods,
                    ArrayList<UmlEnumValue> values,
                    float umlClassNormalXPos, float umlClassNormalYPos) {
        super(name, TypeLevel.PROJECT);
        mUmlClassType = umlClassType;
        mAttributes = attributes;
        mUmlClassAttributeCount = 0;
        mMethods = methods;
        mUmlClassMethodCount = 0;
        mEnumValues = values;
        mValueCount = 0;
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

    public ArrayList<UmlClassAttribute> getAttributes() {
        return mAttributes;
    }

    public ArrayList<UmlClassMethod> getMethods() {
        return mMethods;
    }

    public ArrayList<UmlEnumValue> getValues() {
        return mEnumValues;
    }

    public float getNormalRightEnd() {
        return mUmlClassNormalXPos + mUmlClassNormalWidth;
    }

    public float getNormalBottomEnd() {
        return mUmlClassNormalYPos + mUmlClassNormalHeight;
    }

    public void setAttributes(ArrayList<UmlClassAttribute> attributes) {
        mAttributes = attributes;
    }

    public void setMethods(ArrayList<UmlClassMethod> methods) {
        mMethods = methods;
    }

    public void setValues(ArrayList<UmlEnumValue> values) {
        mEnumValues = values;
    }

    public void setUmlClassAttributeCount(int umlClassAttributeCount) {
        mUmlClassAttributeCount = umlClassAttributeCount;
    }

    public void setUmlClassMethodCount(int umlClassMethodCount) {
        mUmlClassMethodCount = umlClassMethodCount;
    }

    public void setValueCount(int valueCount) {
        mValueCount = valueCount;
    }

    public int getUmlClassAttributeCount() {
        return mUmlClassAttributeCount;
    }

    public int getUmlClassMethodCount() {
        return mUmlClassMethodCount;
    }

    public int getValueCount() {
        return mValueCount;
    }

    @Override
    public void setName(String name) {
        super.setName(name);
    }

    @Override
    public String getName() {
        return super.getName();
    }

    public int getClassOrder() {
        return mClassOrder;
    }

    public void setClassOrder(int classOrder) {
        mClassOrder = classOrder;
    }

    public UmlClassAttribute findAttributeByOrder(int attributeOrder) {
        for (UmlClassAttribute a : mAttributes)
            if (a.getAttributeOrder() == attributeOrder) return a;
        return null;
    }

    public UmlClassMethod findMethodByOrder(int methodOrder) {
        for (UmlClassMethod m : mMethods)
            if (m.getMethodOrder() == methodOrder) return m;
        return null;
    }

    public UmlEnumValue findValueByOrder(int valueOrder) {
        for (UmlEnumValue v : mEnumValues)
            if (v.getValueOrder() == valueOrder) return v;
        return null;
    }

    public UmlClassAttribute getAttribute(String attributeName) {
        for (UmlClassAttribute a : mAttributes)
            if (a.getName().equals(attributeName))
                return a;
        return null;
    }

//    **********************************************************************************************
//    Modifiers
//    **********************************************************************************************

    public void addMethod(UmlClassMethod method) {
        mMethods.add(method);
        mUmlClassMethodCount++;
    }

    public void removeMethod(UmlClassMethod method) {
        mMethods.remove(method);
    }

    public void addAttribute(UmlClassAttribute attribute) {
        mAttributes.add(attribute);
        mUmlClassAttributeCount++;
    }

    public void removeAttribute(UmlClassAttribute attribute) {
        mAttributes.remove(attribute);
    }

    public void addValue(UmlEnumValue value) {
        mEnumValues.add(value);
        mValueCount++;
    }

    public void removeValue(UmlEnumValue value) {
        mEnumValues.remove(value);
    }

    public void incrementUmlClassAttributeCount() {
        mUmlClassAttributeCount++;
    }

    public void incrementUmlClassMethodCount() {
        mUmlClassMethodCount++;
    }

    public void incrementValueCount() {
        mValueCount++;
    }

//    **********************************************************************************************
//    Test methods
//    **********************************************************************************************

    public boolean containsPoint(float absoluteX, float absoluteY) {
        return (absoluteX <= mUmlClassNormalXPos + mUmlClassNormalWidth &&
                absoluteX >= mUmlClassNormalXPos &&
                absoluteY <= mUmlClassNormalYPos + mUmlClassNormalHeight &&
                absoluteY >= mUmlClassNormalYPos);
    }

    public boolean isSouthOf(UmlClass umlClass) {
        //is this in South quarter of umlClass ?
        return (this.getUmlClassNormalYPos() >= umlClass.getNormalBottomEnd() &&
                this.getNormalRightEnd() >= umlClass.getUmlClassNormalXPos() - this.getUmlClassNormalYPos() + umlClass.getNormalBottomEnd() &&
                this.getUmlClassNormalXPos() <= umlClass.getNormalRightEnd() + this.getUmlClassNormalYPos() - umlClass.getNormalBottomEnd());
    }

    public boolean isNorthOf(UmlClass umlClass) {
        //is this in North quarter of umlClass ?
        return (this.getNormalBottomEnd() <= umlClass.getUmlClassNormalYPos() &&
                this.getNormalRightEnd() >= umlClass.getUmlClassNormalXPos() - umlClass.getUmlClassNormalYPos() + this.getNormalBottomEnd() &&
                this.getUmlClassNormalXPos() <= umlClass.getNormalRightEnd() + umlClass.getUmlClassNormalYPos() - this.getNormalBottomEnd());
    }

    public boolean isWestOf(UmlClass umlClass) {
        //is this in West quarter of umlClass ?
        return (this.getNormalRightEnd() <= umlClass.getUmlClassNormalXPos() &&
                this.getNormalBottomEnd() >= umlClass.getUmlClassNormalYPos() - umlClass.getUmlClassNormalXPos() + this.getNormalRightEnd() &&
                this.getUmlClassNormalYPos() <= umlClass.getNormalBottomEnd() + umlClass.getUmlClassNormalXPos() - this.getNormalRightEnd());
    }

    public boolean isEastOf(UmlClass umlClass) {
        //is this in East Quarter of umlClass ?
        return (this.getUmlClassNormalXPos() >= umlClass.getNormalRightEnd() &&
                this.getNormalBottomEnd() >= umlClass.getUmlClassNormalYPos() - this.getUmlClassNormalXPos() + umlClass.getNormalRightEnd() &&
                this.getUmlClassNormalYPos() <= umlClass.getNormalBottomEnd() + this.getUmlClassNormalXPos() - umlClass.getNormalRightEnd());
    }

    public boolean isInvolvedInRelation(UmlRelation umlRelation) {
        return (this == umlRelation.getRelationOriginClass() || this == umlRelation.getRelationEndClass());
    }

    public boolean alreadyExists(UmlProject inProject) {
        //check whether class name already exists

        for (UmlClass c : inProject.getUmlClasses())
            if (this.getName().equals(c.getName())) return true;

        return false;
    }

    public boolean containsAttributeNamed(String attributeName) {
        for (UmlClassAttribute a : mAttributes)
            if (a.getName() != null && a.getName().equals(attributeName))
                return true;
        return false;
    }

    public boolean containsEquivalentMethodTo(UmlClassMethod method) {
        for (UmlClassMethod m : mMethods)
            if (m.isEquivalentTo(method))
                return true;
        return false;
    }

//    **********************************************************************************************
//    JSON methods
//    **********************************************************************************************

    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(JSON_CLASS_NAME, this.getName().toString());
            jsonObject.put(JSON_CLASS_INDEX, mClassOrder);
            jsonObject.put(JSON_CLASS_CLASS_TYPE, mUmlClassType);
            jsonObject.put(JSON_CLASS_ATTRIBUTES, getAttributesToJSONArray());
            jsonObject.put(JSON_CLASS_ATTRIBUTE_COUNT, mUmlClassAttributeCount);
            jsonObject.put(JSON_CLASS_METHODS, getMethodsToJSONArray());
            jsonObject.put(JSON_CLASS_METHOD_COUNT, mUmlClassMethodCount);
            jsonObject.put(JSON_CLASS_VALUES, getValuesToJSONArray());
            jsonObject.put(JSON_CLASS_VALUE_COUNT, mValueCount);
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
            UmlClass umlClass = project.getUmlClass(jsonObject.getString(JSON_CLASS_NAME));

            umlClass.setClassOrder(jsonObject.getInt(JSON_CLASS_INDEX));
            umlClass.setUmlClassType(UmlClassType.valueOf(jsonObject.getString(JSON_CLASS_CLASS_TYPE)));
            umlClass.setAttributes(getAttributesFromJSONArray(jsonObject.getJSONArray(JSON_CLASS_ATTRIBUTES)));
            umlClass.setMethods(getMethodsFromJSONArray(jsonObject.getJSONArray(JSON_CLASS_METHODS)));
            umlClass.setValues(getValuesFromJSONArray(jsonObject.getJSONArray(JSON_CLASS_VALUES)));
            umlClass.setUmlClassNormalXPos(jsonObject.getInt(JSON_CLASS_NORMAL_XPOS));
            umlClass.setUmlClassNormalYPos(jsonObject.getInt(JSON_CLASS_NORMAL_YPOS));
            umlClass.setUmlClassAttributeCount(jsonObject.getInt(JSON_CLASS_ATTRIBUTE_COUNT));
            umlClass.setUmlClassMethodCount(jsonObject.getInt(JSON_CLASS_METHOD_COUNT));
            umlClass.setValueCount(jsonObject.getInt(JSON_CLASS_VALUE_COUNT));

        } catch (JSONException ignored) {

        }
    }

    private JSONArray getAttributesToJSONArray() {
        JSONArray jsonArray = new JSONArray();

        for (UmlClassAttribute a : mAttributes) jsonArray.put(a.toJSONObject());
        return jsonArray;
    }

    private static ArrayList<UmlClassAttribute> getAttributesFromJSONArray(JSONArray jsonArray) {
        ArrayList<UmlClassAttribute> umlClassAttributes = new ArrayList<>();

        JSONObject jsonAttribute = (JSONObject) jsonArray.remove(0);
        while (jsonAttribute != null) {
            umlClassAttributes.add(UmlClassAttribute.fromJSONObject(jsonAttribute));
            jsonAttribute = (JSONObject) jsonArray.remove(0);
        }
        return umlClassAttributes;
    }

    private JSONArray getMethodsToJSONArray() {
        JSONArray jsonArray = new JSONArray();

        for (UmlClassMethod m : mMethods) jsonArray.put(m.toJSONObject());
        return jsonArray;
    }

    private static ArrayList<UmlClassMethod> getMethodsFromJSONArray(JSONArray jsonArray) {
        ArrayList<UmlClassMethod> umlClassMethods = new ArrayList<>();

        JSONObject jsonMethod = (JSONObject) jsonArray.remove(0);
        while (jsonMethod != null) {
            umlClassMethods.add(UmlClassMethod.fromJSONObject(jsonMethod));
            jsonMethod = (JSONObject) jsonArray.remove(0);
        }
        return umlClassMethods;
    }

    private JSONArray getValuesToJSONArray() {
        JSONArray jsonArray = new JSONArray();

        for (UmlEnumValue v : mEnumValues) jsonArray.put(v.toJSONObject());
        return jsonArray;
    }

    private static ArrayList<UmlEnumValue> getValuesFromJSONArray(JSONArray jsonArray) {
        ArrayList<UmlEnumValue> values = new ArrayList<>();

        JSONObject jsonValue = (JSONObject) jsonArray.remove(0);
        while (jsonValue != null) {
            values.add(UmlEnumValue.fromJSONObject(jsonValue));
            jsonValue = (JSONObject) jsonArray.remove(0);
        }
        return values;
    }

    public Paint getmUmlClassHeaderPaint() {
        return mUmlClassHeaderPaint;
    }

    public void setmUmlClassHeaderPaint(Paint mUmlClassHeaderPaint) {
        this.mUmlClassHeaderPaint = mUmlClassHeaderPaint;
    }

    public int getColorPaint() {
        return colorPaint;
    }

    public void setColorPaint(int colorPaint) {
        this.colorPaint = colorPaint;
    }
}
