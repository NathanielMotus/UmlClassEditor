package com.nathaniel.motus.umlclasseditor.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nathaniel.motus.umlclasseditor.R;
import com.nathaniel.motus.umlclasseditor.controller.FragmentObserver;
import com.nathaniel.motus.umlclasseditor.model.UmlClass;
import com.nathaniel.motus.umlclasseditor.model.UmlClassAttribute;
import com.nathaniel.motus.umlclasseditor.model.UmlClassMethod;
import com.nathaniel.motus.umlclasseditor.model.UmlType;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ClassEditorFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, RadioGroup.OnCheckedChangeListener {

    private TextView mEditClassText;
    private EditText mClassNameEdit;
    private Button mDeleteClassButton;
    private RadioGroup mClassTypeRadioGroup;
    private RadioButton mJavaRadio;
    private RadioButton mAbstractRadio;
    private RadioButton mInterfaceRadio;
    private RadioButton mEnumRadio;
    private RelativeLayout mAttributeRelative;
    private Button mNewAttributeButton;
    private ListView mAttributeList;
    private RelativeLayout mMethodRelative;
    private Button mNewMethodButton;
    private ListView mMethodList;
    private RelativeLayout mValueRelative;
    private Button mNewValueButton;
    private ListView mValueList;
    private Button mOKButton;
    private Button mCancelButton;

    private static final int NEW_ATTRIBUTE_BUTTON_TAG=210;
    private static final int ATTRIBUTE_LIST_TAG=220;
    private static final int NEW_METHOD_BUTTON_TAG=230;
    private static final int METHOD_LIST_TAG=240;
    private static final int OK_BUTTON_TAG=250;
    private static final int CANCEL_BUTTON_TAG=260;
    private static final int DELETE_CLASS_BUTTON_TAG=270;
    private static final int NEW_VALUE_BUTTON_TAG=280;
    private static final int VALUE_LIST_TAG=290;

    private FragmentObserver mCallback;

    private ArrayList<UmlClassAttribute> mUmlClassAttributes;
    private ArrayList<UmlClassMethod> mUmlClassMethods;
    private ArrayList<String> mValues;
    private float mXPos;
    private float mYPos;
    private int mClassIndex;
    private UmlClass mUmlClass;
    //class index in current project, -1 if new class

    private static final String XPOS_KEY="xPos";
    private static final String YPOS_KEY="yPos";
    private static final String CLASS_INDEX_KEY="classIndex";



//    **********************************************************************************************
//    Constructors
//    **********************************************************************************************

    public ClassEditorFragment() {
        // Required empty public constructor
    }

    public static ClassEditorFragment newInstance(float xPos, float yPos,int classIndex) {
        ClassEditorFragment fragment = new ClassEditorFragment();
        Bundle args = new Bundle();
        args.putFloat(XPOS_KEY,xPos);
        args.putFloat(YPOS_KEY,yPos);
        args.putInt(CLASS_INDEX_KEY,classIndex);
        fragment.setArguments(args);
        return fragment;
    }

//    **********************************************************************************************
//    Getters and setters
//    **********************************************************************************************

    public ArrayList<UmlClassAttribute> getUmlClassAttributes() {
        return mUmlClassAttributes;
    }

    public void setUmlClassAttributes(ArrayList<UmlClassAttribute> umlClassAttributes) {
        mUmlClassAttributes = umlClassAttributes;
    }

    public ArrayList<UmlClassMethod> getUmlClassMethods() {
        return mUmlClassMethods;
    }

    public void setUmlClassMethods(ArrayList<UmlClassMethod> umlClassMethods) {
        mUmlClassMethods = umlClassMethods;
    }

    public ArrayList<String> getValues() {
        return mValues;
    }

    public void setValues(ArrayList<String> values) {
        mValues = values;
    }

//    **********************************************************************************************
//    Fragment events
//    **********************************************************************************************

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mXPos=getArguments().getFloat(XPOS_KEY);
            mYPos=getArguments().getFloat(YPOS_KEY);
            mClassIndex=getArguments().getInt(CLASS_INDEX_KEY,-1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_class_editor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        configureViews();
        createCallbackToParentActivity();
        initializeMembers();
        initializeFields();
        if (mClassIndex==-1) setOnCreateDisplay();
        else setOnEditDisplay();
        if (mClassIndex!=-1 && mUmlClass.getUmlClassType()== UmlClass.UmlClassType.ENUM) setOnEnumDisplay();
        else setOnJavaClassDisplay();
    }

//    **********************************************************************************************
//    Configuration methods
//    **********************************************************************************************

    private void configureViews() {
        mEditClassText =getActivity().findViewById(R.id.edit_class_text);

        mClassNameEdit=getActivity().findViewById(R.id.class_name_input);

        mDeleteClassButton=getActivity().findViewById(R.id.delete_class_button);
        mDeleteClassButton.setTag(DELETE_CLASS_BUTTON_TAG);
        mDeleteClassButton.setOnClickListener(this);

        mClassTypeRadioGroup=getActivity().findViewById(R.id.class_type_radio_group);
        mClassTypeRadioGroup.setOnCheckedChangeListener(this);

        mJavaRadio=getActivity().findViewById(R.id.class_java_radio);
        mAbstractRadio=getActivity().findViewById(R.id.class_abstract_radio);
        mInterfaceRadio=getActivity().findViewById(R.id.class_interface_radio);
        mEnumRadio=getActivity().findViewById(R.id.class_enum_radio);

        mAttributeRelative=getActivity().findViewById(R.id.class_attributes_relative);

        mNewAttributeButton=getActivity().findViewById(R.id.class_add_attribute_button);
        mNewAttributeButton.setTag(NEW_ATTRIBUTE_BUTTON_TAG);
        mNewAttributeButton.setOnClickListener(this);

        mAttributeList=getActivity().findViewById(R.id.class_attributes_list);
        mAttributeList.setTag(ATTRIBUTE_LIST_TAG);
        mAttributeList.setOnItemClickListener(this);

        mMethodRelative=getActivity().findViewById(R.id.class_methods_relative);

        mNewMethodButton=getActivity().findViewById(R.id.class_add_method_button);
        mNewMethodButton.setTag(NEW_METHOD_BUTTON_TAG);
        mNewMethodButton.setOnClickListener(this);

        mMethodList=getActivity().findViewById(R.id.class_methods_list);
        mMethodList.setTag(METHOD_LIST_TAG);
        mMethodList.setOnItemClickListener(this);

        mValueRelative=getActivity().findViewById(R.id.class_values_relative);

        mNewValueButton=getActivity().findViewById(R.id.class_add_value_button);
        mNewValueButton.setTag(NEW_VALUE_BUTTON_TAG);
        mNewValueButton.setOnClickListener(this);

        mValueList=getActivity().findViewById(R.id.class_values_list);
        mValueList.setTag(VALUE_LIST_TAG);
        mValueList.setOnItemClickListener(this);
        mValueList.setOnItemLongClickListener(this);

        mOKButton=getActivity().findViewById(R.id.class_ok_button);
        mOKButton.setTag(OK_BUTTON_TAG);
        mOKButton.setOnClickListener(this);

        mCancelButton=getActivity().findViewById(R.id.class_cancel_button);
        mCancelButton.setTag(CANCEL_BUTTON_TAG);
        mCancelButton.setOnClickListener(this);
    }

    private void createCallbackToParentActivity() {
        mCallback=(FragmentObserver)getActivity();
    }

    private void initializeMembers() {
        mUmlClassAttributes=new ArrayList<>();
        mUmlClassMethods=new ArrayList<>();
        mValues=new ArrayList<>();

        if (mClassIndex!=-1) {
            mUmlClass = mCallback.getProject().getUmlClasses().get(mClassIndex);
            mUmlClassAttributes = mUmlClass.getAttributeList();
            mUmlClassMethods=mUmlClass.getMethodList();
            mValues=mUmlClass.getValueList();
        }
    }

    private void initializeFields() {
        if (mClassIndex != -1) {

            mClassNameEdit.setText(mUmlClass.getName());

            switch (mUmlClass.getUmlClassType()) {
                case JAVA_CLASS:
                    mJavaRadio.setChecked(true);
                    break;
                case ABSTRACT_CLASS:
                    mAbstractRadio.setChecked(true);
                    break;
                case INTERFACE:
                    mInterfaceRadio.setChecked(true);
                    break;
                default:
                    mEnumRadio.setChecked(true);
                    break;
            }
            updateLists();
        }
    }

    private void populateAttributeListView() {
        List<String> listViewArray=new ArrayList<String>();
        for (UmlClassAttribute a : mUmlClassAttributes)
            listViewArray.add(a.getName());
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(this.getContext(),android.R.layout.simple_list_item_1,listViewArray);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mAttributeList.setAdapter(arrayAdapter);
    }

    private void populateMethodListView() {
        List<String> listViewArray=new ArrayList<String>();
        for (UmlClassMethod m : mUmlClassMethods)
            listViewArray.add(m.getName());
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(this.getContext(),android.R.layout.simple_list_item_1,listViewArray);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mMethodList.setAdapter(arrayAdapter);
    }

    private void populateValueListView() {
        List<String> listViewArray=new ArrayList<>();
        listViewArray.addAll(mValues);
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,listViewArray);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mValueList.setAdapter(arrayAdapter);
    }

    public void updateLists() {
        populateAttributeListView();
        populateMethodListView();
        populateValueListView();
    }

    private void setOnEditDisplay() {
        mEditClassText.setText("Edit class");
        mDeleteClassButton.setVisibility(View.VISIBLE);
    }

    private void setOnCreateDisplay() {
        mEditClassText.setText("Create class");
        mDeleteClassButton.setVisibility(View.INVISIBLE);
    }

    private void setOnJavaClassDisplay() {
        mAttributeRelative.setVisibility(View.VISIBLE);
        mMethodRelative.setVisibility(View.VISIBLE);
        mValueRelative.setVisibility(View.GONE);
    }

    private void setOnEnumDisplay() {
        mAttributeRelative.setVisibility(View.GONE);
        mMethodRelative.setVisibility(View.GONE);
        mValueRelative.setVisibility(View.VISIBLE);
    }


