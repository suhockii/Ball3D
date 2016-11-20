package com.example.hzkto.ball.system;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.example.hzkto.ball.tools.Sphere3D;

/**
 * Created by hzkto on 10/26/2016.
 */

public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private DrawThread drawThread;
    public int screenHeight;
    public int screenWidth;

    public MySurfaceView(Context context) {
        super(context);
        getHolder().addCallback(this);
        setZOrderOnTop(true);
        screenHeight = context.getResources().getDisplayMetrics().heightPixels;
        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        drawThread = new DrawThread(getContext(), this);
        drawThread.setRunning(true);
        drawThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        drawThread.setRunning(false);
        while (retry) {
            try {
                drawThread.join();
                retry = false;
            } catch (InterruptedException ignored) {
            }
        }
    }

}
