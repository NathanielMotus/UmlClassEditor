package com.nathaniel.motus.umlclasseditor.model;

import java.util.ArrayList;

public class UmlClass extends UmlType {

    public enum UmlClassType{JAVA_CLASS,ABSTRACT_CLASS,INTERFACE,ENUM}

    private UmlClassType mUmlClassType=UmlClassType.JAVA_CLASS;
    private ArrayList<UmlClassAttribute> mAttributeList;
    private ArrayList<UmlClassMethod> mMethodList;
    private ArrayList<String> mValueList; //in case of an Enum

    //Location and size of graphical representation of the class
    //before moving or zooming the graph
    private float mUmlClassXPos;
    private float mUmlClassYPos;
    private float mUmlClassWidth;
    private float mUmlClassHeight;
    private float mUmlClassNormalXPos;
    private float mUmlClassNormalYPos;
    private float mUmlClassNormalWidth;
    private float mUmlClassNormalHeight;


//    **********************************************************************************************
//    Constructors
//    **********************************************************************************************

    public UmlClass(String name) {
        this(name,UmlClassType.JAVA_CLASS);
    }

    public UmlClass(String name, UmlClassType umlClassType) {
        super(name);
        mUmlClassType = umlClassType;
        mAttributeList= new ArrayList<>();
        mMethodList=new ArrayList<>();
        mValueList=new ArrayList<>();
    }

    public UmlClass(String name, UmlClassType umlClassType, ArrayList<UmlClassAttribute> attributeList, ArrayList<UmlClassMethod> methodList, float umlClassNormalXPos, float umlClassNormalYPos) {
        super(name);
        mUmlClassType = umlClassType;
        mAttributeList = attributeList;
        mMethodList = methodList;
        mUmlClassNormalXPos = umlClassNormalXPos;
        mUmlClassNormalYPos = umlClassNormalYPos;
    }

    public UmlClass(String name, UmlClassType umlClassType, ArrayList<UmlClassAttribute> attributeList, ArrayList<UmlClassMethod> methodList, ArrayList<String> valueList, float umlClassNormalXPos, float umlClassNormalYPos) {
        super(name);
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

    public float getUmlClassXPos() {
        return mUmlClassXPos;
    }

    public void setUmlClassXPos(float umlClassXPos) {
        mUmlClassXPos = umlClassXPos;
    }

    public float getUmlClassYPos() {
        return mUmlClassYPos;
    }

    public void setUmlClassYPos(float umlClassYPos) {
        mUmlClassYPos = umlClassYPos;
    }

    public float getUmlClassWidth() {
        return mUmlClassWidth;
    }

    public void setUmlClassWidth(float umlClassWidth) {
        mUmlClassWidth = umlClassWidth;
    }

    public float getUmlClassHeight() {
        return mUmlClassHeight;
    }

    public void setUmlClassHeight(float umlClassHeight) {
        mUmlClassHeight = umlClassHeight;
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

}
