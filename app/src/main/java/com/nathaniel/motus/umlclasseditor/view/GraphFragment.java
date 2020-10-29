package com.nathaniel.motus.umlclasseditor.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.nathaniel.motus.umlclasseditor.R;
import com.nathaniel.motus.umlclasseditor.controller.FragmentObserver;

public class GraphFragment extends Fragment implements View.OnClickListener {

    private GraphView mGraphView;
    private TextView mGraphText;
    private Button mInheritanceButton;
    private Button mRealizationButton;
    private Button mAggregationButton;
    private Button mEscapeButton;
    private Button mAssociationButton;
    private Button mDependancyButton;
    private Button mCompositionButton;
    private Button mNewClassButton;

    public static final int GRAPHVIEW_TAG=110;
    public static final int NEW_CLASS_BUTTON_TAG=120;
    public static final int INHERITANCE_BUTTON_TAG=130;
    public static final int REALIZATION_BUTTON_TAG=140;
    public static final int AGGREGATION_BUTTON_TAG=150;
    public static final int ESCAPE_BUTTON_TAG=160;
    public static final int ASSOCIATION_BUTTON_TAG=170;
    public static final int DEPENDENCY_BUTTON_TAG =180;
    public static final int COMPOSITION_BUTTON_TAG=190;

    private FragmentObserver mCallBack;



//    **********************************************************************************************
//    Constructors
//    **********************************************************************************************
    public GraphFragment() {
        // Required empty public constructor
    }

    public static GraphFragment newInstance() {
        GraphFragment fragment = new GraphFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

//    **********************************************************************************************
//    Getters and setters
//    **********************************************************************************************

    public FragmentObserver getCallBack() {
        return mCallBack;
    }

//    **********************************************************************************************
//    Fragment events
//    **********************************************************************************************

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_graph, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        createCallbackToParentActivity();
        configureViews();
    }

//    **********************************************************************************************
//    Setup methods
//    **********************************************************************************************

    private void configureViews() {
        mGraphView=getActivity().findViewById(R.id.graphview);
        mGraphView.setTag(GRAPHVIEW_TAG);

        mGraphText=getActivity().findViewById(R.id.graph_text);

        mInheritanceButton=getActivity().findViewById(R.id.inheritance_button);
        mInheritanceButton.setTag(INHERITANCE_BUTTON_TAG);
        mInheritanceButton.setOnClickListener(this);

        mRealizationButton=getActivity().findViewById(R.id.realization_button);
        mRealizationButton.setTag(REALIZATION_BUTTON_TAG);
        mRealizationButton.setOnClickListener(this);

        mAggregationButton=getActivity().findViewById(R.id.aggregation_button);
        mAggregationButton.setTag(AGGREGATION_BUTTON_TAG);
        mAggregationButton.setOnClickListener(this);

        mEscapeButton=getActivity().findViewById(R.id.escape_button);
        mEscapeButton.setTag(ESCAPE_BUTTON_TAG);
        mEscapeButton.setOnClickListener(this);

        mAssociationButton=getActivity().findViewById(R.id.association_button);
        mAssociationButton.setTag(ASSOCIATION_BUTTON_TAG);
        mAssociationButton.setOnClickListener(this);

        mDependancyButton=getActivity().findViewById(R.id.dependancy_button);
        mDependancyButton.setTag(DEPENDENCY_BUTTON_TAG);
        mDependancyButton.setOnClickListener(this);

        mCompositionButton=getActivity().findViewById(R.id.composition_button);
        mCompositionButton.setTag(COMPOSITION_BUTTON_TAG);
        mCompositionButton.setOnClickListener(this);

        mNewClassButton=getActivity().findViewById(R.id.new_class_button);
        mNewClassButton.setTag(NEW_CLASS_BUTTON_TAG);
        mNewClassButton.setOnClickListener(this);
    }

    private void createCallbackToParentActivity() {
        mCallBack=(FragmentObserver)getActivity();
    }

//    **********************************************************************************************
//    Modifiers
//    **********************************************************************************************

    public void setPrompt(String prompt) {
        mGraphText.setText(prompt);
    }

    public void clearPrompt() {
        mGraphText.setText("");
    }

//    **********************************************************************************************
//    Listener methods
//    **********************************************************************************************

    @Override
    public void onClick(View v) {

        int tag=(int)v.getTag();

        switch (tag) {

            case NEW_CLASS_BUTTON_TAG:
                mCallBack.setExpectingTouchLocation(true);
                this.setPrompt("Locate the new class");
                break;

            default:
                break;

        }
    }

}