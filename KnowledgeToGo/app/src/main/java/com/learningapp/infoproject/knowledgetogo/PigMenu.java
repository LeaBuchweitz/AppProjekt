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
public class PigMenu {

    private Bitmap mWalk;

    private Rect mSourceRect;    // the rectangle to be drawn from the animation bitmap
    private Rect mDestinationRect;

    private int mCurrentFrame;    // the current frame
    private static final int mFrameCount = 2; // Count of walking pictures, the first pictures
    private static final int mAdditionalFrames = 6;

    private long mFrameTicker;    // the time of the last frame update
    private int mFramePeriod;    // milliseconds between each frame (1000/fps)

    private int mWalkWidth;    // the width of the sprite to calculate the cut out rectangle
    private int mWalkHeight;    // the height of the sprite

    private int mWalkX;                // the X coordinate of the object (top left of the image)
    private int mWalkY;                // the Y coordinate of the object (top left of the image) initially

    private boolean updated;

    private double mActionDuration;
    private double mActionBegin;
    private double mActionTime;
    private double mActionAt;

    public PigMenu(Resources res, int mCanvasHeight, int mCanvasWidth) {

        mWalk = BitmapFactory.decodeResource(res, R.drawable.schwein);

        mCurrentFrame = 0;

        mWalkWidth = mWalk.getWidth() / (mFrameCount + mAdditionalFrames); // + for additional Frames
        mWalkHeight = mWalk.getHeight();
        mWalkX = mCanvasHeight / 2 - mWalkHeight / 2;
        mWalkY = mCanvasWidth / 2 - mWalkWidth / 2 + 26;

        mSourceRect = new Rect(0, 0, mWalkWidth, mWalkHeight);
        mDestinationRect = new Rect(mWalkX, mWalkY, mWalkX + mWalkWidth, mWalkY + mWalkHeight);

        mFramePeriod = 1000 / PigThread.FPS;
        mFrameTicker = 0l;

        mActionTime = 0;

        mActionAt = 0;

    }

    public void update(long now) {

        if (mActionAt < now && now - mActionAt < 200) {
            mCurrentFrame = 1;
            updated = false;
        } else if (!updated) {
            mActionAt = now + ((int) (Math.random() * 2000 + 500));
            mCurrentFrame = 0;
            updated = true;
        }

        // define the rectangle to cut out sprite
        mSourceRect.left = mCurrentFrame * mWalkWidth;
        mSourceRect.right = mSourceRect.left + mWalkWidth;

        mDestinationRect.set(mWalkX, mWalkY, mWalkX + mWalkWidth, mWalkY + mWalkHeight);

    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(mWalk, mSourceRect, mDestinationRect, null);
    }
}
