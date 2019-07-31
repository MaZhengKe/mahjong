package com.mk;

import com.mk.recognition.*;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;

public class Main {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {

        JFrame f = new JFrame("雀魂牌效计算");

        //ImagePanel handImagePanel = new ImagePanel();
        ImagePanel poolImagePanel = new ImagePanel();
        ImagePanel flImagePanel = new ImagePanel();

        //JScrollPane handJSP = new JScrollPane(handImagePanel);
        JScrollPane poolJSP = new JScrollPane(poolImagePanel);
        JScrollPane flJSP = new JScrollPane(flImagePanel);

        //handJSP.setBounds(0, 100, 220, 1000);
        poolJSP.setBounds(0, 100, 100, 500);
        flJSP.setBounds(100, 100, 100, 500);

        //f.add(handJSP);
        f.add(poolJSP);
        f.add(flJSP);

        f.setSize(350, 250);
        f.setLocation(0, 0);
        f.setLayout(null);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setAlwaysOnTop(true);

        JRadioButton autoRadioButton = new JRadioButton("自动");
        JLabel handLabel = new JLabel();
        JTextArea analysisArea = new JTextArea();

        handLabel.setBounds(0, 0, 600, 30);
        analysisArea.setBounds(0, 35, 1000, 30);
        autoRadioButton.setBounds(0, 65, 100, 30);

        f.add(handLabel);
        f.add(analysisArea);
        f.add(autoRadioButton);

        f.setVisible(true);

        Screen screen = new Screen();
        new Thread(() -> {
            while (true) {
                screen.refresh();
                AbstractRecognition<LinkedHashMap<Mat, Mahjong>> handRecognition = new HandRecognition(screen).start();
                AbstractRecognition<Map<Mat, Mahjong>> poolRecognition = new PoolRecognition(screen).start();
                AbstractRecognition<Map<Mat, Mahjong>> fLRecognition = new FLRecognition(screen).start();
                AbstractRecognition<org.opencv.core.Point> reachRecognition = new ReachRecognition(screen).start();

                LinkedHashMap<Mat, Mahjong> handMap = handRecognition.get();
                Map<Mat, Mahjong> poolMap = poolRecognition.get();
                Map<Mat, Mahjong> flMap = fLRecognition.get();
                org.opencv.core.Point reachPoint = reachRecognition.get();


                //handImagePanel.changeData(handMap);
                poolImagePanel.changeData(poolMap);
                flImagePanel.changeData(flMap);


                List<Mahjong> handList = new ArrayList<>(handMap.values());
                int handSize = handList.size();

                handLabel.setForeground(handSize % 3 != 2 ? Color.red : Color.black);
                handLabel.setText(handSize + "张：" + handList);

                if (handSize % 3 == 2) {
                    List<Pai> paiList = XiangTingNotList.get(handList, poolMap.values(), flMap.values());

                    StringBuilder list = new StringBuilder();

                    paiList.stream().sorted().forEach(pai -> list.append(pai).append("\n"));
                    analysisArea.setText(list.toString());
                    Optional<Pai> max = paiList.stream().sorted().findFirst();
                    if (autoRadioButton.isSelected() && max.isPresent()) {
                        Pai pai = max.get();
                        if (reachPoint != null) {
                            if (pai.getJzs() > 2) {
                                Util.click(reachPoint);
                                try {
                                    Thread.sleep(300);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        int paiIndex = pai.getIndex();
                        Mahjong target = handList.stream().filter(mahjong -> mahjong.getIndex() == paiIndex).findFirst().orElseThrow(() -> new RuntimeException("ERROR"));
                        int index = handList.indexOf(target);
                        Util.click(365 + 81 * index, 950);
                    }
                }
            }
        }).start();

        CheckOver checkOver = new CheckOver(screen);
        CheckButton checkButton = new CheckButton(screen);

        checkOver.start();
        checkButton.start();

        autoRadioButton.addActionListener(e -> {
            if (autoRadioButton.isSelected()) {
                checkOver.resumeThread();
                checkButton.resumeThread();
            } else {
                checkOver.pauseThread();
                checkButton.pauseThread();
            }
        });
    }
}