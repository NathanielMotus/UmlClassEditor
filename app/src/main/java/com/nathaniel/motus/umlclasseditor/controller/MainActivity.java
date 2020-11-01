package com.nathaniel.motus.umlclasseditor.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;

import com.nathaniel.motus.umlclasseditor.model.TypeMultiplicity;
import com.nathaniel.motus.umlclasseditor.model.UmlClass;
import com.nathaniel.motus.umlclasseditor.model.UmlClassAttribute;
import com.nathaniel.motus.umlclasseditor.model.UmlClassMethod;
import com.nathaniel.motus.umlclasseditor.model.UmlProject;
import com.nathaniel.motus.umlclasseditor.model.UmlRelation;
import com.nathaniel.motus.umlclasseditor.model.Visibility;
import com.nathaniel.motus.umlclasseditor.view.AttributeEditorFragment;
import com.nathaniel.motus.umlclasseditor.view.ClassEditorFragment;
import com.nathaniel.motus.umlclasseditor.view.GraphFragment;
import com.nathaniel.motus.umlclasseditor.view.GraphView;
import com.nathaniel.motus.umlclasseditor.view.MethodEditorFragment;
import com.nathaniel.motus.umlclasseditor.view.ParameterEditorFragment;
import com.nathaniel.motus.umlclasseditor.R;

public class MainActivity extends AppCompatActivity implements FragmentObserver,
        GraphView.GraphViewObserver {

    private UmlProject mProject;
    private boolean mExpectingTouchLocation=false;
    private Purpose mPurpose= FragmentObserver.Purpose.NONE;
    private float mXLocationFromGraphView;
    private float mYLocationFromGraphView;

//    **********************************************************************************************
//    Fragments declaration
//    **********************************************************************************************
    private GraphFragment mGraphFragment;
    private ClassEditorFragment mClassEditorFragment;
    private AttributeEditorFragment mAttributeEditorFragment;
    private MethodEditorFragment mMethodEditorFragment;
    private ParameterEditorFragment mParameterEditorFragment;

    private static final String GRAPH_FRAGMENT_TAG="graphFragment";
    private static final String CLASS_EDITOR_FRAGMENT_TAG="classEditorFragment";
    private static final String ATTRIBUTE_EDITOR_FRAGMENT_TAG="attributeEditorFragment";
    private static final String METHOD_EDITOR_FRAGMENT_TAG="methodEditorFragment";
    private static final String PARAMETER_EDITOR_FRAGMENT_TAG="parameterEditorFragment";

//    **********************************************************************************************
//    Views declaration
//    **********************************************************************************************
    private FrameLayout mMainActivityFrame;
    private GraphView mGraphView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Instantiate views
        mMainActivityFrame=findViewById(R.id.activity_main_frame);

        mProject=new UmlProject("testProjet",getApplicationContext());
        populateProject();
        configureAndDisplayGraphFragment(R.id.activity_main_frame);


    }

    @Override
    protected void onStart() {
        super.onStart();

        mGraphView=findViewById(R.id.graphview);
        mGraphView.setUmlProject(mProject);
    }

//    **********************************************************************************************
//    Fragment management
//    **********************************************************************************************
    private void configureAndDisplayGraphFragment(int viewContainerId){
        //handle graph fragment

        mGraphFragment=new GraphFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(viewContainerId,mGraphFragment,GRAPH_FRAGMENT_TAG)
                .addToBackStack(GRAPH_FRAGMENT_TAG)
                .commit();
    }

    private void configureAndDisplayClassEditorFragment(int viewContainerId,float xLocation,float yLocation,int classIndex) {
        //handle class editor fragment

        mClassEditorFragment=ClassEditorFragment.newInstance(xLocation,yLocation,classIndex);
        getSupportFragmentManager().beginTransaction()
                .hide(mGraphFragment)
                .add(viewContainerId,mClassEditorFragment,CLASS_EDITOR_FRAGMENT_TAG)
                .addToBackStack(CLASS_EDITOR_FRAGMENT_TAG)
                .commit();
    }

    private void configureAndDisplayAttributeEditorFragment(int viewContainerId,int attributeIndex) {

        mAttributeEditorFragment=AttributeEditorFragment.newInstance(mClassEditorFragment.getTag(),attributeIndex);
        getSupportFragmentManager().beginTransaction()
                .hide(mClassEditorFragment)
                .add(viewContainerId,mAttributeEditorFragment,ATTRIBUTE_EDITOR_FRAGMENT_TAG)
                .addToBackStack(ATTRIBUTE_EDITOR_FRAGMENT_TAG)
                .commit();
    }

    private void configureAndDisplayMethodEditorFragment(int viewContainerId, int methodIndex) {
        mMethodEditorFragment=MethodEditorFragment.newInstance(mClassEditorFragment.getTag(),methodIndex);
        getSupportFragmentManager().beginTransaction()
                .hide(mClassEditorFragment)
                .add(viewContainerId,mMethodEditorFragment,METHOD_EDITOR_FRAGMENT_TAG)
                .addToBackStack(METHOD_EDITOR_FRAGMENT_TAG)
                .commit();
    }

    private void closeFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .remove(fragment)
                .commit();
        getSupportFragmentManager().popBackStackImmediate();
    }

//    **********************************************************************************************
//    Getters and setters
//    **********************************************************************************************

    public void setProject(UmlProject project) {
        mProject = project;
    }


//    **********************************************************************************************
//    Callback methods
//    **********************************************************************************************

