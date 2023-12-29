package com.nathaniel.motus.umlclasseditor.controller;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.nathaniel.motus.umlclasseditor.model.MethodParameter;
import com.nathaniel.motus.umlclasseditor.model.UmlClass;
import com.nathaniel.motus.umlclasseditor.model.UmlClassAttribute;
import com.nathaniel.motus.umlclasseditor.model.UmlClassMethod;
import com.nathaniel.motus.umlclasseditor.model.UmlEnumValue;
import com.nathaniel.motus.umlclasseditor.model.UmlProject;
import com.nathaniel.motus.umlclasseditor.view.GraphView;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class IOUtils {

    private IOUtils() {

    }

    public static void saveFileToInternalStorage(String data,File file) {
        try {
            FileWriter fileWriter=new FileWriter(file);
            fileWriter.append(data);
            fileWriter.flush();
            fileWriter.close();
            } catch (IOException e) {
            Log.i("TEST","Saving failed");
        }
    }

    public static String getFileFromInternalStorage(File file) {
        String projectString="";
        if (file.exists()) {
            BufferedReader bufferedReader;
            try {
                bufferedReader = new BufferedReader(new FileReader(file));
                try {
                    String readString = bufferedReader.readLine();
                    while (readString != null) {
                        projectString = projectString + readString;
                        readString = bufferedReader.readLine();
                    }
                } finally {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                Log.i("TEST","Loading failed");
            }
        }
        return projectString;
    }

    public static void saveFileToExternalStorage(Context context, String data, Uri externalStorageUri) {
        try {
            OutputStream outputStream=context.getContentResolver().openOutputStream(externalStorageUri);
            outputStream.write(data.getBytes());
            outputStream.flush();
            outputStream.close();
            Log.i("TEST", "Project saved");
        } catch (IOException e) {
            Log.i("TEST","Failed saving project");
            Log.i("TEST",e.getMessage());
        }
    }

    public static String readFileFromExternalStorage(Context context, Uri externalStorageUri) {
        String data="";
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(externalStorageUri);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            data = bufferedReader.readLine();
            Log.i("TEST","Project loaded");
        } catch (IOException e) {
            Log.i("TEST","Failed loading project");
        }

        return data;
    }

    public static ArrayList<String> sortedFiles(File file) {
        File[] files=file.listFiles();
        ArrayList<String> fileList=new ArrayList<>();

        for (File f:files) fileList.add(f.getName());

        Collections.sort(fileList);
        return fileList;
    }

    public static String readRawHtmlFile(Context context,int rawId) {
        InputStream inputStream=context.getResources().openRawResource(rawId);
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        int i;
        try {
            i = inputStream.read();
            while (i != -1) {
                byteArrayOutputStream.write(i);
                i = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArrayOutputStream.toString();
    }
//    **********************************************************************************************
//      Export PDF
//    **********************************************************************************************

    public static void savePdfToExternalStorage(Context context, GraphView graphView, Uri externalStorageUri) {

        Bitmap main_bitmap = getBitmapFromView(graphView);
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(main_bitmap.getWidth(), main_bitmap.getHeight(), 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        canvas.drawPaint(paint);

        Bitmap secondary_bitmap = Bitmap.createScaledBitmap(main_bitmap, main_bitmap.getWidth(), main_bitmap.getHeight(), true);

        paint.setColor(Color.BLACK);
        canvas.drawBitmap(secondary_bitmap, 0, 0, null);

        document.finishPage(page);

        try{
            document.writeTo(context.getContentResolver().openOutputStream(externalStorageUri));
        } catch (IOException e) {
            Log.e("TEST", Objects.requireNonNull(e.getMessage()));
        } finally {
            document.close();
        }

    }

    public static Bitmap getBitmapFromView(GraphView view) {
        if (view == null) return null;
       view.adjustViewToProject();
        boolean drawingCacheEnabled = view.isDrawingCacheEnabled();
        boolean willNotCacheDrawing = view.willNotCacheDrawing();
        view.setDrawingCacheEnabled(true);
        view.setWillNotCacheDrawing(false);
        Bitmap drawingCache = view.getDrawingCache();
        Bitmap bitmap;
        if (null == drawingCache) {
            view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
            view.buildDrawingCache();
            drawingCache = view.getDrawingCache();
            if (drawingCache != null) {
                bitmap = Bitmap.createBitmap(drawingCache);
            } else {
                bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                view.draw(canvas);
            }
        } else {
            bitmap = Bitmap.createBitmap(drawingCache);
        }
        view.destroyDrawingCache();
        view.setWillNotCacheDrawing(willNotCacheDrawing);
        view.setDrawingCacheEnabled(drawingCacheEnabled);
        return bitmap;

    }

//    **********************************************************************************************
//    Code Generation
//    **********************************************************************************************

    public static boolean export2Java(Context context,UmlProject project){
        List<Boolean> list = new ArrayList<>();
        if (project.getUmlClasses().size() == 0) {
            Toast.makeText(context, "The project has no existing classes, do make one to generate.", Toast.LENGTH_SHORT).show();
            return false;
        }
        for (UmlClass umlClass:project.getUmlClasses()){
            StringBuilder javaClass = new StringBuilder();
                switch (umlClass.getUmlClassType()){
                    case INTERFACE:
                        javaClass.append("public interface ").append(umlClass.getName()).append(" {\n");
                        javaClass.append(extractAttrib(umlClass.getAttributes()));
                        javaClass.append(extractMethods(umlClass.getMethods(),true));
                        javaClass.append("\n\n}");
                        break;
                    case ENUM:
                        javaClass.append("public enum ").append(umlClass.getName()).append(" {\n");
                        int i=1;
                        for (UmlEnumValue x:umlClass.getValues()){
                            javaClass.append(x.getName());
                            if(umlClass.getValues().size() >1 && i!=umlClass.getValues().size()) javaClass.append(",");
                            javaClass.append("\n");
                            i++;
                        }
                        javaClass.append("}");
                        break;
                    case ABSTRACT_CLASS:
                        javaClass.append("public abstract class ").append(umlClass.getName()).append(" {\n");
                        javaClass.append(extractAttrib(umlClass.getAttributes()));
                        javaClass.append(extractMethods(umlClass.getMethods(),false));
                        javaClass.append("\n\n}");
                        break;
                    default:
                        javaClass.append("public class ").append(umlClass.getName()).append(" {\n");
                        javaClass.append(extractAttrib(umlClass.getAttributes()));
                        javaClass.append(extractMethods(umlClass.getMethods(),false));
                        javaClass.append("\n\n}");
                        break;
                }
            list.add(saveFiles(context,javaClass.toString(),project.getName(),umlClass.getName()));
        }
        return !list.contains(false);
    }

    static StringBuilder extractAttrib(ArrayList<UmlClassAttribute> listOfAttrib){
        StringBuilder attrib = new StringBuilder();
        for (UmlClassAttribute x:listOfAttrib){
            attrib.append("\t").append(x.getVisibility().toString().toLowerCase(Locale.ROOT)).append(x.isStatic() ? " static" : "").append(x.isFinal() ? " final" : "").append(" ");

            switch (x.getTypeMultiplicity()){
                case ARRAY:
                    attrib.append(x.getUmlType().getName()).append("[").append(x.getArrayDimension()).append("]").append(" ").append(x.getName());
                    break;
                case COLLECTION:
                    attrib.append("List<").append(x.getUmlType().getName()).append(">").append(" ").append(x.getName());
                    break;
                default:
                    attrib.append(x.getUmlType().getName()).append(" ").append(x.getName());
                    break;
            }
            attrib.append(";\n");
        }

        return attrib;
    }

    static StringBuilder extractMethods(ArrayList<UmlClassMethod> methods,boolean isInterface){
        StringBuilder meths = new StringBuilder();
        for (UmlClassMethod x:methods){
            meths.append(x.getVisibility().toString().toLowerCase(Locale.ROOT)).append(x.isStatic()?" static":"").append(" ");
            switch (x.getTypeMultiplicity()){
                case COLLECTION:
                    meths.append("List<").append(x.getUmlType().getName()).append("> ").append(x.getName());
                    break;
                case ARRAY:
                    meths.append(x.getUmlType().getName()).append("[] ").append(x.getName());
                    break;
                default:
                    meths.append(x.getUmlType().getName()).append(" ").append(x.getName());
                    break;
            }
            if(!isInterface){
                if(x.getParameterCount()==0) meths.append("() {");
                else{
                    meths.append("(");
                    int i = 1;
                    for (MethodParameter y:x.getParameters()){
                        meths.append(y.getUmlType().getName()).append(" ").append(y.getName());
                        if(x.getParameters().size()>1 && i != x.getParameters().size()) meths.append(",");
                        i++;
                    }
                    meths.append(") {");
                }
                if(!x.getUmlType().getName().equalsIgnoreCase("void")){
                    String type = x.getUmlType().getName();
                    if(type.equalsIgnoreCase("int")||
                            type.equalsIgnoreCase("double")||
                            type.equalsIgnoreCase("float")||
                            type.equalsIgnoreCase("long")
                    )
                        meths.append("return 0;");
                    else if(type.equalsIgnoreCase("boolean")) meths.append("\nreturn false;");
                    else meths.append("\nreturn null;");

                }else{
                    meths.append("\n// please complete your code");
                }
                meths.append("\n");
                meths.append(" }\n");
            }else{
                meths.append("(");
                int i = 1;
                for (MethodParameter y:x.getParameters()){
                    meths.append(y.getUmlType().getName()).append(" ").append(y.getName());
                    if(x.getParameters().size()>1 && i != x.getParameters().size()) meths.append(",");
                    i++;
                }
                meths.append(");\n");

            }
        }

        return meths;
    }

    static boolean saveFiles(Context context, String data,String projectName, String name) {
        try {
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                File dir = Environment.getExternalStorageDirectory();

                File base = new File(dir, "UmlClassEditor");
                if (!base.exists()) {
                    if (!base.mkdirs()) {
                        Log.e("TEST", "Failed to create directory");
                        return false;
                    }
                }
                File folder = new File(base, projectName);
                if (!folder.exists()) {
                    if (!folder.mkdirs()) {
                        Log.e("TEST", "Failed to create directory");
                        return false;
                    }
                }

                File file = new File(folder, name + ".java");
                FileOutputStream outputStream = new FileOutputStream(file);
                outputStream.write(data.getBytes());
                outputStream.close();
                MediaScannerConnection.scanFile(context, new String[]{file.getAbsolutePath()}, null, null);

                Log.i("IOUtils", "File saved: " + file.getAbsolutePath());
                return true;
            } else {
                Log.e("IOUtils", "External storage not available for writing");
                return false;
            }
        } catch (IOException e) {
            Log.e("TEST", e.getMessage(), e);
            return false;
        }
    }


//    **********************************************************************************************
//    Side utilities
//    **********************************************************************************************

    public static int getAppVersionCode(Context context) {
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                return (int) info.getLongVersionCode();
            } else {
                return info.versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return -1;
        }
    }
}
