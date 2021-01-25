package com.nathaniel.motus.umlclasseditor.controller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.nathaniel.motus.umlclasseditor.model.UmlClass;
import com.nathaniel.motus.umlclasseditor.model.UmlProject;
import com.nathaniel.motus.umlclasseditor.model.UmlRelation;
import com.nathaniel.motus.umlclasseditor.model.UmlType;
import com.nathaniel.motus.umlclasseditor.view.AttributeEditorFragment;
import com.nathaniel.motus.umlclasseditor.view.ClassEditorFragment;
import com.nathaniel.motus.umlclasseditor.view.GraphFragment;
import com.nathaniel.motus.umlclasseditor.view.GraphView;
import com.nathaniel.motus.umlclasseditor.view.MethodEditorFragment;
import com.nathaniel.motus.umlclasseditor.view.ParameterEditorFragment;
import com.nathaniel.motus.umlclasseditor.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class MainActivity extends AppCompatActivity implements FragmentObserver,
        GraphView.GraphViewObserver,
        NavigationView.OnNavigationItemSelectedListener{

    //todo : user manual
    //todo : permissions for lollypop+
    //todo : app icon

    private UmlProject mProject;
    private boolean mExpectingTouchLocation=false;
    private Purpose mPurpose= FragmentObserver.Purpose.NONE;
    private float mXLocationFromGraphView;
    private float mYLocationFromGraphView;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private TextView mMenuHeaderProjectNameText;
    private String pFileName;

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

    private static final String SHARED_PREFERENCES_PROJECT_NAME="sharedPreferencesProjectName";

    private static final int INTENT_CREATE_DOCUMENT=1000;
    private static final int INTENT_OPEN_DOCUMENT=2000;

    public static final String JSON_PACKAGE_VERSION_CODE="PackageVersionCode";
    public static final String JSON_CUSTOM_TYPES="CustomTypes";

    public static final String CUSTOM_TYPES_FILENAME="custom_types";

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

        UmlType.initializePrimitiveUmlTypes(this);
        initializeCustomUmlTypes();
        getPreferences();
        configureToolbar();
        configureDrawerLayout();
        configureNavigationView();
        configureAndDisplayGraphFragment(R.id.activity_main_frame);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_toolbar_menu,menu);
        MenuCompat.setGroupDividerEnabled(menu,true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mGraphView=findViewById(R.id.graphview);
        mGraphView.setUmlProject(mProject);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mProject.save(getApplicationContext());
        savePreferences();
        saveCustomTypes();
    }

