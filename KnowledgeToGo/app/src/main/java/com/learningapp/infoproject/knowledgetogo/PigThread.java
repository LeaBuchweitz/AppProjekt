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
class PigThread extends Thread {

    private double mSpeed;

    /* FPS */
    public static final int FPS = 8;

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

    // Heart
    private Drawable mHeart;

    // Background
    private Bitmap mBackgroundImage;
    private int mBackgroundImageX;
    private int mBackgroundImageY;
    private int mBackgroundImageWidth;

    // Stone
    private Bitmap mStone;
    private int mStoneX;
    private int mStoneY;
    private boolean mFailing;
    private boolean mJumping;
    private double mActionBegin;

    // Walkcycle
    private Pig pig;

    private int mLifes;
    private int mScore;

    public PigThread(SurfaceHolder surfaceHolder, Context context, Handler handler) {

        // get handles to some important objects
        mSurfaceHolder = surfaceHolder;
        mHandler = handler;
        mContext = context;

        mResources = context.getResources();
        mStone = BitmapFactory.decodeResource(mResources, R.drawable.stone);
        mBackgroundImage = BitmapFactory.decodeResource(mResources,
                R.drawable.boom2);
        mHeart = mResources.getDrawable(R.drawable.heart);
    }


    /**
     * Starts the animation, setting parameters.
     */
    public void initialize() {
        synchronized (mSurfaceHolder) {
            mSpeed = 100;

            mLastTime = System.currentTimeMillis() + 100;
            mIsRunning = true;

            mStoneX = mCanvasWidth + 1;
            mStoneY = 90;
            mFailing = false;

            mBackgroundImageX = 0;
            mBackgroundImageY = 0;

            pig = new Pig(mResources, mCanvasHeight, mCanvasWidth);

            // Score
            mScore = 0;
            mLifes = 3;

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

        if (mFailing) {
            if (mActionBegin + 2000 > now) {
                mStoneX = mStoneX - (int) Math.round(mSpeed * 2.3 * elapsed);
            } else if (mActionBegin + 3000 > now) {
                mSpeed = 0;
            } else {
                mSpeed = 100;
                mFailing = false;
                mStoneX = mCanvasWidth + 1;
            }
        }

        if (mJumping) {
            if (mStoneX + 300 > 0) {
                mStoneX = mStoneX - (int) Math.round(mSpeed * 3 * elapsed);
            } else {
                mJumping = false;
                mStoneX = mCanvasWidth + 1;
            }
        }

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

        canvas.drawBitmap(mStone, mStoneX, mStoneY, null);

        pig.draw(canvas);

        canvas.drawText("Score: "+Integer.toString(mScore),10,80,mBlack);

        for (int i = 0; i < mLifes; i++) {
            mHeart.setBounds(10 + i * 40, 20, 40 + i * 40, 50 );
            mHeart.draw(canvas);
        }
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

            // Sonst ist der Stein im Bild
            mStoneX = mCanvasWidth + 1;

            // don't forget to resize the background image
	        mBackgroundImageWidth = Math.round(mBackgroundImage.getWidth() * mCanvasHeight / mBackgroundImage.getHeight());
            mBackgroundImage = Bitmap.createScaledBitmap(mBackgroundImage, mBackgroundImageWidth, mCanvasHeight, true);
    	}
    }

    public boolean getRunningState(){
        return mIsRunning;
    }

    public void setScore(int score){
        mScore = score;
    }

    public void pigJump(){
        mJumping = true;
        mActionBegin = System.currentTimeMillis();
        pig.jump(2000,1000,System.currentTimeMillis());
    }

    public void pigFail(){
        mFailing = true;
        mActionBegin = System.currentTimeMillis();
        pig.fail(2000, System.currentTimeMillis());
        mLifes--;
    }

}
