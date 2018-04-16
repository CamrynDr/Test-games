import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
    private boolean paused = true;
    private Canvas canvas;
    private Paint paint;
    private long fps;
    private long timeThisFrame;
    private int screenx;
    private int screeny;
    private PlayerShip playerShip;
    private Bullet bullet;
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

    screenx = x;
    screeny = y;
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
    }}


private void update() {
        boolean bumped = false;
        boolean lost = false;
        if(lost) {
            prepareLevel();
        }
    }

    private void prepareLevel() {
    }


private void draw() {
    if (ourHolder.getSurface().isValid()) {
        canvas = ourHolder.lockCanvas();
        canvas.drawColor(Color.argb(255, 26, 128, 182));
        paint.setColor(Color.argb(255, 255,255,255));
        paint.setColor(Color.argb(255,249,129,0));
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
            break;
        case MotionEvent.ACTION_UP:
            break;
    }
    return true;
}
}

