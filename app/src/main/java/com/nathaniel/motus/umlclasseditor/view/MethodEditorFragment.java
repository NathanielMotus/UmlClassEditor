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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nathaniel.motus.umlclasseditor.R;
import com.nathaniel.motus.umlclasseditor.controller.CustomExpandableListViewAdapter;
import com.nathaniel.motus.umlclasseditor.controller.FragmentObserver;
import com.nathaniel.motus.umlclasseditor.model.AdapterItem;
import com.nathaniel.motus.umlclasseditor.model.AdapterItemComparator;
import com.nathaniel.motus.umlclasseditor.model.AddItemString;
import com.nathaniel.motus.umlclasseditor.model.MethodParameter;
import com.nathaniel.motus.umlclasseditor.model.TypeMultiplicity;
import com.nathaniel.motus.umlclasseditor.model.TypeNameComparator;
import com.nathaniel.motus.umlclasseditor.model.UmlClass;
import com.nathaniel.motus.umlclasseditor.model.UmlClassMethod;
import com.nathaniel.motus.umlclasseditor.model.UmlType;
import com.nathaniel.motus.umlclasseditor.model.Visibility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MethodEditorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MethodEditorFragment extends Fragment implements View.OnClickListener,
        RadioGroup.OnCheckedChangeListener,
        AdapterView.OnItemLongClickListener,
        ExpandableListView.OnChildClickListener{

    private static final String METHOD_ORDER_KEY ="methodOrder";
    private static final String CLASS_ORDER_KEY ="classOrder";
    private static final String CLASS_EDITOR_FRAGMENT_TAG_KEY="classEditorFragmentTag";
    private int mMethodOrder;
    private int mClassOrder;
    private UmlClassMethod mUmlClassMethod;
    private UmlClass mUmlClass;
    private String mClassEditorFragmentTag;
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
    private ExpandableListView mParameterList;
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

    public static MethodEditorFragment newInstance(String classEditorFragmentTag, int methodOrder,int classOrder) {
        MethodEditorFragment fragment = new MethodEditorFragment();
        Bundle args = new Bundle();
        args.putString(CLASS_EDITOR_FRAGMENT_TAG_KEY, classEditorFragmentTag);
        args.putInt(METHOD_ORDER_KEY, methodOrder);
        args.putInt(CLASS_ORDER_KEY,classOrder);
        fragment.setArguments(args);
        return fragment;
    }


//    **********************************************************************************************
//    Getters and setters
//    **********************************************************************************************

    public UmlClassMethod getUmlClassMethod() {
        return mUmlClassMethod;
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
            mClassEditorFragmentTag =getArguments().getString(CLASS_EDITOR_FRAGMENT_TAG_KEY);
            mMethodOrder = getArguments().getInt(METHOD_ORDER_KEY);
            mClassOrder =getArguments().getInt(CLASS_ORDER_KEY);
        }
        setOnBackPressedCallback();
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

        createCallbackToParentActivity();
        initializeMembers();
        configureViews();
        initializeFields();
        if (mMethodOrder !=-1) setOnEditDisplay();
        else setOnCreateDisplay();
        if (mMethodOrder !=-1 && mUmlClassMethod.getTypeMultiplicity()== TypeMultiplicity.ARRAY) setOnArrayDisplay();
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
        mParameterList.setOnChildClickListener(this);
        mParameterList.setOnItemLongClickListener(this);

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
        mUmlClass=mCallback.getProject().findClassByOrder(mClassOrder);

        if (mMethodOrder != -1) {
            mUmlClassMethod = mUmlClass.findMethodByOrder(mMethodOrder);
        } else {
            mUmlClassMethod=new UmlClassMethod(mUmlClass.getUmlClassMethodCount());
            mUmlClass.addMethod(mUmlClassMethod);
        }

    }

    private void initializeFields() {
        if (mMethodOrder != -1) {
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
        }
        populateTypeSpinner();
        populateParameterListView();
    }

    private void populateTypeSpinner() {
        List<String> spinnerArray=new ArrayList<>();
        for (UmlType t:UmlType.getUmlTypes())
            spinnerArray.add(t.getName());
        Collections.sort(spinnerArray,new TypeNameComparator());
        ArrayAdapter<String> adapter=new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_item,spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTypeSpinner.setAdapter(adapter);
        if (mMethodOrder !=-1)
            mTypeSpinner.setSelection(spinnerArray.indexOf(mUmlClassMethod.getUmlType().getName()));
        else mTypeSpinner.setSelection(spinnerArray.indexOf("void"));
    }

    private void populateParameterListView() {
        List<AdapterItem> parameterList=new ArrayList<>();
        for (MethodParameter p:mUmlClassMethod.getParameters())
            parameterList.add(p);
        Collections.sort(parameterList,new AdapterItemComparator());
        parameterList.add(0,new AddItemString(getString(R.string.new_parameter_string)));

        HashMap<String,List<AdapterItem>> hashMap=new HashMap<>();
        hashMap.put(getString(R.string.parameters_string),parameterList);

        List<String> title=new ArrayList<>();
        title.add(getString(R.string.parameters_string));

        CustomExpandableListViewAdapter adapter=new CustomExpandableListViewAdapter(getContext(),title,hashMap);
        mParameterList.setAdapter(adapter);
    }

    private void setOnEditDisplay() {
        mEditMethodText.setText("Edit method");
        mDeleteMethodButton.setVisibility(View.VISIBLE);
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

    private void setOnBackPressedCallback() {
        OnBackPressedCallback onBackPressedCallback=new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                onCancelButtonClicked();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this,onBackPressedCallback);
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
                if(createOrUpdateMethod())
                    mCallback.closeMethodEditorFragment(this);
                break;
            case DELETE_METHOD_BUTTON_TAG:
                startDeleteMethodDialog();
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
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
        ExpandableListView expandableListView=(ExpandableListView)view.getParent();
        long pos=expandableListView.getExpandableListPosition(position);

        int itemType=expandableListView.getPackedPositionType(pos);
        int groupPos=expandableListView.getPackedPositionGroup(pos);
        int childPos=expandableListView.getPackedPositionChild(pos);

        AdapterItem item=(AdapterItem)expandableListView.getExpandableListAdapter().getChild(groupPos,childPos);

        if (itemType==ExpandableListView.PACKED_POSITION_TYPE_CHILD && childPos!=0)
            startDeleteParameterDialog(((MethodParameter)item).getParameterOrder());

        return true;
    }

    @Override
    public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
        AdapterItem item=(AdapterItem) expandableListView.getExpandableListAdapter().getChild(i,i1);
        if (item.getName().equals(getString(R.string.new_parameter_string)) && i1==0)
            mCallback.openParameterEditorFragment(-1,mUmlClassMethod.getMethodOrder(),mUmlClass.getClassOrder());
        else
            mCallback.openParameterEditorFragment(((MethodParameter)item).getParameterOrder(),
                    mUmlClassMethod.getMethodOrder(),mUmlClass.getClassOrder());
        return true;
    }

    private void onCancelButtonClicked() {
        if (mMethodOrder ==-1) mUmlClass.removeMethod(mUmlClassMethod);
        mCallback.closeMethodEditorFragment(this);
    }

