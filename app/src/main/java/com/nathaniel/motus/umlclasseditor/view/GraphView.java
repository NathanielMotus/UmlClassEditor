package com.nathaniel.motus.umlclasseditor.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.nathaniel.motus.umlclasseditor.R;
import com.nathaniel.motus.umlclasseditor.model.UmlClass;
import com.nathaniel.motus.umlclasseditor.model.UmlClassAttribute;
import com.nathaniel.motus.umlclasseditor.model.UmlClassMethod;
import com.nathaniel.motus.umlclasseditor.model.UmlProject;
import com.nathaniel.motus.umlclasseditor.model.UmlRelation;

public class GraphView extends View implements View.OnTouchListener{

    enum TouchMode{DRAG,ZOOM}

    private GraphFragment mGraphFragment;
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
    private Paint linePaint;
    private Paint dashPaint;
    private Paint solidBlackPaint;
    private Paint solidWhitePaint;
    private UmlClass mMovingClass;
    private GraphViewObserver mCallback;
    private long mActionDownEventTime;
    private long mFirstClickTime=0;
    private static final long CLICK_DELAY=200;
    private static final long DOUBLE_CLICK_DELAY=500;

//    **********************************************************************************************
//    Standard drawing dimensions (in dp)
//    **********************************************************************************************

    private static final float FONT_SIZE=20;
    private static final float INTERLINE=10;
    private static final float ARROW_SIZE=10;


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

        linePaint =new Paint();
        linePaint.setColor(Color.DKGRAY);
        linePaint.setStyle(Paint.Style.STROKE);

        dashPaint=new Paint();
        dashPaint.setColor(Color.DKGRAY);
        dashPaint.setStyle(Paint.Style.STROKE);
        dashPaint.setPathEffect(new DashPathEffect(new float[]{10f,10f},0));

        solidBlackPaint=new Paint();
        solidBlackPaint.setColor(Color.DKGRAY);
        solidBlackPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        solidWhitePaint=new Paint();
        solidWhitePaint.setColor(Color.WHITE);
        solidWhitePaint.setStyle(Paint.Style.FILL);

        setOnTouchListener(this);
        createCallbackToParentActivity();
    }

    private void init(AttributeSet attrs) {
        TypedArray attr=getContext().obtainStyledAttributes(attrs, R.styleable.GraphView);
        float zoom=attr.getFloat(R.styleable.GraphView_zoom,-1);
        int xOffset=attr.getInt(R.styleable.GraphView_xOffset,-1);
        int yOffset=attr.getInt(R.styleable.GraphView_yOffset,-1);

        init(zoom,xOffset,yOffset);
    }

//    **********************************************************************************************
//    Callback interface
//    **********************************************************************************************

    public interface GraphViewObserver{
        public boolean isExpectingTouchLocation();
        public void createClass(float xLocation, float yLocation);
        public void editClass(UmlClass umlClass);
        public void createRelation(UmlClass startClass, UmlClass endClass, UmlRelation.UmlRelationType relationType);
    }

//    **********************************************************************************************
//    Getters and setter
//    **********************************************************************************************

    public void setUmlProject(UmlProject umlProject) {
        mUmlProject = umlProject;
        mZoom=mUmlProject.getZoom();
        mXOffset=mUmlProject.getXOffset();
        mYOffset=mUmlProject.getYOffset();
        this.invalidate();
    }

    public void setGraphFragment(GraphFragment graphFragment) {
        mGraphFragment = graphFragment;
    }

    public void setZoom(float zoom) {
        mZoom = zoom;
    }

    public void setXOffset(float XOffset) {
        mXOffset = XOffset;
    }

    public void setYOffset(float YOffset) {
        mYOffset = YOffset;
    }

