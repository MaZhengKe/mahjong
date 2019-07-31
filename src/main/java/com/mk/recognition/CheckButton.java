package com.mk.recognition;

import com.mk.Screen;
import com.mk.SimilarPoint;
import com.mk.Util;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class CheckButton extends Thread {
    Screen screen;
    private final Object lock = new Object();
    private boolean pause = false;

    public void pauseThread() {
        pause = true;
    }

    public CheckButton(Screen screen) {
        this.screen = screen;
    }

    private static Mat button = Imgcodecs.imread("pic\\" + "hu.png");

    @Override
    public void run() {
        pauseThread();
        while (true) {
            if (pause)
                onPause();
            Mat buttonMet = screen.getButtonMet();
            SimilarPoint similarPoint = Util.maxPointInMat(buttonMet, button);
            if (similarPoint.getSimilarity() > 0.98) {
                try {
                    Thread.sleep(2000);
                    Util.click(162, 599);
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Util.click(162, 655);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void onPause() {
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void resumeThread() {
        pause = false;
        synchronized (lock) {
            lock.notify();
        }
    }

}