//    **********************************************************************************************
//    UI events
//    **********************************************************************************************

    @Override
    public void onClick(View v) {

        int tag=(int)v.getTag();

        switch (tag) {

            case OK_BUTTON_TAG:
                 if(createOrUpdateClass())
                     mCallback.closeClassEditorFragment(this);
                break;

            case CANCEL_BUTTON_TAG:
                mCallback.closeClassEditorFragment(this);
                break;

            case NEW_ATTRIBUTE_BUTTON_TAG:
                mCallback.openAttributeEditorFragment(-1);
                break;

            case NEW_METHOD_BUTTON_TAG:
                mCallback.openMethodEditorFragment(-1);
                break;

            case DELETE_CLASS_BUTTON_TAG:
                final Fragment fragment=this;
                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                builder.setTitle("Delete class ?")
                        .setMessage("Are you sure you want to delete this class ?");
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCallback.getProject().removeUmlClass(mUmlClass);
                        mCallback.closeClassEditorFragment(fragment);
                    }
                });
                AlertDialog dialog=builder.create();
                dialog.show();
                break;

            case NEW_VALUE_BUTTON_TAG:
                AlertDialog.Builder adb=new AlertDialog.Builder(getContext());
                adb.setTitle("Add a value")
                        .setMessage("Enter value :");
                final EditText input=new EditText(getContext());
                adb.setView(input)
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mValues.add(input.getText().toString());
                                updateLists();
                            }
                        });
                Dialog inputDialog=adb.create();
                inputDialog.show();
                break;
            default:
                break;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int tag=(int)parent.getTag();

        switch (tag) {
            case ATTRIBUTE_LIST_TAG:
                mCallback.openAttributeEditorFragment(position);
                break;
            case METHOD_LIST_TAG:
                mCallback.openMethodEditorFragment(position);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
            AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
            builder.setTitle("Delete value")
                    .setMessage("Are you sure you want to delete this value ?")
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mValues.remove(position);
                            updateLists();
                        }
                    });
            AlertDialog dialog=builder.create();
            dialog.show();

        return false;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId==R.id.class_enum_radio) setOnEnumDisplay();
        else setOnJavaClassDisplay();
    }