//    **********************************************************************************************
//    Overridden methods
//    **********************************************************************************************

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        updateProjectGeometricalParameters();

        for (UmlClass c:mUmlProject.getUmlClasses())
            drawUmlClass(canvas,c);

        for (UmlRelation r:mUmlProject.getUmlRelations())
            drawRelation(canvas,r);
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
                linePaint);

        //separation under title
        canvas.drawLine(visibleX(umlClass.getUmlClassNormalXPos()),
                visibleY(umlClass.getUmlClassNormalYPos())+(INTERLINE*2f+FONT_SIZE)*mZoom,
                visibleX(umlClass.getUmlClassNormalXPos())+umlClass.getUmlClassWidth(),
                visibleY(umlClass.getUmlClassNormalYPos())+(INTERLINE*2f+FONT_SIZE)*mZoom,
                linePaint);

        //separation under attributes
        canvas.drawLine(visibleX(umlClass.getUmlClassNormalXPos()),
                visibleY(umlClass.getUmlClassNormalYPos())+(INTERLINE*2f+(1f+umlClass.getAttributeList().size())*(INTERLINE+FONT_SIZE))*mZoom,
                visibleX(umlClass.getUmlClassNormalXPos())+umlClass.getUmlClassWidth(),
                visibleY(umlClass.getUmlClassNormalYPos())+(INTERLINE*2f+(1f+umlClass.getAttributeList().size())*(INTERLINE+FONT_SIZE))*mZoom,
                linePaint);
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

    private void drawRelation(Canvas canvas,UmlRelation umlRelation) {

        float originAbsoluteLeft=umlRelation.getRelationOriginClass().getUmlClassNormalXPos();
        float originAbsoluteRight=umlRelation.getRelationOriginClass().getNormalRightEnd();
        float originAbsoluteTop=umlRelation.getRelationOriginClass().getUmlClassNormalYPos();
        float originAbsoluteBottom=umlRelation.getRelationOriginClass().getNormalBottomEnd();
        float endAbsoluteLeft=umlRelation.getRelationEndClass().getUmlClassNormalXPos();
        float endAbsoluteRight=umlRelation.getRelationEndClass().getNormalRightEnd();
        float endAbsoluteTop=umlRelation.getRelationEndClass().getUmlClassNormalYPos();
        float endAbsoluteBottom=umlRelation.getRelationEndClass().getNormalBottomEnd();
        float absoluteXOrigin=0;
        float absoluteYOrigin=0;
        float absoluteXEnd=0;
        float absoluteYEnd=0;

        //End in South quarter of Origin
        if (umlRelation.getRelationEndClass().isSouthOf(umlRelation.getRelationOriginClass())) {
            float lowerXLimit= originAbsoluteLeft-endAbsoluteTop+originAbsoluteBottom-umlRelation.getRelationEndClass().getUmlClassNormalWidth();
            float upperXLimit=originAbsoluteRight+endAbsoluteTop-originAbsoluteBottom;

            absoluteXEnd=endAbsoluteRight-
                   umlRelation.getRelationEndClass().getUmlClassNormalWidth()/(upperXLimit-lowerXLimit)*
                           (endAbsoluteLeft-lowerXLimit);
            absoluteYEnd=endAbsoluteTop;

            absoluteXOrigin=originAbsoluteLeft+
                   umlRelation.getRelationOriginClass().getUmlClassNormalWidth()/(upperXLimit-lowerXLimit)*
                           (endAbsoluteLeft-lowerXLimit);

            absoluteYOrigin=originAbsoluteBottom;
        }

        //End in North quarter or Origin
        if (umlRelation.getRelationEndClass().isNorthOf(umlRelation.getRelationOriginClass())) {
            float lowerXLimit=originAbsoluteLeft-originAbsoluteTop+endAbsoluteBottom-umlRelation.getRelationEndClass().getUmlClassNormalWidth();
            float upperXLimit=originAbsoluteRight+originAbsoluteTop-endAbsoluteBottom;

            absoluteXEnd=endAbsoluteRight-
                    umlRelation.getRelationEndClass().getUmlClassNormalWidth()/(upperXLimit-lowerXLimit)*
                            (endAbsoluteLeft-lowerXLimit);

            absoluteYEnd=endAbsoluteBottom;

            absoluteXOrigin=originAbsoluteLeft+
                    umlRelation.getRelationOriginClass().getUmlClassNormalWidth()/(upperXLimit-lowerXLimit)*
                            (endAbsoluteLeft-lowerXLimit);

            absoluteYOrigin=originAbsoluteTop;
        }

        //End in West quarter of Origin
        if (umlRelation.getRelationEndClass().isWestOf(umlRelation.getRelationOriginClass())) {
            float lowerYLimit=originAbsoluteTop-originAbsoluteLeft+endAbsoluteRight-umlRelation.getRelationEndClass().getUmlClassNormalHeight();
            float upperYLimit=originAbsoluteBottom+originAbsoluteLeft-endAbsoluteRight;

            absoluteXEnd=endAbsoluteRight;

            absoluteYEnd=endAbsoluteBottom-
                    umlRelation.getRelationEndClass().getUmlClassNormalHeight()/(upperYLimit-lowerYLimit)*
                            (endAbsoluteTop-lowerYLimit);

            absoluteXOrigin=originAbsoluteLeft;

            absoluteYOrigin=originAbsoluteTop+
                    umlRelation.getRelationOriginClass().getUmlClassNormalHeight()/(upperYLimit-lowerYLimit)*
                            (endAbsoluteTop-lowerYLimit);
        }

        //End in East quarter of Origin
        if (umlRelation.getRelationEndClass().isEastOf(umlRelation.getRelationOriginClass())) {
            float lowerYLimit=originAbsoluteTop-endAbsoluteLeft+originAbsoluteRight-umlRelation.getRelationEndClass().getUmlClassNormalHeight();
            float upperYLimit=originAbsoluteBottom+endAbsoluteLeft-originAbsoluteRight;

            absoluteXEnd=endAbsoluteLeft;

            absoluteYEnd=endAbsoluteBottom-
                    umlRelation.getRelationEndClass().getUmlClassNormalHeight()/(upperYLimit-lowerYLimit)*
                            (endAbsoluteTop-lowerYLimit);

            absoluteXOrigin=originAbsoluteRight;

            absoluteYOrigin=originAbsoluteTop+
                    umlRelation.getRelationOriginClass().getUmlClassNormalHeight()/(upperYLimit-lowerYLimit)*
                            (endAbsoluteTop-lowerYLimit);
        }
        //update relation coordinates
        umlRelation.setXOrigin(absoluteXOrigin);
        umlRelation.setYOrigin(absoluteYOrigin);
        umlRelation.setXEnd(absoluteXEnd);
        umlRelation.setYEnd(absoluteYEnd);

        Path path=new Path();
        path.moveTo(visibleX(absoluteXOrigin),visibleY(absoluteYOrigin));
        path.lineTo(visibleX(absoluteXEnd),visibleY(absoluteYEnd));

        switch (umlRelation.getUmlRelationType()) {
            case INHERITANCE:
                canvas.drawPath(path,linePaint);
                drawSolidWhiteArrow(canvas,
                        visibleX(absoluteXOrigin),
                        visibleY(absoluteYOrigin),
                        visibleX(absoluteXEnd),
                        visibleY(absoluteYEnd));
                break;

            case ASSOCIATION:
                canvas.drawPath(path,linePaint);
                break;

            case AGGREGATION:
                canvas.drawPath(path,linePaint);
                drawSolidWhiteRhombus(canvas,
                        visibleX(absoluteXOrigin),
                        visibleY(absoluteYOrigin),
                        visibleX(absoluteXEnd),
                        visibleY(absoluteYEnd));
                break;

            case COMPOSITION:
                canvas.drawPath(path,linePaint);
                drawSolidBlackRhombus(canvas,
                        visibleX(absoluteXOrigin),
                        visibleY(absoluteYOrigin),
                        visibleX(absoluteXEnd),
                        visibleY(absoluteYEnd));
                break;

            case DEPENDENCY:
                canvas.drawPath(path,dashPaint);
                drawArrow(canvas,
                        visibleX(absoluteXOrigin),
                        visibleY(absoluteYOrigin),
                        visibleX(absoluteXEnd),
                        visibleY(absoluteYEnd));
                break;

            case REALIZATION:
                canvas.drawPath(path,dashPaint);
                drawSolidWhiteArrow(canvas,
                        visibleX(absoluteXOrigin),
                        visibleY(absoluteYOrigin),
                        visibleX(absoluteXEnd),
                        visibleY(absoluteYEnd));
                break;

            default:
                break;
        }
    }

    private void drawArrow(Canvas canvas,float xOrigin, float yOrigin, float xEnd, float yEnd) {
        //draw an arrow at the end of the segment

        canvas.save();
        canvas.rotate(getAngle(xEnd,yEnd,xOrigin,yOrigin),xEnd,yEnd);
        Path path=new Path();
        path.moveTo(xEnd+ARROW_SIZE*mZoom,yEnd-ARROW_SIZE*1.414f/2f*mZoom);
        path.lineTo(xEnd,yEnd);
        path.lineTo(xEnd+ARROW_SIZE*mZoom,yEnd+ARROW_SIZE*1.414f/2f*mZoom);
        canvas.drawPath(path,linePaint);
        canvas.restore();
    }

    private void drawSolidWhiteArrow(Canvas canvas,float xOrigin, float yOrigin, float xEnd, float yEnd) {
        //draw a solid white arrow at the end of the segment

        canvas.save();
        canvas.rotate(getAngle(xEnd,yEnd,xOrigin,yOrigin),xEnd,yEnd);
        Path path=new Path();
        path.moveTo(xEnd,yEnd);
        path.lineTo(xEnd+ARROW_SIZE*mZoom,yEnd-ARROW_SIZE*1.414f/2f*mZoom);
        path.lineTo(xEnd+ARROW_SIZE*mZoom,yEnd+ARROW_SIZE*1.414f/2f*mZoom);
        path.close();
        canvas.drawPath(path,solidWhitePaint);
        canvas.drawPath(path,linePaint);
        canvas.restore();
    }

    private void drawSolidWhiteRhombus(Canvas canvas, float xOrigin, float yOrigin, float xEnd, float yEnd) {
        //draw a solid white rhombus at the end of the segment

        canvas.save();
        canvas.rotate(getAngle(xEnd,yEnd,xOrigin,yOrigin),xEnd,yEnd);
        Path path=new Path();
        path.moveTo(xEnd,yEnd);
        path.lineTo(xEnd+ARROW_SIZE*mZoom,yEnd-ARROW_SIZE*1.414f/2f*mZoom);
        path.lineTo(xEnd+ARROW_SIZE*2f*mZoom,yEnd);
        path.lineTo(xEnd+ARROW_SIZE*mZoom,yEnd+ARROW_SIZE*1.414f/2f*mZoom);
        path.close();
        canvas.drawPath(path,solidWhitePaint);
        canvas.drawPath(path,linePaint);
        canvas.restore();
    }

    private void drawSolidBlackRhombus(Canvas canvas, float xOrigin, float yOrigin, float xEnd, float yEnd) {
        //draw a solid black rhombus at the end of the segment

        canvas.save();
        canvas.rotate(getAngle(xEnd,yEnd,xOrigin,yOrigin),xEnd,yEnd);
        Path path=new Path();
        path.moveTo(xEnd,yEnd);
        path.lineTo(xEnd+ARROW_SIZE*mZoom,yEnd-ARROW_SIZE*1.414f/2f*mZoom);
        path.lineTo(xEnd+ARROW_SIZE*2f*mZoom,yEnd);
        path.lineTo(xEnd+ARROW_SIZE*mZoom,yEnd+ARROW_SIZE*1.414f/2f*mZoom);
        path.close();
        canvas.drawPath(path,solidBlackPaint);
        canvas.restore();
    }

