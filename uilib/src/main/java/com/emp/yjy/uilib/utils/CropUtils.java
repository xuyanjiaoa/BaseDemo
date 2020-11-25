package com.emp.yjy.uilib.utils;

import android.graphics.Point;

/**
 * @author Created by LRH
 * @date 2020/11/10 11:30
 */

public class CropUtils {

    public static double getPointsDistance(Point p1, Point p2) {
        return getPointsDistance(p1.x, p1.y, p2.x, p2.y);
    }

    public static double getPointsDistance(float x1, float y1, float x2, float y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

}
