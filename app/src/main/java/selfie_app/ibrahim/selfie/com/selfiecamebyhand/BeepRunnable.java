package selfie_app.ibrahim.selfie.com.selfiecamebyhand;

import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

public final class BeepRunnable implements Runnable {
    private final MediaPlayer mediaPlayer;
    private final TextView view;
    private final int repeats;
    private final int interval;
    private int currentRepeat;

    public BeepRunnable(@NonNull TextView view, int repeats, int interval) {
        this.view = view;
        mediaPlayer = MediaPlayer.create(this.view.getContext(), R.raw.camerafocus);
        this.repeats = repeats;
        this.interval = interval;
    }

    @Override
    public void run() {
        mediaPlayer.start();
        if (currentRepeat < repeats) {
            // set to beep again
            currentRepeat = currentRepeat + 1;
            view.postDelayed(this, interval);
            view.setText(String.valueOf(repeats));
        }
        else {
            // beep is over, just reset the counter
            reset();
        }
    }

    public void reset() {
        currentRepeat = 0;
    }

    public void destroy() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }

        mediaPlayer.release();
        view.removeCallbacks(this);
    }
}
