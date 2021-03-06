package com.example.hzkto.ball.system;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;

import com.example.hzkto.ball.sphere.Point3D;
import com.example.hzkto.ball.sphere.Sphere3D;
import com.example.hzkto.ball.tools.MathTools;

import static android.content.Context.SENSOR_SERVICE;
import static com.example.hzkto.ball.Constants.PROJECTION_AXONOMETRIC;
import static com.example.hzkto.ball.Constants.PROJECTION_FRONT;
import static com.example.hzkto.ball.Constants.PROJECTION_PERSPECTIVE;
import static com.example.hzkto.ball.Constants.PROJECTION_HORIZONTAL;
import static com.example.hzkto.ball.Constants.PROJECTION_OBLIQUE;
import static com.example.hzkto.ball.Constants.PROJECTION_PROFILE;
import static com.example.hzkto.ball.Constants.SETTINGS_LIGHT;
import static com.example.hzkto.ball.Constants.SETTINGS_MOVE;
import static com.example.hzkto.ball.Constants.SETTINGS_PERFOMANCE;
import static com.example.hzkto.ball.Constants.SETTINGS_RESET;
import static com.example.hzkto.ball.Constants.SETTINGS_ROTATE;
import static com.example.hzkto.ball.Constants.SETTINGS_SCALE;
import static com.example.hzkto.ball.tools.SystemTools.getStandartLightPoint;
import static com.example.hzkto.ball.tools.SystemTools.getStandartLightPointPr;
import static com.example.hzkto.ball.tools.SystemTools.getStandartRadius;
import static com.example.hzkto.ball.tools.SystemTools.getViewCenter;
import static java.lang.Math.toRadians;

/**
 * Created by hzkto on 10/26/2016.
 */

public class DrawThread extends Thread implements View.OnTouchListener, View.OnClickListener {
    public static double radius;
    public static Point3D center;
    public static double angleX, angleY, angleZ;
    public static double scaleX, scaleY, scaleZ;
    public static Point3D lightPoint;
    public static int polygons;
    public static boolean reflect, invisLines;
    public static int[] color;
    public static boolean projectionOblique, projectionAxonometric, projectionPerspective;
    public static boolean projectionFrontal, projectionHorizontal, projectionProfile;
    public static double obliqueL, obliqueAlpha, axonometricFi;
    public static double axonometricPsi, perspectiveD, perspectiveQ;
    public static double perspectiveFi, perspectivePsi;
    private boolean runFlag = false;
    private SurfaceHolder surfaceHolder;
    private long prevTime;
    private SensorManager sensorManager;
    private Sensor sensorAccel;
    private Sensor sensorMagnet;
    private Sphere3D sphere;
    MySurfaceView surfaceView;

    public DrawThread(MySurfaceView surfaceView) {
        this.surfaceHolder = surfaceView.getHolder();
        setStandartParams(surfaceView);
        surfaceView.setOnTouchListener(this);
        prevTime = System.currentTimeMillis();
    }

    private void setStandartParams(MySurfaceView view) {
        center = getViewCenter(view);
        radius = getStandartRadius(center);
        lightPoint = getStandartLightPoint(center);
        angleX = 0;
        angleY = 0;
        angleZ = toRadians(90);
        polygons = 18;
        color = new int[]{255, 255, 0};
        reflect = true;
        invisLines = true;
        scaleX = 1;
        scaleY = 1;
        scaleZ = 1;
        reflect = true;
        invisLines = true;
    }

    public DrawThread(MySurfaceView surfaceView, Bundle bundle) {
        this.surfaceView = surfaceView;
        parseBundle(bundle);
        surfaceHolder = surfaceView.getHolder();
        prevTime = System.currentTimeMillis();
        surfaceView.setOnTouchListener(this);
    }

