package com.nathaniel.motus.umlclasseditor.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nathaniel.motus.umlclasseditor.R;
import com.nathaniel.motus.umlclasseditor.controller.FragmentObserver;
import com.nathaniel.motus.umlclasseditor.model.MethodParameter;
import com.nathaniel.motus.umlclasseditor.model.TypeMultiplicity;
import com.nathaniel.motus.umlclasseditor.model.UmlClassMethod;
import com.nathaniel.motus.umlclasseditor.model.UmlType;
import com.nathaniel.motus.umlclasseditor.model.Visibility;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MethodEditorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MethodEditorFragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, AdapterView.OnItemClickListener {

    private static final String METHOD_INDEX_KEY ="methodIndex";
    private static final String CLASS_EDITOR_FRAGMENT_TAG_KEY="classEditorFragmentTag";
    private int mMethodIndex;
    private UmlClassMethod mUmlClassMethod;
    private String mClassEditorFragmentTag;
    private ArrayList<UmlClassMethod> mUmlClassMethods;
    private ArrayList<MethodParameter> mMethodParameters;
    private FragmentObserver mCallback;

    private TextView mEditMethodText;
    private Button mDeleteMethodButton;
    private EditText mMethodNameEdit;
    private RadioButton mPublicRadio;
    private RadioButton mProtectedRadio;
    private RadioButton mPrivateRadio;
    private CheckBox mStaticCheck;
    private Spinner mTypeSpinner;
    private RadioGroup mMethodMultiplicityRadioGroup;
    private RadioButton mSingleRadio;
    private RadioButton mCollectionRadio;
    private RadioButton mArrayRadio;
    private TextView mDimText;
    private EditText mDimEdit;
    private ListView mParameterList;
    private Button mAddParameterButton;
    private Button mCancelButton;
    private Button mOKButton;

    private static final int DELETE_METHOD_BUTTON_TAG=410;
    private static final int CANCEL_BUTTON_TAG=420;
    private static final int OK_BUTTON_TAG=430;
    private static final int ADD_PARAMETER_BUTTON_TAG=440;


//    **********************************************************************************************
//    Constructors
//    **********************************************************************************************

    public MethodEditorFragment() {
        // Required empty public constructor
    }

    public static MethodEditorFragment newInstance(String classEditorFragmentTag, int methodIndex) {
        MethodEditorFragment fragment = new MethodEditorFragment();
        Bundle args = new Bundle();
        args.putString(CLASS_EDITOR_FRAGMENT_TAG_KEY, classEditorFragmentTag);
        args.putInt(METHOD_INDEX_KEY, methodIndex);
        fragment.setArguments(args);
        return fragment;
    }


//    **********************************************************************************************
//    Getters and setters
//    **********************************************************************************************

    public ArrayList<MethodParameter> getMethodParameters() {
        return mMethodParameters;
    }


//    **********************************************************************************************
//    Fragment events
//    **********************************************************************************************

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mClassEditorFragmentTag =getArguments().getString(CLASS_EDITOR_FRAGMENT_TAG_KEY);
            mMethodIndex = getArguments().getInt(METHOD_INDEX_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_method_editor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeMembers();
        configureViews();
        createCallbackToParentActivity();
        initializeFields();
        if (mMethodIndex!=-1) setOnEditDisplay();
        else setOnCreateDisplay();
        if (mMethodIndex!=-1 && mUmlClassMethod.getTypeMultiplicity()== TypeMultiplicity.ARRAY) setOnArrayDisplay();
        else setOnSingleDisplay();
    }

//    **********************************************************************************************
//    Configuration methods
//    **********************************************************************************************

    private void configureViews() {
        mEditMethodText=getActivity().findViewById(R.id.edit_method_text);

        mDeleteMethodButton=getActivity().findViewById(R.id.delete_method_button);
        mDeleteMethodButton.setOnClickListener(this);
        mDeleteMethodButton.setTag(DELETE_METHOD_BUTTON_TAG);

        mMethodNameEdit=getActivity().findViewById(R.id.method_name_input);

        mPublicRadio=getActivity().findViewById(R.id.method_public_radio);

        mProtectedRadio=getActivity().findViewById(R.id.method_protected_radio);

        mPrivateRadio=getActivity().findViewById(R.id.method_private_radio);

        mStaticCheck=getActivity().findViewById(R.id.method_static_check);

        mTypeSpinner=getActivity().findViewById(R.id.method_type_spinner);

        mMethodMultiplicityRadioGroup=getActivity().findViewById(R.id.method_multiplicity_radio_group);
        mMethodMultiplicityRadioGroup.setOnCheckedChangeListener(this);

        mSingleRadio=getActivity().findViewById(R.id.method_simple_radio);

        mCollectionRadio=getActivity().findViewById(R.id.method_collection_radio);

        mArrayRadio=getActivity().findViewById(R.id.method_array_radio);

        mDimText=getActivity().findViewById(R.id.method_dimension_text);

        mDimEdit=getActivity().findViewById(R.id.method_dimension_input);

        mParameterList=getActivity().findViewById(R.id.method_parameters_list);
        mParameterList.setOnItemClickListener(this);

        mAddParameterButton=getActivity().findViewById(R.id.method_add_parameter_button);
        mAddParameterButton.setTag(ADD_PARAMETER_BUTTON_TAG);
        mAddParameterButton.setOnClickListener(this);

        mCancelButton=getActivity().findViewById(R.id.method_cancel_button);
        mCancelButton.setOnClickListener(this);
        mCancelButton.setTag(CANCEL_BUTTON_TAG);

        mOKButton=getActivity().findViewById(R.id.method_ok_button);
        mOKButton.setOnClickListener(this);
        mOKButton.setTag(OK_BUTTON_TAG);
    }

    private void createCallbackToParentActivity() {
        mCallback=(FragmentObserver)getActivity();
    }

    private void initializeMembers() {
        mUmlClassMethods=((ClassEditorFragment)getFragmentManager().findFragmentByTag(mClassEditorFragmentTag)).getUmlClassMethods();

        if (mMethodIndex != -1) {
            mUmlClassMethod = mUmlClassMethods.get(mMethodIndex);
            mMethodParameters = mUmlClassMethod.getParameters();
        } else {
            mMethodParameters=new ArrayList<>();
        }

    }

    private void initializeFields() {
        if (mMethodIndex != -1) {
            mMethodNameEdit.setText(mUmlClassMethod.getName());

            switch (mUmlClassMethod.getVisibility()) {
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

            mStaticCheck.setChecked(mUmlClassMethod.isStatic());

            switch (mUmlClassMethod.getTypeMultiplicity()) {
                case SINGLE:
                    mSingleRadio.setChecked(true);
                    break;
                case COLLECTION:
                    mCollectionRadio.setChecked(true);
                    break;
                default:
                    mArrayRadio.setChecked(true);
                    break;
            }

            mDimEdit.setText(Integer.toString(mUmlClassMethod.getArrayDimension()));
            populateParameterListView();
        }
        populateTypeSpinner();
    }

    private void populateTypeSpinner() {
        List<String> spinnerArray=new ArrayList<>();
        for (UmlType t:mCallback.getProject().getUmlTypes())
            spinnerArray.add(t.getName());
        ArrayAdapter<String> adapter=new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_item,spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTypeSpinner.setAdapter(adapter);
        if (mMethodIndex!=-1)
            mTypeSpinner.setSelection(mCallback.getProject().getUmlTypes().indexOf(mUmlClassMethod.getUmlType()));
    }

    private void populateParameterListView() {
        List<String> listViewArray=new ArrayList<>();
        for (MethodParameter p:mMethodParameters)
            listViewArray.add(p.getName());
        ArrayAdapter<String> adapter=new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,listViewArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mParameterList.setAdapter(adapter);
    }

    private void setOnEditDisplay() {
        mEditMethodText.setText("Edit method");
        mDeleteMethodButton.setVisibility(View.VISIBLE);
        JSONObject jj=mUmlClassMethod.toJSONObject();
        Log.i("TEST",jj.toString());
        JSONObject jj2=(UmlClassMethod.fromJSONObject(jj,mCallback.getProject())).toJSONObject();
        Log.i("TEST",jj2.toString());
    }

    private void setOnCreateDisplay() {
        mEditMethodText.setText("Create method");
        mDeleteMethodButton.setVisibility(View.INVISIBLE);
    }

    private void setOnArrayDisplay() {
        mDimText.setVisibility(View.VISIBLE);
        mDimEdit.setVisibility(View.VISIBLE);
    }

    private void setOnSingleDisplay() {
        mDimText.setVisibility(View.INVISIBLE);
        mDimEdit.setVisibility(View.INVISIBLE);
    }

//    **********************************************************************************************
//    UI events
//    **********************************************************************************************

    @Override
    public void onClick(View v) {
        int tag=(int)v.getTag();

        switch (tag) {
            case CANCEL_BUTTON_TAG:
                mCallback.closeMethodEditorFragment(this);
                break;
            case OK_BUTTON_TAG:
                createOrUpdateMethod();
                mCallback.closeMethodEditorFragment(this);
                break;
            case DELETE_METHOD_BUTTON_TAG:
                final Fragment fragment=this;
                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                builder.setTitle("Delete method ?")
                        .setMessage("Are you sure you want to delete this method ?")
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mUmlClassMethods.remove(mUmlClassMethod);
                                mCallback.closeMethodEditorFragment(fragment);
                            }
                        });
                AlertDialog dialog=builder.create();
                dialog.show();
                break;
            case ADD_PARAMETER_BUTTON_TAG:
                mCallback.openParameterEditorFragment(-1);
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId==R.id.method_array_radio) setOnArrayDisplay();
        else setOnSingleDisplay();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mCallback.openParameterEditorFragment(position);
    }

