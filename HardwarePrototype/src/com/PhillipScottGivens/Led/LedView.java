package com.PhillipScottGivens.Led;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created with IntelliJ IDEA.
 * User: psgivens
 * Date: 5/24/12
 * Time: 7:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class LedView {
    private LedModel led;
    private Rect rectangle;
    private int ledSpacing;
    private Paint paint;
    private Paint rimPaint;

    public LedView(LedModel led)
    {
        this.led = led;
        paint = new Paint();
        paint.setColor(Color.BLACK);
        rimPaint = new Paint();
        rimPaint.setColor(Color.YELLOW);
    }

    public void clear()
    {
        setColor(Color.BLACK);
    }

    public int getColor()
    {
        return led.getColor();
    }

    public void setColor(int value)
    {
        led.setColor(value);
        paint.setColor(value);
    }

    public void setMeasurements(Rect rectangle, int ledSpacing)
    {
        this.rectangle = rectangle;
        this.ledSpacing = ledSpacing;
    }

    public boolean isHit(int x, int y)
    {
        return  (rectangle.contains(x, y));
    }

    public Rect getBounds()
    {
        return rectangle;
    }

    public void draw(Canvas canvas)
    {
        int centerX = rectangle.centerX();
        canvas.drawCircle(rectangle.centerX(),
                rectangle.centerY(),
                rectangle.width()/2 - ledSpacing + 1,
                rimPaint);
        canvas.drawCircle(rectangle.centerX(),
                rectangle.centerY(),
                rectangle.width()/2 - ledSpacing,
                paint);
    }
}
