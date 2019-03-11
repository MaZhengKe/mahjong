package com.mk;

import org.opencv.core.Mat;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.List;
import javax.swing.*;

public class CaptureScreen {

    private static Robot robot = null;
    static {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private static BufferedImage getBufferedImage() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle screenRectangle = new Rectangle(screenSize);
        return robot.createScreenCapture(screenRectangle);
    }

    private static void click(int x, int y) {

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


        JFrame f = new JFrame("雀魂牌效练习");
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
                        Arrays.toString(Desk.M) + "\n"
                        + Arrays.toString(Desk.P) + "\n"
                        + Arrays.toString(Desk.S) + "\n"
                        + Arrays.toString(Desk.Z) + "\n"
                        + list.toString()

                );
                if (radioButton.isSelected() && max.isPresent()) {
                    Pai pai = max.get();
                    if (SearchPic.liZhiDian.x > 1) {
                        System.out.println(SearchPic.liZhiDian.x);
                        if (pai.getJzs() > 2) {
                            click((int) SearchPic.liZhiDian.x, (int) SearchPic.liZhiDian.y);
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
            Arrays.fill(Desk.M, (byte) 0);
            Arrays.fill(Desk.P, (byte) 0);
            Arrays.fill(Desk.S, (byte) 0);
            Arrays.fill(Desk.Z, (byte) 0);
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

}