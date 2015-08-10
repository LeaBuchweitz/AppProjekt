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

    /* Timer */
    private double mTimer;

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
    private TextPaint mWhite;

    /* Resources */
    private Resources mResources;

    // Heart
    private Drawable mHeart;

    // Sky
    private Bitmap mSky;

    // Clouds
    private Bitmap mClouds;
    private int mCloudsX;
    private int mCloudsY;

    // Background
    private Bitmap mBackgroundImage;
    private int mBackgroundImageX;
    private int mBackgroundImageY;
    private int mBackgroundImageWidth;

    // Huegel
    private Bitmap mBackground1;
    private int mBackground1X;
    private int mBackground1Y;
    private int mBackground1Width;

    // Mountains
    private Bitmap mBackground2;
    private int mBackground2X;
    private int mBackground2Y;
    private int mBackground2Width;

    // Schild
    private Bitmap mSchild;

    // Brett
//    private Bitmap mBrett;


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
        mSky = BitmapFactory.decodeResource(mResources, R.drawable.sky);
        mClouds = BitmapFactory.decodeResource(mResources, R.drawable.clouds);
        mBackgroundImage = BitmapFactory.decodeResource(mResources,
                R.drawable.boom2);
        mBackground1 = BitmapFactory.decodeResource(mResources,
                R.drawable.huegel);
        mBackground2 = BitmapFactory.decodeResource(mResources,
                R.drawable.berge);
        mSchild = BitmapFactory.decodeResource(mResources, R.drawable.schild);
       // mBrett = BitmapFactory.decodeResource(mResources, R.drawable.brett);
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
            mStoneY = (int) Math.round(mCanvasHeight * 0.07) + 50;
            mFailing = false;

            mCloudsX = 500;
            mCloudsY = (int) Math.round(mCanvasHeight * 0.06);

            mBackgroundImageX = 0;
            mBackgroundImageY = 0;

            mBackground1X = 0;
            mBackground1Y = (int) Math.round(mCanvasHeight * 0.02);
            mBackground1Width = mBackground1.getWidth();

            mBackground2X = 0;
            mBackground2Y = (int) Math.round(mCanvasHeight * 0.07);
            mBackground2Width = mBackground2.getWidth();

            pig = new Pig(mResources, mCanvasHeight, mCanvasWidth);

            // Score
            mScore = 0;
            mLifes = 3;

            mBlack = new TextPaint();
            mBlack.setColor(Color.BLACK);
            mBlack.setTextSize(40);

            mWhite = new TextPaint();
            mWhite.setColor(Color.WHITE);
            mWhite.setTextSize(21);
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

        mTimer += elapsed;

        pig.update(now);

        if (mFailing) {
            if (mStoneX > mCanvasWidth/2 + 35) {
                mStoneX = mStoneX - (int) Math.round(mSpeed * 3 * elapsed  * (((float) mCanvasWidth)/1080));
            } else if (mActionBegin + 3000 > now) {
                mSpeed = 0;
                pig.fail(0, System.currentTimeMillis(), mActionBegin + 3000 - now);
            } else {
                mSpeed = 100;
                mFailing = false;
                mStoneX = mCanvasWidth + 1;
            }
        }

        if (mJumping) {
            if (mStoneX + 300 > 0) {
                mStoneX = mStoneX - (int) Math.round(mSpeed * 3 * elapsed * (((float) mCanvasWidth)/1080));
            } else {
                mJumping = false;
                mStoneX = mCanvasWidth + 1;
            }
        }

        mCloudsX = mCloudsX + mBackgroundImageWidth > 0 ?
                mCloudsX - (int) Math.round(mSpeed * 0.4 * elapsed) :
                mBackgroundImageWidth;

        mBackgroundImageX = mBackgroundImageX + mBackgroundImageWidth > 0 ?
                mBackgroundImageX - (int) Math.round(mSpeed * elapsed) :
                mBackgroundImageWidth;

        mBackground1X = mBackground1X + mBackground1Width > 0 ?
                mBackground1X - (int) Math.round(mSpeed * 0.5 * elapsed) :
                mBackground1Width;

        mBackground2X = mBackground2X + mBackground2Width > 0 ?
                mBackground2X - (int) Math.round(mSpeed * 0.3 * elapsed) :
                mBackground2Width;

        mLastTime = now;
    }

    /**
     * Draws everything.
     */
    private void doDraw(Canvas canvas) {
        // Draw the background image. Operations on the Canvas accumulate
        // so this is like clearing the screen.
        canvas.drawBitmap(mSky, 0, 0, null);

        canvas.drawBitmap(mBackground2, mBackground2X, mBackground2Y, null);
        canvas.drawBitmap(mBackground2,
                mBackground2X < 0 ?
                        mBackground2X + mBackground2Width :
                        mBackground2X - mBackground2Width
                , mBackground2Y, null);


        canvas.drawBitmap(mClouds, mCloudsX, mCloudsY, null);

        canvas.drawBitmap(mBackground1, mBackground1X, mBackground1Y, null);
        canvas.drawBitmap(mBackground1,
                mBackground1X < 0 ?
                        mBackground1X + mBackground1Width :
                        mBackground1X - mBackground1Width
                , mBackground1Y, null);

        canvas.drawBitmap(mBackgroundImage, mBackgroundImageX, mBackgroundImageY, null);
        canvas.drawBitmap(mBackgroundImage,
                mBackgroundImageX < 0 ?
                        mBackgroundImageX + mBackgroundImageWidth :
                        mBackgroundImageX - mBackgroundImageWidth
                , mBackgroundImageY, null);


        canvas.drawBitmap(mStone, mStoneX, mStoneY, null);

        pig.draw(canvas);

        if (!mJumping && !mFailing) {
            canvas.drawBitmap(mSchild,mCanvasWidth - 90, (int) Math.round(mCanvasHeight * 0.55), null);
            canvas.drawText(Integer.toString(30*4-((int) Math.round(mTimer*4)))+" m", mCanvasWidth - 82, (int) Math.round(mCanvasHeight * 0.65), mWhite);
        }

        //canvas.drawBitmap(mBrett, -2, -5, null);
        //canvas.drawText("Score: "+Integer.toString(mScore),10,40,mWhite);


        for (int i = 0; i < mLifes; i++) {
            mHeart.setBounds(10 + i * 40, 15, 40 + i * 40, 45 );
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
        if (!mFailing && !mJumping) {
            mJumping = true;
            mActionBegin = System.currentTimeMillis();
            pig.jump(1700, 700, System.currentTimeMillis());
        }
        mTimer = 0;
    }

    public void pigFail(){
        if (!mFailing && !mJumping) {
            mFailing = true;
            mActionBegin = System.currentTimeMillis();
        }
        mTimer = 0;
        mLifes--;
    }

    public void resetTimer() {
        mTimer = 0;
    }
}
