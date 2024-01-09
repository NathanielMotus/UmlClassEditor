package com.nathaniel.motus.umlclasseditor.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
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

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.nathaniel.motus.umlclasseditor.R;
import com.nathaniel.motus.umlclasseditor.controller.CustomExpandableListViewAdapter;
import com.nathaniel.motus.umlclasseditor.databinding.FragmentMethodEditorBinding;
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
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MethodEditorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MethodEditorFragment extends EditorFragment implements View.OnClickListener,
        RadioGroup.OnCheckedChangeListener,
        AdapterView.OnItemLongClickListener,
        ExpandableListView.OnChildClickListener{
    private FragmentMethodEditorBinding binding;
    private static final String METHOD_ORDER_KEY ="methodOrder";
    private static final String CLASS_ORDER_KEY ="classOrder";
    private static final String CLASS_EDITOR_FRAGMENT_TAG_KEY="classEditorFragmentTag";
    private int mMethodOrder;
    private int mClassOrder;
    private UmlClassMethod mUmlClassMethod;
    private UmlClass mUmlClass;
    private String mClassEditorFragmentTag;
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
//    Fragment events
//    **********************************************************************************************

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMethodEditorBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

//    **********************************************************************************************
//    Configuration methods
//    **********************************************************************************************

    @Override
    protected void readBundle() {
        mClassEditorFragmentTag =getArguments().getString(CLASS_EDITOR_FRAGMENT_TAG_KEY);
        mMethodOrder = getArguments().getInt(METHOD_ORDER_KEY);
        mClassOrder =getArguments().getInt(CLASS_ORDER_KEY);
    }

    @Override
    protected void setOnCreateOrEditDisplay() {
        if (mMethodOrder!=-1)
            setOnEditDisplay();
        else
            setOnCreateDisplay();
    }

    protected void configureViews() {
        // delete btn
        binding.deleteMethodButton.setOnClickListener(this);
        binding.deleteMethodButton.setTag(DELETE_METHOD_BUTTON_TAG);

        // method type single =void, collection = list, array = array
        binding.methodMultiplicityRadioGroup.setOnCheckedChangeListener(this);

        // parameters expanded list view
        binding.methodParametersList.setOnChildClickListener(this);
        binding.methodParametersList.setOnItemLongClickListener(this);

        // cancel btn
        binding.methodCancelButton.setOnClickListener(this);
        binding.methodCancelButton.setTag(CANCEL_BUTTON_TAG);

        // ok btn
        binding.methodOkButton.setOnClickListener(this);
        binding.methodOkButton.setTag(OK_BUTTON_TAG);
    }

    protected void initializeMembers() {
        mUmlClass=mCallback.getProject().findClassByOrder(mClassOrder);

        if (mMethodOrder != -1) {
            mUmlClassMethod = mUmlClass.findMethodByOrder(mMethodOrder);
        } else {
            mUmlClassMethod=new UmlClassMethod(mUmlClass.getUmlClassMethodCount());
            mUmlClass.addMethod(mUmlClassMethod);
        }
    }

    protected void initializeFields() {
        if (mMethodOrder != -1) {
            binding.methodNameInput.setText(mUmlClassMethod.getName());

            switch (mUmlClassMethod.getVisibility()) {
                case PUBLIC:
                    binding.methodPublicRadio.setChecked(true);
                    break;
                case PROTECTED:
                    binding.methodProtectedRadio.setChecked(true);
                    break;
                default:
                    binding.methodPrivateRadio.setChecked(true);
                    break;
            }

            binding.methodStaticCheck.setChecked(mUmlClassMethod.isStatic());

            switch (mUmlClassMethod.getTypeMultiplicity()) {
                case SINGLE:
                    binding.methodSimpleRadio.setChecked(true);
                    break;
                case COLLECTION:
                    binding.methodCollectionRadio.setChecked(true);
                    break;
                default:
                    binding.methodArrayRadio.setChecked(true);
                    break;
            }

            binding.methodDimensionInput.setText(Integer.toString(mUmlClassMethod.getArrayDimension()));
            if (mUmlClassMethod.getTypeMultiplicity() == TypeMultiplicity.ARRAY)
                setOnArrayDisplay();
            else setOnSingleDisplay();
        } else {
            binding.methodNameInput.setText("");
            binding.methodPublicRadio.setChecked(true);
            binding.methodStaticCheck.setChecked(false);
            binding.methodSimpleRadio.setChecked(true);
            binding.methodDimensionInput.setText("");
            setOnSingleDisplay();
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
        binding.methodTypeSpinner.setAdapter(adapter);
        if (mMethodOrder !=-1)
            binding.methodTypeSpinner.setSelection(spinnerArray.indexOf(mUmlClassMethod.getUmlType().getName()));
        else binding.methodTypeSpinner.setSelection(spinnerArray.indexOf("void"));
    }

    private void populateParameterListView() {
        boolean parameterGroupIsExpanded=false;
        ExpandableListView paramList = binding.methodParametersList;
        if (paramList.getExpandableListAdapter()!=null && paramList.isGroupExpanded(0))
            parameterGroupIsExpanded=true;

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
        paramList.setAdapter(adapter);
        if (parameterGroupIsExpanded)
            paramList.expandGroup(0);
    }

    private void setOnEditDisplay() {
        binding.editMethodText.setText("Edit Method");
        binding.deleteMethodButton.setVisibility(View.VISIBLE);
    }

    private void setOnCreateDisplay() {
        binding.editMethodText.setText("Create method");
        binding.deleteMethodButton.setVisibility(View.INVISIBLE);
    }

    private void setOnArrayDisplay() {
        binding.methodDimensionText.setVisibility(View.VISIBLE);
        binding.methodDimensionInput.setVisibility(View.VISIBLE);
    }

    private void setOnSingleDisplay() {
        binding.methodDimensionText.setVisibility(View.INVISIBLE);
        binding.methodDimensionInput.setVisibility(View.INVISIBLE);
    }

    public void updateMethodEditorFragment(int methodOrder,int classOrder) {
        mMethodOrder=methodOrder;
        mClassOrder=classOrder;
        initializeMembers();
        initializeFields();
        if (mMethodOrder !=-1) setOnEditDisplay();
        else setOnCreateDisplay();
        if (mMethodOrder !=-1 && mUmlClassMethod.getTypeMultiplicity()== TypeMultiplicity.ARRAY) setOnArrayDisplay();
        else setOnSingleDisplay();
        setOnBackPressedCallback();
    }

    @Override
    protected void closeFragment() {
        mCallback.closeMethodEditorFragment(this);
    }

//    **********************************************************************************************
//    UI events
//    **********************************************************************************************

    @Override
    public void onClick(View v) {
        int tag=(int)v.getTag();

        switch (tag) {
            case CANCEL_BUTTON_TAG:
                onCancelButtonCLicked();
                break;
            case OK_BUTTON_TAG:
                onOKButtonClicked();
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

//    **********************************************************************************************
//    Edition methods
//    **********************************************************************************************

    @Override
    protected void clearDraftObject() {
        if (mMethodOrder==-1)
            mUmlClass.removeMethod(mUmlClassMethod);
    }

    @Override
    protected boolean createOrUpdateObject() {
        return createOrUpdateMethod();
    }

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
        return Objects.requireNonNull(binding.methodNameInput.getText()).toString();
    }

    private Visibility getMethodVisibility() {
        if (binding.methodPublicRadio.isChecked()) return Visibility.PUBLIC;
        if (binding.methodPrivateRadio.isChecked()) return Visibility.PROTECTED;
        return Visibility.PRIVATE;
    }

    private boolean isStatic() {
        return binding.methodStaticCheck.isChecked();
    }

    private UmlType getMethodType() {
        return UmlType.valueOf(binding.methodTypeSpinner.getSelectedItem().toString(),UmlType.getUmlTypes());
    }

    private TypeMultiplicity getMethodMultiplicity() {
        if (binding.methodSimpleRadio.isChecked()) return TypeMultiplicity.SINGLE;
        if (binding.methodCollectionRadio.isChecked()) return TypeMultiplicity.COLLECTION;
        return TypeMultiplicity.ARRAY;
    }

    private int getArrayDimension() {
        if (binding.methodDimensionInput.getText().toString().equals("")) return 0;
        return Integer.parseInt(binding.methodDimensionInput.getText().toString());
    }

    public void updateLists() {
        populateParameterListView();
    }

//    **********************************************************************************************
//    Alert dialogs
//    **********************************************************************************************
    private void startDeleteMethodDialog() {
        final Fragment fragment=this;
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete method ?")
                .setMessage("Are you sure you want to delete this method ?")
                .setNegativeButton("NO", (d, which) -> d.dismiss())
                .setPositiveButton("YES", (d, which) -> {
                    mUmlClass.removeMethod(mUmlClassMethod);
                    mCallback.closeMethodEditorFragment(fragment);
                }).show();
    }

    private void startDeleteParameterDialog(final int parameterIndex) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete parameter ?")
                .setMessage("Are you sure you want delete this parameter ?")
                .setNegativeButton("NO", (d, i) -> d.dismiss())
                .setPositiveButton("YES", (d, i) -> {
                    mUmlClassMethod.removeParameter(mUmlClassMethod.findParameterByOrder(parameterIndex));
                    updateLists();
                })
                .show();
    }
}