//    **********************************************************************************************
//    Edition methods
//    **********************************************************************************************

    private boolean createOrUpdateClass() {

        if (getClassName().equals("")) {
            Toast.makeText(getContext(),"Name cannot be blank",Toast.LENGTH_SHORT).show();
            return false;
        } else if (mCallback.getProject().containsClassNamed(getClassName())
                && mCallback.getProject().getUmlClasses().indexOf(mCallback.getProject().getUmlClass(getClassName()))!=mClassIndex) {
            Toast.makeText(getContext(),"This name already exists in project",Toast.LENGTH_SHORT).show();
            return false;
        } else if (UmlType.containsUmlTypeNamed(getClassName()) && UmlType.valueOf(getClassName(),UmlType.getUmlTypes()).getTypeLevel()!= UmlType.TypeLevel.PROJECT) {
            Toast.makeText(getContext(), "This name already exists as standard or custom type", Toast.LENGTH_SHORT).show();
            return false;
        }else if (mClassIndex == -1) {
            mCallback.getProject().addUmlClass(new UmlClass(getClassName(), getClassType(), mUmlClassAttributes, mUmlClassMethods, mValues, mXPos, mYPos));
            return true;
        } else {
            mCallback.getProject().getUmlClasses().get(mClassIndex).setName(getClassName());
            mCallback.getProject().getUmlClasses().get(mClassIndex).setUmlClassType(getClassType());
            mCallback.getProject().getUmlClasses().get(mClassIndex).setAttributeList(mUmlClassAttributes);
            mCallback.getProject().getUmlClasses().get(mClassIndex).setMethodList(mUmlClassMethods);
            mCallback.getProject().getUmlClasses().get(mClassIndex).setValueList(mValues);
            return true;
        }
    }

    private String getClassName() {
        return mClassNameEdit.getText().toString();
    }

    private UmlClass.UmlClassType getClassType() {
        if (mJavaRadio.isChecked()) return UmlClass.UmlClassType.JAVA_CLASS;
        if (mAbstractRadio.isChecked()) return UmlClass.UmlClassType.ABSTRACT_CLASS;
        if (mInterfaceRadio.isChecked()) return UmlClass.UmlClassType.INTERFACE;
        return UmlClass.UmlClassType.ENUM;
    }

}