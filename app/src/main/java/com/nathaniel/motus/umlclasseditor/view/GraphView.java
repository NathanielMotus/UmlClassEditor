package com.nathaniel.motus.umlclasseditor.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.nathaniel.motus.umlclasseditor.R;
import com.nathaniel.motus.umlclasseditor.model.UmlClass;
import com.nathaniel.motus.umlclasseditor.model.UmlClassAttribute;
import com.nathaniel.motus.umlclasseditor.model.UmlClassMethod;
import com.nathaniel.motus.umlclasseditor.model.UmlProject;

public class GraphView extends View implements View.OnTouchListener{

    enum TouchMode{DRAG,ZOOM}

    private float mZoom;
    private float mXOffset;
    private float mYOffset;
    private UmlProject mUmlProject;
    private float mLastTouchX;
    private float mLastTouchY;
    private float mOldDist;
    private float mNewDist;
    private TouchMode mTouchMode=TouchMode.DRAG;
    private int mPrimaryPointerIndex;
    private float mXMidPoint;
    private float mYMidpoint;
    private float mOldXMidPoint;
    private float mOldYMidPoint;
    private Paint textPaint;
    private Paint framePaint;

//    **********************************************************************************************
//    Standard drawing dimensions (in dp)
//    **********************************************************************************************

    private static final float FONT_SIZE=20;
    private static final float INTERLINE=10;


//    **********************************************************************************************
//    Constructors
//    **********************************************************************************************

    public GraphView(Context context) {
        super(context);
        init(-1,-1,-1);
    }

    public GraphView(Context context, float zoom, int xOffset, int yOffset) {
        super(context);
        init(zoom,xOffset,yOffset);
    }

    public GraphView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public GraphView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(float zoom, int xOffset, int yOffset) {
        if (zoom!=-1)
            mZoom=zoom;
        else
            mZoom=1;

        if (xOffset!=-1)
            mXOffset=xOffset;
        else
            mXOffset=0;

        if (yOffset!=-1)
            mYOffset=yOffset;
        else
            mYOffset=0;

        textPaint=new Paint();
        textPaint.setColor(Color.DKGRAY);

        framePaint=new Paint();
        framePaint.setColor(Color.DKGRAY);
        framePaint.setStyle(Paint.Style.STROKE);

        setOnTouchListener(this);
    }

    private void init(AttributeSet attrs) {
        TypedArray attr=getContext().obtainStyledAttributes(attrs, R.styleable.GraphView);
        float zoom=attr.getFloat(R.styleable.GraphView_zoom,-1);
        int xOffset=attr.getInt(R.styleable.GraphView_xOffset,-1);
        int yOffset=attr.getInt(R.styleable.GraphView_yOffset,-1);

        init(zoom,xOffset,yOffset);
    }

//    **********************************************************************************************
//    Getters and setter
//    **********************************************************************************************

    public void setUmlProject(UmlProject umlProject) {
        mUmlProject = umlProject;
        this.invalidate();
    }


//    **********************************************************************************************
//    Overrode methods
//    **********************************************************************************************

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        displaySomeText(canvas);
        for (UmlClass c:mUmlProject.getUmlClasses())
            drawUmlClass(canvas,c);
    }

