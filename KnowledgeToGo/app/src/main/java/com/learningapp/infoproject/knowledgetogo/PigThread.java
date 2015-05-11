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
import android.view.SurfaceHolder;

/**
 * Created by Fabian on 01.05.15.
 */
class PigThread extends Thread {

    public static final double mSpeed = 100;

    /* FPS */
    public static final int FPS = 6;

    /** Message handler used by thread to interact with TextView */
    private Handler mHandler;
    /** Handle to the surface manager object we interact with */
    private final SurfaceHolder mSurfaceHolder;
    /** Context of the application */
    private Context mContext;

    /** Used to figure out elapsed time between frames */
    private long mLastTime;

    /* Current state of the animation */
    private boolean mIsRunning;

    /* Toggles canvas painting */
    private boolean mDrawable;
    private final Object mDrawableLock = new Object();

    /* Size of the canvas */
    private int mCanvasWidth;
    private int mCanvasHeight;

    private TextPaint mBlack;

    /* Resources */
    private Resources mResources;

    // Image
    private Drawable image;
    private int mImageX;
    private int mImageY;

    // Background
    private Bitmap mBackgroundImage;
    private int mBackgroundImageX;
    private int mBackgroundImageY;
    private int mBackgroundImageWidth;

    // Walkcycle
    private Pig pig;


    private int mScore;

    public PigThread(SurfaceHolder surfaceHolder, Context context, Handler handler) {

        // get handles to some important objects
        mSurfaceHolder = surfaceHolder;
        mHandler = handler;
        mContext = context;

        mResources = context.getResources();
        image = mContext.getResources().getDrawable(R.drawable.logo);
        mBackgroundImage = BitmapFactory.decodeResource(mResources,
                R.drawable.boom);
    }


    /**
     * Starts the animation, setting parameters.
     */
    public void initialize() {
        synchronized (mSurfaceHolder) {
            mLastTime = System.currentTimeMillis() + 100;
            mIsRunning = true;

            mImageX = mCanvasHeight/2;
            mImageY = mCanvasWidth/2;

            mBackgroundImageX = 0;
            mBackgroundImageY = 0;

            pig = new Pig(mResources, mCanvasHeight, mCanvasWidth);

            // Score
            mScore = 0;

            mBlack = new TextPaint();
            mBlack.setColor(Color.BLACK);
            mBlack.setTextSize(40);
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
        // Move the real time clock up to now
        synchronized (mSurfaceHolder) {
            mLastTime = System.currentTimeMillis() + 100;
        }
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

        // Do nothing if mLastTime is in the future.
        // This allows the game-start to delay the start
        // by 100ms or whatever.
        if (mLastTime > now) return;

        double elapsed = (now - mLastTime) / 1000.0;

        pig.update(now);

        mImageX = mImageX < mCanvasWidth ? mImageX + (int) Math.round(mSpeed * elapsed) : - image.getIntrinsicWidth()/20;
        mImageY = mImageY < mCanvasHeight ? mImageY + (int) Math.round(mSpeed * elapsed) : - image.getIntrinsicHeight()/20;

        mBackgroundImageX = mBackgroundImageX + mBackgroundImageWidth > 0 ?
                mBackgroundImageX - (int) Math.round(mSpeed * elapsed) :
                mBackgroundImageWidth;

        mLastTime = now;
    }

    /**
     * Draws everything.
     */
    private void doDraw(Canvas canvas) {
        // Draw the background image. Operations on the Canvas accumulate
        // so this is like clearing the screen.
        canvas.drawColor(Color.rgb(160,170,240));

        canvas.drawBitmap(mBackgroundImage, mBackgroundImageX, mBackgroundImageY, null);
        canvas.drawBitmap(mBackgroundImage,
                mBackgroundImageX < 0 ?
                        mBackgroundImageX + mBackgroundImageWidth :
                        mBackgroundImageX - mBackgroundImageWidth
                , mBackgroundImageY, null);

        image.setBounds(mImageX, mImageY, mImageX + image.getIntrinsicWidth() / 20, mImageY + image.getIntrinsicHeight() / 20);
        image.draw(canvas);

        pig.draw(canvas);

        canvas.drawText("Score: "+Integer.toString(mScore),10,30,mBlack);
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
	        mBackgroundImageWidth = mBackgroundImage.getWidth() * mCanvasHeight / mBackgroundImage.getHeight();
            mBackgroundImage = Bitmap.createScaledBitmap(mBackgroundImage, mBackgroundImageWidth, mCanvasHeight, true);
    	}
    }

    public boolean getRunningState(){
        return mIsRunning;
    }

    public void setScore(int score){
        mScore = score;
    }

    public int getScore(){
        return mScore;
    }


    /**
     * Initiates Pig-Jump with
     * @param duration in ms
     */
    public void pigJump(double duration, double delay){
        pig.jump(duration,delay,System.currentTimeMillis());
    }

}
