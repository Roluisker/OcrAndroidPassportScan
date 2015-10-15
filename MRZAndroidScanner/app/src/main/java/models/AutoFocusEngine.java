package models;

import android.hardware.Camera;

import java.util.Timer;
import java.util.TimerTask;


@SuppressWarnings("deprecation")
public class AutoFocusEngine implements Camera.AutoFocusCallback {

    private static final long AUTO_FOCUS_INTERVAL_MS = 14000;

    private Timer timer;
    private Camera camera;
    private TimerTask timerTask;

    private AutoFocusEngine(Camera camera) {
        this.camera = camera;
        this.timer = new Timer();
    }


    static public AutoFocusEngine New(Camera camera) {
        return new AutoFocusEngine(camera);
    }

    private boolean running;

    public boolean isRunning() {
        return running;
    }

    public void start() {
        work();
        running = true;
    }

    public void stop() {

        camera.cancelAutoFocus();

        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }

        running = false;

    }

    private void work() {
        camera.autoFocus(this);
    }

    @Override
    public void onAutoFocus(boolean success, Camera camera) {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                work();
            }
        };
        timer.schedule(timerTask, AUTO_FOCUS_INTERVAL_MS);
    }
}
