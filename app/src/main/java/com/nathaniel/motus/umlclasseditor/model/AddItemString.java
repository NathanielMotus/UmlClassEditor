package com.nathaniel.motus.umlclasseditor.model;

public class AddItemString implements AdapterItem {
    //class to create "New (attribute, method, value or parameter)..." in ExpandableListView

    private String mName;

    public AddItemString(String name) {
        mName = name;
    }

    @Override
    public String getName() {
        return mName;
    }
}
