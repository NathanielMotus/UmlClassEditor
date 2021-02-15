package com.nathaniel.motus.umlclasseditor.model;

import java.util.Comparator;

public class AdapterItemComparator implements Comparator<AdapterItem> {

    @Override
    public int compare(AdapterItem adapterItem, AdapterItem t1) {
        return adapterItem.getName().compareTo(t1.getName());
    }
}
