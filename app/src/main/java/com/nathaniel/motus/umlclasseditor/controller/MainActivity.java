package com.nathaniel.motus.umlclasseditor.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.nathaniel.motus.umlclasseditor.model.TypeMultiplicity;
import com.nathaniel.motus.umlclasseditor.model.UmlClass;
import com.nathaniel.motus.umlclasseditor.model.UmlClassAttribute;
import com.nathaniel.motus.umlclasseditor.model.UmlClassMethod;
import com.nathaniel.motus.umlclasseditor.model.UmlProject;
import com.nathaniel.motus.umlclasseditor.model.UmlType;
import com.nathaniel.motus.umlclasseditor.model.Visibility;
import com.nathaniel.motus.umlclasseditor.view.AttributeEditorFragment;
import com.nathaniel.motus.umlclasseditor.view.ClassEditorFragment;
import com.nathaniel.motus.umlclasseditor.view.GraphFragment;
import com.nathaniel.motus.umlclasseditor.view.GraphView;
import com.nathaniel.motus.umlclasseditor.view.MethodEditorFragment;
import com.nathaniel.motus.umlclasseditor.view.ParameterEditorFragment;
import com.nathaniel.motus.umlclasseditor.R;

public class MainActivity extends AppCompatActivity implements GraphFragment.GraphFragmentNewClassButtonClickListener {

    private UmlProject mProject;

//    **********************************************************************************************
//    Fragments declaration
//    **********************************************************************************************
    private GraphFragment mGraphFragment;
    private ClassEditorFragment mClassEditorFragment;
    private AttributeEditorFragment mAttributeEditorFragment;
    private MethodEditorFragment mMethodEditorFragment;
    private ParameterEditorFragment mParameterEditorFragment;

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
                .replace(viewContainerId,mGraphFragment)
                .addToBackStack("GRAPH_FRAGMENT")
                .commit();
    }

    private void configureAndDisplayClassEditorFragment(int viewContainerId) {
        //handle class editor fragment

        mClassEditorFragment=new ClassEditorFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(viewContainerId,mClassEditorFragment)
                .addToBackStack("CLASS_EDITOR_FRAGMENT")
                .commit();
    }

//    **********************************************************************************************
//    Callback methods
//    **********************************************************************************************

    @Override
    public void onNewClassButtonClicked() {
        configureAndDisplayClassEditorFragment(R.id.activity_main_frame);
    }

//    **********************************************************************************************
//    Test methods
//    **********************************************************************************************

    private void populateProject() {
        mProject.initializeStandardTypes(getApplicationContext());
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
        someJavaClass.addMethod(new UmlClassMethod("uneMéthodePublic",Visibility.PUBLIC,false,
                false,mProject.getUmlTypes().get(0),TypeMultiplicity.SINGLE,0));
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
                false,mProject.getUmlTypes().get(0),TypeMultiplicity.SINGLE,0));
        someInterface.addMethod(new UmlClassMethod("encoreUneMéthodePublic",Visibility.PUBLIC,false,
                false,mProject.getUmlTypes().get(0),TypeMultiplicity.SINGLE,0));
        //Coordinates
        someInterface.setUmlClassNormalXPos(300);
        someInterface.setUmlClassNormalYPos(300);
        mProject.addUmlClass(someInterface);


    }
}