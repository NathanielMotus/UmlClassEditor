package com.nathaniel.motus.umlclasseditor.model;

public class UmlRelation {

    public enum UmlRelationType {INHERITANCE, REALIZATION,AGGREGATION,COMPOSITION,ASSOCIATION,DEPENDENCY}

    private UmlClass mRelationOriginClass; //arrow starts from this
    private UmlClass mRelationEndClass; //to this
    private UmlRelationType mUmlRelationType;

//    **********************************************************************************************
//    Constructors
//    **********************************************************************************************

    public UmlRelation(UmlClass relationOriginClass, UmlClass relationEndClass, UmlRelationType umlRelationType) {
        mRelationOriginClass = relationOriginClass;
        mRelationEndClass = relationEndClass;
        mUmlRelationType = umlRelationType;
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
}
