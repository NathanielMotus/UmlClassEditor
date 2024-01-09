package com.nathaniel.motus.umlclasseditor.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.nathaniel.motus.umlclasseditor.R;
import com.nathaniel.motus.umlclasseditor.databinding.FragmentParameterEditorBinding;
import com.nathaniel.motus.umlclasseditor.model.MethodParameter;
import com.nathaniel.motus.umlclasseditor.model.TypeMultiplicity;
import com.nathaniel.motus.umlclasseditor.model.TypeNameComparator;
import com.nathaniel.motus.umlclasseditor.model.UmlClass;
import com.nathaniel.motus.umlclasseditor.model.UmlClassMethod;
import com.nathaniel.motus.umlclasseditor.model.UmlType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ParameterEditorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ParameterEditorFragment extends EditorFragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private FragmentParameterEditorBinding binding;
    private static final String PARAMETER_ORDER_KEY ="parameterOrder";
    private static final String METHOD_ORDER_KEY ="methodOrder";
    private static final String CLASS_ORDER_KEY ="classOrder";
    private static final String METHOD_EDITOR_FRAGMENT_TAG_KEY="methodEditorFragmentTag";
    private int mParameterOrder;
    private int mMethodOrder;
    private int mClassOrder;
    private String mMethodEditorFragmentTag;
    private MethodParameter mMethodParameter;
    private UmlClassMethod mUmlClassMethod;
    private UmlClass mUmlClass;

    private static final int DELETE_PARAMETER_BUTTON_TAG=510;
    private static final int CANCEL_BUTTON_TAG=520;
    private static final int OK_BUTTON_TAG=530;