//    GraphFragmentObserver

    @Override
    public void setExpectingTouchLocation(boolean b) {
        mExpectingTouchLocation=b;
    }

    @Override
    public void setPurpose(Purpose purpose) {
        mPurpose=purpose;
    }

    @Override
    public void closeClassEditorFragment(Fragment fragment) {
        closeFragment(fragment);
        mGraphView.invalidate();
    }

    @Override
    public void closeAttributeEditorFragment(Fragment fragment) {
        closeFragment(fragment);
        mClassEditorFragment.updateLists();
    }

    @Override
    public void closeMethodEditorFragment(Fragment fragment) {
        closeFragment(fragment);
        mClassEditorFragment.updateLists();
    }

    @Override
    public void closeParameterEditorFragment(Fragment fragment) {

    }

    @Override
    public void closeValueEditorFragment(Fragment fragment) {
        closeFragment(fragment);
        mClassEditorFragment.updateLists();
    }

    @Override
    public void openAttributeEditorFragment(int attributeIndex) {
        configureAndDisplayAttributeEditorFragment(R.id.activity_main_frame,attributeIndex);
    }

    @Override
    public void openMethodEditorFragment(int methodIndex) {
        configureAndDisplayMethodEditorFragment(R.id.activity_main_frame,methodIndex);
    }

    @Override
    public void openParameterEditorFragment(int parameterIndex) {

    }

    @Override
    public UmlProject getProject() {
        return this.mProject;
    }

//    GraphViewObserver

    @Override
    public boolean isExpectingTouchLocation() {
        return mExpectingTouchLocation;
    }

    @Override
    public void createClass(float xLocation, float yLocation) {
        setExpectingTouchLocation(false);
        configureAndDisplayClassEditorFragment(R.id.activity_main_frame,xLocation,yLocation,-1);
    }

    @Override
    public void editClass(UmlClass umlClass) {
        configureAndDisplayClassEditorFragment(R.id.activity_main_frame,0,0,mProject.getUmlClasses().indexOf(umlClass));
    }



//    **********************************************************************************************
//    Test methods
//    **********************************************************************************************

    private void populateProject() {
        //JavaClass
        UmlClass someJavaClass=new UmlClass("UneClasseJava", UmlClass.UmlClassType.JAVA_CLASS);
        //Attributes
        someJavaClass.addAttribute(new UmlClassAttribute("unAttributPublic", Visibility.PUBLIC,
                false,false,mProject.getUmlTypes().get(1), TypeMultiplicity.SINGLE,0));
        someJavaClass.addAttribute(new UmlClassAttribute("unAttributPrivate",Visibility.PRIVATE,
                false,false,mProject.getUmlTypes().get(2),TypeMultiplicity.COLLECTION,0));
        someJavaClass.addAttribute(new UmlClassAttribute("unAttributStaticFinal",Visibility.PROTECTED,
                true,true,mProject.getUmlTypes().get(3),TypeMultiplicity.ARRAY,3));
        //Methods
        someJavaClass.addMethod(new UmlClassMethod("uneMéthodePublic",Visibility.PUBLIC,false,mProject.getUmlTypes().get(0),TypeMultiplicity.SINGLE,0));
        //Coordinates
        someJavaClass.setUmlClassNormalXPos(150);
        someJavaClass.setUmlClassNormalYPos(200);
        mProject.addUmlClass(someJavaClass);

        //Interface
        UmlClass someInterface=new UmlClass("UneInterface", UmlClass.UmlClassType.INTERFACE);
        //Attributes
        someInterface.addAttribute(new UmlClassAttribute("unAttributPublic", Visibility.PUBLIC,
                false,false,mProject.getUmlTypes().get(1), TypeMultiplicity.SINGLE,0));
        someInterface.addAttribute(new UmlClassAttribute("unAttributPrivate",Visibility.PRIVATE,
                false,false,mProject.getUmlTypes().get(2),TypeMultiplicity.COLLECTION,0));
        someInterface.addAttribute(new UmlClassAttribute("unAttributStaticFinal",Visibility.PROTECTED,
                true,true,mProject.getUmlTypes().get(3),TypeMultiplicity.ARRAY,3));
        //Methods
        someInterface.addMethod(new UmlClassMethod("uneMéthodePublic",Visibility.PUBLIC,false,
                mProject.getUmlTypes().get(0),TypeMultiplicity.SINGLE,0));
        someInterface.addMethod(new UmlClassMethod("encoreUneMéthodePublic",Visibility.PUBLIC,false,
                mProject.getUmlTypes().get(0),TypeMultiplicity.SINGLE,0));
        //Coordinates
        someInterface.setUmlClassNormalXPos(300);
        someInterface.setUmlClassNormalYPos(300);
        mProject.addUmlClass(someInterface);

        //Abstract class
        UmlClass someAbstractClass=new UmlClass("UneClasseAbstraite", UmlClass.UmlClassType.ABSTRACT_CLASS);
        someAbstractClass.setUmlClassNormalXPos(400);
        someAbstractClass.setUmlClassNormalYPos(400);
        mProject.addUmlClass(someAbstractClass);

        //Relation
        UmlRelation someRelation=new UmlRelation(someJavaClass,someInterface, UmlRelation.UmlRelationType.REALIZATION);
        mProject.addUmlRelation(someRelation);

        UmlRelation otherRelation=new UmlRelation(someAbstractClass,someInterface, UmlRelation.UmlRelationType.INHERITANCE);
        mProject.addUmlRelation(otherRelation);

        UmlRelation lastRelation=new UmlRelation(someJavaClass,someAbstractClass, UmlRelation.UmlRelationType.COMPOSITION);
        mProject.addUmlRelation(lastRelation);


    }
}