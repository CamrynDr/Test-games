package com.example.cdrake.scrolling;
import android.graphics.PointF;

public class Bullet {
    private PointF point;
    private float horizontalVelocity;
    private float verticalVelocity;
    float speed = 50;
    private boolean isActive;

    public Bullet() {
        isActive = false;
        point = new PointF();
    }

    public boolean shoot(float startX, float startY, float direction) {
        if (!isActive) {
            point.x = startX;
            point.y = startY;
            horizontalVelocity = (float)(Math.cos(Math.toRadians(direction)));
            verticalVelocity = (float)(Math.sin(Math.toRadians(direction)));
            isActive = true;
            return true;
        }
        return false;
    }

    public void update(long fps){
        point.x = point.x + horizontalVelocity * speed / fps;
        point.y = point.y + verticalVelocity * speed / fps;
    }

    public PointF getPoint(){
        return  point;
    }

    public boolean getStatus(){
        return isActive;
    }

    public void setInactive(){
        isActive = false;
    }
}
