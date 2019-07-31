package com.mk.recognition;

import com.mk.Mahjong;
import com.mk.Screen;
import com.mk.Util;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


public class FLRecognition extends AbstractRecognition<Map<Mat, Mahjong>> {
    private Screen screen;

    public FLRecognition(Screen screen) {
        this.screen = screen;
    }

    @Override
    public Map<Mat, Mahjong> call() {
//        System.out.println("计算副露");
        List<Mat> flMats = screen.getFLMats();
        Map<Mat, Mahjong> result = new LinkedHashMap<>();
        for (Mat flMat : flMats) {
            List<Mat> mahjongMats = getMahjongMatsFromFLMat(flMat);
            Map<Mat, Mahjong> collect = mahjongMats.stream().collect(Collectors.toMap(Function.identity(), Util::smallMat2Mahjong, throwingMerger(), LinkedHashMap::new));
            result.putAll(collect);
        }
        return result;
    }

    private static List<Mat> getMahjongMatsFromFLMat(Mat FLMat) {

        Mat thresholdMat = Util.mat2ThresholdMat(FLMat);
        List<Mat> res = new ArrayList<>();
        Mat panbie = thresholdMat.submat(thresholdMat.rows() - 45, thresholdMat.rows(), 0, thresholdMat.cols());
        int next = thresholdMat.cols();
        for (int i = thresholdMat.cols() - 36; i > 0; i--) {
            if (Core.countNonZero(panbie.col(i)) < 20) {
                Mat ge;
                if ((next - i) < 57) {
                    if ((next - i) > 42) {
                        ge = FLMat.submat(thresholdMat.rows() - 38, thresholdMat.rows(), next - 45, next);
                        Mat tmp = new Mat();
                        Core.transpose(ge, tmp);
                        Core.flip(tmp, ge, 1);

                    } else {
                        ge = FLMat.submat(thresholdMat.rows() - 45, thresholdMat.rows(), next - 37, next);
                        Mat lizhiPanDing = thresholdMat.submat(thresholdMat.rows() - 45, thresholdMat.rows(), next - 37, next);
                        if (Util.isLiZhi(lizhiPanDing)) {
                            continue;
                        }
                    }
                    next = i - 1;
                    i -= 36;
                    res.add(ge);
                }

            }
        }
        return res;
    }
}
