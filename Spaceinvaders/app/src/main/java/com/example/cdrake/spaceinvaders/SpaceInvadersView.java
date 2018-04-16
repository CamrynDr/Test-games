package com.example.cdrake.spaceinvaders;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by cdrake on 2/27/2018.
 */

public class SpaceInvadersView extends SurfaceView implements Runnable {
    Context context;
    private Thread gameThread = null;
    private SurfaceHolder ourHolder;
    private volatile boolean playing;
    public boolean paused = true;
    private Canvas canvas;
    private Paint paint;
    private long fps;
    private long timeThisFrame;
    public int screenX;
    public int screenY;
    public PlayerShip playerShip;
    public Bullet bullet;
    private Bullet[] invadersBullets = new Bullet[200];
    private int nextBullet;
    private int maxInvaderBullets = 10;
    Invader[] invaders = new Invader[60];
    int numInvaders = 0;
    private DefenceBrick[] bricks = new DefenceBrick[400];
    private int numBricks;
    private SoundPool soundPool;
    private int playerExplodeID = -1;
    private int invaderExplodeID = -1;
    private int shootID = -1;
    private int damageShelterID = -1;
    private int uhID = -1;
    private int ohID = -1;
    int score = 0;
    private int lives = 3;
    private long menaceInterval = 100;
    private boolean uhOrOh;
    private long lastMenaceTime = System.currentTimeMillis();

    public SpaceInvadersView(Context context) {
        super(context);
    }


@RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
public SpaceInvadersView(Context context, int x, int y) {
    super(context);
    this.context = context;
    ourHolder = getHolder();
    paint = new Paint();

    screenX = x;
    screenY = y;
    soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC,0);
        try{
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;
            descriptor = assetManager.openFd("shoot.ogg");
            invaderExplodeID = soundPool.load(descriptor, 0);
            descriptor = assetManager.openFd("invaderexplode.ogg");
            invaderExplodeID = soundPool.load(descriptor, 0);
            descriptor = assetManager.openFd("damageshelter.ogg");
            damageShelterID = soundPool.load(descriptor, 0);
            descriptor = assetManager.openFd("playerexplode.ogg");
            playerExplodeID = soundPool.load(descriptor, 0);
            descriptor = assetManager.openFd("damageshelter.ogg");
            damageShelterID = soundPool.load(descriptor, 0);
            descriptor = assetManager.openFd("uh.ogg");
            uhID = soundPool.load(descriptor, 0);
            descriptor = assetManager.openFd("oh.ogg");
            ohID = soundPool.load(descriptor, 0);
        }
        catch(IOException e) {
            Log.e("error", "failed to load sound files");
        }
        prepareLevel();
}

public void run() {
    while (playing) {
        long startFrameTime = System.currentTimeMillis();
        if (!paused) {
            update();
        }
        draw();
        timeThisFrame = System.currentTimeMillis() - startFrameTime;
        if (timeThisFrame >= 1) {
            fps = 1000 / timeThisFrame;
        }

    if(!paused) {
        if ((startFrameTime - lastMenaceTime) > menaceInterval) {
            if(uhOrOh) {
                soundPool.play(uhID, 1, 1, 0, 0, 1);
            }
            else {
                soundPool.play(ohID, 1, 1, 0, 0, 1);
            }
            lastMenaceTime = System.currentTimeMillis();
            uhOrOh = !uhOrOh;
        }
    }
    }}


