package com.mk;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

public class Screen extends Thread {

    private Mat screenCaptureMat;

//    static {
//        refresh();
//    }


    Screen() {
        refresh();
    }

    @Override
    public void run() {
        synchronized (Screen.class) {
            refresh();
        }
    }

    void refresh() {
        BufferedImage screenCaptureImage = Util.getScreenCapture();
        screenCaptureMat = Util.bufImg2Mat(screenCaptureImage);
    }

    public synchronized Mat getReachMet() {
        return screenCaptureMat.submat(750, 880, 650, 1350);
    }
    public synchronized Mat getOverMet() {
        return screenCaptureMat.submat(927, 1018, 1600, 1750);
    }
    public synchronized Mat getButtonMet() {
        return screenCaptureMat.submat(570, 633, 133, 188);
    }

    public synchronized Mat getHandMet() {
        return screenCaptureMat.submat(860, 1028, 320, 1785);
    }

    public synchronized List<Mat> getPoolMats() {
        Mat correctedMat = correct(screenCaptureMat);
        return getPoolMats(correctedMat);
    }

    public synchronized List<Mat> getFLMats() {
        Mat correctedMat = correct(screenCaptureMat);
        return getFLMats(correctedMat);
    }


    private static List<Mat> getPoolMats(Mat source) {
        Mat mat1 = source.submat(397, 547, 825, 1074);
        Mat mat2 = source.submat(548, 816, 685, 835);
        Mat mat3 = source.submat(527, 778, 1082, 1232);
        Mat mat01 = new Mat();
        Mat mat02 = new Mat();
        Mat mat03 = new Mat();
        Core.flip(mat1, mat01, -1);
        Core.transpose(mat2, mat02);
        Core.flip(mat02, mat2, 0);
        Core.transpose(mat3, mat03);
        Core.flip(mat03, mat3, 1);
        return Arrays.asList(mat01, mat2, mat3,
                source.submat(788, 938, 842, 1091));
    }


    private static List<Mat> getFLMats(Mat source) {
//        BufferedImage bufferedImage = Mat2BufImg(source);
        Mat mat1 = source.submat(239, 322, 520, 1010);
//        bufferedImage = Mat2BufImg(mat1);
        Mat mat2 = source.submat(580, 1077, 490, 570);
//        bufferedImage = Mat2BufImg(mat2);
        Mat mat3 = source.submat(247, 746, 1360, 1435);
//        bufferedImage = Mat2BufImg(mat3);
        Mat mat01 = new Mat();
        Mat mat02 = new Mat();
        Mat mat03 = new Mat();
        Core.flip(mat1, mat01, -1);
        Core.transpose(mat2, mat02);
        Core.flip(mat02, mat2, 0);
        Core.transpose(mat3, mat03);
        Core.flip(mat03, mat3, 1);
        return Arrays.asList(mat01, mat2, mat3,
                source.submat(1046, 1122, 913, 1402));
    }

    private static Mat correct(Mat src) {

        MatOfPoint2f srcTri = new MatOfPoint2f((
                new Point(0, 0)),
                new Point(src.cols() - 1, 0),
                new Point(0, src.rows() - 1),
                new Point(src.cols() - 1, src.rows() - 1));
        MatOfPoint2f dstTri = new MatOfPoint2f((
                new Point(0, 0)),
                new Point(src.cols() - 1, 0),
                new Point(src.cols() * 0.215, src.rows() * 1.09),
                new Point(src.cols() * 0.785, src.rows() * 1.09));

        final Mat perspectiveTransform = Imgproc.getPerspectiveTransform(srcTri, dstTri);

        Mat dst = new Mat();
        Imgproc.warpPerspective(src, dst, perspectiveTransform, new Size(src.size().width, src.size().height * 1.09));
        return dst;
    }

    public void cancel() {
        interrupt();
    }
}
