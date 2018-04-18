package com.example.cdrake.scrolling;
import android.graphics.Color;
import android.graphics.RectF;
import java.util.Random;

public class Brick {
    private RectF rect;
    Random random = new Random();
    boolean destroyed;
    private boolean isRight;
    private boolean isLeft;
    private boolean isTop;
    private int color;
    private boolean clipped;

    public Brick(int columnNum, int rowNum, boolean isLeft, boolean isRight, boolean isTop) {
        int brickWidth = 1;
        int brickHeight = 1;
        this.isLeft = isLeft;
        this.isRight = isRight;
        this.isTop = isTop;
        float brickXPadding = .0f;
        float brickYPadding = .0f;
        rect = new RectF((columnNum * (brickXPadding + brickWidth + brickXPadding) + brickXPadding),
                ((rowNum * (brickYPadding + brickHeight + brickYPadding)) + brickYPadding),
                (columnNum * (brickXPadding + brickWidth + brickXPadding) + brickXPadding + brickWidth),
                ((rowNum * (brickYPadding + brickHeight + brickYPadding)) + brickYPadding + brickHeight)
        );
        if (random.nextInt(9) == 0) {
            int alpha = random.nextInt(256);
            color = Color.argb(alpha, 139, 0, 0);
        } else {
            color = Color.argb(205, 0, 0, 0);
        }
    }

    public void update() {
        if (!destroyed) {
            if (random.nextInt(6000) == 0) {
                if (random.nextInt(9) == 0) {
                    int alpha = random.nextInt(256);
                    color = Color.argb(alpha, 255, 50, 0);
                } else {
                    color = Color.argb(255, 63, 1, 0);
                }
            }
        } else {
            int whichColor = random.nextInt(3);
            switch (whichColor) {
                case 0:
                    color = Color.argb(255, 255, 0, 0);
                    break;

                case 1:
                    color = Color.argb(255, 245, 143, 10);
                    break;

                case 2:
                    color = Color.argb(255, 250, 250, 10);
                    break;
            }
        }
    }
    public void destroy() {
        destroyed = true;
    }
    public boolean isDestroyed() {
        return destroyed;
    }
    public void clip() {
        clipped = true;
    }
    public void unClip() {
        clipped = false;
    }
    public boolean isClipped() {
        return clipped;
    }
    public RectF getRect(){
        return this.rect;
    }
    public boolean getLeft(){
        return isLeft;
    }
    public int getColor(){
        return color;
    }
    public boolean getRight(){
        return isRight;
    }
    public boolean getTop(){
        return isTop;
    }
}
