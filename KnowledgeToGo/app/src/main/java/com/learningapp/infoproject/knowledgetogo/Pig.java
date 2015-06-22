package com.learningapp.infoproject.knowledgetogo;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

/**
 * Created by Fabian on 02.05.15.
 */
public class Pig {

    private Bitmap mWalk;

    private Rect mSourceRect;    // the rectangle to be drawn from the animation bitmap
    private Rect mDestinationRect;

    private int mCurrentFrame;    // the current frame
    private static final int mFrameCount = 5; // Count of walking pictures, the first pictures
    private static final int mAdditionalFrames = 3;
    private static final int mWalkStartFrame = 3;
    private static final int mJumpingFrame = 5; // Needs to lie outside of the walk cycle!
    private static final int mJumpStartFrame = 0; // Needs to lie within the walk cycle!
    private static final int mFailFrame = 6;
    private static final int mFailFrameTwo = 7;

    private long mFrameTicker;    // the time of the last frame update
    private int mFramePeriod;    // milliseconds between each frame (1000/fps)

    private int mWalkWidth;    // the width of the sprite to calculate the cut out rectangle
    private int mWalkHeight;    // the height of the sprite

    private int mWalkX;                // the X coordinate of the object (top left of the image)
    private int mWalkY;                // the Y coordinate of the object (top left of the image) initially
    private int mWalkYCurrent;     // the current Y coordinate
    private float mRotation;

    // For the jump cycle
    private boolean mJumping;
    private static final double mJumpHeight = 100;
    private static final int mJumpRotation = 20;

    // For the fail cycle
    private boolean mFailing;


    private double mActionDuration;
    private double mActionBegin;
    private double mActionTime;
    private double mActionAt;

    public Pig(Resources res, int mCanvasHeight, int mCanvasWidth) {

        mWalk = BitmapFactory.decodeResource(res, R.drawable.schwein);

        mCurrentFrame = mWalkStartFrame;

        mWalkWidth = mWalk.getWidth() / (mFrameCount + mAdditionalFrames); // + for additional Frames
        mWalkHeight = mWalk.getHeight();
        mWalkX = mCanvasHeight / 2 - mWalkHeight / 2;
        mWalkY = mCanvasWidth / 2 - mWalkWidth / 2 + 26;
        mWalkYCurrent = mWalkY;
        mRotation = 0;

        mSourceRect = new Rect(0, 0, mWalkWidth, mWalkHeight);
        mDestinationRect = new Rect(mWalkX, mWalkY, mWalkX + mWalkWidth, mWalkY + mWalkHeight);

        mFramePeriod = 1000 / PigThread.FPS;
        mFrameTicker = 0l;

        mFailing = false;
        mJumping = false;
        mActionTime = 0;

    }

    public void update(long now) {

        // Begins Jump - for a fluent animation wait for the starting picture
        if (mJumping && mCurrentFrame == mJumpStartFrame && now > mActionAt) {
            mCurrentFrame = mJumpingFrame;
            mActionBegin = now;
        }
        // During Jump
        else if (mJumping && mCurrentFrame == mJumpingFrame && mActionTime < mActionDuration) {
            mActionTime = now - mActionBegin;
            mWalkYCurrent = mWalkY - calculateJumpParabola(mActionTime, mActionDuration, mJumpHeight);
            mRotation = (float) ((mActionDuration - mActionTime) / mActionDuration * mJumpRotation - mJumpRotation / 2);
        }
        // Jump ends
        else if (mJumping && mCurrentFrame == mJumpingFrame && mActionTime >= mActionDuration) {
            mJumping = false;
            mRotation = 0;
            mCurrentFrame = mWalkStartFrame;
        }

        // Begins Fail
        else if (mFailing && now > mActionAt && mActionTime <= mActionDuration / 3) {
            mActionTime = now - mActionAt;
            mCurrentFrame = mFailFrame;
        }
        // Failing - Pig lies
        else if (mFailing && mActionTime > mActionDuration / 3 && mActionTime < mActionDuration) {
            mActionTime = now - mActionAt;
            mCurrentFrame = mFailFrameTwo;
        }
        // Failing Ends
        else if (mFailing && mActionTime >= mActionDuration) {
            mFailing = false;
            mCurrentFrame = mWalkStartFrame;
        }

        // Walking
        else {
            if (now > mFrameTicker + mFramePeriod) {
                mFrameTicker = now;
                // increment the frame
                mCurrentFrame++;
                if (mCurrentFrame >= mFrameCount) {
                    mCurrentFrame = 0;
                }
            }
        }

        // define the rectangle to cut out sprite
        mSourceRect.left = mCurrentFrame * mWalkWidth;
        mSourceRect.right = mSourceRect.left + mWalkWidth;

        mDestinationRect.set(mWalkX, mWalkYCurrent, mWalkX + mWalkWidth, mWalkYCurrent + mWalkHeight);

    }

    public void draw(Canvas canvas) {
        canvas.save();
        canvas.rotate(-mRotation, mWalkX + mWalkWidth / 2, mWalkYCurrent + mWalkHeight / 2);
        canvas.drawBitmap(mWalk, mSourceRect, mDestinationRect, null);
        canvas.restore();
    }

    /**
     * Initiates Pig-Jump with
     * @param jumpDuration in ms
     */
    public void jump(double jumpDuration, double delay, double now) {
        if (!mJumping) {
            mJumping = true;
            mActionDuration = jumpDuration;
            mActionAt = now + delay;
            mActionTime = 0;
        }
    }

    public void fail(double delay, double now, double duration){
        if (!mFailing) {
            mFailing = true;
            mActionDuration = duration;
            mActionAt = now + delay;
            mActionTime = 0;
        }
    }

    /**
     * Calculates y of a parabola starting 0,0 with
     * @param jumpTime x-Axis
     * @param duration the length 0 -> 0
     * @param height the highest point at x = duration / 2
     * @return y-Axis
     */
    public static int calculateJumpParabola(double jumpTime, double duration, double height) {
        return (int) Math.round(-(4 * height / (duration * duration)) * Math.pow((jumpTime - duration / 2), 2) + height);
    }
}
