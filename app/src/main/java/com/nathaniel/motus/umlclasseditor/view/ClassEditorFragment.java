package com.nathaniel.motus.umlclasseditor.view;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nathaniel.motus.umlclasseditor.R;
import com.nathaniel.motus.umlclasseditor.controller.CustomExpandableListViewAdapter;
import com.nathaniel.motus.umlclasseditor.controller.FragmentObserver;
import com.nathaniel.motus.umlclasseditor.model.AdapterItem;
import com.nathaniel.motus.umlclasseditor.model.AdapterItemComparator;
import com.nathaniel.motus.umlclasseditor.model.AddItemString;
import com.nathaniel.motus.umlclasseditor.model.UmlClass;
import com.nathaniel.motus.umlclasseditor.model.UmlClassAttribute;
import com.nathaniel.motus.umlclasseditor.model.UmlClassMethod;
import com.nathaniel.motus.umlclasseditor.model.UmlEnumValue;
import com.nathaniel.motus.umlclasseditor.model.UmlType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ClassEditorFragment extends Fragment implements View.OnClickListener
        , AdapterView.OnItemLongClickListener,
        RadioGroup.OnCheckedChangeListener,
        ExpandableListView.OnChildClickListener{

    private TextView mEditClassText;
    private EditText mClassNameEdit;
    private Button mDeleteClassButton;
    private RadioGroup mClassTypeRadioGroup;
    private RadioButton mJavaRadio;
    private RadioButton mAbstractRadio;
    private RadioButton mInterfaceRadio;
    private RadioButton mEnumRadio;
    private RelativeLayout mMemberRelative;
    private ExpandableListView mMemberListView;
    private Button mOKButton;
    private Button mCancelButton;

    private static boolean sIsJavaClass=true;

    private static final int NEW_ATTRIBUTE_BUTTON_TAG=210;
    private static final int MEMBER_LIST_TAG =220;
    private static final int NEW_METHOD_BUTTON_TAG=230;
    private static final int METHOD_LIST_TAG=240;
    private static final int OK_BUTTON_TAG=250;
    private static final int CANCEL_BUTTON_TAG=260;
    private static final int DELETE_CLASS_BUTTON_TAG=270;
    private static final int NEW_VALUE_BUTTON_TAG=280;
    private static final int VALUE_LIST_TAG=290;

    private FragmentObserver mCallback;

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

        createCallbackToParentActivity();
        configureViews();
        initializeMembers();
        initializeFields();
        if (mClassIndex==-1) setOnCreateDisplay();
        else setOnEditDisplay();
        if (mClassIndex!=-1 && mUmlClass.getUmlClassType()== UmlClass.UmlClassType.ENUM) sIsJavaClass=false;
        else sIsJavaClass=true;
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

        mMemberRelative =getActivity().findViewById(R.id.class_members_relative);

        mMemberListView =getActivity().findViewById(R.id.class_members_list);
        mMemberListView.setTag(MEMBER_LIST_TAG);
        mMemberListView.setOnChildClickListener(this);
        mMemberListView.setOnItemLongClickListener(this);

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
        if (mClassIndex != -1) {
            mUmlClass = mCallback.getProject().getUmlClasses().get(mClassIndex);
        } else {
            mUmlClass=new UmlClass(mCallback.getProject().getUmlClassCount());
            mCallback.getProject().addUmlClass(mUmlClass);
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
        }
        updateLists();
    }

    private void populateMemberListViewForJavaClass() {
        List<AdapterItem> attributeList=new ArrayList<>();
        for (UmlClassAttribute a:mUmlClass.getAttributes())
            attributeList.add(a);
        Collections.sort(attributeList,new AdapterItemComparator());
        attributeList.add(0,new AddItemString(getString(R.string.new_attribute_string)));

        List<AdapterItem> methodList=new ArrayList<>();
        for (UmlClassMethod m:mUmlClass.getMethods())
            methodList.add(m);
        Collections.sort(methodList,new AdapterItemComparator());
        methodList.add(0,new AddItemString(getString(R.string.new_method_string)));

        List<String> title=new ArrayList<>();
        title.add(0,getString(R.string.attributes_string));
        title.add(1,getString(R.string.methods_string));

        HashMap<String,List<AdapterItem>> hashMap=new HashMap<>();
        hashMap.put(getString(R.string.attributes_string),attributeList);
        hashMap.put(getString(R.string.methods_string),methodList);
        CustomExpandableListViewAdapter adapter=new CustomExpandableListViewAdapter(getContext(),title,hashMap);
        mMemberListView.setAdapter(adapter);
    }

    private void populateMemberListViewForEnum() {
        List<AdapterItem> valueList=new ArrayList<>();
        valueList.addAll(mUmlClass.getValues());
        Collections.sort(valueList,new AdapterItemComparator());
        valueList.add(0,new AddItemString(getString(R.string.new_value_string)));

        List<String> title=new ArrayList<>();
        title.add(getString(R.string.values_string));

        HashMap<String,List<AdapterItem>> hashMap=new HashMap<>();
        hashMap.put(getString(R.string.values_string),valueList);
        CustomExpandableListViewAdapter adapter=new CustomExpandableListViewAdapter(getContext(),title,hashMap);
        mMemberListView.setAdapter(adapter);
    }

    public void updateLists() {
        if (sIsJavaClass) populateMemberListViewForJavaClass();
        else populateMemberListViewForEnum();
    }

    private void setOnEditDisplay() {
        mEditClassText.setText("Edit class");
        mDeleteClassButton.setVisibility(View.VISIBLE);
    }

    private void setOnCreateDisplay() {
        mEditClassText.setText("Create class");
        mDeleteClassButton.setVisibility(View.INVISIBLE);
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
                if (mClassIndex==-1) mCallback.getProject().getUmlClasses().remove(mUmlClass);
                mCallback.closeClassEditorFragment(this);
                break;

            case DELETE_CLASS_BUTTON_TAG:
                startDeleteClassDialog();
                break;

            default:
                break;
        }

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        ExpandableListView expandableListView=(ExpandableListView)view.getParent();
        long pos=expandableListView.getExpandableListPosition(position);

        int itemType=expandableListView.getPackedPositionType(pos);
        int groupPos=expandableListView.getPackedPositionGroup(pos);
        int childPos=expandableListView.getPackedPositionChild(pos);

        Log.i("TEST",Integer.toString(groupPos)+"-"+Integer.toString(childPos));

        return true;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId==R.id.class_enum_radio) sIsJavaClass=false;
        else sIsJavaClass=true;
        updateLists();
    }

    @Override
    public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
        String title=(String) expandableListView.getExpandableListAdapter().getGroup(i);
        AdapterItem item=(AdapterItem) expandableListView.getExpandableListAdapter().getChild(i,i1);

        if (item.getName().equals(getString(R.string.new_attribute_string)))
            mCallback.openAttributeEditorFragment(-1,mUmlClass.getClassIndex());
        else if(item.getName().equals(getString(R.string.new_method_string)))
            mCallback.openMethodEditorFragment(-1,mUmlClass.getClassIndex());
        else if (item.getName().equals(getString(R.string.new_value_string)))
            startNewValueDialog();
        else{
            if (title.equals(getString(R.string.attributes_string)))
                mCallback.openAttributeEditorFragment(mUmlClass.getAttributes().indexOf((UmlClassAttribute)item),mUmlClass.getClassIndex());
            else if (title.equals(getString(R.string.methods_string)))
                mCallback.openMethodEditorFragment(mUmlClass.getMethods().indexOf((UmlClassMethod)item),mUmlClass.getClassIndex());
        }
        return true;
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
        } else {
            mUmlClass.setName(getClassName());
            mUmlClass.setUmlClassType(getClassType());
            if (mClassIndex == -1) {
                mUmlClass.setUmlClassNormalXPos(mXPos);
                mUmlClass.setUmlClassNormalYPos(mYPos);
            }
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

//    **********************************************************************************************
//    Alert dialogs
//    **********************************************************************************************

    private void startNewValueDialog() {
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
                        mUmlClass.getValues().add(new UmlEnumValue(input.getText().toString(),mUmlClass.getValueCount()));
                        updateLists();
                    }
                });
        Dialog inputDialog=adb.create();
        inputDialog.show();
    }

    private void startDeleteClassDialog() {
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
    }

    private void startDeleteValueDialog(final int position) {
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
                    mUmlClass.getValues().remove(position);
                    updateLists();
                }
            });
        AlertDialog dialog=builder.create();
        dialog.show();
    }

}