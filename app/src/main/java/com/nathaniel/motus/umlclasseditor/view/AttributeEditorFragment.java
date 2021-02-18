package com.nathaniel.motus.umlclasseditor.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nathaniel.motus.umlclasseditor.R;
import com.nathaniel.motus.umlclasseditor.controller.FragmentObserver;
import com.nathaniel.motus.umlclasseditor.model.TypeMultiplicity;
import com.nathaniel.motus.umlclasseditor.model.TypeNameComparator;
import com.nathaniel.motus.umlclasseditor.model.UmlClass;
import com.nathaniel.motus.umlclasseditor.model.UmlClassAttribute;
import com.nathaniel.motus.umlclasseditor.model.UmlType;
import com.nathaniel.motus.umlclasseditor.model.Visibility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AttributeEditorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AttributeEditorFragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private static final String ATTRIBUTE_ORDER_KEY = "attributeOrder";
    private static final String CLASS_ORDER_KEY ="classOrder";
    private static final String CLASS_EDITOR_FRAGMENT_TAG_KEY="classEditorFragmentTag";
    private int mAttributeOrder;
    private int mClassOrder;
    private UmlClassAttribute mUmlClassAttribute;
    private String mClassEditorFragmentTag;
    private FragmentObserver mCallback;
    private UmlClass mUmlClass;

    private TextView mEditAttributeText;
    private Button mDeleteAttributeButton;
    private EditText mAttributeNameEdit;
    private RadioButton mPublicRadio;
    private RadioButton mProtectedRadio;
    private RadioButton mPrivateRadio;
    private CheckBox mStaticCheck;
    private CheckBox mFinalCheck;
    private Spinner mTypeSpinner;
    private RadioGroup mMultiplicityRadioGroup;
    private RadioButton mSimpleRadio;
    private RadioButton mCollectionRadio;
    private RadioButton mArrayRadio;
    private TextView mDimText;
    private EditText mDimEdit;
    private Button mOKButton;
    private Button mCancelButton;

    private static final int TYPE_SPINNER_TAG=310;
    private static final int OK_BUTTON_TAG=320;
    private static final int CANCEL_BUTTON_TAG=330;
    private static final int DELETE_ATTRIBUTE_BUTTON_TAG=340;

    private static OnBackPressedCallback mOnBackPressedCallback;

//    **********************************************************************************************
//    Constructors
//    **********************************************************************************************

    public AttributeEditorFragment() {
        // Required empty public constructor
    }

    public static AttributeEditorFragment newInstance(String classEditorFragmentTag, int attributeOrder,int classOrder) {
        AttributeEditorFragment fragment = new AttributeEditorFragment();
        Bundle args = new Bundle();
        args.putInt(ATTRIBUTE_ORDER_KEY,attributeOrder);
        args.putInt(CLASS_ORDER_KEY,classOrder);
        args.putString(CLASS_EDITOR_FRAGMENT_TAG_KEY,classEditorFragmentTag);
        fragment.setArguments(args);
        return fragment;
    }

//    **********************************************************************************************
//    Getters and setters
//    **********************************************************************************************

    public UmlClassAttribute getUmlClassAttribute() {
        return mUmlClassAttribute;
    }

    public UmlClass getUmlClass() {
        return mUmlClass;
    }


//    **********************************************************************************************
//    Fragment events
//    **********************************************************************************************

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAttributeOrder =getArguments().getInt(ATTRIBUTE_ORDER_KEY,-1);
            mClassOrder =getArguments().getInt(CLASS_ORDER_KEY,-1);
            mClassEditorFragmentTag=getArguments().getString(CLASS_EDITOR_FRAGMENT_TAG_KEY);
        }
        createOnBackPressedCallback();
        setOnBackPressedCallback();
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

        createCallbackToParentActivity();
        configureViews();
        initializeMembers();
        initializeFields();
        if (mAttributeOrder ==-1) setOnCreateDisplay();
        else setOnEditDisplay();
        if (mAttributeOrder !=-1 && mUmlClassAttribute.getTypeMultiplicity()==TypeMultiplicity.ARRAY) setOnArrayDisplay();
        else setOnSingleDisplay();
    }