//    **********************************************************************************************
//    Configuration methods
//    **********************************************************************************************

    private void configureToolbar() {
        mToolbar=findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(mToolbar);
    }

    private void configureDrawerLayout() {
        mDrawerLayout=findViewById(R.id.activity_main_drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void configureNavigationView() {
        mNavigationView=findViewById(R.id.activity_main_navigation_view);
        mMenuHeaderProjectNameText= mNavigationView.getHeaderView(0).findViewById(R.id.activity_main_navigation_view_header_project_name_text);
        updateNavigationView();
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    private void updateNavigationView() {
        mMenuHeaderProjectNameText.setText(mProject.getName());
    }

    private void savePreferences() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString(SHARED_PREFERENCES_PROJECT_NAME,mProject.getName());
        editor.apply();
    }

    private void getPreferences() {
        SharedPreferences preferences=getPreferences(MODE_PRIVATE);
        String projectName=preferences.getString(SHARED_PREFERENCES_PROJECT_NAME,null);
        if (projectName != null) {
            mProject = UmlProject.load(getApplicationContext(), projectName);
        } else {
            mProject=new UmlProject("NewProject",getApplicationContext());
        }
    }

    private void saveCustomTypes() {
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put(JSON_CUSTOM_TYPES,UmlType.getCustomUmlTypesToJSONArray());
            jsonObject.put(JSON_PACKAGE_VERSION_CODE,IOUtils.getAppVersionCode(this));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        IOUtils.saveFileToInternalStorage(jsonObject.toString(),new File(getFilesDir(),CUSTOM_TYPES_FILENAME));
    }

    private void initializeCustomUmlTypes() {
        JSONArray jsonCustomTypes=new JSONArray();
        JSONObject jsonObject;
        try {
            jsonObject=new JSONObject(IOUtils.getFileFromInternalStorage(new File(getFilesDir(),CUSTOM_TYPES_FILENAME)));
            jsonCustomTypes=jsonObject.getJSONArray(JSON_CUSTOM_TYPES);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        UmlType.getCustomUmlTypesFromJSONArray(jsonCustomTypes);
    }

//    **********************************************************************************************
//    Fragment management
//    **********************************************************************************************
    private void configureAndDisplayGraphFragment(int viewContainerId){
        //handle graph fragment

        mGraphFragment=new GraphFragment();
        getSupportFragmentManager().beginTransaction()
                .add(viewContainerId,mGraphFragment,GRAPH_FRAGMENT_TAG)
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

    private void configureAndDisplayParameterEditorFragment(int viewContainerId, int parameterIndex) {
        mParameterEditorFragment=ParameterEditorFragment.newInstance(mMethodEditorFragment.getTag(),parameterIndex);
        getSupportFragmentManager().beginTransaction()
                .hide(mMethodEditorFragment)
                .add(viewContainerId,mParameterEditorFragment,PARAMETER_EDITOR_FRAGMENT_TAG)
                .addToBackStack(PARAMETER_EDITOR_FRAGMENT_TAG)
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
        closeFragment(fragment);
        mMethodEditorFragment.updateLists();
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
        configureAndDisplayParameterEditorFragment(R.id.activity_main_frame,parameterIndex);
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
        configureAndDisplayClassEditorFragment(R.id.activity_main_frame,xLocation,yLocation,-1);
    }

    @Override
    public void editClass(UmlClass umlClass) {
        configureAndDisplayClassEditorFragment(R.id.activity_main_frame,0,0,mProject.getUmlClasses().indexOf(umlClass));
    }

    @Override
    public void createRelation(UmlClass startClass, UmlClass endClass, UmlRelation.UmlRelationType relationType) {
        if (!mProject.relationAlreadyExistsBetween(startClass,endClass))
            mProject.addUmlRelation(new UmlRelation(startClass,endClass,relationType));
    }

//    **********************************************************************************************
//    Navigation view events
//    **********************************************************************************************

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int menuId=item.getItemId();
        if (menuId == R.id.drawer_menu_new_project) {
            drawerMenuNewProject();
        } else if (menuId == R.id.drawer_menu_load_project) {
            drawerMenuLoadProject();
        } else if (menuId == R.id.drawer_menu_save_as) {
            drawerMenuSaveAs();
        } else if (menuId == R.id.drawer_menu_merge_project) {
            drawerMenuMerge();
        } else if (menuId == R.id.drawer_menu_delete_project) {
            drawerMenuDeleteProject();
        }
        this.mDrawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

//    **********************************************************************************************
//    Navigation view called methods
//    **********************************************************************************************

    private void drawerMenuSaveAs() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        final EditText editText=new EditText(this);
        editText.setText(mProject.getName());
        builder.setTitle("Save as")
                .setMessage("Enter new name :")
                .setView(editText)
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        saveAs(editText.getText().toString());
                    }
                })
                .create()
                .show();
    }

    private void drawerMenuNewProject() {
        mProject.save(this);
        UmlType.clearProjectUmlTypes();
        mProject=new UmlProject("NewProject",this);
        mGraphView.setUmlProject(mProject);
        updateNavigationView();
    }

    private void drawerMenuLoadProject() {
        mProject.save(this);

        final Spinner spinner=new Spinner(this);
        spinner.setAdapter(projectDirectoryAdapter());

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Load project")
                .setMessage("Choose project to load :")
                .setView(spinner)
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String fileName=spinner.getSelectedItem().toString();
                        if (fileName!=null) {
                            UmlType.clearProjectUmlTypes();
                            mProject = UmlProject.load(getApplicationContext(), fileName);
                            mGraphView.setUmlProject(mProject);
                            updateNavigationView();
                        }
                    }
                })
                .create()
                .show();
    }

    private void drawerMenuDeleteProject() {

        final Context context=this;

        final Spinner spinner=new Spinner(this);
        spinner.setAdapter(projectDirectoryAdapter());

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Delete project")
                .setMessage("Choose project to delete :")
                .setView(spinner)
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String fileName=spinner.getSelectedItem().toString();
                        if (fileName!=null) {
                            File pathName=new File(getFilesDir(),UmlProject.PROJECT_DIRECTORY);
                            final File file=new File(pathName,fileName);
                            AlertDialog.Builder alert=new AlertDialog.Builder(context);
                            alert.setTitle("Delete Project")
                                    .setMessage("Are you sure you want to delete "+fileName+" ?")
                                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    })
                                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            file.delete();
                                        }
                                    })
                                    .create()
                                    .show();
                        }
                    }
                })
                .create()
                .show();
    }

    private void drawerMenuMerge() {
        final Spinner spinner=new Spinner(this);
        spinner.setAdapter(projectDirectoryAdapter());
        final Context currentContext=this;

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Merge project")
                .setMessage("Choose project to merge")
                .setView(spinner)
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String fileName=spinner.getSelectedItem().toString();
                        if (fileName!=null) {
                            UmlProject project = UmlProject.load(getApplicationContext(), fileName);
                            mProject.mergeWith(project);
                            mGraphView.invalidate();
                        }

                    }
                })
                .create()
                .show();

    }

    private ArrayAdapter<String> projectDirectoryAdapter() {
        //Create an array adapter to set a spinner with all project file names
        ArrayAdapter<String> adapter=new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,IOUtils.sortedFiles(new File(getFilesDir(),UmlProject.PROJECT_DIRECTORY)));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

