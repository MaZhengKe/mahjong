package com.mk.util;

import com.mk.Image;
import com.mk.Pai;
import org.opencv.core.Mat;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;

public class CaptureScreen {

    public static final String path = "D:\\mj\\";
    public static List<Point> total = new ArrayList<>();
    public static Image cap;

    public static Map<String, Image> imageMap = new HashMap<>();
    public static Robot robot = null;

    static {
        load(9, "m");
        load(9, "p");
        load(9, "s");
        load(7, "z");


        BufferedImage bufferedImage;
        bufferedImage = getBufferedImageFromFile(path + "s5m.png");
        imageMap.put("s5m", getImageGRB(bufferedImage, "s5m"));
        bufferedImage = getBufferedImageFromFile(path + "s5p.png");
        imageMap.put("s5p", getImageGRB(bufferedImage, "s5p"));
        bufferedImage = getBufferedImageFromFile(path + "s5s.png");
        imageMap.put("s5s", getImageGRB(bufferedImage, "s5s"));
    }

    public static void load(int num, String type) {
        for (int i = 1; i <= num; i++) {
            String name = i + type;
            BufferedImage bufferedImage = getBufferedImageFromFile(path + i + type + ".png");
            imageMap.put(name, getImageGRB(bufferedImage, name));
        }
    }

    public static void captureScreen(String folder, String fileName) throws Exception {

        BufferedImage image = getBufferedImage();
        // 截图保存的路径
        File screenFilePath = new File(folder);
        // 如果路径不存在,则创建
        if (!screenFilePath.getParentFile().exists()) {
            boolean mkdirResult = screenFilePath.getParentFile().mkdirs();
            assert mkdirResult;
        }
        //判断文件是否存在，不存在就创建文件
        if (!screenFilePath.exists() && !screenFilePath.isDirectory()) {
            boolean mkdirResult = screenFilePath.mkdir();
            assert mkdirResult;
        }

        File f = new File(screenFilePath, fileName);
        ImageIO.write(image, "png", f);
        //自动打开
        /*if (Desktop.isDesktopSupported()
                 && Desktop.getDesktop().isSupported(Desktop.Action.OPEN))
                    Desktop.getDesktop().open(f);*/
    }

