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


public class PoolRecognition extends AbstractRecognition<Map<Mat, Mahjong>> {
    private Screen screen;

    public PoolRecognition(Screen screen) {
        this.screen = screen;
    }

    @Override
    public Map<Mat, Mahjong> call() {
//        System.out.println("计算牌池");
        List<Mat> poolMats = screen.getPoolMats();
//        BufferedImage bufferedImage = Util.Mat2BufImg(handMet);
        Map<Mat, Mahjong> result = new LinkedHashMap<>();
        for (Mat poolMat : poolMats) {
            List<Mat> mahjongMats = getMahjongMatsFromPoolMat(poolMat);
            Map<Mat, Mahjong> collect = mahjongMats.stream()
                    .collect(Collectors.toMap(Function.identity(), Util::smallMat2Mahjong, throwingMerger(), LinkedHashMap::new));
            result.putAll(collect);
        }

        return result;
    }

    private static List<Mat> getMahjongMatsFromPoolMat(Mat poolMat) {
        List<Mat> mahjongs = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            final Mat rowMat = poolMat.submat(i * 50, (i + 1) * 50, 0, poolMat.cols());
            mahjongs.addAll(getMahjongMatsFromRow(rowMat));
        }
        return mahjongs;
    }


    private static List<Mat> getMahjongMatsFromRow(Mat rowMat) {
        Mat thresholdMat = Util.mat2ThresholdMat(rowMat);

        List<Mat> res = new ArrayList<>();
        int left = 0;
        for (int right = 0; right < thresholdMat.cols(); right++) {
            if (Core.countNonZero(thresholdMat.col(right)) < 17) {
                if (right < 5) {
                    left = right;
                    continue;
                }
                Mat ge;
                int width = right - left;
                if (width < 60 && width > 30) {
                    // 如果宽度太太，说明有可能是立直牌
                    if (width > 44 && Util.isLiZhi(thresholdMat.submat(0, thresholdMat.rows(), left + 1, left + 38))) {
                        ge = rowMat.submat(7, 44, left + 1, left + 51);
                        Mat tmp = new Mat();
                        Core.transpose(ge, tmp);
                        Core.flip(tmp, ge, 0);
                    } else {
                        ge = rowMat.submat(0, thresholdMat.rows(), Math.max(0, left - 1), Math.min(thresholdMat.cols(), left + 40));
                    }
                    left = right + 1;
                    right += 36;
                    res.add(ge);
                }

            }
        }
        return res;
    }

}
