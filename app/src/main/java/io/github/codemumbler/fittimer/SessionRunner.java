package io.github.codemumbler.fittimer;

import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.widget.TextView;

import io.github.codemumbler.fittimer.model.FitCountDownTimer;
import io.github.codemumbler.fittimer.model.Session;
import io.github.codemumbler.fittimer.model.SoundMaker;

public class SessionRunner {

    private Session session;
    private TextView contentDisplay;
    private TextView timerDisplay;
    private FitCountDownTimer timer;
    private boolean isPaused = true;
    private boolean transition = false;
    private Callback completionCallback;
    private Callback onReady;
    private TextToSpeechWrapper textToSpeech;

    SessionRunner(final Session session) {
        this.session = session;
    }

    public void setContentDisplay(TextView contentDisplay) {
        this.contentDisplay = contentDisplay;
    }

    public TextView getContentDisplay() {
        return contentDisplay;
    }

    public void setTimerDisplay(TextView timerDisplay) {
        this.timerDisplay = timerDisplay;
    }

    public TextView getTimerDisplay() {
        return timerDisplay;
    }

    public void updateTimerText(Long remainingTime) {
        getTimerDisplay().setText(String.format("%.1f", remainingTime/1000.0));
    }

    public void next() {
        if (session.next()) {
            startPose();
        } else {
            complete();
        }
    }

    private void startPose() {
        if (timer != null) {
            timer.cancel();
        }
        if (session.hasTransitions() && transition) {
            getContentDisplay().setText(" ");
            timer = new FitCountDownTimer(session.getTransitionDuration(),
                    new AndroidFitHandler(this));
            transition = false;
        } else if (!session.hasTransitions() || !transition) {
            getContentDisplay().setText(session.getCurrentPose().getName());
            textToSpeech.speak(session.getCurrentPose().getName());
            timer = new FitCountDownTimer(session.getCurrentPose().getDuration(),
                    new AndroidFitHandler(this));
            transition = true;
        }
        timer.start();
        isPaused = false;
    }

    public boolean complete() {
        if (timer != null) {
            timer.cancel();
        }
        textToSpeech.shutdown();
        if (this.completionCallback != null) {
            completionCallback.execute();
        }
        return session.complete();
    }

    public void init(Context context) {
        this.textToSpeech = new TextToSpeechWrapper(onReady, context);
    }

    public void start() {
        timer = new FitCountDownTimer(3000, new StartFitHandler(this));
        timer.start();
        transition = false;
        isPaused = false;
    }

    private void pause() {
        if (timer != null) {
            timer.pause();
        }
        isPaused = true;
    }

    private void resume() {
        if (timer != null) {
            timer.start();
            isPaused = false;
        }
    }

    public void pausePlay() {
        if (!isPaused) {
            pause();
        } else {
            resume();
        }
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void onComplete(Callback callback) {
        completionCallback = callback;
    }

    public void onReady(Callback callback) { onReady = callback; }

    public void prev() {
        if (session.prev()) {
            transition = false;
            startPose();
        }
    }

    public void endPoseSound() {
        SoundMaker.startTone(ToneGenerator.TONE_PROP_BEEP2);
    }

    public void tickSound() {
        SoundMaker.startTone(ToneGenerator.TONE_PROP_BEEP);
    }

    public interface Callback {

        void execute();
    }
}
