package com.nathaniel.motus.umlclasseditor.model;

import org.json.JSONException;
import org.json.JSONObject;

public class UmlEnumValue implements AdapterItem{

    private String mName;
    private int mValueOrder;

    private static final String JSON_ENUM_VALUE_NAME="EnumValueName";
    private static final String JSON_ENUM_VALUE_INDEX="EnumValueIndex";

//    **********************************************************************************************
//    Constructors
//    **********************************************************************************************

    public UmlEnumValue(String name, int valueOrder) {
        mName = name;
        mValueOrder = valueOrder;
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

    public int getValueOrder() {
        return mValueOrder;
    }

//    **********************************************************************************************
//    JSON methods
//    **********************************************************************************************
    public JSONObject toJSONObject() {
        JSONObject jsonObject=new JSONObject();

        try {
            jsonObject.put(JSON_ENUM_VALUE_NAME, mName);
            jsonObject.put(JSON_ENUM_VALUE_INDEX, mValueOrder);
            return jsonObject;
        } catch (JSONException jsonException) {
            return null;
        }
    }

    public static UmlEnumValue fromJSONObject(JSONObject jsonObject) {
        try {
            return new UmlEnumValue(jsonObject.getString(JSON_ENUM_VALUE_NAME),
                    jsonObject.getInt(JSON_ENUM_VALUE_INDEX));
        } catch (JSONException jsonException) {
            return null;
        }
    }

}
