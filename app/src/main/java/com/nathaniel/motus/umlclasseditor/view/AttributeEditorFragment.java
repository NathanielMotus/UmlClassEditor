package com.nathaniel.motus.umlclasseditor.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AttributeEditorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AttributeEditorFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final String ATTRIBUTE_INDEX_KEY = "attributeIndex";
    private static final String CLASS_EDITOR_FRAGMENT_ID_KEY="classEditorFragmentId";
    private int mAttributeIndex;
    private UmlClassAttribute mUmlClassAttribute;
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
    private RadioButton mArrayRadio;
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
//    Getters and setters
//    **********************************************************************************************


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
        initializeMembers();
        initializeFields();
        populateTypeSpinner();
    }

//    **********************************************************************************************
//    Configuration methods
//    **********************************************************************************************

    public void configureViews() {
        mAttributeNameEdit=getActivity().findViewById(R.id.attribute_name_input);

        mPublicRadio=getActivity().findViewById(R.id.attribute_public_radio);

        mProtectedRadio=getActivity().findViewById(R.id.attribute_protected_radio);

        mPrivateRadio=getActivity().findViewById(R.id.attribute_private_radio);

        mStaticCheck=getActivity().findViewById(R.id.attribute_static_check);

        mFinalCheck=getActivity().findViewById(R.id.attribute_final_check);

        mTypeSpinner=getActivity().findViewById(R.id.attribute_type_spinner);
        mTypeSpinner.setTag(TYPE_SPINNER_TAG);
        mTypeSpinner.setOnItemSelectedListener(this);

        mSimpleRadio =getActivity().findViewById(R.id.attribute_simple_radio);

        mCollectionRadio=getActivity().findViewById(R.id.attribute_collection_radio);

        mArrayRadio =getActivity().findViewById(R.id.attribute_array_radio);

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

    private void initializeMembers() {
        if (mAttributeIndex != -1) {
            mUmlClassAttribute=mUmlClassAttributes.get(mAttributeIndex);
        }
    }

    private void initializeFields() {
        if (mAttributeIndex != -1) {
            mAttributeNameEdit.setText(mUmlClassAttribute.getName());

            switch (mUmlClassAttribute.getVisibility()) {
                case PUBLIC:
                    mPublicRadio.setChecked(true);
                    break;
                case PROTECTED:
                    mProtectedRadio.setChecked(true);
                    break;
                default:
                    mPrivateRadio.setChecked(true);
                    break;
            }

            mStaticCheck.setChecked(mUmlClassAttribute.isStatic());
            mFinalCheck.setChecked(mUmlClassAttribute.isFinal());

            switch (mUmlClassAttribute.getTypeMultiplicity()) {
                case SINGLE:
                    mSimpleRadio.setChecked(true);
                    break;
                case COLLECTION:
                    mCollectionRadio.setChecked(true);
                    break;
                default:
                    mArrayRadio.setChecked(true);
                    break;
            }
            mDimEdit.setText(Integer.toString(mUmlClassAttribute.getTableDimension()));
        }
    }

    private void populateTypeSpinner() {
        List<String> spinnerArray=new ArrayList<>();
        for (UmlType t:mCallback.getProject().getUmlTypes())
            spinnerArray.add(t.getName());
        ArrayAdapter<String> adapter=new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_item,spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTypeSpinner.setAdapter(adapter);
        if (mAttributeIndex!=-1)
            mTypeSpinner.setSelection(mCallback.getProject().getUmlTypes().indexOf(mUmlClassAttribute.getUmlType()));
    }

//    **********************************************************************************************
//    UI events
//    **********************************************************************************************


    @Override
    public void onClick(View v) {

        int tag=(int)v.getTag();

        switch (tag) {

            case OK_BUTTON_TAG:
                createOrUpdateAttribute();
                mCallback.closeAttributeEditorFragment(this);
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

//    **********************************************************************************************
//    Edition methods
//    **********************************************************************************************

    private void createOrUpdateAttribute() {
        if (getAttributeName().equals("")) {
            Toast.makeText(getContext(), "Attribute name cannot be blank", Toast.LENGTH_SHORT).show();
        } else {
            if (mAttributeIndex == -1) {
                mUmlClassAttributes.add(new UmlClassAttribute(getAttributeName(), getVisibility(), isStatic(), isFinal(), getType(), getMultiplicity(), getArrayDimension()));
            } else {
                mUmlClassAttribute.setName(getAttributeName());
                mUmlClassAttribute.setVisibility(getVisibility());
                mUmlClassAttribute.setStatic(isStatic());
                mUmlClassAttribute.setFinal(isFinal());
                mUmlClassAttribute.setUmlType(getType());
                mUmlClassAttribute.setTypeMultiplicity(getMultiplicity());
                mUmlClassAttribute.setTableDimension(getArrayDimension());
            }
        }
    }

    private String getAttributeName() {
        return mAttributeNameEdit.getText().toString();
    }

    private Visibility getVisibility() {
        if (mPublicRadio.isChecked()) return Visibility.PUBLIC;
        if (mProtectedRadio.isChecked()) return Visibility.PROTECTED;
        return Visibility.PRIVATE;
    }

    private boolean isStatic() {
        return mStaticCheck.isChecked();
    }

    private boolean isFinal() {
        return mFinalCheck.isChecked();
    }

    private UmlType getType() {
        return mCallback.getProject().getUmlTypes().get(mTypeSpinner.getSelectedItemPosition());
    }

    private TypeMultiplicity getMultiplicity() {
        if (mSimpleRadio.isChecked()) return TypeMultiplicity.SINGLE;
        if (mCollectionRadio.isChecked()) return TypeMultiplicity.COLLECTION;
        return TypeMultiplicity.ARRAY;
    }

    private int getArrayDimension() {
        if (mDimEdit.getText()==null) return 0;
        return Integer.parseInt(mDimEdit.getText().toString());
    }
}