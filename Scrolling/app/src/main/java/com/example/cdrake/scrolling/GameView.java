package com.example.cdrake.scrolling;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class GameView extends SurfaceView implements Runnable{
    Random random = new Random();
    private SoundPool soundPool;
    private int shootID = -1;
    private int damageBuildingID = -1;
    HUD hud;
    int worldWidth = 0;
    int targetWorldWidth = 500;
    int targetWorldHeight = 150;
    int groundLevel = 145;
    RectF convertedRect = new RectF();
    PointF convertedPointA = new PointF();
    PointF convertedPointB = new PointF();
    PointF convertedPointC = new PointF();
    PointF tempPointF = new PointF();
    private Thread gameThread = null;
    private SurfaceHolder ourHolder;
    private volatile boolean playing;
    private boolean paused = true;
    private Paint paint;
    private long fps;
    private Brick[] bricks = new Brick[20000];
    private int numBricks;
    private Star[] stars = new Star[5000];
    private int numStars;
    Ship player;
    private Bullet[] playerBullets = new Bullet[10];
    private int nextPlayerBullet;
    private int maxPlayerBullets = 10;
    ViewPort vp;


    public GameView(Context context, int screenX, int screenY) {
        super(context);
        ourHolder = getHolder();
        paint = new Paint();
        vp = new ViewPort(screenX, screenY);
        hud = new HUD(screenX, screenY);
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        try {
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;
            descriptor = assetManager.openFd("shoot.ogg");
            shootID = soundPool.load(descriptor, 0);
            descriptor = assetManager.openFd("damagebuilding.ogg");
            damageBuildingID = soundPool.load(descriptor, 0);
        } catch (IOException e) {
            Log.e("error", "failed to load sound files");
        }
        prepareLevel();
    }

    private void prepareLevel() {
        player = new Ship();
        for (int i = 0; i < playerBullets.length; i++) {
            playerBullets[i] = new Bullet();
        }
        vp.setWorldCentre(player.getCentre().x, player.getCentre().y);
        Random random = new Random();
        int gapFromLastBuilding;
        int maxGap = 25;
        int buildingWidth;
        int maxBuildingWidth = 10;
        int buildingHeight;
        int maxBuildingHeight = 85;
        for (worldWidth = 0;
             worldWidth < targetWorldWidth;
             worldWidth += buildingWidth + gapFromLastBuilding) {
            buildingWidth = random.nextInt(maxBuildingWidth) + 3;
            buildingHeight = random.nextInt(maxBuildingHeight) + 1;
            gapFromLastBuilding = random.nextInt(maxGap) + 1;
            for (int x = 0; x < buildingWidth; x++) {
                for (int y = groundLevel; y > groundLevel - buildingHeight; y--) {
                    boolean isLeft = false;
                    boolean isRight = false;
                    boolean isTop = false;
                    if (x == 0) {
                        isLeft = true;
                    }
                    if (x == buildingWidth - 1) {
                        isRight = true;
                    }
                    if (y == (groundLevel - buildingHeight) + 1) {
                        isTop = true;
                    }
                    bricks[numBricks] = new Brick(x + worldWidth, y,
                            isLeft, isRight, isTop);
                    numBricks++;
                }
            }
        }
        for (int i = 0; i < 500; i++) {
            stars[i] = new Star(targetWorldWidth, targetWorldHeight);
            numStars++;
        }
    }

    public void run() {
        while (playing) {
            long startFrameTime = System.currentTimeMillis();
            if (!paused) {
                update();
            }
            draw();
            long timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame >= 1) {
                fps = 1000 / timeThisFrame;
            }
        }
    }

    private void update() {
        vp.setWorldCentre(player.getCentre().x, player.getCentre().y);
        for (int i = 0; i < numBricks; i++) {
            if (vp.clipObjects(bricks[i].getRect().left,
                    bricks[i].getRect().top, 1, 1)) {
                bricks[i].clip();
            } else {
                bricks[i].unClip();
            }
        }
        player.update(fps);
        for (Bullet playerBullet : playerBullets) {
            if (playerBullet.getStatus()) {
                playerBullet.update(fps);
            }
        }
        for (int i = 0; i < maxPlayerBullets; i++) {
            if (playerBullets[i].getStatus()) {
                for (int j = 0; j < numBricks; j++) {
                    if (!bricks[j].isClipped()) {
                        if(!bricks[j].isDestroyed()) {
                            if (bricks[j].getRect().contains(
                                    playerBullets[i].getPoint().x,
                                    playerBullets[i].getPoint().y)) {
                                playerBullets[i].setInactive();
                                soundPool.play(damageBuildingID, 1, 1, 0, 0, 1);
                                bricks[j].destroy();
                                int chainReactionSize = random.nextInt(6);
                                for(int k = 1; k < chainReactionSize; k++){
                                    bricks[j+k].destroy();
                                    bricks[j-k].destroy();
                                }
                            }
                        }
                    }
                }
            }
        }
        for (int i = 0; i < maxPlayerBullets; i++) {
            if (playerBullets[i].getStatus()) {
                if(playerBullets[i].getPoint().x < 0){
                    playerBullets[i].setInactive();
                }
                else if(playerBullets[i].getPoint().x > targetWorldWidth){
                    playerBullets[i].setInactive();
                }
                else if(playerBullets[i].getPoint().y < 0){
                    playerBullets[i].setInactive();
                }
                else if(playerBullets[i].getPoint().y > targetWorldHeight){
                    playerBullets[i].setInactive();
                }
            }
        }
        for (int i = 0; i < numStars; i++) {
            stars[i].update();
        }
        for (int i = 0; i < numBricks; i++) {
            if (!bricks[i].isClipped()) {
                bricks[i].update();
            }
        }
        for (int i = 0; i < numBricks; i++) {
            if (!bricks[i].isClipped() && !bricks[i].isDestroyed()) {
                if (bricks[i].getRect().contains(player.getA().x, player.getA().y) ||
                        bricks[i].getRect().contains(player.getB().x, player.getB().y) ||
                        bricks[i].getRect().contains(player.getC().x, player.getC().y)) {
                    player.bump();
                }
            }
        }
        if (player.getB().y > groundLevel || player.getC().y > groundLevel) {
            player.bump();
        }
        if (player.getA().y < 0 ||
                player.getB().y < 0 ||
                player.getC().y < 0) {
            player.bump();
        }
        if (player.getA().x < 0 ||
                player.getB().x < 0 ||
                player.getC().x < 0) {player.bump();
        }
        if (player.getB().x > worldWidth || player.getC().x > worldWidth) {
            player.bump();
        }
    }

    private void draw() {
        if (ourHolder.getSurface().isValid()) {
            Canvas canvas = ourHolder.lockCanvas();
            canvas.drawColor(Color.argb(255, 0, 0, 0));
            paint.setColor(Color.argb(255, 138, 43, 226));
            convertedRect = vp.worldToScreen(
                    0,
                    0,
                    targetWorldWidth,
                    1
            );
            canvas.drawRect(convertedRect, paint);
            convertedRect = vp.worldToScreen(
                    0,
                    0,
                    1,
                    targetWorldHeight
            );
            canvas.drawRect(convertedRect, paint);
            convertedRect = vp.worldToScreen(
                    targetWorldWidth,
                    0,
                    1,
                    targetWorldHeight
            );
            canvas.drawRect(convertedRect, paint);
            paint.setColor(Color.argb(255, 255, 255, 255));
            for (int i = 0; i < numStars; i++) {
                if (stars[i].getVisibility()) {
                    tempPointF = vp.worldToScreenPoint(stars[i].getX(), stars[i].getY());
                    canvas.drawPoint(tempPointF.x, tempPointF.y, paint);
                }
            }
            for (int i = 0; i < numBricks; i++) {
                if (!bricks[i].isClipped()) {
                    paint.setColor(bricks[i].getColor());
                    convertedRect = vp.worldToScreen(
                            bricks[i].getRect().left,
                            bricks[i].getRect().top,
                            bricks[i].getRect().right - bricks[i].getRect().left,
                            bricks[i].getRect().bottom - bricks[i].getRect().top
                    );
                    canvas.drawRect(convertedRect, paint);
                    paint.setColor(Color.argb(255, 190, 190, 190));
                    if (bricks[i].getLeft()) {
                        canvas.drawLine(
                                convertedRect.left,
                                convertedRect.top,
                                convertedRect.left,
                                convertedRect.bottom,
                                paint
                        );
                    }
                    if (bricks[i].getRight()) {
                        canvas.drawLine(
                                convertedRect.right,
                                convertedRect.top,
                                convertedRect.right,
                                convertedRect.bottom,
                                paint
                        );
                    }
                    if (bricks[i].getTop()) {
                        canvas.drawLine(
                                convertedRect.left,
                                convertedRect.top,
                                convertedRect.right,
                                convertedRect.top,
                                paint
                        );
                    }
                }
            }
            tempPointF = vp.worldToScreenPoint(player.getA().x, player.getA().y);
            convertedPointA.x = tempPointF.x;
            convertedPointA.y = tempPointF.y;
            tempPointF = vp.worldToScreenPoint(player.getB().x, player.getB().y);
            convertedPointB.x = tempPointF.x;
            convertedPointB.y = tempPointF.y;
            tempPointF = vp.worldToScreenPoint(player.getC().x, player.getC().y);
            convertedPointC.x = tempPointF.x;
            convertedPointC.y = tempPointF.y;
            paint.setColor(Color.argb(255, 255, 255, 255));
            canvas.drawLine(convertedPointA.x, convertedPointA.y,
                    convertedPointB.x, convertedPointB.y,
                    paint);
            canvas.drawLine(convertedPointB.x, convertedPointB.y,
                    convertedPointC.x, convertedPointC.y,
                    paint);
            canvas.drawLine(convertedPointC.x, convertedPointC.y,
                    convertedPointA.x, convertedPointA.y,
                    paint);
            canvas.drawPoint(convertedPointA.x, convertedPointA.y, paint);
            for (Bullet playerBullet : playerBullets) {
                if (playerBullet.getStatus()) {

                    tempPointF = vp.worldToScreenPoint(
                            playerBullet.getPoint().x, playerBullet.getPoint().y);

                    canvas.drawRect(tempPointF.x, tempPointF.y,
                            tempPointF.x + 4, tempPointF.y + 4, paint);
                }
            }
            paint.setColor(Color.argb(255, 255, 255, 255));
            paint.setTextSize(20);
            canvas.drawText("FPS = " + fps, 20, 70, paint);
            convertedRect = vp.worldToScreen(
                    -10,
                    groundLevel,
                    targetWorldWidth + 10,
                    targetWorldHeight - groundLevel
            );
            paint.setColor(Color.argb(255, 5, 66, 9));
            canvas.drawRect(convertedRect, paint);
            paint.setColor(Color.argb(80, 255, 255, 255));
            for (Rect rect : hud.currentButtonList) {
                RectF rf = new RectF(rect.left, rect.top, rect.right, rect.bottom);
                canvas.drawRoundRect(rf, 15f, 15f, paint);
            }
            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    public void pause() {
        playing = false;
        try {
            gameThread.join();
        }
        catch (InterruptedException e) {
            Log.e("Error:" , "joining thread");
        }
    }

    public void resume() {
        playing = true;
        gameThread = new Thread( this);
        gameThread.start();
    }

    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent motionEvent) {
        hud.handleInput(motionEvent);
        return true;
    }

    class HUD {
        Rect left;
        Rect right;
        Rect thrust;
        Rect shoot;
        Rect pause;

        private ArrayList<Rect> currentButtonList = new ArrayList<>();
        HUD(int screenWidth, int screenHeight) {
            int buttonWidth = screenWidth / 8;
            int buttonHeight = screenHeight / 7;
            int buttonPadding = screenWidth / 80;
            left = new Rect(buttonPadding,
                    screenHeight - buttonHeight - buttonPadding,
                    buttonWidth,
                    screenHeight - buttonPadding);
            right = new Rect(buttonWidth + buttonPadding,
                    screenHeight - buttonHeight - buttonPadding,
                    buttonWidth + buttonPadding + buttonWidth,
                    screenHeight - buttonPadding);
            thrust = new Rect(screenWidth - buttonWidth - buttonPadding,
                    screenHeight - buttonHeight - buttonPadding - buttonHeight - buttonPadding,
                    screenWidth - buttonPadding,
                    screenHeight - buttonPadding - buttonHeight - buttonPadding);
            shoot = new Rect(screenWidth - buttonWidth - buttonPadding,
                    screenHeight - buttonHeight - buttonPadding,
                    screenWidth - buttonPadding,
                    screenHeight - buttonPadding);
            pause = new Rect(screenWidth - buttonPadding - buttonWidth,
                    buttonPadding,
                    screenWidth - buttonPadding,
                    buttonPadding + buttonHeight);

            currentButtonList.add(left);
            currentButtonList.add(right);
            currentButtonList.add(thrust);
            currentButtonList.add(shoot);
            currentButtonList.add(pause);
        }

        private void handleInput(MotionEvent motionEvent) {
            int x = (int) motionEvent.getX(0);
            int y = (int) motionEvent.getY(0);
            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    if (right.contains(x, y)) {
                        player.setMovementState(player.RIGHT);
                    } else if (left.contains(x, y)) {
                        player.setMovementState(player.LEFT);
                    } else if (thrust.contains(x, y)) {
                        player.setMovementState(player.THRUSTING);
                    } else if (shoot.contains(x, y)) {
                        playerBullets[nextPlayerBullet].shoot(
                                player.getA().x,player.getA().y, player.getFacingAngle());
                        nextPlayerBullet++;
                        if (nextPlayerBullet == maxPlayerBullets) {
                            nextPlayerBullet = 0;
                        }
                        soundPool.play(shootID, 1, 1, 0, 0, 1);
                    }
                    else if(pause.contains(x, y)) {
                        paused = !paused;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    player.setMovementState(player.STOPPING);
            }
        }
    }
}



