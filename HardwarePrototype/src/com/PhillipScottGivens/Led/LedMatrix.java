package com.PhillipScottGivens.Led;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.PhillipScottGivens.R;

/**
 * Created with IntelliJ IDEA.
 * User: psgivens
 * Date: 5/24/12
 * Time: 7:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class LedMatrix extends View  implements View.OnTouchListener {

    private LedView[][] leds;
    private int matrixDimension;
    private int ledWidthAndHeight;
    private int ledSpacing;
    private int currentColor = Color.BLUE;

    public LedMatrix(Context context, AttributeSet attributeSet)
    {
        super(context, attributeSet);

        TypedArray attributeArray=getContext().obtainStyledAttributes(
                attributeSet,
                R.styleable.LedMatrix);

        matrixDimension = attributeArray.getInteger(R.styleable.LedMatrix_dimension, 1);
        ledSpacing = attributeArray.getInteger(R.styleable.LedMatrix_ledSpacing, 1);
        boolean supportsTouch = attributeArray.getBoolean(R.styleable.LedMatrix_enableTouch, true);

        //Don't forget this
        attributeArray.recycle();

        leds = new LedView[matrixDimension][matrixDimension];
        for(int rowIndex=0; rowIndex< matrixDimension;rowIndex++)
            for(int columnIndex=0; columnIndex< matrixDimension;columnIndex++)
                leds[rowIndex][columnIndex] = new LedView(new LedModel());

        if (supportsTouch)
            setOnTouchListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(widthMeasureSpec);
        int dimensions = Math.min(parentWidth, parentHeight);
        setMeasuredDimension(dimensions, dimensions);
        ledWidthAndHeight = dimensions / matrixDimension;

        for(int rowIndex=0; rowIndex< matrixDimension;rowIndex++)
        {
            int top = ledWidthAndHeight * rowIndex + ledSpacing;
            int bottom = top + ledWidthAndHeight - 2 * ledSpacing;
            for(int columnIndex=0; columnIndex< matrixDimension;columnIndex++)
            {
                int left = ledWidthAndHeight * columnIndex + ledSpacing;
                int right = left + ledWidthAndHeight - 2 * ledSpacing;
                leds[rowIndex][columnIndex].setMeasurements(new Rect(left,top,right,bottom), ledSpacing);
            }
        }
    }

    protected void onDraw(Canvas canvas)
    {
        for(int rowIndex=0; rowIndex<matrixDimension; rowIndex++)
            for(int columnIndex=0; columnIndex<matrixDimension; columnIndex++)
                leds[rowIndex][columnIndex].draw(canvas);
    }

    public boolean onTouch(View view, MotionEvent event) {
        // if(event.getAction() != MotionEvent.ACTION_DOWN)
        // return super.onTouchEvent(event);

        int x = (int)event.getX();
        int y = (int)event.getY();
        for(int rowIndex=0; rowIndex<matrixDimension; rowIndex++)
            for(int columnIndex=0; columnIndex<matrixDimension; columnIndex++)
            {
                LedView led = leds[rowIndex][columnIndex];
                if (led.isHit(x, y))
                {
                    led.setColor(currentColor);
                    invalidate(led.getBounds());
                }
            }

        return true;
    }

    public void setColor(int color)
    {
        this.currentColor = color;
    }
    public int getColor()
    {
        return currentColor;
    }

    public void clear()
    {
        for(int rowIndex=0; rowIndex<matrixDimension; rowIndex++)
            for(int columnIndex=0; columnIndex<matrixDimension; columnIndex++)
                leds[rowIndex][columnIndex].clear();
        invalidate();
    }

    public void setLed(int x, int y, int color)
    {
        LedView led = leds[x][y];
        led.setColor(color);
        invalidate(led.getBounds());
    }
}
