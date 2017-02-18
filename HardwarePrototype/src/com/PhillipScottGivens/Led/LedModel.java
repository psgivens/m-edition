package com.PhillipScottGivens.Led;

import android.graphics.Color;

/**
 * Created with IntelliJ IDEA.
 * User: psgivens
 * Date: 5/24/12
 * Time: 6:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class LedModel {
    private int color = Color.TRANSPARENT;

    public int getColor()
    {
        return color;
    }
    public  void setColor(int value)
    {
        color = value;
    }
}
