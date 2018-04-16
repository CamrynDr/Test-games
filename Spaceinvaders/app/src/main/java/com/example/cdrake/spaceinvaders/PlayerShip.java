package com.example.cdrake.spaceinvaders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;


/**
 * Created by cdrake on 2/27/2018.
 */

public class PlayerShip {
    private Bitmap bitmap = null;
    private final int height;
    Rect rect;
    private float length;
    private float x;
    private float y;
    private float shipSpeed;
    public final int STOPPED = 0;
    public final int LEFT = 1;
    public final int RIGHT = 2;
    private int shipMoving = STOPPED;

    public PlayerShip(Context context, int screenX, int screenY) {
        rect = new Rect();
        length = screenX/10;
        height = screenY / 10;
        x = screenX/2;
        y = screenX - 20;
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.playership);
        bitmap = Bitmap.createScaledBitmap(bitmap,
                (int) (length),
                height,
                false);

        shipSpeed = 350;
    }

    public Rect getRect() {
        return rect;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public float getX() {
        return x;
    }

    public float getLength() {
        return length;
    }

    public void setMovementState(int state) {
        shipMoving = state;
    }
    public void update(long fps) {
        if(shipMoving == LEFT) {
            x = x - shipSpeed / fps;
        }
        if(shipMoving == RIGHT) {
            x = x + shipSpeed / fps;
        }
        rect.top = (int) y;
        rect.bottom= (int) (y + height);
        rect.left = (int) x;
        rect.right = (int) (x + length);
    }

}
