package com.nathaniel.motus.umlclasseditor.controller;

import androidx.fragment.app.Fragment;

import com.nathaniel.motus.umlclasseditor.model.UmlProject;

//    **********************************************************************************************
//    Callback interface
//    **********************************************************************************************
    public interface FragmentObserver {


        void setPurpose(Purpose purpose);
        UmlProject getProject();
        void closeClassEditorFragment(Fragment fragment);
        void closeAttributeEditorFragment(Fragment fragment);
        void closeMethodEditorFragment(Fragment fragment);
        void closeParameterEditorFragment(Fragment fragment);
        void closeValueEditorFragment(Fragment fragment);
        void openAttributeEditorFragment(int attributeIndex);
        void openMethodEditorFragment(int methodIndex);
        void openParameterEditorFragment(int parameterIndex);

        enum Purpose{NONE,CREATE_CLASS,EDIT_CLASS,CREATE_ATTRIBUTE,EDIT_ATTRIBUTE,CREATE_METHOD,EDIT_METHOD,CREATE_PARAMETER,EDIT_PARAMETER}
}
