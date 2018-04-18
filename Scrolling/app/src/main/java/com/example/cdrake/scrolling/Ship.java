package com.example.cdrake.scrolling;

import android.graphics.PointF;

public class Ship {
    PointF a;
    PointF b;
    PointF c;
    PointF centre;
    float facingAngle = 270;
    private float speed = 0;
    private float horizontalVelocity;
    private float verticalVelocity;
    public final int STOPPING = 0;
    public final int LEFT = 1;
    public final int RIGHT = 2;
    public final int THRUSTING = 3;
    private int shipMoving = STOPPING;
    private float previousFA;

    public Ship() {
        float length = 2.5f;
        float width = 1.25f;
        a = new PointF();
        b = new PointF();
        c = new PointF();
        centre = new PointF();
        centre.x = 50;
        centre.y = 50;
        a.x = centre.x;
        a.y = centre.y - length / 2;
        b.x = centre.x - width / 2;
        b.y = centre.y + length / 2;
        c.x = centre.x + width / 2;
        c.y = centre.y + length / 2;
    }

    public PointF getCentre(){
        return  centre;
    }
    public PointF getA(){
        return  a;
    }
    public PointF getB(){
        return  b;
    }
    public PointF getC(){
        return  c;
    }
    float getFacingAngle(){
        return facingAngle;
    }
    public void bump(){
        speed = 0;
        centre.x = centre.x - horizontalVelocity * 2;
        centre.y = centre.y - verticalVelocity * 2;
        a.x = a.x - horizontalVelocity * 2;
        a.y = a.y - verticalVelocity * 2;
        b.x = b.x - horizontalVelocity * 2;
        b.y = b.y - verticalVelocity * 2;
        c.x = c.x - horizontalVelocity * 2;
        c.y = c.y - verticalVelocity * 2;
    }

    public void setMovementState(int state){
        shipMoving = state;
    }

    public void update(long fps) {
        final float ROTATION_SPEED = 200;
        final float BREAK_RATE = 30;
        float previousFA = facingAngle;
        if(shipMoving == LEFT) {
            facingAngle = facingAngle - ROTATION_SPEED / fps;
            if(facingAngle < 1) {
                facingAngle = 360;
                facingAngle = 1;
            }
        }
        if(shipMoving == THRUSTING) {
            final float MAX_SPEED = 80;
            final float ACCELERATION_RATE = 40;
            if(speed <MAX_SPEED) {
                speed = speed + (ACCELERATION_RATE / fps);
                speed = speed - (BREAK_RATE / fps);
            }
        }
        float tempX;
        float tempY;
        a.x = a.x - centre.x;
        a.y = a.y - centre.y;
        tempX = (float)(a.x * Math.cos(Math.toRadians(facingAngle - previousFA)) -
                a.y * Math.sin(Math.toRadians(facingAngle - previousFA)));
        tempY = (float)(a.x * Math.sin(Math.toRadians(facingAngle - previousFA)) +
                a.y * Math.cos(Math.toRadians(facingAngle - previousFA)));
        a.x = tempX + centre.x;
        a.y = tempY + centre.y;
        b.x = b.x - centre.x;
        b.y = b.y - centre.y;
        tempX = (float)(b.x * Math.cos(Math.toRadians(facingAngle - previousFA)) -
                b.y * Math.sin(Math.toRadians(facingAngle - previousFA)));
        tempY = (float)(b.x * Math.sin(Math.toRadians(facingAngle - previousFA)) +
                b.y * Math.cos(Math.toRadians(facingAngle - previousFA)));
        b.x = tempX + centre.x;
        b.y = tempY + centre.y;
        c.x = c.x - centre.x;
        c.y = c.y - centre.y;
        tempX = (float)(c.x * Math.cos(Math.toRadians(facingAngle - previousFA)) -
                c.y * Math.sin(Math.toRadians(facingAngle - previousFA)));
        tempY = (float)(c.x * Math.sin(Math.toRadians(facingAngle - previousFA)) +
                c.y * Math.cos(Math.toRadians(facingAngle - previousFA)));
        c.x = tempX + centre.x;
        c.y = tempY + centre.y;
    }
}
