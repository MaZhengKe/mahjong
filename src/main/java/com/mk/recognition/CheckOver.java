package com.mk.recognition;

import com.mk.Screen;
import com.mk.SimilarPoint;
import com.mk.Util;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class CheckOver extends Thread {
    Screen screen;
    private final Object lock = new Object();
    private boolean pause = false;

    public void pauseThread() {
        pause = true;
    }

    public CheckOver(Screen screen) {
        this.screen = screen;
    }

    private static Mat over = Imgcodecs.imread("pic\\" + "over.png");

    @Override
    public void run() {
        pauseThread();
        while (true) {
            if (pause)
                onPause();
            Mat overMet = screen.getOverMet();
            SimilarPoint similarPoint = Util.maxPointInMat(overMet, over);
            if (similarPoint.getSimilarity() > 0.98) {
                try {
                    Util.click(1675, 983);
                    Thread.sleep(3000);
                    Util.click(1337, 417);
                    Thread.sleep(3000);
                    Util.click(1337, 600);
                    Thread.sleep(3000);
                    Util.click(1337, 600);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
