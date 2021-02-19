package com.nathaniel.motus.umlclasseditor.model;

import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.nathaniel.motus.umlclasseditor.controller.FragmentObserver;

public abstract class EditorFragment extends Fragment {

    private FragmentObserver mCallback;
    private OnBackPressedCallback mOnBackPressedCallback;

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
    private void createOnBackPressedCallback() {
        mOnBackPressedCallback=new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                onCancelButtonCLicked();
            }
        };
    }

    private void onCancelButtonCLicked() {
        clearDraftObject();
        mOnBackPressedCallback.remove();
        closeFragment();
    }

    private void onOKButtonClicked() {
        if (createOrUpdateObject()) {
            mOnBackPressedCallback.remove();
            closeFragment();
        }
    }

    private void setOnBackPressedCallback() {
        requireActivity().getOnBackPressedDispatcher().addCallback(this,mOnBackPressedCallback);
    }

    private void createCallbackToParentActivity() {
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
