package com.mk.recognition;

import com.mk.Mahjong;
import com.mk.Screen;
import com.mk.Util;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;


public class HandRecognition extends AbstractRecognition<LinkedHashMap<Mat, Mahjong>> {
    private Screen screen;

    public HandRecognition(Screen screen) {
        this.screen = screen;
    }

    @Override
    public LinkedHashMap<Mat, Mahjong> call() {
//        System.out.println("计算手牌");
        Mat handMet = screen.getHandMet();
        Mat thresholdMat = Util.mat2ThresholdMat(handMet);
        List<Mat> handMats = getHandMats(thresholdMat, handMet);
        return handMats.stream().collect(Collectors.toMap(mat -> mat, Util::mat2Mahjong, throwingMerger(), LinkedHashMap::new));
    }


    private static List<Mat> getHandMats(Mat thresholdMat, Mat colorMat) {

        List<Mat> hands = new ArrayList<>();
        int left = 0;
        //遍历每一列
        for (int right = 1; right < thresholdMat.cols(); right++) {

            if (Core.countNonZero(thresholdMat.col(right)) < 65) {
                if (right < 5) {
                    left = right;
                    continue;
                }
                int width = right - left;
                if (width < 120 && width > 75) {
                    Mat hand = colorMat.submat(0, thresholdMat.rows(), left + 1, right);
                    hands.add(hand);
                    left = right + 1;
                    right += 60;
                }
            }
        }
        return hands;
    }

}