    /**
     * 根据BufferedImage获取图片RGB数组
     *
     * @param bfImage 图像
     * @return RGB
     */
    public static Image getImageGRB(BufferedImage bfImage, String name) {


        int width = bfImage.getWidth();
        int height = bfImage.getHeight();


        boolean[][] result = new boolean[height][width];
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                //使用getRGB(w, h)获取该点的颜色值是ARGB，而在实际应用中使用的是RGB，所以需要将ARGB转化成RGB，即bufImg.getRGB(w, h) & 0xFFFFFF。
                int RGB = bfImage.getRGB(w, h) & 0xFFFFFF;
                boolean b = getGrayFromRGB(RGB);
                result[h][w] = b;
            }
        }
        return new Image(result, name);
    }

    private static boolean getGrayFromRGB(int rgb) {
        int gray = (rgb / 65536 * 19595 + rgb / 256 % 256 * 38469 + rgb % 256 * 7472) >> 16;
        return gray < 90;
    }

    public static java.util.List<Point> searchImage(Image source, Image target) {
        java.util.List<Point> points = new ArrayList<Point>();
        int targetWidth = target.getWidth() - 1;
        int targetHeight = target.getHeight() - 1;
        int sourceWidth = source.getWidth() - 1;
        int sourceHeight = source.getHeight() - 1;

        boolean[][] sourceRGB = source.getRGB();


        boolean isDuplicate;
//
//        for (int h = 0; h < sourceHeight - targetHeight; h++) {
//            for (int w = 0; w < sourceWidth - targetWidth; w++) {
        for (int h = 850; h < 950; h++) {
            for (int w = 300; w < sourceWidth - targetWidth; w++) {
                isDuplicate = false;
                for (Point point : total) {
                    if (Math.abs(point.getY() - w) < 70) {
                        isDuplicate = true;
                        break;
                    }
                }
                if (isDuplicate) {
                    continue;
                }
                if (sourceRGB[h][w]
                        || sourceRGB[h + targetHeight][w]
                        || sourceRGB[h][w + targetWidth]
                        || sourceRGB[h + targetHeight][w + targetWidth]
                ) {
                } else {
                    if (isSame(source, target, h, w)) {
                        Point point = new Point(h, w);
                        points.add(point);
                        total.add(point);
                        w += 50;
                    }
                }
            }
        }
        return points;
    }

    private static boolean isSame(Image source, Image target, int x, int y) {
        int targetHeight = target.getHeight() - 1;
        int targetWidth = target.getWidth() - 1;

        int total = targetHeight * targetWidth;
        total *= target.getRate() / 2.8;

        if (target.getName().equals("9p"))
            total += 100;

        int count = 0;

        for (int h = x; h < x + targetHeight; h++) {
            for (int w = y; w < y + targetWidth; w++) {
                if (source.get(h, w) != target.get(h - x, w - y)) {
                    count++;
                    if (count > total) {

                        return false;
                    }
                }
            }
        }
        // System.out.println(target.getName() + ":" + count + "/" + total + " ef  " + target.getEffective() + "  " + target.getTotal());
        return true;
    }

    private static BufferedImage getBufferedImage() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle screenRectangle = new Rectangle(screenSize);
        Robot robot = null;
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        return robot.createScreenCapture(screenRectangle);
    }

    public static BufferedImage getBufferedImageFromFile(String fileName) {

        File f = new File(fileName);
        try {
            return ImageIO.read(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void click(int x, int y) {

//鼠标移动到某一点
        PointerInfo pinfo = MouseInfo.getPointerInfo();
        Point p = pinfo.getLocation();

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

    public static void main(String[] args) {

        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }


        JFrame f = new JFrame("mj");
        JRadioButton radioButton = new JRadioButton();

        f.setSize(350, 250);
        f.setLocation(0, 0);
        f.setLayout(null);
        JLabel l = new JLabel();

        JTextArea res = new JTextArea();
        l.setBounds(0, 0, 300, 30);
        res.setBounds(0, 35, 1000, 300);
        radioButton.setBounds(300,0,50,30);


        f.add(l);
        f.add(res);
        f.add(radioButton);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
        f.setAlwaysOnTop(true);
        while (true) {
            List<Integer> pais = getAll(radioButton);
            if (pais.size() % 3 == 2) {
                pais = getAll(radioButton);
            }
            String text = getText(pais);
            int size = text.length() - 4;
            l.setForeground(size % 3 != 2 ? Color.red : Color.black);
            l.setText(size + "张：" + text);

            if (size % 3 == 2) {
                List<Pai> paiList = XiangTingNotList.get(pais);
                if (paiList == null) {
                    continue;
                }
                StringBuilder list = new StringBuilder();

                paiList.stream().sorted().forEach(pai -> list.append(pai).append("\n"));

                Optional<Pai> max = paiList.stream().sorted().findFirst();
                res.setText("[0, 1, 2, 3, 4, 5, 6, 7, 8, 9]\n" +
                        Arrays.toString(Dask.M) + "\n"
                        + Arrays.toString(Dask.P) + "\n"
                        + Arrays.toString(Dask.S) + "\n"
                        + Arrays.toString(Dask.Z) + "\n"
                        + list.toString()

                );
                if (radioButton.isSelected() && max.isPresent()) {
                    Pai pai = max.get();
                    if (SearchPic.lizhidian.x > 1) {
                        System.out.println(SearchPic.lizhidian.x);
                        if (pai.getJzs() > 2) {
                            click((int) SearchPic.lizhidian.x, (int) SearchPic.lizhidian.y);
                            try {
                                Thread.sleep(300);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    int paiIndex = pai.getIndex();
                    int index = pais.contains(paiIndex) ? pais.indexOf(paiIndex) : pais.indexOf(paiIndex - 5);
                    click(365 + 81 * index, 950);
                }
                //System.out.println("推荐花费" + (System.currentTimeMillis() - timeMillis));
            }

            total.clear();
        }
    }

    private static String getText(List<Integer> pais) {
        StringBuilder sb = new StringBuilder();

        pais.stream().filter(pai -> pai < 10).forEach(pai -> sb.append(pai % 10));
        sb.append("万");
        pais.stream().filter(pai -> pai >= 10 && pai < 20).forEach(pai -> sb.append(pai % 10));
        sb.append("筒");
        pais.stream().filter(pai -> pai >= 20 && pai < 30).forEach(pai -> sb.append(pai % 10));
        sb.append("索");
        pais.stream().filter(pai -> pai >= 30).forEach(pai -> sb.append(pai % 10));
        sb.append("字");

        return sb.toString();
    }


    public static void test() {
        imageMap.forEach((s, image) -> {
            System.out.println("test " + s);

            imageMap.forEach((s1, image1) -> {

                boolean[][] result = new boolean[image1.getHeight() + 100][image1.getWidth() + 100];
                for (int h = 0; h < image1.getHeight() + 100; h++) {
                    for (int w = 0; w < image1.getWidth() + 100; w++) {
                        if (h > 50 && h < image1.getHeight() + 50 && w > 50 && w < image1.getWidth() + 50) {
                            result[h][w] = image1.get(h - 50, w - 50);
                        } else {
                            result[h][w] = false;
                        }

                    }
                }

                Image image2 = new Image(result, "");


                List<Point> points = searchImage(image2, image);
                total.clear();
                if (points.size() != 0) {
                    System.out.println(s + " = " + s1);
                }

            });

            System.out.println(" ");
        });
    }


    private static List<Integer> getAll(JRadioButton radioButton) {
        BufferedImage source = getBufferedImage();
        Mat mat = null;
        try {
            mat = SearchPic.BufferedImage2Mat(source);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert mat != null;
        List<Integer> pais = SearchPic.getsPai(mat);
        if(radioButton.isSelected()){
            isHu(mat);
            isOver(mat);
        }

        int size = pais.size();
        if (size % 3 == 2) {
            Arrays.fill(Dask.M, (byte) 0);
            Arrays.fill(Dask.P, (byte) 0);
            Arrays.fill(Dask.S, (byte) 0);
            Arrays.fill(Dask.Z, (byte) 0);
            SearchPic.set(mat);
        }
        return pais;
    }

    private static void isOver(Mat mat) {
        boolean over = SearchPic.isOver(mat);
        if (over) {
            try {
                click(1675, 983);
                Thread.sleep(3000);
                click(1337, 417);
                Thread.sleep(3000);
                click(1337, 600);
                Thread.sleep(3000);
                click(1337, 600);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void isHu(Mat mat) {
        boolean hu = SearchPic.isHu(mat);
        if (hu) {
            try {
                Thread.sleep(2000);
                click(162, 599);
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            click(162, 655);
        }
    }

    private static void search(StringBuilder sb, Image imageGRB, int num, String type) {
        boolean has = false;
        BufferedImage target;
        for (int i = 1; i <= num; i++) {

            if (!type.equals("z") && i == 5) {
                String name = "s" + i + type;
                List<Point> points = searchImage(imageGRB, imageMap.get(name));
                for (Point point : points) {
                    //System.out.println(point);
                    sb.append(i);
                    has = true;
                }
            }
            String name = i + type;
            List<Point> points = searchImage(imageGRB, imageMap.get(name));
            for (Point point : points) {
                //System.out.println(point);
                sb.append(i);
                has = true;
            }
        }
        //if (has)
        sb.append(type);
    }


    private static void search() {
        BufferedImage source = getBufferedImageFromFile("D:\\image\\source.png");
        BufferedImage target = getBufferedImageFromFile("D:\\image\\2s.png");
        java.util.List<Point> points = searchImage(getImageGRB(source, ""), getImageGRB(target, ""));
        points.forEach(System.out::println);
    }

    public static void cap() {

        Date dt = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss");
        String data = sdf.format(dt);
        String rd = sdf1.format(dt);
        try {
            captureScreen("D:\\image\\" + data, rd + ".png");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}