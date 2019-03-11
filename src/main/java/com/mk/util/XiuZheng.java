package com.mk.util;

import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import static org.opencv.imgproc.Imgproc.ADAPTIVE_THRESH_MEAN_C;

public class XiuZheng {
    public static void main(String[] args) {

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Mat src = Imgcodecs.imread("D:/image/18.png");

        MatOfPoint2f srcTri = new MatOfPoint2f((
                new Point(0, 0)),
                new Point(src.cols() - 1, 0),
                new Point(0, src.rows() - 1),
                new Point(src.cols() - 1, src.rows() - 1));
        MatOfPoint2f dstTri = new MatOfPoint2f((
                new Point(0, 0)),
                new Point(src.cols() - 1, 0),
                new Point(src.cols() * 0.12, src.rows() - 1),
                new Point(src.cols() * 0.88, src.rows() - 1));

        final Mat perspectiveTransform = Imgproc.getPerspectiveTransform(srcTri, dstTri);

        Mat dst = new Mat();
        Imgproc.warpPerspective(src, dst, perspectiveTransform, src.size());

    }

    public static BufferedImage Mat2BufImg(Mat matrix) {
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
}
