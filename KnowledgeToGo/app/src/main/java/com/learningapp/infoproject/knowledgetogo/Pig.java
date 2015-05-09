package com.learningapp.infoproject.knowledgetogo;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Created by Fabian on 02.05.15.
 */
public class Pig {

    private Bitmap mWalk;

    private Rect mSourceRect;	// the rectangle to be drawn from the animation bitmap
    private Rect mDestinationRect;
    private int mFrameCount;		// number of frames in animation
    private int mCurrentFrame;	// the current frame
    private long mFrameTicker;	// the time of the last frame update
    private int mFramePeriod;	// milliseconds between each frame (1000/fps)

    private int mWalkWidth;	// the width of the sprite to calculate the cut out rectangle
    private int mWalkHeight;	// the height of the sprite

    private int mWalkX;				// the X coordinate of the object (top left of the image)
    private int mWalkY;				// the Y coordinate of the object (top left of the image)

    public Pig(Resources res, int mCanvasHeight, int mCanvasWidth){

        mWalk = BitmapFactory.decodeResource(res, R.drawable.walk);

        mCurrentFrame = 0;
        mFrameCount = 5;
        mWalkWidth = mWalk.getWidth() / mFrameCount;
        mWalkHeight = mWalk.getHeight();
        mWalkX = mCanvasHeight / 2 - mWalkHeight / 2;
        mWalkY = mCanvasWidth / 2 - mWalkWidth / 2;
        mSourceRect = new Rect(0, 0, mWalkWidth, mWalkHeight);
        mDestinationRect = new Rect(mWalkX, mWalkY, mWalkX + mWalkWidth, mWalkY + mWalkHeight);
        mFramePeriod = 1000 / PigThread.FPS;
        mFrameTicker = 0l;

    }

    public void update(long now) {

        if (now > mFrameTicker + mFramePeriod) {
            mFrameTicker = now;
            // increment the frame
            mCurrentFrame++;
            if (mCurrentFrame >= mFrameCount) {
                mCurrentFrame = 0;
            }
        }
        // define the rectangle to cut out sprite
        this.mSourceRect.left = mCurrentFrame * mWalkWidth;
        this.mSourceRect.right = this.mSourceRect.left + mWalkWidth;

    }

    public void draw(Canvas canvas) {

        canvas.drawBitmap(mWalk, mSourceRect, mDestinationRect, null);

    }
}
