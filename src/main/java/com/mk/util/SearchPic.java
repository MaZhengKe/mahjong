package com.mk.util;

import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.opencv.imgproc.Imgproc.*;

public class SearchPic {

    private static Mat[] pa = new Mat[37];
    private static Mat[] spa = new Mat[37];
    private static Mat lizhi;
    static Point lizhidian;
    private static Mat hu;
    private static Mat over;

    static {

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);


        final String s = "pic\\";
        final Size dsize = new Size(31, 42);
        for (int i = 0; i < 9; i++) {
            Mat tmp = Imgcodecs.imread(s + (i + 1) + "m.png");
            Mat dst = new Mat();
            Imgproc.resize(tmp, dst, dsize);
            pa[i] = dst;
            spa[i] = tmp;
        }
        for (int i = 9; i < 18; i++) {
            Mat tmp = Imgcodecs.imread(s + (i - 8) + "p.png");
            Mat dst = new Mat();
            Imgproc.resize(tmp, dst, dsize);
            pa[i] = dst;
            spa[i] = tmp;
        }
        for (int i = 18; i < 27; i++) {
            Mat tmp = Imgcodecs.imread(s + (i - 17) + "s.png");
            Mat dst = new Mat();
            Imgproc.resize(tmp, dst, dsize);
            pa[i] = dst;
            spa[i] = tmp;
        }
        for (int i = 27; i < 34; i++) {
            Mat tmp = Imgcodecs.imread(s + (i - 26) + "z.png");
            Mat dst = new Mat();
            Imgproc.resize(tmp, dst, dsize);
            pa[i] = dst;
            spa[i] = tmp;
        }
        Mat tmp1 = Imgcodecs.imread(s + "s5m.png");
        Mat dst1 = new Mat();
        Imgproc.resize(tmp1, dst1, dsize);
        pa[34] = dst1;
        spa[34] = tmp1;
        Mat tmp2 = Imgcodecs.imread(s + "s5p.png");
        Mat dst2 = new Mat();
        Imgproc.resize(tmp2, dst2, dsize);
        pa[35] = dst2;
        spa[35] = tmp2;
        Mat tmp3 = Imgcodecs.imread(s + "s5s.png");
        Mat dst3 = new Mat();
        Imgproc.resize(tmp3, dst3, dsize);
        pa[36] = dst3;
        spa[36] = tmp3;
        lizhi = Imgcodecs.imread(s + "lizhi.png");
        hu = Imgcodecs.imread(s + "hu.png");
        over = Imgcodecs.imread(s + "over.png");
    }

    private static Mat xiuzheng(Mat src) {

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

    static boolean isHu(Mat source) {

        Mat huqu = source.submat(570, 633, 133, 188);

        Mat g_result = new Mat();
        Imgproc.matchTemplate(huqu, hu, g_result, Imgproc.TM_CCORR_NORMED);
        Core.MinMaxLocResult mmlr = Core.minMaxLoc(g_result);
        Point maxLoc = mmlr.maxLoc;
        double[] doubles = g_result.get((int) maxLoc.y, (int) maxLoc.x);
        double v = doubles[0];

        return v > 0.99;


    }

    static boolean isOver(Mat source) {

        Mat overqu = source.submat(927, 1018, 1600, 1750);

        Mat g_result = new Mat();
        Imgproc.matchTemplate(overqu, over, g_result, Imgproc.TM_CCORR_NORMED);
        Core.MinMaxLocResult mmlr = Core.minMaxLoc(g_result);
        Point maxLoc = mmlr.maxLoc;
        double[] doubles = g_result.get((int) maxLoc.y, (int) maxLoc.x);
        double v = doubles[0];

        return v > 0.98;


    }

    static List<Integer> getsPai(Mat source) {
        lizhidian = new Point();
        Mat lizhiqu = source.submat(750, 880, 650, 1350);
        //BufferedImage bufferedImage = Mat2BufImg(lizhiqu);

        Mat g_result = new Mat();
        Imgproc.matchTemplate(lizhiqu, lizhi, g_result, Imgproc.TM_CCORR_NORMED);
        Core.MinMaxLocResult mmlr = Core.minMaxLoc(g_result);
        Point maxLoc = mmlr.maxLoc;
        double[] doubles = g_result.get((int) maxLoc.y, (int) maxLoc.x);
        double v = doubles[0];

        if (v > 0.97) {
            maxLoc.x += 750;
            maxLoc.y += 800;
            lizhidian = maxLoc;
        }


        List<Integer> res = new ArrayList<>();

        Mat submat = source.submat(860, 1028, 320, 1785);

        Mat gray = new Mat();

        Imgproc.cvtColor(submat, gray, Imgproc.COLOR_BGR2GRAY);

        Mat dst = new Mat();
        Imgproc.adaptiveThreshold(gray, dst, 255, ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 9, 11);

        List<Mat> matList = getSinglesPai(dst, submat);
        for (Mat mat : matList) {
            res.add(searchs(mat));
        }

        return res;
    }

    private static List<Mat> getPaiChi(Mat source) {
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

    private static List<Mat> getFL(Mat source) {
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

    private static List<Mat> getPaiBySample(Mat source, Mat colorSource) {
        List<Mat> res = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            final Mat hang = source.submat(i * 50, (i + 1) * 50, 0, source.cols());
            final Mat colorHang = colorSource.submat(i * 50, (i + 1) * 50, 0, source.cols());

//            BufferedImage bufferedImage = Mat2BufImg(hang);
            res.addAll(getSinglePai(hang, colorHang));
        }

        return res;
    }

    private static List<Mat> getFLPaiBySample(Mat source, Mat colorSource) {
        List<Mat> res = new ArrayList<>();
        Mat panbie = source.submat(source.rows() - 45, source.rows(), 0, source.cols());
//        BufferedImage bufferedImage = Mat2BufImg(panbie);
        int next = source.cols();
        for (int i = source.cols() - 36; i > 0; i--) {
            if (Core.countNonZero(panbie.col(i)) < 20) {
                Mat ge;
                if ((next - i) < 57) {
                    if ((next - i) > 42) {
                        ge = colorSource.submat(source.rows() - 38, source.rows(), next - 45, next);
                        Mat tmp = new Mat();
                        Core.transpose(ge, tmp);
                        Core.flip(tmp, ge, 1);

                    } else {
                        ge = colorSource.submat(source.rows() - 45, source.rows(), next - 37, next);
                        Mat lizhiPanDing = source.submat(source.rows() - 45, source.rows(), next - 37, next);
                        if (isLiZhi(lizhiPanDing)) {
                            continue;
                        }
                    }
                    next = i - 1;
                    i -= 36;
                    res.add(ge);
//                    Imgcodecs.imwrite("D:/image/color/lie" + " " + i + ".png", ge);
                }

            }
        }
        return res;
    }

    private static List<Mat> getSinglePai(Mat source, Mat colorSource) {

        List<Mat> res = new ArrayList<>();
        int next = 0;
        for (int i = 0; i < source.cols(); i++) {
            if (Core.countNonZero(source.col(i)) < 17) {
                if (i < 5) {
                    next = i;
                    continue;
                }
                Mat ge;
                if ((i - next) < 52 && (i - next) > 30) {
                    if ((i - next) > 44) {

                        ge = source.submat(0, source.rows(), next + 1, next + 38);

                        if (isLiZhi(ge)) {
                            ge = colorSource.submat(7, 44, next + 1, next + 51);
                            Mat tmp = new Mat();
                            Core.transpose(ge, tmp);
                            Core.flip(tmp, ge, 0);

                        } else {

                            ge = colorSource.submat(0, source.rows(), next + 1, next + 38);

                        }
                    } else {
                        ge = colorSource.submat(0, source.rows(), next + 1, next + 38);
                    }
                    next = i + 1;
                    i += 36;
                    res.add(ge);
                    //Imgcodecs.imwrite("D:/image/color/lie" + name + " " + i + ".png", ge);
                }

            }
        }
        return res;
    }


    private static List<Mat> getSinglesPai(Mat source, Mat colorSource) {

        List<Mat> res = new ArrayList<>();
        int next = 0;
        for (int i = 0; i < source.cols(); i++) {
            if (Core.countNonZero(source.col(i)) < 65) {
                if (i < 5) {
                    next = i;
                    continue;
                }
                Mat ge;
                if ((i - next) < 120) {
                    if ((i - next) > 75) {
                        ge = colorSource.submat(0, source.rows(), next + 1, i);
                        res.add(ge);
                        //Imgcodecs.imwrite("D:/image/color/lie" + " " + i + ".png", ge);
                        next = i + 1;
                        i += 60;
                    }
                }
            }
        }
        return res;
    }

    private static boolean isLiZhi(Mat source) {
        for (int i = 3; i < source.rows() - 3; i++) {
            if (Core.countNonZero(source.row(i)) < 8) {
                return true;
            }
        }
        return false;
    }


    public static Mat BufImg2Mat(BufferedImage original, int imgType, int matType) {
        if (original == null) {
            throw new IllegalArgumentException("original == null");
        }

        // Don't convert if it already has correct type
        if (original.getType() != imgType) {

            // Create a buffered image
            BufferedImage image = new BufferedImage(original.getWidth(), original.getHeight(), imgType);

            // Draw the image onto the new buffer
            Graphics2D g = image.createGraphics();
            try {
                g.setComposite(AlphaComposite.Src);
                g.drawImage(original, 0, 0, null);
            } finally {
                g.dispose();
            }
        }

        DataBuffer dataBuffer = original.getRaster().getDataBuffer();
        int[] pixels = ((DataBufferInt) dataBuffer).getData();
        Mat mat = Mat.eye(original.getHeight(), original.getWidth(), matType);
        mat.put(0, 0, pixels);
        return mat;
    }

    static Mat BufferedImage2Mat(BufferedImage image) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", byteArrayOutputStream);
        byteArrayOutputStream.flush();
        return Imgcodecs.imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
    }


    static void set(Mat image) {

        long now = System.currentTimeMillis();
        //Mat source = Imgcodecs.imread("D:/image/test4.png");

        Mat gray = new Mat();

        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);

        gray = xiuzheng(gray);
        image = xiuzheng(image);

        Mat dst = new Mat();
        Imgproc.adaptiveThreshold(gray, dst, 255, ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 9, 11);


//        BufferedImage bufferedImage = Mat2BufImg(dst);
        //bufferedImage = Mat2BufImg(dst);
//        Imgcodecs.imwrite("D:/image/tmp/res555.png", dst);

        final List<Mat> paiChis = getPaiChi(dst);
        List<Mat> colorPaiChi = getPaiChi(image);
        final List<Mat> fl = getFL(dst);
        List<Mat> colorFL = getFL(image);


        for (int i = 0; i < paiChis.size(); i++) {
            //Imgcodecs.imwrite("D:/image/tmp/res" + i + ".png", paiChis.get(i));
            //Imgcodecs.imwrite("D:/image/tmp/colorRes" + i + ".png", colorPaiChi.get(i));
//            getPai(paiChis.get(i));
            final List<Mat> pais = getPaiBySample(paiChis.get(i), colorPaiChi.get(i));

            for (Mat pai : pais) {
                search(pai);
            }
        }

        for (int i = 0; i < fl.size(); i++) {
//            Imgcodecs.imwrite("D:/image/tmp/res" + i + ".png", fl.get(i));
//            Imgcodecs.imwrite("D:/image/tmp/colorRes" + i + ".png", colorFL.get(i));

            final List<Mat> pais = getFLPaiBySample(fl.get(i), colorFL.get(i));

            for (Mat pai : pais) {
                search(pai);
            }
        }


        //System.out.println("搜索牌池：" + (System.currentTimeMillis() - now));
    }

    private static void search(Mat source) {
//        BufferedImage bufferedImage = Mat2BufImg(source);

        Mat g_result = new Mat();
        int maxIndex = 0;
        double max = 0;
        for (int i = 0; i < 37; i++) {
//            if (i == 31)
//                continue;
            Mat templ = pa[i];
            //bufferedImage = Mat2BufImg(templ);
            Imgproc.matchTemplate(source, templ, g_result, Imgproc.TM_CCORR_NORMED);
            Core.MinMaxLocResult mmlr = Core.minMaxLoc(g_result);
            Point maxLoc = mmlr.maxLoc;
            double[] doubles = g_result.get((int) maxLoc.y, (int) maxLoc.x);
            double v = doubles[0];
//            if (i == 9)
//                v *= 1.003;
            if (v > max) {
                if (i == 31 && v < 0.98)
                    continue;
                max = v;
                maxIndex = i;
            }
//             System.out.println((i % 9 + 1) + " " + i / 9 + " " + v);
        }

        int i = maxIndex / 9;
        int num = maxIndex % 9 + 1;
        if (maxIndex >= 34) {
            i = (maxIndex - 34) % 3;
            num = 5;
        }
        switch (i) {
            case 0:
                Desk.M[num]++;
                return;
            case 1:
                Desk.P[num]++;
                return;
            case 2:
                Desk.S[num]++;
                return;
            case 3:
                Desk.Z[num]++;
                return;
            default:
        }
    }

    private static Integer searchs(Mat source) {
        //BufferedImage bufferedImage = Mat2BufImg(source);

        Mat g_result = new Mat();
        int maxIndex = 0;
        double max = 0;
        for (int i = 0; i < 37; i++) {
            Mat templ = spa[i];
            Imgproc.matchTemplate(source, templ, g_result, Imgproc.TM_CCORR_NORMED);
            Core.MinMaxLocResult mmlr = Core.minMaxLoc(g_result);
            Point maxLoc = mmlr.maxLoc;
            double[] doubles = g_result.get((int) maxLoc.y, (int) maxLoc.x);
            double v = doubles[0];
//            if (i == 9)
//                v *= 1.003;
            if (v > max) {
                if (i == 31 && v < 0.98)
                    continue;
                max = v;
                maxIndex = i;
            }
            // System.out.println((i % 9 + 1) + " " + i / 9 + " " + v);
        }

        int type = maxIndex / 9;
        int num = maxIndex % 9 + 1;
        if (maxIndex >= 34) {
            type = (maxIndex - 34) % 3;
            num = 0;
        }
        switch (type) {
            case 0:
                return num;
            case 1:
                return num + 10;
            case 2:
                return num + 20;
            case 3:
                return num + 30;
            default:
                return -1;
        }
    }

    private static BufferedImage Mat2BufImg(Mat matrix) {
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