//    **********************************************************************************************
//    UI events
//    **********************************************************************************************

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        int action=event.getActionMasked();

        switch (action) {
            case (MotionEvent.ACTION_DOWN):
                mActionDownEventTime=event.getEventTime();
                mLastTouchX=event.getX();
                mLastTouchY=event.getY();
                mMovingClass=getTouchedClass(mLastTouchX,mLastTouchY);
                mTouchMode=TouchMode.DRAG;
                mPrimaryPointerIndex=0;
                break;

            case (MotionEvent.ACTION_POINTER_DOWN):
                mOldDist=spacing(event);
                calculateMidPoint(event);
                mOldXMidPoint=mXMidPoint;
                mOldYMidPoint=mYMidpoint;
                if (mOldDist>10f) {
                    mTouchMode = TouchMode.ZOOM;
                    mMovingClass = null;
                }
                break;

            case (MotionEvent.ACTION_MOVE):
                if (mTouchMode==TouchMode.DRAG) {
                    if (mMovingClass == null) {
                        mXOffset = mXOffset + event.getX() - mLastTouchX;
                        mYOffset = mYOffset + event.getY() - mLastTouchY;
                    } else {
                        mMovingClass.setUmlClassNormalXPos(mMovingClass.getUmlClassNormalXPos()+(event.getX()-mLastTouchX)/mZoom);
                        mMovingClass.setUmlClassNormalYPos(mMovingClass.getUmlClassNormalYPos()+(event.getY()-mLastTouchY)/mZoom);
                    }
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

            case (MotionEvent.ACTION_UP):

                //double click
                if (event.getEventTime() - mActionDownEventTime <= CLICK_DELAY && event.getEventTime() - mFirstClickTime <= DOUBLE_CLICK_DELAY) {
                    if (getTouchedClass(mLastTouchX, mLastTouchY) != null) mCallback.editClass(getTouchedClass(mLastTouchX,mLastTouchY));
                    else if (getTouchedRelation(mLastTouchX,mLastTouchY)!=null) {
                        promptDeleteRelation(getTouchedRelation(mLastTouchX,mLastTouchY),this);
                    }
                    else adjustViewToProject();
                }

                //simple click
                if (event.getEventTime()-mActionDownEventTime<=CLICK_DELAY) {
                    mFirstClickTime = event.getEventTime();

                    //locate new classe
                    if (mGraphFragment.isExpectingTouchLocation()) {
                        mGraphFragment.setExpectingTouchLocation(false);
                        mGraphFragment.clearPrompt();
                        mCallback.createClass(absoluteX(mLastTouchX), absoluteY(mLastTouchY));

                        //touch relation end class
                    } else if (mGraphFragment.isExpectingEndClass()
                            && getTouchedClass(mLastTouchX,mLastTouchY)!=null
                            && getTouchedClass(mLastTouchX,mLastTouchY)!=mGraphFragment.getStartClass()) {
                        mGraphFragment.setEndClass(getTouchedClass(mLastTouchX,mLastTouchY));
                        mGraphFragment.setExpectingEndClass(false);
                        mGraphFragment.clearPrompt();
                        mCallback.createRelation(mGraphFragment.getStartClass(),mGraphFragment.getEndClass(),mGraphFragment.getUmlRelationType());

                        //touch relation origin class
                    } else if (mGraphFragment.isExpectingStartClass() && getTouchedClass(mLastTouchX, mLastTouchY) != null) {
                        mGraphFragment.setStartClass(getTouchedClass(mLastTouchX,mLastTouchY));
                        mGraphFragment.setExpectingStartClass(false);
                        mGraphFragment.setExpectingEndClass(true);
                        mGraphFragment.setPrompt("Choose end class");
                    }
                }
                break;

            default:
                return false;
        }
        invalidate();
        return true;

    }

