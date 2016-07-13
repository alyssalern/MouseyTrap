package com.alyssalerner.mouseytrap;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

/** A game object in the shape of a circle, separated for the different intersection detection algorithm
 * Created by Alyssa on 2016-04-26.
 */
public class CircularGameObject extends GameObject {
    public static String TAG = "CircularGameObject";

    public CircularGameObject(Bitmap image, int height) {
        super(image, height);
    }

    /* Determine if this circular object collides with another circular object.
     * other: The other circular object.
     */
    public boolean collidesWith(CircularGameObject other) {
        // Return false early if the rectangles don't intersect
        if(!super.collidesWith(other))
            return false;

        // Determine the distance between the two circles' centers
        double xDif = this.getX() - other.getX();
        double yDif = this.getY() - other.getY();
        double distance = Math.sqrt(Math.pow(xDif, 2.0) + Math.pow(yDif, 2.0));

        // If this distance is less than the sum of the circles' radii, they must intersect
        double sumOfRadii = (this.getHeight() + other.getHeight()) / 2.0;
        if(distance < sumOfRadii)
            return true;

        return false;
    }

    /* Determine if this circular object collides with a rectangular object
     * The rectangular object.
     * Theory: If a circle and rectangle intersect, at least 1 of the following must be true:
     *   (i) The circle's center is inside the rectangle
     *   (ii) The point at the very top, bottom, left or right of the circle is inside the rectangle
     *   (iii) A corner of the rectangle is inside the circle
     * Each of these possibilities is tested for in this method.
     */
    public boolean collidesWith(GameObject other) {
        // Return false early if the rectangles don't intersect
        if(!super.collidesWith(other))
            return false;

        Rect thisRect = getRectangle();
        Rect otherRect = other.getRectangle();
        double centerX = thisRect.exactCenterX();
        double centerY = thisRect.exactCenterY();

        double radius = getWidth() / 2.0;
        // If rectangle contains circle's center or 1 of its sides, it definitely intersects
        if(otherRect.contains((int) centerX, (int) centerY) ||
                otherRect.contains((int)centerX, (int)(centerY + radius)) ||
                otherRect.contains((int)centerX, (int)(centerY - radius)) ||
                otherRect.contains((int)(centerX + radius), (int)centerY) ||
                otherRect.contains((int)(centerX - radius), (int)centerY))
            return true;

        // Find rectangle's corner that is closest to the circle
        double cornerX = (Math.abs(otherRect.left - centerX) < Math.abs(otherRect.right - centerX))? otherRect.left : otherRect.right;
        double cornerY = (Math.abs(otherRect.bottom - centerY) < Math.abs(otherRect.top - centerY))? otherRect.bottom : otherRect.top;

        // Find point on circle's circumference that is between the circle's center and this corner
        double dist = Math.sqrt(Math.pow(cornerY - centerY, 2) + Math.pow(cornerX - centerX, 2));   // Distance between circle's center point and trap's nearest corner
        double cirX = centerX + (radius * (cornerX - centerX) / dist);
        double cirY = centerY + (radius * (cornerY - centerY) / dist);

        return otherRect.contains((int)cirX, (int)cirY);
    }
}
