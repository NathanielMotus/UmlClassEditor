package com.nathaniel.motus.umlclasseditor.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.nathaniel.motus.umlclasseditor.controller.FragmentObserver;

public abstract class EditorFragment extends Fragment {

    protected FragmentObserver mCallback;
    protected OnBackPressedCallback mOnBackPressedCallback;

//    **********************************************************************************************
//    Abstract methods
//    **********************************************************************************************
    protected abstract void readBundle();
    protected abstract void clearDraftObject();
    protected abstract boolean createOrUpdateObject();
    protected abstract void closeFragment();
    protected abstract void configureViews();
    protected abstract void initializeMembers();
    protected abstract void initializeFields();
    protected abstract void setOnCreateOrEditDisplay();

//    **********************************************************************************************
//    Common methods
//    **********************************************************************************************

    protected void createOnBackPressedCallback() {
        mOnBackPressedCallback=new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                onCancelButtonCLicked();
            }
        };
    }

    protected void onCancelButtonCLicked() {
        clearDraftObject();
        mOnBackPressedCallback.remove();
        closeFragment();
    }

    protected void onOKButtonClicked() {
        if (createOrUpdateObject()) {
            mOnBackPressedCallback.remove();
            closeFragment();
        }
    }

    protected void setOnBackPressedCallback() {
        requireActivity().getOnBackPressedDispatcher().addCallback(this,mOnBackPressedCallback);
    }

    protected void createCallbackToParentActivity() {
        mCallback=(FragmentObserver)getActivity();
    }

//    **********************************************************************************************
//    Fragment events
//    **********************************************************************************************
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
            readBundle();

        createOnBackPressedCallback();
        setOnBackPressedCallback();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        createCallbackToParentActivity();
        configureViews();
        initializeMembers();
        initializeFields();
        setOnCreateOrEditDisplay();
    }



}
