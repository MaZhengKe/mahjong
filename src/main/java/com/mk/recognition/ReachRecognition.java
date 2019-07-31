package com.mk.recognition;

import com.mk.Screen;
import com.mk.SimilarPoint;
import com.mk.Util;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;


public class ReachRecognition extends AbstractRecognition<Point> {

    private static Mat lizhi;

    private Screen screen;

    public ReachRecognition(Screen screen) {
        this.screen = screen;
    }
    static {
        final String picPath = "pic\\";
        lizhi = Imgcodecs.imread(picPath + "lizhi.png");
    }

    @Override
    public Point call() {
        Mat reachMet = screen.getReachMet();
        SimilarPoint similarPoint = Util.maxPointInMat(reachMet, lizhi);

        if (similarPoint.getSimilarity() > 0.97) {
            Point point = similarPoint.getPoint();
            point.x += 750;
            point.y += 800;
            return point;
        }
        return null;
    }
}
