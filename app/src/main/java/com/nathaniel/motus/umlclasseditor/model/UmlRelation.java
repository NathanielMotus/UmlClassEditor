package com.nathaniel.motus.umlclasseditor.model;

public class UmlRelation {

    public enum UmlRelationType {INHERITANCE, REALIZATION,AGGREGATION,COMPOSITION,ASSOCIATION,DEPENDENCY}

    private UmlClass mRelationOriginClass; //arrow starts from this
    private UmlClass mRelationEndClass; //to this
    private float mXOrigin;
    private float mYOrigin;
    private float mXEnd;
    private float mYEnd;
    private UmlRelationType mUmlRelationType;

//    **********************************************************************************************
//    Constructors
//    **********************************************************************************************

    public UmlRelation(UmlClass relationOriginClass, UmlClass relationEndClass, UmlRelationType umlRelationType) {
        mRelationOriginClass = relationOriginClass;
        mRelationEndClass = relationEndClass;
        mUmlRelationType = umlRelationType;
        mXOrigin =0;
        mYOrigin =0;
        mXEnd =0;
        mYEnd =0;
    }

//    **********************************************************************************************
//    Getters and setters
//    **********************************************************************************************

    public UmlClass getRelationOriginClass() {
        return mRelationOriginClass;
    }

    public void setRelationOriginClass(UmlClass relationOriginClass) {
        mRelationOriginClass = relationOriginClass;
    }

    public UmlClass getRelationEndClass() {
        return mRelationEndClass;
    }

    public void setRelationEndClass(UmlClass relationEndClass) {
        mRelationEndClass = relationEndClass;
    }

    public UmlRelationType getUmlRelationType() {
        return mUmlRelationType;
    }

    public void setUmlRelationType(UmlRelationType umlRelationType) {
        mUmlRelationType = umlRelationType;
    }

    public float getXOrigin() {
        return mXOrigin;
    }

    public void setXOrigin(float XOrigin) {
        mXOrigin = XOrigin;
    }

    public float getYOrigin() {
        return mYOrigin;
    }

    public void setYOrigin(float YOrigin) {
        mYOrigin = YOrigin;
    }

    public float getXEnd() {
        return mXEnd;
    }

    public void setXEnd(float XEnd) {
        mXEnd = XEnd;
    }

    public float getYEnd() {
        return mYEnd;
    }

    public void setYEnd(float YEnd) {
        mYEnd = YEnd;
    }

//    **********************************************************************************************
//    Test methods
//    **********************************************************************************************

//    **********************************************************************************************
//    Other methods
//    **********************************************************************************************

}
