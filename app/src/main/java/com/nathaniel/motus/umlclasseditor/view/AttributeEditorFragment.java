package com.nathaniel.motus.umlclasseditor.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.nathaniel.motus.umlclasseditor.R;
import com.nathaniel.motus.umlclasseditor.controller.FragmentObserver;
import com.nathaniel.motus.umlclasseditor.model.TypeMultiplicity;
import com.nathaniel.motus.umlclasseditor.model.UmlClassAttribute;
import com.nathaniel.motus.umlclasseditor.model.UmlType;
import com.nathaniel.motus.umlclasseditor.model.Visibility;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AttributeEditorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AttributeEditorFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final String ATTRIBUTE_INDEX_KEY = "attributeIndex";
    private static final String CLASS_EDITOR_FRAGMENT_ID_KEY="classEditorFragmentId";
    private int mAttributeIndex;
    private int mClassEditorFragmentId;
    private ArrayList<UmlClassAttribute> mUmlClassAttributes;
    private FragmentObserver mCallback;

    private EditText mAttributeNameEdit;
    private RadioButton mPublicRadio;
    private RadioButton mProtectedRadio;
    private RadioButton mPrivateRadio;
    private CheckBox mStaticCheck;
    private CheckBox mFinalCheck;
    private Spinner mTypeSpinner;
    private RadioButton mSimpleRadio;
    private RadioButton mCollectionRadio;
    private RadioButton mArrayRadion;
    private EditText mDimEdit;
    private Button mOKButton;
    private Button mCancelButton;

    private static final int TYPE_SPINNER_TAG=310;
    private static final int OK_BUTTON_TAG=320;
    private static final int CANCEL_BUTTON_TAG=330;

//    **********************************************************************************************
//    Constructors
//    **********************************************************************************************

    public AttributeEditorFragment() {
        // Required empty public constructor
    }

    public static AttributeEditorFragment newInstance(int classEditorFragmentId, int attributeIndex) {
        AttributeEditorFragment fragment = new AttributeEditorFragment();
        Bundle args = new Bundle();
        args.putInt(ATTRIBUTE_INDEX_KEY,attributeIndex);
        args.putInt(CLASS_EDITOR_FRAGMENT_ID_KEY,classEditorFragmentId);
        fragment.setArguments(args);
        return fragment;
    }

//    **********************************************************************************************
//    Fragment events
//    **********************************************************************************************

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAttributeIndex=getArguments().getInt(ATTRIBUTE_INDEX_KEY,-1);
            mClassEditorFragmentId=getArguments().getInt(CLASS_EDITOR_FRAGMENT_ID_KEY);
        }
        mUmlClassAttributes=((ClassEditorFragment)getFragmentManager().findFragmentById(mClassEditorFragmentId)).getUmlClassAttributes();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_attribute_editor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        configureViews();
        createCallbackToParentActivity();
    }

    //    **********************************************************************************************
//    Configuration methods
//    **********************************************************************************************

    public void configureViews() {
        mAttributeNameEdit=getActivity().findViewById(R.id.attribute_name_input);

        mPublicRadio=getActivity().findViewById(R.id.attribute_public_radio);

        mProtectedRadio=getActivity().findViewById(R.id.attribute_protected_radio);

        mPrivateRadio=getActivity().findViewById(R.id.attribute_private_radio);

        mTypeSpinner=getActivity().findViewById(R.id.attribute_type_spinner);
        mTypeSpinner.setTag(TYPE_SPINNER_TAG);
        mTypeSpinner.setOnItemSelectedListener(this);

        mSimpleRadio =getActivity().findViewById(R.id.attribute_simple_radio);

        mCollectionRadio=getActivity().findViewById(R.id.attribute_collection_radio);

        mArrayRadion=getActivity().findViewById(R.id.attribute_array_radio);

        mDimEdit=getActivity().findViewById(R.id.attribute_dimension_input);

        mOKButton=getActivity().findViewById(R.id.attribute_ok_button);
        mOKButton.setTag(OK_BUTTON_TAG);
        mOKButton.setOnClickListener(this);

        mCancelButton=getActivity().findViewById(R.id.attribute_cancel_button);
        mCancelButton.setTag(CANCEL_BUTTON_TAG);
        mCancelButton.setOnClickListener(this);

    }

    private void createCallbackToParentActivity() {
        mCallback=(FragmentObserver)getActivity();
    }

//    **********************************************************************************************
//    UI events
//    **********************************************************************************************


    @Override
    public void onClick(View v) {

        int tag=(int)v.getTag();

        switch (tag) {

            case OK_BUTTON_TAG:
                mCallback.closeFragment(this);
                break;

            default:
                break;
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}