//    **********************************************************************************************
//    Constructors
//    **********************************************************************************************

    public ParameterEditorFragment() {
        // Required empty public constructor
    }

    public static ParameterEditorFragment newInstance(String methodEditorFragmentTag, int parameterOrder,int methodOrder,int classOrder) {
        ParameterEditorFragment fragment = new ParameterEditorFragment();
        Bundle args = new Bundle();
        args.putString(METHOD_EDITOR_FRAGMENT_TAG_KEY,methodEditorFragmentTag);
        args.putInt(PARAMETER_ORDER_KEY,parameterOrder);
        args.putInt(METHOD_ORDER_KEY,methodOrder);
        args.putInt(CLASS_ORDER_KEY,classOrder);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentParameterEditorBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

//    **********************************************************************************************
//    UI events
//    **********************************************************************************************

    @Override
    public void onClick(View v) {
        int tag=(int)v.getTag();
        switch (tag) {
            case CANCEL_BUTTON_TAG:
                onCancelButtonClicked();
                break;
            case OK_BUTTON_TAG:
                onOKButtonClicked();
                break;
            case DELETE_PARAMETER_BUTTON_TAG:
                startDeleteParameterDialog();
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId==R.id.parameter_array_radio) setOnArrayDisplay();
        else setOnSingleDisplay();
    }

    private void onCancelButtonClicked() {
        if (mParameterOrder ==-1) mUmlClassMethod.removeParameter(mMethodParameter);
        mOnBackPressedCallback.remove();
        mCallback.closeParameterEditorFragment(this);
    }

//    **********************************************************************************************
//    Configuration methods
//    **********************************************************************************************

    @Override
    protected void setOnCreateOrEditDisplay() {
        if (mParameterOrder==-1)
            setOnCreateDisplay();
        else
            setOnEditDisplay();
    }

    @Override
    protected void readBundle() {
        mMethodEditorFragmentTag = getArguments().getString(METHOD_EDITOR_FRAGMENT_TAG_KEY);
        mParameterOrder = getArguments().getInt(PARAMETER_ORDER_KEY);
        mClassOrder =getArguments().getInt(CLASS_ORDER_KEY);
        mMethodOrder =getArguments().getInt(METHOD_ORDER_KEY);
    }

    protected void initializeMembers() {
        mUmlClass=mCallback.getProject().findClassByOrder(mClassOrder);
        mUmlClassMethod=mUmlClass.findMethodByOrder(mMethodOrder);

        if (mParameterOrder != -1) {
            mMethodParameter = mUmlClassMethod.findParameterByOrder(mParameterOrder);
        } else {
            mMethodParameter=new MethodParameter(mUmlClassMethod.getParameterCount());
            mUmlClassMethod.addParameter(mMethodParameter);
        }
    }

    protected void configureViews() {
        // delete btn
        binding.deleteParameterButton.setOnClickListener(this);
        binding.deleteParameterButton.setTag(DELETE_PARAMETER_BUTTON_TAG);

        // param type single =void, collection = list, array = array
        binding.parameterMultiplicityRadioGroup.setOnCheckedChangeListener(this);

        // cancel btn
        binding.parameterCancelButton.setTag(CANCEL_BUTTON_TAG);
        binding.parameterCancelButton.setOnClickListener(this);

        // ok btn
        binding.parameterOkButton.setTag(OK_BUTTON_TAG);
        binding.parameterOkButton.setOnClickListener(this);
    }

    protected void initializeFields() {
        if (mParameterOrder != -1) {
            binding.parameterNameInput.setText(mMethodParameter.getName());
            if (mMethodParameter.getTypeMultiplicity() == TypeMultiplicity.SINGLE)
                binding.parameterSimpleRadio.setChecked(true);
            if (mMethodParameter.getTypeMultiplicity() == TypeMultiplicity.COLLECTION)
                binding.parameterCollectionRadio.setChecked(true);
            if (mMethodParameter.getTypeMultiplicity() == TypeMultiplicity.ARRAY)
                binding.parameterArrayRadio.setChecked(true);
            binding.parameterDimensionInput.setText(Integer.toString(mMethodParameter.getArrayDimension()));
            if (mMethodParameter.getTypeMultiplicity() == TypeMultiplicity.ARRAY)
                setOnArrayDisplay();
            else setOnSingleDisplay();
        } else {
            binding.parameterNameInput.setText("");
            binding.parameterSimpleRadio.setChecked(true);
            binding.parameterDimensionInput.setText("");
            setOnSingleDisplay();
        }
        populateTypeSpinner();
    }

    private void populateTypeSpinner() {
        List<String> arrayList=new ArrayList<>();
        for (UmlType t:UmlType.getUmlTypes())
            if(!t.getName().equals("void")) arrayList.add(t.getName());
        Collections.sort(arrayList,new TypeNameComparator());
        ArrayAdapter<String> adapter=new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_item,arrayList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.parameterTypeSpinner.setAdapter(adapter);
        if (mParameterOrder !=-1)
            binding.parameterTypeSpinner.setSelection(arrayList.indexOf(mMethodParameter.getUmlType().getName()));
    }

    private void setOnEditDisplay(){
        binding.editParameterText.setText("Edit parameter");
        binding.deleteParameterButton.setVisibility(View.VISIBLE);
    }

    private void setOnCreateDisplay() {
        binding.editParameterText.setText("Create parameter");
        binding.deleteParameterButton.setVisibility(View.INVISIBLE);
    }

    private void setOnSingleDisplay() {
        binding.parameterDimensionText.setVisibility(View.INVISIBLE);
        binding.parameterDimensionInput.setVisibility(View.INVISIBLE);
    }

    private void setOnArrayDisplay() {
        binding.parameterDimensionText.setVisibility(View.VISIBLE);
        binding.parameterDimensionInput.setVisibility(View.VISIBLE);
    }

    public void updateParameterEditorFragment(int parameterOrder,int methodOrder,int classOrder) {
        mParameterOrder=parameterOrder;
        mMethodOrder=methodOrder;
        mClassOrder=classOrder;
        initializeMembers();
        initializeFields();
        if (mParameterOrder ==-1) setOnCreateDisplay();
        else setOnEditDisplay();
        if (mParameterOrder !=-1 && mMethodParameter.getTypeMultiplicity()==TypeMultiplicity.ARRAY)
            setOnArrayDisplay();
        else setOnSingleDisplay();
        setOnBackPressedCallback();
    }

    @Override
    protected void closeFragment() {
        mCallback.closeParameterEditorFragment(this);
    }

//    **********************************************************************************************
//    Edition methods
//    **********************************************************************************************

    @Override
    protected void clearDraftObject() {
        if (mParameterOrder==-1)
            mUmlClassMethod.removeParameter(mMethodParameter);
    }

    @Override
    protected boolean createOrUpdateObject() {
        return createOrUpdateParameter();
    }

    private boolean createOrUpdateParameter() {
        if (getParameterName().equals("")) {
            Toast.makeText(getContext(), "Parameter cannot be blank", Toast.LENGTH_SHORT).show();
            return false;
        } else if (mUmlClassMethod.containsParameterNamed(getParameterName()) &&
                mUmlClassMethod.getParameter(getParameterName()).getParameterOrder()!=mParameterOrder) {
            Toast.makeText(getContext(),"This named is already used",Toast.LENGTH_SHORT).show();
            return false;
        }else {
                mMethodParameter.setName(getParameterName());
                mMethodParameter.setUmlType(getParameterType());
                mMethodParameter.setTypeMultiplicity(getParameterMultiplicity());
                mMethodParameter.setArrayDimension(getArrayDimension());
            return true;
        }
    }

    private String getParameterName() {
        return Objects.requireNonNull(binding.parameterNameInput.getText()).toString();
    }

    private UmlType getParameterType() {
        return UmlType.valueOf(binding.parameterTypeSpinner.getSelectedItem().toString(),UmlType.getUmlTypes());
    }

    private TypeMultiplicity getParameterMultiplicity() {
        if (binding.parameterSimpleRadio.isChecked()) return TypeMultiplicity.SINGLE;
        if (binding.parameterCollectionRadio.isChecked()) return TypeMultiplicity.COLLECTION;
        return TypeMultiplicity.ARRAY;
    }

    private int getArrayDimension() {
        if (binding.parameterDimensionInput.getText().toString().equals("")) return 0;
        return Integer.parseInt(binding.parameterDimensionInput.getText().toString());
    }

    //    **********************************************************************************************
//    Alert dialogs
//    **********************************************************************************************
    private void startDeleteParameterDialog() {
        final Fragment fragment=this;
        new MaterialAlertDialogBuilder(requireContext())
        .setTitle("Delete Parameter")
                .setMessage("Are you sure you to delete this parameter ?")
                .setNegativeButton("NO", (DialogInterface.OnClickListener) (d, which) -> d.dismiss())
                .setPositiveButton("YES", (DialogInterface.OnClickListener) (d, which) -> {
                    mUmlClassMethod.removeParameter(mMethodParameter);
                    mCallback.closeParameterEditorFragment(fragment);
                }).show();

    }
}