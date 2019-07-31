package com.mk;

import org.opencv.core.Mat;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedHashMap;
import java.util.Map;

public class ImagePanel extends JPanel {

    private final Map<Mat, Mahjong> matMahjongMap;

    public ImagePanel() {
        this.setPreferredSize(new Dimension(200, 1000));
        this.setLayout(null);
        this.matMahjongMap = new LinkedHashMap<>();
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int index = 0;
        Map<Mat, Mahjong> copy;
        synchronized (matMahjongMap) {
            copy = new LinkedHashMap<>(matMahjongMap);
        }

        for (Mat mat : copy.keySet()) {
            drawImage(g, mat, 0, index * 50);
            drawImage(g, copy.get(mat).getSmallMat(), 40, index * 50);
            this.setPreferredSize(new Dimension(50, (index + 1) * 50));
            index++;
        }
    }
    public void changeData(Map<Mat, Mahjong> newMap){
        synchronized (matMahjongMap) {
            matMahjongMap.clear();
            matMahjongMap.putAll(newMap);
        }
        this.repaint();
    }

    private void drawImage(Graphics g, Mat mat, int x, int y) {
        BufferedImage image = Util.mat2BufImg(mat);
        int x1 = x + (40 - image.getWidth()) / 2;
        int y1 = y + (50 - image.getHeight()) / 2;
        g.drawImage(image, x1, y1, null);
    }
}