    private void parseBundle(Bundle bundle) {
        projectionOblique = false;
        projectionPerspective = false;
        projectionAxonometric = false;
        projectionFrontal = false;
        projectionHorizontal = false;
        projectionProfile = false;
        lightPoint = getStandartLightPoint(center);
        switch (bundle.getInt("label")) {
            case SETTINGS_RESET:
                setStandartParams(surfaceView);
                break;
            case SETTINGS_LIGHT:
                lightPoint = new Point3D(bundle.getDouble("lightX"),
                        bundle.getDouble("lightY"),
                        bundle.getDouble("lightZ"));
                color = bundle.getIntArray("color");
                break;
            case SETTINGS_MOVE:
                center = new Point3D(bundle.getDouble("centerX"),
                        bundle.getDouble("centerY"),
                        bundle.getDouble("centerZ"));
                radius = bundle.getDouble("radius");
                break;
            case SETTINGS_PERFOMANCE:
                polygons = bundle.getInt("polygonsCount");
                reflect = bundle.getBoolean("reflect");
                invisLines = bundle.getBoolean("invisLines");
                break;
            case SETTINGS_ROTATE:
                angleX = toRadians(bundle.getDouble("rotateX"));
                angleY = toRadians(bundle.getDouble("rotateY"));
                angleZ = toRadians(bundle.getDouble("rotateZ"));
                break;
            case SETTINGS_SCALE:
                scaleX = bundle.getDouble("scaleX");
                scaleY = bundle.getDouble("scaleY");
                scaleZ = bundle.getDouble("scaleZ");
                break;
            case PROJECTION_FRONT:
                angleX = toRadians(0);
                angleY = toRadians(0);
                angleZ = toRadians(90);
                lightPoint = getStandartLightPoint(center);

                break;
            case PROJECTION_HORIZONTAL:
                angleX = toRadians(0);
                angleY = toRadians(90);
                angleZ = toRadians(90);
                break;
            case PROJECTION_PROFILE:
                angleX = toRadians(0);
                angleY = toRadians(0);
                angleZ = toRadians(90);
                lightPoint = getStandartLightPointPr(center);
                break;
            case PROJECTION_OBLIQUE:
                projectionOblique = true;
                obliqueAlpha = bundle.getDouble("alpha");
                obliqueL = bundle.getDouble("l");
//                reflect = false;
//                invisLines = false;
                break;
            case PROJECTION_PERSPECTIVE:
                projectionPerspective = true;
                perspectiveD = bundle.getDouble("d");
                perspectiveFi = bundle.getDouble("fi");
                perspectivePsi = bundle.getDouble("psi");
                perspectiveQ = bundle.getDouble("q");
//                reflect = false;
//                invisLines = false;
                break;
            case PROJECTION_AXONOMETRIC:
                projectionAxonometric = true;
                axonometricFi = bundle.getDouble("fi");
                axonometricPsi = bundle.getDouble("psi");
//                reflect = false;
//                invisLines = false;
                break;
        }

    }

    float[] valuesAccel = new float[3];
    float[] valuesMagnet = new float[3];
    float[] valuesResult = new float[3];
    float[] valuesResult2 = new float[3];

    int rotation;

//    private void initSensors(Context context) {
//        sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
//        sensorAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        sensorMagnet = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
//
//        sensorManager.registerListener(listener, sensorAccel, SensorManager.SENSOR_DELAY_UI);
//        sensorManager.registerListener(listener, sensorMagnet, SensorManager.SENSOR_DELAY_UI);
//
//        WindowManager windowManager = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE));
//        Display display = windowManager.getDefaultDisplay();
//        rotation = display.getRotation();
//    }
//
//    SensorEventListener listener = new SensorEventListener() {
//
//        @Override
//        public void onAccuracyChanged(Sensor sensor, int accuracy) {
//        }
//
//        @Override
//        public void onSensorChanged(SensorEvent event) {
//            switch (event.sensor.getType()) {
//                case Sensor.TYPE_ACCELEROMETER:
//                    for (int i = 0; i < 3; i++) {
//                        valuesAccel[i] = event.values[i];
//                    }
//                    break;
//                case Sensor.TYPE_MAGNETIC_FIELD:
//                    for (int i = 0; i < 3; i++) {
//                        valuesMagnet[i] = event.values[i];
//                    }
//                    break;
//            }
//        }
//    };

//    float[] r = new float[9];
//    float[] inR = new float[9];
//    float[] outR = new float[9];
//
//    void getActualDeviceOrientation() {
//        SensorManager.getRotationMatrix(inR, null, valuesAccel, valuesMagnet);
//        int x_axis = SensorManager.AXIS_X;
//        int y_axis = SensorManager.AXIS_Y;
//        switch (rotation) {
//            case (Surface.ROTATION_0):
//                break;
//            case (Surface.ROTATION_90):
//                x_axis = SensorManager.AXIS_Y;
//                y_axis = SensorManager.AXIS_MINUS_X;
//                break;
//            case (Surface.ROTATION_180):
//                y_axis = SensorManager.AXIS_MINUS_Y;
//                break;
//            case (Surface.ROTATION_270):
//                x_axis = SensorManager.AXIS_MINUS_Y;
//                y_axis = SensorManager.AXIS_X;
//                break;
//            default:
//                break;
//        }
//        SensorManager.remapCoordinateSystem(inR, x_axis, y_axis, outR);
//        SensorManager.getOrientation(outR, valuesResult2);
//        valuesResult2[0] = (float) Math.toDegrees(valuesResult2[0]);
//        valuesResult2[1] = (float) Math.toDegrees(valuesResult2[1]);
//        valuesResult2[2] = (float) Math.toDegrees(valuesResult2[2]);
//        return;
//    }
//
//    void getDeviceOrientation() {
//        SensorManager.getRotationMatrix(r, null, valuesAccel, valuesMagnet);
//        SensorManager.getOrientation(r, valuesResult);
//
//        valuesResult[0] = (float) Math.toDegrees(valuesResult[0]);
//        valuesResult[1] = (float) Math.toDegrees(valuesResult[1]);
//        valuesResult[2] = (float) Math.toDegrees(valuesResult[2]);
//        return;
//    }