//    **********************************************************************************************
//    Edition methods
//    **********************************************************************************************

    private void createOrUpdateMethod() {
        if (getMethodName() == "") {
            Toast.makeText(getContext(), "Method name cannot be blank", Toast.LENGTH_SHORT).show();
        } else {
            if (mMethodIndex == -1) {
                mUmlClassMethods.add(new UmlClassMethod(getMethodName(), getMethodVisibility(), isStatic(), getMethodType(), getMethodMultiplicity(), getArrayDimension(),mMethodParameters));
            } else {
                mUmlClassMethod.setName(getMethodName());
                mUmlClassMethod.setVisibility(getMethodVisibility());
                mUmlClassMethod.setStatic(isStatic());
                mUmlClassMethod.setUmlType(getMethodType());
                mUmlClassMethod.setTypeMultiplicity(getMethodMultiplicity());
                mUmlClassMethod.setArrayDimension(getArrayDimension());
            }
        }
    }

    private String getMethodName() {
        return mMethodNameEdit.getText().toString();
    }

    private Visibility getMethodVisibility() {
        if (mPublicRadio.isChecked()) return Visibility.PUBLIC;
        if (mPrivateRadio.isChecked()) return Visibility.PROTECTED;
        return Visibility.PRIVATE;
    }

    private boolean isStatic() {
        return mStaticCheck.isChecked();
    }

    private UmlType getMethodType() {
        return mCallback.getProject().getUmlTypes().get(mTypeSpinner.getSelectedItemPosition());
    }

    private TypeMultiplicity getMethodMultiplicity() {
        if (mSingleRadio.isChecked()) return TypeMultiplicity.SINGLE;
        if (mCollectionRadio.isChecked()) return TypeMultiplicity.COLLECTION;
        return TypeMultiplicity.ARRAY;
    }

    private int getArrayDimension() {
        if (mDimEdit.getText().toString().equals("")) return 0;
        return Integer.parseInt(mDimEdit.getText().toString());
    }

    public void updateLists() {
        populateParameterListView();
    }
}