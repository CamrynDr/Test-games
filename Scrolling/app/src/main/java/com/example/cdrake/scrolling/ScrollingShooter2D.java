package com.example.cdrake.scrolling;
import android.app.Activity;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;


public class ScrollingShooter2D extends Activity {
    GameView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display display = getWindowManager().
                getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        view = new GameView(this, size.x, size.y);
        setContentView(view);
    }
    @Override
    protected void onResume() {
        super.onResume();
        view.resume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        view.pause();
    }0o0o
}