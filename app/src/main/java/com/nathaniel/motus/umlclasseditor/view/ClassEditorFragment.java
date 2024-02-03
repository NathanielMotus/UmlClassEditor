package com.nathaniel.motus.umlclasseditor.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.nathaniel.motus.umlclasseditor.R;
import com.nathaniel.motus.umlclasseditor.controller.CustomExpandableListViewAdapter;
import com.nathaniel.motus.umlclasseditor.databinding.FragmentClassEditorBinding;
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
import java.util.Objects;

public class ClassEditorFragment extends EditorFragment implements View.OnClickListener
        , AdapterView.OnItemLongClickListener,
        RadioGroup.OnCheckedChangeListener,
        ExpandableListView.OnChildClickListener{
    private FragmentClassEditorBinding binding;

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

    private float mXPos;
    private float mYPos;
    private int mClassOrder;
    private UmlClass mUmlClass;
    //class index in current project, -1 if new class

    private static final String XPOS_KEY="xPos";
    private static final String YPOS_KEY="yPos";
    private static final String CLASS_ORDER_KEY ="classOrder";

//    **********************************************************************************************
//    Constructors
//    **********************************************************************************************

    public ClassEditorFragment() {
        // Required empty public constructor
    }

    public static ClassEditorFragment newInstance(float xPos, float yPos,int classOrder) {
        ClassEditorFragment fragment = new ClassEditorFragment();
        Bundle args = new Bundle();
        args.putFloat(XPOS_KEY,xPos);
        args.putFloat(YPOS_KEY,yPos);
        args.putInt(CLASS_ORDER_KEY,classOrder);
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
        binding = FragmentClassEditorBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

//    **********************************************************************************************
//    Configuration methods
//    **********************************************************************************************
    @Override
    protected void readBundle() {
        mXPos=getArguments().getFloat(XPOS_KEY);
        mYPos=getArguments().getFloat(YPOS_KEY);
        mClassOrder =getArguments().getInt(CLASS_ORDER_KEY,-1);
    }

    @Override
    protected void setOnCreateOrEditDisplay() {
        if (mClassOrder ==-1) setOnCreateDisplay();
        else setOnEditDisplay();
    }

    @Override
    protected void configureViews() {
        // delete btn
        binding.deleteClassButton.setTag(DELETE_CLASS_BUTTON_TAG);
        binding.deleteClassButton.setOnClickListener(this);

        // class type radio group
        binding.classTypeRadioGroup.setOnCheckedChangeListener(this);

        // class members - methods & attributes
        binding.classMembersList.setTag(MEMBER_LIST_TAG);
        binding.classMembersList.setOnChildClickListener(this);
        binding.classMembersList.setOnItemLongClickListener(this);

        // ok btn
        binding.classOkButton.setTag(OK_BUTTON_TAG);
        binding.classOkButton.setOnClickListener(this);

        // cancel btn
        binding.classCancelButton.setTag(CANCEL_BUTTON_TAG);
        binding.classCancelButton.setOnClickListener(this);
    }

    @Override
    protected void initializeMembers() {
        if (mClassOrder != -1) {
            mUmlClass = mCallback.getProject().findClassByOrder(mClassOrder);
        } else {
            //class without type
            mUmlClass=new UmlClass(mCallback.getProject().getUmlClassCount());
            mCallback.getProject().addUmlClass(mUmlClass);
        }
        if (mClassOrder !=-1 && mUmlClass.getUmlClassType()== UmlClass.UmlClassType.ENUM) sIsJavaClass=false;
        else sIsJavaClass=true;
    }

    @Override
    protected void initializeFields() {
        if (mClassOrder != -1) {

            binding.classNameInput.setText(mUmlClass.getName());

            switch (mUmlClass.getUmlClassType()) {
                case JAVA_CLASS:
                    binding.classJavaRadio.setChecked(true);
                    break;
                case ABSTRACT_CLASS:
                    binding.classAbstractRadio.setChecked(true);
                    break;
                case INTERFACE:
                    binding.classInterfaceRadio.setChecked(true);
                    break;
                default:
                    binding.classEnumRadio.setChecked(true);
                    break;
            }
        } else {
            binding.classNameInput.setText("");
            binding.classJavaRadio.setChecked(true);
        }
        updateLists();
    }

    private void populateMemberListViewForJavaClass() {
        boolean attributeGroupIsExpanded=false;
        boolean methodGroupIsExpanded=false;
        ExpandableListView membersListView = binding.classMembersList;
        if (membersListView.getExpandableListAdapter() != null) {
            if (membersListView.isGroupExpanded(0))
                attributeGroupIsExpanded = true;
            if (membersListView.isGroupExpanded(1))
                methodGroupIsExpanded=true;
        }

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
        membersListView.setAdapter(adapter);

        if (attributeGroupIsExpanded)
            membersListView.expandGroup(0);
        if (methodGroupIsExpanded)
            membersListView.expandGroup(1);
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
        binding.classMembersList.setAdapter(adapter);
    }

    public void updateLists() {
        if (sIsJavaClass) populateMemberListViewForJavaClass();
        else populateMemberListViewForEnum();
    }

    private void setOnEditDisplay() {
        binding.editClassText.setText("Edit class");
        binding.deleteClassButton.setVisibility(View.VISIBLE);
    }

    private void setOnCreateDisplay() {
        binding.editClassText.setText("Create class");
        binding.deleteClassButton.setVisibility(View.GONE);
    }

    public void updateClassEditorFragment(float xPos, float yPos, int classOrder) {
        mXPos=xPos;
        mYPos=yPos;
        mClassOrder=classOrder;
        initializeMembers();
        initializeFields();
        if (mClassOrder ==-1) setOnCreateDisplay();
        else setOnEditDisplay();
        sIsJavaClass= mClassOrder == -1 || mUmlClass.getUmlClassType() != UmlClass.UmlClassType.ENUM;
        setOnBackPressedCallback();
    }

    @Override
    protected void closeFragment() {
        mCallback.closeClassEditorFragment(this);
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

        if (itemType == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
            AdapterItem item=(AdapterItem)expandableListView.getExpandableListAdapter().getChild(groupPos,childPos);

            if ((expandableListView.getExpandableListAdapter().getGroup(groupPos)).equals(getString(R.string.values_string)) && childPos!=0)
                startDeleteValueDialog(((UmlEnumValue)item).getValueOrder());
            else if ((expandableListView.getExpandableListAdapter().getGroup(groupPos)).equals(getString(R.string.attributes_string)) && childPos!=0)
                startDeleteAttributeDialog(((UmlClassAttribute)item).getAttributeOrder());
            else if ((expandableListView.getExpandableListAdapter().getGroup(groupPos)).equals(getString(R.string.methods_string)) && childPos!=0)
                startDeleteMethodDialog(((UmlClassMethod)item).getMethodOrder());
        }
        return true;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        sIsJavaClass= checkedId != R.id.class_enum_radio;
        updateLists();
    }

    @Override
    public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
        String title=(String) expandableListView.getExpandableListAdapter().getGroup(i);
        AdapterItem item=(AdapterItem) expandableListView.getExpandableListAdapter().getChild(i,i1);

        if (item.getName().equals(getString(R.string.new_attribute_string)) && i1==0)
            mCallback.openAttributeEditorFragment(-1,mUmlClass.getClassOrder());
        else if(item.getName().equals(getString(R.string.new_method_string)) && i1==0)
            mCallback.openMethodEditorFragment(-1,mUmlClass.getClassOrder());
        else if (item.getName().equals(getString(R.string.new_value_string)) && i1==0)
            startNewValueDialog();
        else{
            if (title.equals(getString(R.string.attributes_string)))
                mCallback.openAttributeEditorFragment(((UmlClassAttribute)item).getAttributeOrder(),mUmlClass.getClassOrder());
            else if (title.equals(getString(R.string.methods_string)))
                mCallback.openMethodEditorFragment(((UmlClassMethod)item).getMethodOrder(),mUmlClass.getClassOrder());
            else if (title.equals(getString(R.string.values_string)))
                startRenameValueDialog(((UmlEnumValue)item).getValueOrder());
        }
        return true;
    }

//    **********************************************************************************************
//    Edition methods
//    **********************************************************************************************
    @Override
    protected void clearDraftObject() {
        if (mClassOrder ==-1) mCallback.getProject().removeUmlClass(mUmlClass);
    }

    @Override
    protected boolean createOrUpdateObject() {
        return createOrUpdateClass();
    }

    private boolean createOrUpdateClass() {

        if (getClassName().equals("")) {
            Toast.makeText(getContext(),"Name cannot be blank",Toast.LENGTH_SHORT).show();
            return false;
        } else if (mCallback.getProject().containsClassNamed(getClassName())
                && mCallback.getProject().getUmlClass(getClassName()).getClassOrder()!= mClassOrder) {
            Toast.makeText(getContext(),"This name already exists in project",Toast.LENGTH_SHORT).show();
            return false;
        } else if (UmlType.containsUmlTypeNamed(getClassName()) && UmlType.valueOf(getClassName(),UmlType.getUmlTypes()).getTypeLevel()!= UmlType.TypeLevel.PROJECT) {
            Toast.makeText(getContext(), "This name already exists as standard or custom type", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            mUmlClass.setName(getClassName());
            mUmlClass.setUmlClassType(getClassType());
            if (mClassOrder == -1) {
                mUmlClass.setUmlClassNormalXPos(mXPos);
                mUmlClass.setUmlClassNormalYPos(mYPos);
                //"finish" to declare type
                mUmlClass.upgradeToProjectUmlType();
            }
            return true;
        }
    }

    private String getClassName() {
        return Objects.requireNonNull(binding.classNameInput.getText()).toString();
    }

    private UmlClass.UmlClassType getClassType() {
        if (binding.classJavaRadio.isChecked()) return UmlClass.UmlClassType.JAVA_CLASS;
        if (binding.classAbstractRadio.isChecked()) return UmlClass.UmlClassType.ABSTRACT_CLASS;
        if (binding.classInterfaceRadio.isChecked()) return UmlClass.UmlClassType.INTERFACE;
        return UmlClass.UmlClassType.ENUM;
    }

//    **********************************************************************************************
//    Alert dialogs
//    **********************************************************************************************

    private void startNewValueDialog() {
        final EditText input=new EditText(requireContext());
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Add a value")
                .setMessage("Enter value :")
                .setView(input)
                .setNegativeButton("CANCEL", (d, which) -> d.dismiss())
                .setPositiveButton("OK", (d, which) -> {
                    mUmlClass.addValue(new UmlEnumValue(input.getText().toString(),mUmlClass.getValueCount()));
                    updateLists();
                }).show();
    }

    private void startDeleteClassDialog() {
        final Fragment fragment=this;
       new MaterialAlertDialogBuilder(requireContext())
        .setTitle("Delete class ?")
               .setMessage("Are you sure you want to delete this class ?")
        .setNegativeButton("NO", (d, i) -> d.dismiss())
        .setPositiveButton("YES", (dialog, i) -> {
            mCallback.getProject().removeUmlClass(mUmlClass);
            mCallback.closeClassEditorFragment(fragment);
        }).show();
    }

    private void startRenameValueDialog(final int valueOrder) {
        final EditText editText=new EditText(getContext());

        new MaterialAlertDialogBuilder(requireContext())
                .setView(editText)
                .setTitle("Rename Enum value")
                .setMessage("Enter a new name :")
                .setNegativeButton("CANCEL", (d, i) -> d.dismiss())
                .setPositiveButton("OK", (d, i) -> {
                    mUmlClass.findValueByOrder(valueOrder).setName(editText.getText().toString());
                    updateLists();
                })
                .show();
    }

    private void startDeleteValueDialog(final int valueOrder) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete value ?")
                .setMessage("Are you sure you want to delete this value ?")
                .setNegativeButton("NO", (dialog, i) -> dialog.dismiss())
                .setPositiveButton("YES", (d, i) -> {
                    mUmlClass.removeValue(mUmlClass.findValueByOrder(valueOrder));
                    updateLists();
                }).show();
    }

    private void startDeleteAttributeDialog(final int attributeOrder) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete attribute ?")
                .setMessage("Are you sure you want to delete this attribute ?")
                .setNegativeButton("NO", (d, which) -> d.dismiss())
                .setPositiveButton("YES", (d, which) -> {
                    mUmlClass.removeAttribute(mUmlClass.findAttributeByOrder(attributeOrder));
                    updateLists();
                }).show();
    }

    private void startDeleteMethodDialog(final int methodOrder) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete method ?")
                .setMessage("Are you sure you want to delete this method ?")
                .setNegativeButton("NO", (d, which) -> d.dismiss())
                .setPositiveButton("YES", (d, which) -> {
                    mUmlClass.removeMethod(mUmlClass.findMethodByOrder(methodOrder));
                    updateLists();
                }).show();
    }


}
