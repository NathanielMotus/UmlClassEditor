package com.nathaniel.motus.umlclasseditor.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
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

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.nathaniel.motus.umlclasseditor.R;
import com.nathaniel.motus.umlclasseditor.databinding.FragmentAttributeEditorBinding;
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
public class AttributeEditorFragment extends EditorFragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private FragmentAttributeEditorBinding binding;
    private static final String ATTRIBUTE_ORDER_KEY = "attributeOrder";
    private static final String CLASS_ORDER_KEY ="classOrder";
    private static final String CLASS_EDITOR_FRAGMENT_TAG_KEY="classEditorFragmentTag";
    private int mAttributeOrder;
    private int mClassOrder;
    private UmlClassAttribute mUmlClassAttribute;
    private String mClassEditorFragmentTag;
    private UmlClass mUmlClass;
    private static final int TYPE_SPINNER_TAG=310;
    private static final int OK_BUTTON_TAG=320;
    private static final int CANCEL_BUTTON_TAG=330;
    private static final int DELETE_ATTRIBUTE_BUTTON_TAG=340;

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
//    Fragment events
//    **********************************************************************************************

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAttributeEditorBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

//    **********************************************************************************************
//    Configuration methods
//    **********************************************************************************************

    @Override
    protected void readBundle() {
        mAttributeOrder =getArguments().getInt(ATTRIBUTE_ORDER_KEY,-1);
        mClassOrder =getArguments().getInt(CLASS_ORDER_KEY,-1);
        mClassEditorFragmentTag=getArguments().getString(CLASS_EDITOR_FRAGMENT_TAG_KEY);
    }

    @Override
    protected void setOnCreateOrEditDisplay() {
        if (mAttributeOrder ==-1) setOnCreateDisplay();
        else setOnEditDisplay();
    }

    @Override
    protected void configureViews() {
        // delete btn
        binding.deleteAttributeButton.setTag(DELETE_ATTRIBUTE_BUTTON_TAG);
        binding.deleteAttributeButton.setOnClickListener(this);
        // attrib type radio grp
        binding.attributeMultiplicityRadioGroup.setOnCheckedChangeListener(this);
        // spinner
        binding.attributeTypeSpinner.setTag(TYPE_SPINNER_TAG);
        // ok btn
        binding.attributeOkButton.setTag(OK_BUTTON_TAG);
        binding.attributeOkButton.setOnClickListener(this);
        // cancel btn
        binding.attributeCancelButton.setTag(CANCEL_BUTTON_TAG);
        binding.attributeCancelButton.setOnClickListener(this);

    }

    @Override
    protected void initializeMembers() {
        mUmlClass=mCallback.getProject().findClassByOrder(mClassOrder);

        if (mAttributeOrder != -1) {
            mUmlClassAttribute = mUmlClass.findAttributeByOrder(mAttributeOrder);
        }else {
            mUmlClassAttribute=new UmlClassAttribute(mUmlClass.getUmlClassAttributeCount());
            mUmlClass.addAttribute(mUmlClassAttribute);
        }

    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initializeFields() {
        if (mAttributeOrder != -1) {
            binding.attributeNameInput.setText(mUmlClassAttribute.getName());

            switch (mUmlClassAttribute.getVisibility()) {
                case PUBLIC:
                    binding.attributePublicRadio.setChecked(true);
                    break;
                case PROTECTED:
                    binding.attributeProtectedRadio.setChecked(true);
                    break;
                default:
                    binding.attributePrivateRadio.setChecked(true);
                    break;
            }

            binding.attributeStaticCheck.setChecked(mUmlClassAttribute.isStatic());
            binding.attributeFinalCheck.setChecked(mUmlClassAttribute.isFinal());

            switch (mUmlClassAttribute.getTypeMultiplicity()) {
                case SINGLE:
                    binding.attributeSimpleRadio.setChecked(true);
                    break;
                case COLLECTION:
                    binding.attributeCollectionRadio.setChecked(true);
                    break;
                default:
                    binding.attributeArrayRadio.setChecked(true);
                    break;
            }
            binding.attributeDimensionInput.setText(Integer.toString(mUmlClassAttribute.getArrayDimension()));
            if (mUmlClassAttribute.getTypeMultiplicity() == TypeMultiplicity.ARRAY)
                setOnArrayDisplay();
            else
                setOnSingleDisplay();

        } else {
            binding.attributeNameInput.setText("");
            binding.attributePublicRadio.setChecked(true);
            binding.attributeStaticCheck.setChecked(false);
            binding.attributeFinalCheck.setChecked(false);
            binding.attributeSimpleRadio.setChecked(true);
            binding.attributeDimensionInput.setText("");
            setOnSingleDisplay();
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
        binding.attributeTypeSpinner.setAdapter(adapter);
        if (mAttributeOrder !=-1)
            binding.attributeTypeSpinner.setSelection(spinnerArray.indexOf(mUmlClassAttribute.getUmlType().getName()));
    }

    private void setOnEditDisplay() {
        binding.deleteAttributeButton.setVisibility(View.VISIBLE);
        binding.editAttributeText.setText("Edit attribute");
    }

    private void setOnCreateDisplay() {
        binding.deleteAttributeButton.setVisibility(View.INVISIBLE);
        binding.editAttributeText.setText("Create attribute");
    }

    private void setOnArrayDisplay() {
        binding.attributeDimensionText.setVisibility(View.VISIBLE);
        binding.attributeDimensionInput.setVisibility(View.VISIBLE);
    }

    private void setOnSingleDisplay() {
        binding.attributeDimensionText.setVisibility(View.INVISIBLE);
        binding.attributeDimensionInput.setVisibility(View.INVISIBLE);
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

    @Override
    protected void closeFragment() {
        mCallback.closeAttributeEditorFragment(this);
    }

    //    **********************************************************************************************
//    UI events
//    **********************************************************************************************


    @Override
    public void onClick(View v) {

        int tag=(int)v.getTag();

        switch (tag) {

            case OK_BUTTON_TAG:
                onOKButtonClicked();
                break;

            case CANCEL_BUTTON_TAG:
                onCancelButtonCLicked();
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

//    **********************************************************************************************
//    Edition methods
//    **********************************************************************************************

    @Override
    protected boolean createOrUpdateObject() {
        return createOrUpdateAttribute();
    }

    @Override
    protected void clearDraftObject() {
        if (mAttributeOrder==-1)
            mUmlClass.removeAttribute(mUmlClassAttribute);
    }

    private boolean createOrUpdateAttribute() {
        if (getAttributeName().equals("")) {
            Toast.makeText(getContext(), "Attribute name cannot be blank", Toast.LENGTH_SHORT).show();
            return false;
        } else if (mUmlClass.containsAttributeNamed(getAttributeName()) &&
                mUmlClass.getAttribute(getAttributeName()).getAttributeOrder()!=mAttributeOrder) {
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
        return binding.attributeNameInput.getText().toString();
    }

    private Visibility getVisibility() {
        if (binding.attributePublicRadio.isChecked()) return Visibility.PUBLIC;
        if (binding.attributeProtectedRadio.isChecked()) return Visibility.PROTECTED;
        return Visibility.PRIVATE;
    }

    private boolean isStatic() {
        return binding.attributeStaticCheck.isChecked();
    }

    private boolean isFinal() {
        return binding.attributeFinalCheck.isChecked();
    }

    private UmlType getType() {
        return UmlType.valueOf(binding.attributeTypeSpinner.getSelectedItem().toString(),UmlType.getUmlTypes());
    }

    private TypeMultiplicity getMultiplicity() {
        if (binding.attributeSimpleRadio.isChecked()) return TypeMultiplicity.SINGLE;
        if (binding.attributeCollectionRadio.isChecked()) return TypeMultiplicity.COLLECTION;
        return TypeMultiplicity.ARRAY;
    }

    private int getArrayDimension() {
        if (binding.attributeDimensionInput.getText().toString().equals("")) return 0;
        return Integer.parseInt(binding.attributeDimensionInput.getText().toString());
    }

//    **********************************************************************************************
//    Alert dialogs
//    **********************************************************************************************

    private void startDeleteAttributeDialog() {
        final Fragment fragment=this;
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete attribute")
                .setMessage("Are you sure you want to delete this attribute ?")
                .setNegativeButton("NO", (d, which) -> d.dismiss())
                .setPositiveButton("YES", (d, which) -> {
                    mUmlClass.getAttributes().remove(mUmlClassAttribute);
                    mCallback.closeAttributeEditorFragment(fragment);
                }).show();
       
    }
}