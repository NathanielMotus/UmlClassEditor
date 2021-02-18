package com.nathaniel.motus.umlclasseditor.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
    private int mClassOrder;
    private UmlClass mUmlClass;
    //class index in current project, -1 if new class

    private static final String XPOS_KEY="xPos";
    private static final String YPOS_KEY="yPos";
    private static final String CLASS_ORDER_KEY ="classOrder";

    private static OnBackPressedCallback mOnBackPressedCallback;



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
//    Getters and setters
//    **********************************************************************************************

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
            mXPos=getArguments().getFloat(XPOS_KEY);
            mYPos=getArguments().getFloat(YPOS_KEY);
            mClassOrder =getArguments().getInt(CLASS_ORDER_KEY,-1);
        }
        createOnBackPressedCallback();
        setOnBackPressedCallback();
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
        if (mClassOrder ==-1) setOnCreateDisplay();
        else setOnEditDisplay();
        if (mClassOrder !=-1 && mUmlClass.getUmlClassType()== UmlClass.UmlClassType.ENUM) sIsJavaClass=false;
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
        if (mClassOrder != -1) {
            mUmlClass = mCallback.getProject().findClassByOrder(mClassOrder);
        } else {
            mUmlClass=new UmlClass(mCallback.getProject().getUmlClassCount());
            mCallback.getProject().addUmlClass(mUmlClass);
        }
    }

    private void initializeFields() {
        if (mClassOrder != -1) {

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
        boolean attributeGroupIsExpanded=false;
        boolean methodGroupIsExpanded=false;
        if (mMemberListView.getExpandableListAdapter() != null) {
            if (mMemberListView.isGroupExpanded(0))
                attributeGroupIsExpanded = true;
            if (mMemberListView.isGroupExpanded(1))
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
        mMemberListView.setAdapter(adapter);

        if (attributeGroupIsExpanded)
            mMemberListView.expandGroup(0);
        if (methodGroupIsExpanded)
            mMemberListView.expandGroup(1);
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

    public void updateClassEditorFragment(float xPos, float yPos, int classOrder) {
        mXPos=xPos;
        mYPos=yPos;
        mClassOrder=classOrder;
        initializeMembers();
        initializeFields();
        if (mClassOrder ==-1) setOnCreateDisplay();
        else setOnEditDisplay();
        if (mClassOrder !=-1 && mUmlClass.getUmlClassType()== UmlClass.UmlClassType.ENUM) sIsJavaClass=false;
        else sIsJavaClass=true;
        setOnBackPressedCallback();
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
                     mOnBackPressedCallback.remove();
                     mCallback.closeClassEditorFragment(this);
                break;

            case CANCEL_BUTTON_TAG:
               onCancelButtonClicked();
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
        if (checkedId==R.id.class_enum_radio) sIsJavaClass=false;
        else sIsJavaClass=true;
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

    private void onCancelButtonClicked() {
        if (mClassOrder ==-1) mCallback.getProject().removeUmlClass(mUmlClass);
        mOnBackPressedCallback.remove();
        mCallback.closeClassEditorFragment(this);
    }

//    **********************************************************************************************
//    Edition methods
//    **********************************************************************************************

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
                        mUmlClass.addValue(new UmlEnumValue(input.getText().toString(),mUmlClass.getValueCount()));
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

    private void startRenameValueDialog(final int valueOrder) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        final EditText editText=new EditText(getContext());
        builder.setView(editText)
                .setTitle("Rename Enum value")
                .setMessage("Enter a new name :")
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mUmlClass.findValueByOrder(valueOrder).setName(editText.getText().toString());
                        updateLists();
                    }
                })
                .create()
                .show();
    }

    private void startDeleteValueDialog(final int valueOrder) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setTitle("Delete value ?")
            .setMessage("Are you sure you want to delete this value ?")
            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            })
            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mUmlClass.removeValue(mUmlClass.findValueByOrder(valueOrder));
                    updateLists();
                }
            });
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    private void startDeleteAttributeDialog(final int attributeOrder) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setTitle("Delete attribute ?")
                .setMessage("Are you sure you want to delete this attribute ?")
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mUmlClass.removeAttribute(mUmlClass.findAttributeByOrder(attributeOrder));
                        updateLists();
                    }
                });
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    private void startDeleteMethodDialog(final int methodOrder) {
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
                        mUmlClass.removeMethod(mUmlClass.findMethodByOrder(methodOrder));
                        updateLists();
                    }
                });
        AlertDialog dialog=builder.create();
        dialog.show();
    }


}
