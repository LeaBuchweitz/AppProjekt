package com.learningapp.infoproject.knowledgetogo;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextPaint;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * Created by Fabian on 01.05.15.
 */
class MenuBackground extends Thread {

    private double mSpeed;

    /* Timer */
    private double mTimer;

    private double mLastTime;

    /** Handle to the surface manager object we interact with */
    private final SurfaceHolder mSurfaceHolder;

    private Context mContext;

    /* Current state of the animation */
    private boolean mIsRunning;

    /* Toggles canvas painting */
    private boolean mDrawable;
    private final Object mDrawableLock = new Object();

    /* Size of the canvas */
    private int mCanvasWidth;
    private int mCanvasHeight;

    /* Resources */
    private Resources mResources;

    // Clouds
    private Bitmap mClouds;
    private int mCloudsX;
    private int mCloudsY;

    // Background
    private Bitmap mBackgroundImage;
    private int mBackgroundImageX;
    private int mBackgroundImageY;
    private int mBackgroundImageWidth;

    // Walkcycle
    private PigMenu pig;

    public MenuBackground(SurfaceHolder surfaceHolder, Context context, Handler handler) {

        // get handles to some important objects
        mSurfaceHolder = surfaceHolder;
        mContext = context;

        mResources = context.getResources();
        mClouds = BitmapFactory.decodeResource(mResources, R.drawable.clouds);
        mBackgroundImage = BitmapFactory.decodeResource(mResources,
                R.drawable.background2);
    }


    /**
     * Starts the animation, setting parameters.
     */
    public void initialize() {
        synchronized (mSurfaceHolder) {
            mSpeed = 100;

            mIsRunning = true;

            mCloudsX = 500;
            mCloudsY = (int) Math.round(mCanvasHeight * 0.05);

            mBackgroundImageX = 0;
            mBackgroundImageY = 0;

            mLastTime = 0;

            pig = new PigMenu(mResources, (int) Math.round(mCanvasHeight * 0.2), mCanvasWidth);

        }
    }

    /**
     * Pauses the animation.
     */
    public void pause() {
        synchronized (mSurfaceHolder) {
            if (mIsRunning) mIsRunning = false;
        }
    }

    /**
     * Resumes from a pause.
     */
    public void unpause() {
        mIsRunning = true;
    }

    @Override
    public void run() {
        while (mDrawable) {
            Canvas c = null;
            try {
                c = mSurfaceHolder.lockCanvas(null);
                synchronized (mSurfaceHolder) {
                    if (mIsRunning) update();
                    // Critical section. Do not allow mDrawable to be set false until
                    // we are sure all canvas draw operations are complete.
                    //
                    // If mDrawable has been toggled false, inhibit canvas operations.
                    synchronized (mDrawableLock) {
                        if (mDrawable) doDraw(c);
                    }
                }
            } finally {
                // do this in a finally so that if an exception is thrown
                // during the above, we don't leave the Surface in an
                // inconsistent state
                if (c != null) {
                    mSurfaceHolder.unlockCanvasAndPost(c);
                }
            }
        }
    }


    /**
     * Looks what happens next.
     */
    private void update() {
        long now = System.currentTimeMillis();

        double elapsed = (now - mLastTime) / 1000.0;

        mTimer += elapsed;

        pig.update(now);

        mCloudsX = mCloudsX + mBackgroundImageWidth > 0 ?
                mCloudsX - (int) Math.round(mSpeed * 0.4 * elapsed) :
                mBackgroundImageWidth;

    }

    /**
     * Draws everything.
     */
    private void doDraw(Canvas canvas) {
        // Draw the background image. Operations on the Canvas accumulate
        // so this is like clearing the screen.

        canvas.drawBitmap(mBackgroundImage, mBackgroundImageX, mBackgroundImageY, null);

        canvas.drawBitmap(mClouds, mCloudsX, mCloudsY, null);

        pig.draw(canvas);
    }

    /**
     * Used to signal the thread whether it should be running or not.
     * Passing true allows the thread to run; passing false will shut it
     * down if it's already running. Calling start() after this was most
     * recently called with false will result in an immediate shutdown.
     *
     * @param b true to run, false to shut down
     */
    public void setRunning(boolean b) {
        // Do not allow mDrawable to be modified while any canvas operations
        // are potentially in-flight. See doDraw().
        synchronized (mDrawableLock) {
            mDrawable = b;
        }
    }

    /* Callback invoked when the surface dimensions change. */
    public void setSurfaceSize(int width, int height) {
        // synchronized to make sure these all change atomically
        synchronized (mSurfaceHolder) {
            mCanvasWidth = width;
            mCanvasHeight = height;

            // don't forget to resize the background image
            mBackgroundImageWidth = Math.round(mBackgroundImage.getWidth() * mCanvasHeight / mBackgroundImage.getHeight());
            mBackgroundImage = Bitmap.createScaledBitmap(mBackgroundImage, mBackgroundImageWidth, mCanvasHeight, true);
        }
    }

    public boolean getRunningState(){
        return mIsRunning;
    }


    public void resetTimer() {
        mTimer = 0;
    }
}
