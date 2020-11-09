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

//    **********************************************************************************************
//    Test methods
//    **********************************************************************************************

    public static boolean relationAlreadyExistsBetween(UmlClass firstClass, UmlClass secondClass,UmlProject inProject) {
        //check whether there already is a relation between two classes
        //this test is not oriented
        boolean test=false;

        for (UmlRelation r : inProject.getUmlRelations())
            if ((r.getRelationOriginClass()==firstClass && r.getRelationEndClass()==secondClass)
                    || (r.getRelationOriginClass()==secondClass && r.getRelationEndClass()==firstClass))
                test=true;
        return test;
    }
}