private void update() {
        boolean bumped = false;
        boolean lost = false;
    if(lost){
        prepareLevel();
    }
    playerShip.update(fps);
        if(bullet.getStatus()) {
            bullet.update(fps);
        }
    for (Bullet invadersBullet : invadersBullets) {
        if (invadersBullet.getStatus()) {
            invadersBullet.update(fps);
        }
    }
    for(int i = 0; i < numInvaders; i++) {
     if(invaders[i].getVisibility()) {
         invaders[i].update(fps);
         if(invaders[i].takeAim(playerShip.getX(),
                 playerShip.getLength())) {
             if(invadersBullets[nextBullet].shoot(invaders[i].getX()
                             + invaders[i].getLength() / 2,
                     invaders[i].getY(), bullet.DOWN)) {
                 nextBullet++;
             }
             }
     }
     if(invaders[i].getX() > screenX - invaders[i].getLength()
             || invaders[i].getX() < 0) {
         bumped = true;
     }
     if(bumped) {
         for(i = 0; i < numInvaders; i++) {
             invaders[i].dropDownAndReverse();
             if(invaders[i].getY() > screenY - screenY / 10) {
                 lost = true;
             }
         }
         menaceInterval = menaceInterval - 80;
     }
    }
    if(bullet.getImpactPointY() < 0) {
            bullet.setInactive();
    }
    for(int i = 0; i < invadersBullets.length; i++) {
            if(invadersBullets[i].getImpactPointY() > screenY) {
                invadersBullets[i].setInactive();
            }
            if(invadersBullets[i].getStatus()) {
                for(int j = 0; j < numBricks; j++) {
                    if(bricks[j].getVisibility()) {
                        if(RectF.intersects(invadersBullets[i].getRect(), bricks[j].getRect())) {
                            invadersBullets[i].setInactive();
                            bricks[j].setInvisible();
                            soundPool.play(damageShelterID, 1, 1, 0, 0 ,1);

                        }
                    }
                }
                if(RectF.intersects(playerShip.getRect(), invadersBullets[i].getRect())){
                    invadersBullets[i].setInactive();
                    lives--;
                    soundPool.play(playerExplodeID, 1, 1, 0, 0, 1);
                    if(lives == 0) {
                        paused = true;
                        lives = 3;
                        score = 0;
                        prepareLevel();
                    }
                }
            }
    }
    if(bullet.getStatus()) {
            for (int i = 0; i < numInvaders; i++) {
                if(invaders[i].getVisibility()) {
                    if(RectF.intersects(bullet.getRect(), invaders[i].getRect())) {
                        invaders[i].setInvisible();
                        soundPool.play(invaderExplodeID,1, 1,0 ,0, 1);
                        bullet.setInactive();
                        score = score + 10;
                        if (score == numInvaders * 10) {
                            paused = true;
                            score = 0;
                            lives = 3;
                            prepareLevel();
                        }
                    }
                }
            }
            for(int i = 0; i < numBricks; i++) {
                if(bricks[i].getVisibility()) {
                    if(RectF.intersects(bullet.getRect(), bricks[i].getRect())) {
                        bullet.setInactive();
                        bricks[i].setInvisible();
                        soundPool.play(damageShelterID, 1, 1, 0, 0, 1);
                    }
                }
            }
    }
}

    private void prepareLevel() {
        playerShip = new PlayerShip(context, screenX, screenY);
        bullet = new Bullet(screenY);
        for(int i = 0; i < invadersBullets.length; i++) {
            invadersBullets[i] = new Bullet(screenY);
        }
        numInvaders = 0;
        for(int column = 0; column < 6; column++) {
            for(int row = 0; row < 5; row++) {
                invaders[numInvaders] = new Invader(context, row, column, screenX, screenY);
                numInvaders++;
            }
        }
        menaceInterval = 1000;

        numBricks = 0;
        for(int shelterNumber = 0; shelterNumber < 4; shelterNumber++){
            for(int column = 0; column < 10; column ++ ) {
                for (int row = 0; row < 5; row++) {
                    bricks[numBricks] = new DefenceBrick(row, column, shelterNumber, screenX, screenY);
                    numBricks++;
                }
            }
        }
    }

private void draw() {
    if (ourHolder.getSurface().isValid()) {
        canvas = ourHolder.lockCanvas();
        canvas.drawColor(Color.argb(255, 26, 128, 182));
        paint.setColor(Color.argb(255, 255,255,255));
        paint.setColor(Color.argb(255,249,129,0));
        canvas.drawBitmap(playerShip.getBitmap(), playerShip.getX(), screenY - 50, paint);
        if(bullet.getStatus()) {
            canvas.drawRect(bullet.getRect(), paint);
        }
        for (Bullet invadersBullet : invadersBullets) {
            if (invadersBullet.getStatus()) {
                canvas.drawRect(invadersBullet.getRect(), paint);
            }
        }
        for(int i = 0; i < numBricks; i++){
            if(bricks[i].getVisibility()) {
                canvas.drawRect(bricks[i].getRect(), paint);
            }
        }
        for(int i = 0; i < numInvaders; i++){
            if(invaders[i].getVisibility()) {
                if(uhOrOh) {
                    canvas.drawBitmap(invaders[i].getBitmap(), invaders[i].getX(), invaders[i].getY(), paint);
                }else{
                    canvas.drawBitmap(invaders[i].getBitmap2(), invaders[i].getX(), invaders[i].getY(), paint);

                }
            }
        }
        paint.setTextSize(40);
            canvas.drawText("Score: " + score + " Lives: " + lives, 10,50, paint);
        ourHolder.unlockCanvasAndPost(canvas);
    }
    }

public void pause() {
    playing = false;
    try {
        gameThread.join();
    }
    catch (InterruptedException e) {
        Log.e("Error: ", "joining thread");
    }
}

public void resume() {
    playing = true;
    gameThread = new Thread(this);
    gameThread.start();
}
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                paused = false;
                if (motionEvent.getY() > screenY - screenY / 8) {
                    if (motionEvent.getX() > screenX / 2) {
                        playerShip.setMovementState(playerShip.RIGHT);
                    } else {
                        playerShip.setMovementState(playerShip.LEFT);
                    }
                }
                if (motionEvent.getY() < screenY - screenY / 8) {
                    if (bullet.shoot(playerShip.getX() +
                            playerShip.getLength() / 2, screenY, bullet.UP)) {
                        soundPool.play(shootID, 1, 1, 0, 0, 1);
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                if (motionEvent.getY() > screenY - screenY / 10) {
                    playerShip.setMovementState(playerShip.STOPPED);
                }

                break;
        }
        return true;

    }
}

