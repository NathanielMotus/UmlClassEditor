package com.nathaniel.motus.umlclasseditor.model;

public class UmlRelation {

    enum UmlAssociation{INHERITANCE,IMPLEMENTATION,AGGREGATION,COMPOSITION,ASSOCIATION,DEPENDENCY}

    private UmlClass mAssociationRootClass; //arrow starts from this
    private UmlClass mAssociationEndClass; //to this
    private UmlAssociation mAssociation;

//    **********************************************************************************************
//    Constructors
//    **********************************************************************************************

    public UmlRelation(UmlClass associationRootClass, UmlClass associationEndClass, UmlAssociation association) {
        mAssociationRootClass = associationRootClass;
        mAssociationEndClass = associationEndClass;
        mAssociation = association;
    }

//    **********************************************************************************************
//    Getters and setters
//    **********************************************************************************************

    public UmlClass getAssociationRootClass() {
        return mAssociationRootClass;
    }

    public void setAssociationRootClass(UmlClass associationRootClass) {
        mAssociationRootClass = associationRootClass;
    }

    public UmlClass getAssociationEndClass() {
        return mAssociationEndClass;
    }

    public void setAssociationEndClass(UmlClass associationEndClass) {
        mAssociationEndClass = associationEndClass;
    }

    public UmlAssociation getAssociation() {
        return mAssociation;
    }

    public void setAssociation(UmlAssociation association) {
        mAssociation = association;
    }
}