//    **********************************************************************************************
//    Initialization methods
//    **********************************************************************************************

    private void createCallbackToParentActivity() {
        mCallback=(GraphViewObserver)this.getContext();
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
        //you must use the actual dimension normalized at zoom=1
        //because text width is not a linear function of zoom
        textPaint.setTextSize(FONT_SIZE*mZoom);
        umlClass.setUmlClassNormalWidth((getUmlClassMaxTextWidth(umlClass,textPaint)+INTERLINE*2f*mZoom)/mZoom);
        umlClass.setUmlClassNormalHeight(INTERLINE*3f+(FONT_SIZE+INTERLINE)*(1f+umlClass.getAttributeList().size()+umlClass.getMethodList().size()));
    }

    private float getAngle(float xOrigin, float yOrigin, float xEnd, float yEnd) {
        //calculate angle between segment and horizontal
        return (float)(Math.copySign(Math.abs(Math.acos((xEnd-xOrigin)/Math.sqrt((xEnd-xOrigin)*(xEnd-xOrigin)+(yEnd-yOrigin)*(yEnd-yOrigin)))),yEnd-yOrigin)/
                Math.PI*180f);
    }

    private float getAbsoluteProjectWidth() {
        //return the length of the rectangle that can contain all the project

        float minX=1000000;
        float maxX=-1000000;
        for (UmlClass c : mUmlProject.getUmlClasses()) {
            minX=Math.min(c.getUmlClassNormalXPos(),minX);
            maxX=Math.max(c.getNormalRightEnd(),maxX);
        }
        return maxX-minX;
    }

    private float getAbsoluteProjectHeight() {
        //return the height of the rectangle that can contain all the project

        float minY=1000000;
        float maxY=-1000000;
        for (UmlClass c : mUmlProject.getUmlClasses()) {
            minY=Math.min(c.getUmlClassNormalYPos(),minY);
            maxY=Math.max(c.getNormalBottomEnd(),maxY);
        }
        return maxY-minY;
    }

    private float getAbsoluteProjectLeft() {
        float minX=1000000;
        for (UmlClass c : mUmlProject.getUmlClasses()) minX=Math.min(c.getUmlClassNormalXPos(),minX);
        return minX;
    }

    private float getAbsoluteProjectRight() {
        float maxX=-1000000;
        for (UmlClass c : mUmlProject.getUmlClasses()) maxX=Math.max(c.getNormalRightEnd(),maxX);
        return maxX;
    }

    private float getAbsoluteProjectTop() {
        float minY=1000000;
        for (UmlClass c : mUmlProject.getUmlClasses()) minY=Math.min(c.getUmlClassNormalYPos(),minY);
        return minY;
    }

    private float getAbsoluteProjectBottom() {
        float maxY=-1000000;
        for (UmlClass c : mUmlProject.getUmlClasses()) maxY=Math.max(c.getNormalBottomEnd(),maxY);
        return maxY;
    }

    private void adjustViewToProject() {
        float xZoom=this.getMeasuredWidth()/getAbsoluteProjectWidth();
        float yZoom=this.getMeasuredHeight()/getAbsoluteProjectHeight();
        if (xZoom <= yZoom) {
            mZoom = xZoom;
            mXOffset=-getAbsoluteProjectLeft()*mZoom;
            mYOffset =-getAbsoluteProjectTop()*mZoom+(this.getMeasuredHeight()-getAbsoluteProjectHeight()*mZoom)/2f;
        } else {
            mZoom=yZoom;
            mYOffset=-getAbsoluteProjectTop()*mZoom;
            mXOffset=-getAbsoluteProjectLeft()*mZoom+(this.getMeasuredWidth()-getAbsoluteProjectWidth()*mZoom)/2f;
        }
        this.invalidate();
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

//    **********************************************************************************************
//    Other methods
//    **********************************************************************************************

    private UmlClass getTouchedClass(float visibleX, float visibleY) {

        for (UmlClass c:mUmlProject.getUmlClasses()) {
            if (c.containsPoint(absoluteX(visibleX), absoluteY(visibleY)))
                return c;
        }
        return null;
    }

    public UmlRelation getTouchedRelation(float visibleX, float visibleY) {
        for (UmlRelation r : mUmlProject.getUmlRelations()) {
            if (distance(absoluteX(visibleX),absoluteY(visibleY),r.getXOrigin(),r.getYOrigin(),r.getXEnd(),r.getYEnd())<=20
            && absoluteX(visibleX)>=Math.min(r.getXOrigin(),r.getXEnd())-20
            && absoluteX(visibleX)<=Math.max(r.getXOrigin(),r.getXEnd())+20
            && absoluteY(visibleY)>=Math.min(r.getYOrigin(),r.getYEnd())-20
            && absoluteY(visibleY)<=Math.max(r.getYOrigin(),r.getYEnd())+20)
                return r;
        }
        return null;
    }

    private float distance(float dotX, float dotY, float originX, float originY, float endX, float endY) {
        //calculate the distance between a dot and a line
        //uX and uY are coordinates of a normal vector perpendicular to the line

        float uX;
        float uY;

        if (originX == endX) {
            uX=0;
            uY=1;
        } else if (originY == endY) {
            uX = 1;
            uY = 0;
        } else {
            uX = (float) (1f / Math.sqrt(1f + (endX - originX) * (endX - originX) / (endY - originY) / (endY - originY)));
            uY= (float) ((originX-endX)/(endY-originY)/Math.sqrt(1f + (endX - originX) * (endX - originX) / (endY - originY) / (endY - originY)));
        }

        return Math.abs((dotX-originX)*uX+(dotY-originY)*uY);
    }

    private void updateProjectGeometricalParameters() {
        mUmlProject.setZoom(mZoom);
        mUmlProject.setXOffset(mXOffset);
        mUmlProject.setYOffset(mYOffset);
    }

//    **********************************************************************************************
//    Interaction methods
//    **********************************************************************************************
    private void promptDeleteRelation(final UmlRelation umlRelation, final View view) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setTitle("Delete relation")
                .setMessage("Are you sure you want to delete this relation ?")
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mUmlProject.removeUmlRelation(umlRelation);
                        view.invalidate();
                    }
                });
        AlertDialog dialog=builder.create();
        dialog.show();
    }
}