//    **********************************************************************************************
//    Edition methods
//    **********************************************************************************************

    private boolean createOrUpdateMethod() {
        if (getMethodName().equals("")) {
            Toast.makeText(getContext(), "Method name cannot be blank", Toast.LENGTH_SHORT).show();
            return false;
        } else {
                mUmlClassMethod.setName(getMethodName());
                mUmlClassMethod.setVisibility(getMethodVisibility());
                mUmlClassMethod.setStatic(isStatic());
                mUmlClassMethod.setUmlType(getMethodType());
                mUmlClassMethod.setTypeMultiplicity(getMethodMultiplicity());
                mUmlClassMethod.setArrayDimension(getArrayDimension());
        }
        if (mUmlClass.containsEquivalentMethodTo(mUmlClassMethod)) {
            Toast.makeText(getContext(), "This method is already defined", Toast.LENGTH_SHORT).show();
            return false;
        }else
            return true;
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
        return UmlType.valueOf(mTypeSpinner.getSelectedItem().toString(),UmlType.getUmlTypes());
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

//    **********************************************************************************************
//    Alert dialogs
//    **********************************************************************************************
    private void startDeleteMethodDialog() {
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
                        mUmlClass.removeMethod(mUmlClassMethod);
                        mCallback.closeMethodEditorFragment(fragment);
                    }
                });
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    private void startDeleteParameterDialog(final int parameterIndex) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setTitle("Delete parameter ?")
                .setMessage("Are you sure you want delete this parameter ?")
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mUmlClassMethod.removeParameter(mUmlClassMethod.findParameterByOrder(parameterIndex));
                        updateLists();
                    }
                })
                .create()
                .show();
    }
}