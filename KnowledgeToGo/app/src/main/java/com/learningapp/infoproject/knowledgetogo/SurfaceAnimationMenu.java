package com.learningapp.infoproject.knowledgetogo;

import android.content.Context;
import android.os.Message;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.os.Handler;


/**
 * Created by Fabian on 01.05.15.
 */
public class SurfaceAnimationMenu extends SurfaceView implements SurfaceHolder.Callback {

    /** Handle to the application context, used to e.g. fetch Drawables. */
    private Context mContext;

    /** Pointer to the text view to display "Paused.." etc. */
    private TextView mStatusText;

    /** The thread that actually draws the animation */
    private MenuBackground thread;

    public SurfaceAnimationMenu(Context context, AttributeSet attrs){
        super(context,attrs);

        // register our interest in hearing about changes to our surface
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        Handler messageHandler = new Handler() {
            @Override
            public void handleMessage(Message m) {
                //noinspection ResourceType
                mStatusText.setVisibility(m.getData().getInt("viz"));
                mStatusText.setText(m.getData().getString("text"));
            }
        };

        // create thread only; it's started in surfaceCreated()
        thread = new MenuBackground(holder, context, messageHandler);
    }

    public SurfaceAnimationMenu(Context context) {
        super(context);

        // register our interest in hearing about changes to our surface
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        Handler messageHandler = new Handler() {
            @Override
            public void handleMessage(Message m) {
                //mStatusText.setVisibility(m.getData().getInt("viz"));
                mStatusText.setText(m.getData().getString("text"));
            }
        };


        // create thread only; it's started in surfaceCreated()
        thread = new MenuBackground(holder, context, messageHandler);

    }

    /*
     * Callback invoked when the Surface has been created and is ready to be
     * used.
     */
    public void surfaceCreated(SurfaceHolder holder) {
        // start the thread here so that we don't busy-wait in run()
        // waiting for the surface to be created
            thread.setSurfaceSize(getHeight(), getWidth());
            thread.initialize();
            thread.setRunning(true);
            thread.start();

    }

    /*
     * Callback invoked when the Surface has been destroyed and must no longer
     * be touched. WARNING: after this method returns, the Surface/Canvas must
     * never be touched again!
     */
    public void surfaceDestroyed(SurfaceHolder holder) {
        // we have to tell thread to shut down & wait for it to finish, or else
        // it might touch the Surface after we return and explode
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }

    /* Callback invoked when the surface dimensions change. */
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        thread.setSurfaceSize(width, height);
    }

    public MenuBackground getThread(){
        return thread;
    }

}