//    **********************************************************************************************
//    Configuration methods
//    **********************************************************************************************

    public void configureViews() {
        mEditAttributeText=getActivity().findViewById(R.id.edit_attribute_text);

        mDeleteAttributeButton=getActivity().findViewById(R.id.delete_attribute_button);
        mDeleteAttributeButton.setTag(DELETE_ATTRIBUTE_BUTTON_TAG);
        mDeleteAttributeButton.setOnClickListener(this);

        mAttributeNameEdit=getActivity().findViewById(R.id.attribute_name_input);

        mMultiplicityRadioGroup=getActivity().findViewById(R.id.attribute_multiplicity_radio_group);
        mMultiplicityRadioGroup.setOnCheckedChangeListener(this);

        mPublicRadio=getActivity().findViewById(R.id.attribute_public_radio);

        mProtectedRadio=getActivity().findViewById(R.id.attribute_protected_radio);

        mPrivateRadio=getActivity().findViewById(R.id.attribute_private_radio);

        mStaticCheck=getActivity().findViewById(R.id.attribute_static_check);

        mFinalCheck=getActivity().findViewById(R.id.attribute_final_check);

        mTypeSpinner=getActivity().findViewById(R.id.attribute_type_spinner);
        mTypeSpinner.setTag(TYPE_SPINNER_TAG);

        mSimpleRadio =getActivity().findViewById(R.id.attribute_simple_radio);

        mCollectionRadio=getActivity().findViewById(R.id.attribute_collection_radio);

        mArrayRadio =getActivity().findViewById(R.id.attribute_array_radio);

        mDimText=getActivity().findViewById(R.id.attribute_dimension_text);

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
        mUmlClass=mCallback.getProject().findClassByOrder(mClassOrder);

        if (mAttributeOrder != -1) {
            mUmlClassAttribute = mUmlClass.findAttributeByOrder(mAttributeOrder);
        }else {
            mUmlClassAttribute=new UmlClassAttribute(mUmlClass.getUmlClassAttributeCount());
            mUmlClass.addAttribute(mUmlClassAttribute);
        }

    }

    private void initializeFields() {
        if (mAttributeOrder != -1) {
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
            mDimEdit.setText(Integer.toString(mUmlClassAttribute.getArrayDimension()));
        }
        populateTypeSpinner();
    }

    private void populateTypeSpinner() {
        List<String> spinnerArray=new ArrayList<>();
        for (UmlType t:UmlType.getUmlTypes())
            if (!t.getName().equals("void")) spinnerArray.add(t.getName());
        Collections.sort(spinnerArray,new TypeNameComparator());
        ArrayAdapter<String> adapter=new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_item,spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTypeSpinner.setAdapter(adapter);
        if (mAttributeOrder !=-1)
            mTypeSpinner.setSelection(spinnerArray.indexOf(mUmlClassAttribute.getUmlType().getName()));
    }

    private void setOnEditDisplay() {
        mDeleteAttributeButton.setVisibility(View.VISIBLE);
        mEditAttributeText.setText("Edit attribute");
    }

    private void setOnCreateDisplay() {
        mDeleteAttributeButton.setVisibility(View.INVISIBLE);
        mEditAttributeText.setText("Create attribute");
    }

    private void setOnArrayDisplay() {
        mDimText.setVisibility(View.VISIBLE);
        mDimEdit.setVisibility(View.VISIBLE);
    }

    private void setOnSingleDisplay() {
        mDimText.setVisibility(View.INVISIBLE);
        mDimEdit.setVisibility(View.INVISIBLE);
    }

    public void updateAttributeEditorFragment(int attributeOrder, int classOrder) {
        mAttributeOrder=attributeOrder;
        mClassOrder=classOrder;
        initializeMembers();
        initializeFields();
        if (mAttributeOrder ==-1) setOnCreateDisplay();
        else setOnEditDisplay();
        if (mAttributeOrder !=-1 && mUmlClassAttribute.getTypeMultiplicity()==TypeMultiplicity.ARRAY) setOnArrayDisplay();
        else setOnSingleDisplay();
        setOnBackPressedCallback();
    }

    private void createOnBackPressedCallback() {
        mOnBackPressedCallback=new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                onCancelButtonClicked();
            }
        };
    }

    private void setOnBackPressedCallback() {
        requireActivity().getOnBackPressedDispatcher().addCallback(this,mOnBackPressedCallback);
    }

//    **********************************************************************************************
//    UI events
//    **********************************************************************************************


    @Override
    public void onClick(View v) {

        int tag=(int)v.getTag();

        switch (tag) {

            case OK_BUTTON_TAG:
                if(createOrUpdateAttribute())
                    mOnBackPressedCallback.remove();
                    mCallback.closeAttributeEditorFragment(this);
                break;

            case CANCEL_BUTTON_TAG:
                onCancelButtonClicked();
                break;

            case DELETE_ATTRIBUTE_BUTTON_TAG:
                startDeleteAttributeDialog();
                break;

            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId==R.id.attribute_array_radio) setOnArrayDisplay();
        else setOnSingleDisplay();
    }

    private void onCancelButtonClicked() {
        if (mAttributeOrder ==-1) mUmlClass.removeAttribute(mUmlClassAttribute);
        mOnBackPressedCallback.remove();
        mCallback.closeAttributeEditorFragment(this);
    }

//    **********************************************************************************************
//    Edition methods
//    **********************************************************************************************

    private boolean createOrUpdateAttribute() {
        if (getAttributeName().equals("")) {
            Toast.makeText(getContext(), "Attribute name cannot be blank", Toast.LENGTH_SHORT).show();
            return false;
        } else if (mUmlClass.containsAttributeNamed(getAttributeName())) {
            Toast.makeText(getContext(),"This named is already used",Toast.LENGTH_SHORT).show();
            return false;
        } else {
                mUmlClassAttribute.setName(getAttributeName());
                mUmlClassAttribute.setVisibility(getVisibility());
                mUmlClassAttribute.setStatic(isStatic());
                mUmlClassAttribute.setFinal(isFinal());
                mUmlClassAttribute.setUmlType(getType());
                mUmlClassAttribute.setTypeMultiplicity(getMultiplicity());
                mUmlClassAttribute.setArrayDimension(getArrayDimension());
            return true;
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
        return UmlType.valueOf(mTypeSpinner.getSelectedItem().toString(),UmlType.getUmlTypes());
    }

    private TypeMultiplicity getMultiplicity() {
        if (mSimpleRadio.isChecked()) return TypeMultiplicity.SINGLE;
        if (mCollectionRadio.isChecked()) return TypeMultiplicity.COLLECTION;
        return TypeMultiplicity.ARRAY;
    }

    private int getArrayDimension() {
        if (mDimEdit.getText().toString().equals("")) return 0;
        return Integer.parseInt(mDimEdit.getText().toString());
    }

//    **********************************************************************************************
//    Alert dialogs
//    **********************************************************************************************

    private void startDeleteAttributeDialog() {
        final Fragment fragment=this;
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setTitle("Delete attribute")
                .setMessage("Are you sure you want to delete this attribute ?")
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mUmlClass.getAttributes().remove(mUmlClassAttribute);
                        mCallback.closeAttributeEditorFragment(fragment);
                    }
                });
        AlertDialog dialog=builder.create();
        dialog.show();
    }
}