//    **********************************************************************************************
//    Drawing methods
//    **********************************************************************************************

    public void drawUmlClass(Canvas canvas,UmlClass umlClass) {

        //Update class dimensions
        updateUmlClassDimensions(umlClass);
        updateUmlClassNormalDimensions(umlClass);

        //Draw frame
        drawFrame(canvas,umlClass);

        //Draw class name
        drawUmlClassName(canvas,umlClass);

        //Draw attributes
        drawUmlClassAttributes(canvas,umlClass);

        //Draw methods
        drawUmlClassMethods(canvas,umlClass);

    }

    private void drawFrame(Canvas canvas,UmlClass umlClass) {
        //draw class frame

        //outer rectangle
        canvas.drawRect(visibleX(umlClass.getUmlClassNormalXPos()),
                visibleY(umlClass.getUmlClassNormalYPos()),
                visibleX(umlClass.getUmlClassNormalXPos())+umlClass.getUmlClassWidth(),
                visibleY(umlClass.getUmlClassNormalYPos())+umlClass.getUmlClassHeight(),
                framePaint);

        //separation under title
        canvas.drawLine(visibleX(umlClass.getUmlClassNormalXPos()),
                visibleY(umlClass.getUmlClassNormalYPos())+(INTERLINE*2f+FONT_SIZE)*mZoom,
                visibleX(umlClass.getUmlClassNormalXPos())+umlClass.getUmlClassWidth(),
                visibleY(umlClass.getUmlClassNormalYPos())+(INTERLINE*2f+FONT_SIZE)*mZoom,
                framePaint);

        //separation under attributes
        canvas.drawLine(visibleX(umlClass.getUmlClassNormalXPos()),
                visibleY(umlClass.getUmlClassNormalYPos())+(INTERLINE*2f+(1f+umlClass.getAttributeList().size())*(INTERLINE+FONT_SIZE))*mZoom,
                visibleX(umlClass.getUmlClassNormalXPos())+umlClass.getUmlClassWidth(),
                visibleY(umlClass.getUmlClassNormalYPos())+(INTERLINE*2f+(1f+umlClass.getAttributeList().size())*(INTERLINE+FONT_SIZE))*mZoom,
                framePaint);
    }

    private void drawUmlClassName(Canvas canvas, UmlClass umlClass) {
        //draw class name in frame

        float titleX=visibleX(umlClass.getUmlClassNormalXPos())+INTERLINE*mZoom;
        textPaint.setTextSize(FONT_SIZE*mZoom);
        if (getUmlClassMaxTextWidth(umlClass,textPaint)>textPaint.measureText(umlClass.getName()))
            titleX=titleX+(getUmlClassMaxTextWidth(umlClass,textPaint)-textPaint.measureText(umlClass.getName()))/2f;
        canvas.drawText(umlClass.getName(),
                titleX,
                visibleY(umlClass.getUmlClassNormalYPos())+(INTERLINE+FONT_SIZE)*mZoom,
                textPaint);
    }

    private void drawUmlClassAttributes(Canvas canvas, UmlClass umlClass) {
        //draw class attributes in frame

        float currentY=visibleY(umlClass.getUmlClassNormalYPos())+(FONT_SIZE*2f+3f*INTERLINE)*mZoom;
        textPaint.setTextSize(FONT_SIZE*mZoom);
        for (UmlClassAttribute a : umlClass.getAttributeList()) {
            canvas.drawText(a.getName(),
                    visibleX(umlClass.getUmlClassNormalXPos())+INTERLINE*mZoom,
                    currentY,
                    textPaint);
            currentY=currentY+(FONT_SIZE+INTERLINE)*mZoom;
        }
    }

    private void drawUmlClassMethods(Canvas canvas, UmlClass umlClass) {
        //draw class methods in frame

        float currentY=visibleY(umlClass.getUmlClassNormalYPos())+
                (INTERLINE*2f+(2f+umlClass.getAttributeList().size())*(INTERLINE+FONT_SIZE))*mZoom;
        textPaint.setTextSize(FONT_SIZE*mZoom);
        for (UmlClassMethod m : umlClass.getMethodList()) {
            canvas.drawText(m.getName(),
                    visibleX(umlClass.getUmlClassNormalXPos())+INTERLINE*mZoom,
                    currentY,
                    textPaint);
            currentY=currentY+(FONT_SIZE+INTERLINE)*mZoom;
        }
    }

//    **********************************************************************************************
//    Test methods
//    **********************************************************************************************

    private void displaySomeText(Canvas canvas) {
        float xPos=100;
        float yPos=100;
        Paint paint=new Paint();
        paint.setColor(Color.GRAY);
        paint.setTextSize(20*mZoom);
        canvas.drawText("Test string",visibleX(xPos),visibleY(yPos),paint);
        canvas.drawText("X",mXMidPoint,mYMidpoint,paint);
        canvas.drawText(mUmlProject.getName(),200,200,paint);

    }

