package com.nathaniel.motus.umlclasseditor.model;

public class MethodParameter {

    private String mName;
    private UmlType mUmlType;
    private TypeMultiplicity mTypeMultiplicity=TypeMultiplicity.SINGLE;
    private int mTableDimension=1;

//    **********************************************************************************************
//    Constructors
//    **********************************************************************************************

    public MethodParameter(String name, UmlType umlType, TypeMultiplicity typeMultiplicity, int tableDimension) {
        mName = name;
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
