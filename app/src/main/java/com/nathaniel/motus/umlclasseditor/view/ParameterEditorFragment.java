package com.nathaniel.motus.umlclasseditor.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.nathaniel.motus.umlclasseditor.R;
import com.nathaniel.motus.umlclasseditor.controller.FragmentObserver;
import com.nathaniel.motus.umlclasseditor.model.MethodParameter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ParameterEditorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ParameterEditorFragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private static final String PARAMETER_INDEX_KEY="parameterIndex";
    private static final String METHOD_EDITOR_FRAGMENT_TAG_KEY="methodEditorFragmentTag";
    private int mParameterIndex;
    private String mMethodEditorFragmentTag;
    private MethodParameter mMethodParameter;
    private ArrayList<MethodParameter> mMethodParameters;
    private FragmentObserver mCallback;

    private TextView mEditParameterText;
    private Button mDeleteParameterButton;
    private EditText mParameterNameEdit;
    private Spinner mParameterTypeSpinner;
    private RadioGroup mParameterMultiplicityRadioGroup;
    private RadioButton mSingleRadio;
    private RadioButton mCollectionRadio;
    private RadioButton mArrayRadio;
    private TextView mDimText;
    private EditText mDimEdit;
    private Button mCancelButton;
    private Button mOKButton;

    private static final int DELETE_PARAMETER_BUTTON_TAG=510;
    private static final int CANCEL_BUTTON_TAG=520;
    private static final int OK_BUTTON_TAG=530;

//    **********************************************************************************************
//    Constructors
//    **********************************************************************************************

    public ParameterEditorFragment() {
        // Required empty public constructor
    }

    public static ParameterEditorFragment newInstance(String methodEditorFragmentTag, int parameterIndex) {
        ParameterEditorFragment fragment = new ParameterEditorFragment();
        Bundle args = new Bundle();
        args.putString(METHOD_EDITOR_FRAGMENT_TAG_KEY,methodEditorFragmentTag);
        args.putInt(PARAMETER_INDEX_KEY,parameterIndex);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMethodEditorFragmentTag = getArguments().getString(METHOD_EDITOR_FRAGMENT_TAG_KEY);
            mParameterIndex = getArguments().getInt(PARAMETER_INDEX_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_parameter_editor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeMembers();
        createCallbackToParentActivity();
        configureViews();
    }

//    **********************************************************************************************
//    UI events
//    **********************************************************************************************

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

    }


//    **********************************************************************************************
//    Configuration methods
//    **********************************************************************************************

    private void initializeMembers() {
        mMethodParameters=((MethodEditorFragment)getFragmentManager().findFragmentByTag(mMethodEditorFragmentTag)).getMethodParameters();

        if (mParameterIndex != -1) {
            mMethodParameter=mMethodParameters.get(mParameterIndex);
        }
    }

    private void createCallbackToParentActivity() {
        mCallback=(FragmentObserver)getActivity();
    }

    private void configureViews() {
        mEditParameterText=getActivity().findViewById(R.id.edit_parameter_text);

        mDeleteParameterButton=getActivity().findViewById(R.id.delete_parameter_button);
        mDeleteParameterButton.setTag(DELETE_PARAMETER_BUTTON_TAG);
        mDeleteParameterButton.setOnClickListener(this);

        mParameterNameEdit=getActivity().findViewById(R.id.parameter_name_input);

        mParameterTypeSpinner=getActivity().findViewById(R.id.parameter_type_spinner);

        mParameterMultiplicityRadioGroup=getActivity().findViewById(R.id.parameter_multiplicity_radio_group);
        mParameterMultiplicityRadioGroup.setOnCheckedChangeListener(this);

        mSingleRadio=getActivity().findViewById(R.id.parameter_simple_radio);

        mCollectionRadio=getActivity().findViewById(R.id.method_collection_radio);

        mArrayRadio=getActivity().findViewById(R.id.parameter_array_radio);

        mDimText=getActivity().findViewById(R.id.parameter_dimension_text);

        mDimEdit=getActivity().findViewById(R.id.parameter_dimension_input);

        mCancelButton=getActivity().findViewById(R.id.parameter_cancel_button);
        mCancelButton.setTag(CANCEL_BUTTON_TAG);
        mCancelButton.setOnClickListener(this);

        mOKButton=getActivity().findViewById(R.id.parameter_ok_button);
        mOKButton.setTag(OK_BUTTON_TAG);
        mOKButton.setOnClickListener(this);
    }
}