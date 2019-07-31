package com.mk;

import org.opencv.core.Point;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import static org.opencv.imgproc.Imgproc.ADAPTIVE_THRESH_MEAN_C;

public class Util {

    public static final Robot robot = initRobot();
    private static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private static final Rectangle screenRectangle = new Rectangle(screenSize);

    private static final Size smallSize = new Size(31, 42);

    private static Robot initRobot() {
        System.out.println("init robot");
        try {
            return new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    public static BufferedImage getScreenCapture() {
        return robot.createScreenCapture(screenRectangle);
    }

    public static Mat bufImg2Mat(BufferedImage image) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", byteArrayOutputStream);
            byteArrayOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Imgcodecs.imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);

    }

    public static BufferedImage mat2BufImg(Mat matrix) {
        String fileExtension = ".png";
        // convert the matrix into a matrix of bytes appropriate for
        // this file extension
        MatOfByte mob = new MatOfByte();
        Imgcodecs.imencode(fileExtension, matrix, mob);
        // convert the "matrix of bytes" into a byte array
        byte[] byteArray = mob.toArray();
        BufferedImage bufImage = null;
        try {
            InputStream in = new ByteArrayInputStream(byteArray);
            bufImage = ImageIO.read(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bufImage;
    }

    public static List<BufferedImage> mats2BufImgs(List<Mat> mats) {
        return mats.stream().map(Util::mat2BufImg).collect(Collectors.toList());
    }

    public static Mat mat2GrayMat(Mat mat) {
        Mat grayMat = new Mat();
        Imgproc.cvtColor(mat, grayMat, Imgproc.COLOR_BGR2GRAY);
        return grayMat;
    }

    public static Mat mat2smallMat(Mat mat) {
        Mat smallMat = new Mat();
        Imgproc.resize(mat, smallMat, smallSize);
        return smallMat;
    }


    public static Mat mat2ThresholdMat(Mat mat) {
        Mat grayMat = Util.mat2GrayMat(mat);
        Mat thresholdMat = new Mat();
        Imgproc.adaptiveThreshold(grayMat, thresholdMat, 255, ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 9, 11);
        return thresholdMat;

    }


    public static Mahjong mat2Mahjong(Mat mat) {
        Mahjong candidate = null;
        double max = 0.3;
        for (Mahjong mahjong : Mahjong.MAHJONGS) {
            SimilarPoint similarPoint = maxPointInMat(mat, mahjong.getMat());
            double similarity = similarPoint.getSimilarity();
            if (similarity > max) {
                if (mahjong.getType() == Mahjong.Type.Z && mahjong.getPoint() == 5 && similarity < 0.99)
                    continue;
                max = similarity;
                candidate = mahjong;
            }
        }
        return candidate;
    }

    public static SimilarPoint maxPointInMat(Mat mat, Mat subMat) {
        Mat result = new Mat();
        Imgproc.matchTemplate(mat, subMat, result, Imgproc.TM_CCORR_NORMED);
        Core.MinMaxLocResult mmlr = Core.minMaxLoc(result);
        Point maxLoc = mmlr.maxLoc;
        double v = result.get((int) maxLoc.y, (int) maxLoc.x)[0];
        return new SimilarPoint(maxLoc, v);
    }


    public static Mahjong smallMat2Mahjong(Mat mat) {
        Mahjong candidate = null;
        double max = 0.3;
        for (Mahjong mahjong : Mahjong.MAHJONGS) {
            SimilarPoint similarPoint = maxPointInMat(mat, mahjong.getSmallMat());
            double similarity = similarPoint.getSimilarity();
            if (similarity > max) {
                if (mahjong.getType() == Mahjong.Type.Z && mahjong.getPoint() == 5 && similarity < 0.99)
                    continue;
                max = similarity;
                candidate = mahjong;
            }
        }
        return candidate;
    }

    public static void click(Point point) {
        click((int) point.x, (int) point.y);

    }

    public static void click(int x, int y) {

//鼠标移动到某一点
        PointerInfo pinfo = MouseInfo.getPointerInfo();
        java.awt.Point p = pinfo.getLocation();

        robot.mouseMove(x, y);
        try {
            Thread.sleep(100);

            robot.mousePress(InputEvent.BUTTON1_MASK);
            Thread.sleep(50);
            robot.mouseRelease(InputEvent.BUTTON1_MASK);

            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        robot.mouseMove(p.x, p.y);

    }

    public static boolean isLiZhi(Mat source) {
        for (int i = 3; i < source.rows() - 3; i++) {
            if (Core.countNonZero(source.row(i)) < 8) {
                return true;
            }
        }
        return false;
    }

}