//    **********************************************************************************************
//    UI events
//    **********************************************************************************************

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        int action=event.getActionMasked();

        switch (action) {
            case (MotionEvent.ACTION_DOWN):
                mLastTouchX=event.getX();
                mLastTouchY=event.getY();
                mTouchMode=TouchMode.DRAG;
                mPrimaryPointerIndex=0;
                break;

            case (MotionEvent.ACTION_POINTER_DOWN):
                mOldDist=spacing(event);
                calculateMidPoint(event);
                mOldXMidPoint=mXMidPoint;
                mOldYMidPoint=mYMidpoint;
                if (mOldDist>10f)
                    mTouchMode=TouchMode.ZOOM;
                break;

            case (MotionEvent.ACTION_MOVE):
                if (mTouchMode==TouchMode.DRAG) {
                    mXOffset=mXOffset+event.getX()-mLastTouchX;
                    mYOffset=mYOffset+event.getY()-mLastTouchY;
                    mLastTouchX=event.getX();
                    mLastTouchY=event.getY();

                } else if (mTouchMode == TouchMode.ZOOM) {
                    mNewDist=spacing(event);
                    mZoom=mZoom*mNewDist/mOldDist;
                    calculateMidPoint(event);
                    mXOffset=mXMidPoint+(mXOffset-mOldXMidPoint)*mNewDist/mOldDist;
                    mYOffset=mYMidpoint+(mYOffset-mOldYMidPoint)*mNewDist/mOldDist;
                    mOldDist=mNewDist;
                    mOldXMidPoint=mXMidPoint;
                    mOldYMidPoint=mYMidpoint;
                }
                break;

            case(MotionEvent.ACTION_POINTER_UP):
                mTouchMode=TouchMode.DRAG;

                if (event.getActionIndex() == mPrimaryPointerIndex) {
                    mPrimaryPointerIndex=(1+mPrimaryPointerIndex)%2;
                }
                mLastTouchX=event.getX(mPrimaryPointerIndex);
                mLastTouchY=event.getY(mPrimaryPointerIndex);
                break;

            default:
                return false;
        }
        invalidate();
        return true;

    }

//    **********************************************************************************************
//    Calculation methods
//    **********************************************************************************************

    private float spacing(MotionEvent event) {
        float dx=event.getX(0)-event.getX(1);
        float dy=event.getY(0)-event.getY(1);
        return (float) Math.sqrt(dx*dx+dy*dy);
    }

    private void calculateMidPoint(MotionEvent event) {
        mXMidPoint=(event.getX(0)+event.getX(1))/2;
        mYMidpoint=(event.getY(0)+event.getY(1))/2;
    }

    private float getUmlClassMaxTextWidth(UmlClass umlClass, Paint paint) {
        float currentWidth;
        float maxWidth;

        maxWidth=paint.measureText(umlClass.getName());

        for (UmlClassAttribute a : umlClass.getAttributeList()) {
            currentWidth=paint.measureText(a.getName());
            if (currentWidth>maxWidth)
                maxWidth=currentWidth;
        }

        for (UmlClassMethod m : umlClass.getMethodList()) {
            currentWidth=paint.measureText(m.getName());
            if (currentWidth>maxWidth)
                maxWidth=currentWidth;
        }
        return maxWidth;
    }

    private void updateUmlClassDimensions(UmlClass umlClass) {
        textPaint.setTextSize(FONT_SIZE*mZoom);
        umlClass.setUmlClassWidth(getUmlClassMaxTextWidth(umlClass,textPaint)+INTERLINE*2f*mZoom);
        umlClass.setUmlClassHeight((INTERLINE*3f+(FONT_SIZE+INTERLINE)*(1f+umlClass.getAttributeList().size()+umlClass.getMethodList().size()))*mZoom);
    }

    private void updateUmlClassNormalDimensions(UmlClass umlClass) {
        textPaint.setTextSize(FONT_SIZE);
        umlClass.setUmlClassNormalWidth(getUmlClassMaxTextWidth(umlClass,textPaint)+INTERLINE*2f);
        umlClass.setUmlClassNormalHeight(INTERLINE*3f+(FONT_SIZE+INTERLINE)*(1f+umlClass.getAttributeList().size()+umlClass.getMethodList().size()));
    }

//    **********************************************************************************************
//    Coordinates transformations
//    "visible" refers to the screen referential
//    "absolute" refers to the absolute referential, whose coordinates are class attributes
//    **********************************************************************************************

    private float visibleX(float absoluteX) {
        return mXOffset+mZoom*absoluteX;
    }

    private float visibleY(float absoluteY) {
        return mYOffset+mZoom*absoluteY;
    }

    private float absoluteX(float visibleX) {
        return (visibleX-mXOffset)/mZoom;
    }

    private float absoluteY(float visibleY) {
        return (visibleY-mYOffset)/mZoom;
    }
}
