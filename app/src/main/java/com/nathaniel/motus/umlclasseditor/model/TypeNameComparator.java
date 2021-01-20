package com.nathaniel.motus.umlclasseditor.model;

import java.util.Comparator;

public class TypeNameComparator implements Comparator<String> {

    @Override
    public int compare(String s, String t1) {

        if (Character.isLowerCase(s.charAt(0)) && Character.isUpperCase(t1.charAt(0))) return -1;
        if (Character.isUpperCase(s.charAt(0)) && Character.isLowerCase(t1.charAt(0))) return 1;
        return s.compareTo(t1);
    }
}