    public void setRunning(boolean run) {
        runFlag = run;
    }

    @Override
    public void run() {
        Canvas canvas;
        sphere = new Sphere3D(center, radius, angleX, angleY, angleZ, lightPoint, polygons, color);
        while (runFlag) {
            long now = System.currentTimeMillis();
            long elapsedTime = now - prevTime;
            if (elapsedTime > 30) {
                prevTime = now;
            }
            canvas = null;
            try {
                canvas = surfaceHolder.lockCanvas(null);
                synchronized (surfaceHolder) {
                    if (canvas != null) {

//                        canvas.drawColor(Color.CYAN, PorterDuff.Mode.CLEAR);
                        float[] valuesResultTemp = valuesResult2.clone();
//                        getDeviceOrientation();
//                        getActualDeviceOrientation();

//                        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
//                        TextPaint paint = new TextPaint();
//                        paint.setTextSize(50);
//                        canvas.drawText(String.valueOf(elapsedTime), 50, 50, paint);
//                        canvas.drawText(String.valueOf(format(valuesResult)), 50, 200, paint);
//                        canvas.drawText(String.valueOf(format(valuesResult2)), 50, 300, paint);


//                        valuesResultTemp[0] -= valuesResult2[0];
//                        valuesResultTemp[1] -= valuesResult2[1];
//                        valuesResultTemp[2] -= valuesResult2[2];
//                        if (Math.abs(valuesResultTemp[0]) < 20) center.x += valuesResultTemp[0] * 5;
//                        if (Math.abs(valuesResultTemp[1]) < 20) center.y += valuesResultTemp[1] * 5;
//                        if (Math.abs(valuesResultTemp[2]) < 20) center.z += valuesResultTemp[2] * 5;
//                        lightPoint.x = -200000;
//                        center.x = 400;
//                        angleZ += Math.toRadians(1);
//                        angleY -= Math.toRadians(1);
//                        angleX += Math.toRadians(0.2);


//                        angleX += Math.toRadians(2);
                        if (radius < 10) {
                            radius = 10;
                        }
                        sphere.update(center, radius, angleX, angleY, angleZ, lightPoint, scaleX, scaleY, scaleZ);
                        canvas.drawRGB(255, 255, 255);

                        sphere.draw(canvas);
                    }
                }
            } finally {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    private String format(float values[]) {
        return String.format("%1$.1f\t\t%2$.1f\t\t%3$.1f", values[0], values[1], values[2]);
    }

    private int deltaX;
    boolean inTouch;
    int downPI;
    int upPI;
    Point firstPointBefore;
    Point secondPointBefore;
    Point firstPointAfter;
    Point secondPointAfter;

    @Override
    public boolean onTouch(View view, MotionEvent event) {

        // событие
        int actionMask = event.getActionMasked();
        // индекс касания
        int pointerIndex = event.getActionIndex();
        // число касаний
        int pointerCount = event.getPointerCount();

        switch (actionMask) {
            case MotionEvent.ACTION_DOWN: // первое касание
                inTouch = true;
            case MotionEvent.ACTION_POINTER_DOWN: // последующие касания
                if (event.getPointerCount() == 1) {
                    firstPointBefore = new Point();
                    firstPointBefore.x = (int) event.getX(0);
                    firstPointBefore.y = (int) event.getY(0);
                }
                if (event.getPointerCount() == 2) {
                    firstPointBefore = new Point();
                    firstPointBefore.x = (int) event.getX(0);
                    firstPointBefore.y = (int) event.getY(0);
                    secondPointBefore = new Point();
                    secondPointBefore.x = (int) event.getX(1);
                    secondPointBefore.y = (int) event.getY(1);
                }
                downPI = pointerIndex;
                break;

            case MotionEvent.ACTION_UP: // прерывание последнего касания
                inTouch = false;
            case MotionEvent.ACTION_POINTER_UP: // прерывания касаний
                upPI = pointerIndex;
                break;

            case MotionEvent.ACTION_MOVE: // движение
                if (event.getPointerCount() == 1) {
                    if (projectionPerspective) {
                        firstPointAfter = new Point();
                        firstPointAfter.x = (int) event.getX(0);
                        firstPointAfter.y = (int) event.getY(0);
                        double deltaX = firstPointAfter.x - firstPointBefore.x;
                        final double deltaY = firstPointAfter.y - firstPointBefore.y;
                        int deltaMax = 500;
                        int deltaMin = -500;
                        if (!(deltaX > deltaMax || deltaY > deltaMax) &&
                                (!(deltaX < deltaMin || deltaY < deltaMin))) {
                            perspectiveFi += toRadians(deltaX * 3);
                            perspectivePsi -= toRadians(deltaY * 3);
                            if (angleY > toRadians(90)) {
                                angleY = toRadians(90);
                            }
                            if (angleY < toRadians(-90)) {
                                angleY = toRadians(-90);
                            }
                        }
                        firstPointBefore.x = (int) event.getX(0);
                        firstPointBefore.y = (int) event.getY(0);
                    }
                    if (!projectionPerspective && !projectionFrontal &&
                            !projectionProfile && !projectionAxonometric &&
                            !projectionHorizontal && !projectionOblique) {
                        firstPointAfter = new Point();
                        firstPointAfter.x = (int) event.getX(0);
                        firstPointAfter.y = (int) event.getY(0);
                        double deltaX = firstPointAfter.x - firstPointBefore.x;
                        final double deltaY = firstPointAfter.y - firstPointBefore.y;
                        int deltaMax = 500;
                        int deltaMin = -500;
                        if (!(deltaX > deltaMax || deltaY > deltaMax) &&
                                (!(deltaX < deltaMin || deltaY < deltaMin))) {
                            angleX -= toRadians(deltaX / 15);
                            angleY += toRadians(deltaY / 15);
                            if (angleY > toRadians(90)) {
                                angleY = toRadians(90);
                            }
                            if (angleY < toRadians(-90)) {
                                angleY = toRadians(-90);
                            }
                        }
                        firstPointBefore.x = (int) event.getX(0);
                        firstPointBefore.y = (int) event.getY(0);
                    }
                }
                if (event.getPointerCount() == 2) {
                    if (projectionPerspective) {
                        firstPointAfter = new Point();
                        secondPointAfter = new Point();
                        firstPointAfter.x = (int) event.getX(0);
                        firstPointAfter.y = (int) event.getY(0);
                        secondPointAfter.x = (int) event.getX(1);
                        secondPointAfter.y = (int) event.getY(1);
                        double distBetwPointsAfter = MathTools.getDistBetwTwoPoints2D(firstPointAfter, secondPointAfter);
                        double distBetwPointsBefore = MathTools.getDistBetwTwoPoints2D(firstPointBefore, secondPointBefore);
                        firstPointBefore.x = (int) event.getX(0);
                        firstPointBefore.y = (int) event.getY(0);
                        secondPointBefore.x = (int) event.getX(1);
                        secondPointBefore.y = (int) event.getY(1);
                        this.perspectiveD += (distBetwPointsAfter - distBetwPointsBefore) / 2;
                    } else {

                    firstPointAfter = new Point();
                    secondPointAfter = new Point();
                    firstPointAfter.x = (int) event.getX(0);
                    firstPointAfter.y = (int) event.getY(0);
                    secondPointAfter.x = (int) event.getX(1);
                    secondPointAfter.y = (int) event.getY(1);
                    double distBetwPointsAfter = MathTools.getDistBetwTwoPoints2D(firstPointAfter, secondPointAfter);
                    double distBetwPointsBefore = MathTools.getDistBetwTwoPoints2D(firstPointBefore, secondPointBefore);
                    firstPointBefore.x = (int) event.getX(0);
                    firstPointBefore.y = (int) event.getY(0);
                    secondPointBefore.x = (int) event.getX(1);
                    secondPointBefore.y = (int) event.getY(1);
                    this.radius += (distBetwPointsAfter - distBetwPointsBefore) / 2;
                    }
                }
                break;
        }

//        if (inTouch) {
//        }
        return true;
    }

    @Override
    public void onClick(View v) {

    }
}