//    **********************************************************************************************
//    Option menu events
//    **********************************************************************************************

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.toolbar_menu_export) {
            menuItemExport();
        } else if (itemId == R.id.toolbar_menu_import) {
            menuItemImport();
        } else if (itemId == R.id.toolbar_menu_create_custom_type) {
            menuCreateCustomType();
        } else if (itemId == R.id.toolbar_menu_delete_custom_type) {
            menuDeleteCustomType();
        } else if (itemId == R.id.toolbar_menu_export_custom_types) {
            menuExportCustomTypes();
        } else if (itemId == R.id.toolbar_menu_import_custom_types) {
            menuImportCustomTypes();
        }
        return true;
    }

//    **********************************************************************************************
//    Menu item called methods
//    **********************************************************************************************

    private void menuItemExport() {
        Intent intent=new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.setType("text/*");
        startActivityForResult(intent,INTENT_CREATE_DOCUMENT);
    }

    private void menuItemImport() {
        Intent intent=new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("text/*");
        startActivityForResult(intent,INTENT_OPEN_DOCUMENT);
    }

    private void menuCreateCustomType() {
        final EditText editText=new EditText(this);
        final Context context=getApplicationContext();
        AlertDialog.Builder adb=new AlertDialog.Builder(this);
        adb.setTitle("Create custom type")
                .setMessage("Enter custom type name :")
                .setView(editText)
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String typeName=editText.getText().toString();
                        if (typeName.equals(""))
                            Toast.makeText(context,"Failed : name cannot be blank",Toast.LENGTH_SHORT).show();
                        else if (UmlType.containsUmlTypeNamed(typeName))
                            Toast.makeText(context,"Failed : this name is already used",Toast.LENGTH_SHORT).show();
                        else{
                            UmlType.createUmlType(typeName, UmlType.TypeLevel.CUSTOM);
                            Toast.makeText(context,"Custom type created",Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .create()
                .show();
    }

    private void menuDeleteCustomType() {
        //todo : alert dialog with custom types listed, checkbox to choose
    }

    private void menuExportCustomTypes() {
        //todo : alert dialog prompting for a file name, include app version
    }

    private void menuImportCustomTypes() {
        //todo : alert dialog prompting for a file name, check if is a custom types file
    }

//    **********************************************************************************************
//    Intents
//    **********************************************************************************************

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == INTENT_CREATE_DOCUMENT && resultCode == RESULT_OK) {
            Uri fileNameUri=data.getData();
            mProject.exportProject(this,fileNameUri);
        } else if (requestCode == INTENT_OPEN_DOCUMENT && resultCode == RESULT_OK) {
            Uri fileNameUri=data.getData();
            UmlType.clearProjectUmlTypes();
            mProject=UmlProject.importProject(this,fileNameUri);
            mGraphView.setUmlProject(mProject);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


//    **********************************************************************************************
//    Project management methods
//    **********************************************************************************************

    private void saveAs(String projectName) {
        mProject.setName(projectName);
        updateNavigationView();
        mProject.save(getApplicationContext());
    }

}