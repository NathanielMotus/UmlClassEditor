package com.nathaniel.motus.umlclasseditor.model;

public class UmlClassAttribute {

    private String mName;
    private Visibility mVisibility=Visibility.PRIVATE;
    private boolean mStatic=false;
    private boolean mFinal =false;
    private UmlType mUmlType;
    private TypeMultiplicity mTypeMultiplicity=TypeMultiplicity.SINGLE;
    private int mTableDimension=1; //only used if it's a table

//    **********************************************************************************************
//    Constructors
//    **********************************************************************************************

    public UmlClassAttribute(String name, Visibility visibility, boolean aStatic, boolean aFinal, UmlType umlType, TypeMultiplicity typeMultiplicity, int tableDimension) {
        mName = name;
        mVisibility = visibility;
        mStatic = aStatic;
        mFinal = aFinal;
        mUmlType = umlType;
        mTypeMultiplicity = typeMultiplicity;
        mTableDimension = tableDimension;
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

    public int getTableDimension() {
        return mTableDimension;
    }

    public void setTableDimension(int tableDimension) {
        mTableDimension = tableDimension;
    }
}
