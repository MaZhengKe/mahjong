package com.mk;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.util.ArrayList;
import java.util.List;

public class Mahjong {

    static final List<Mahjong> MAHJONGS = new ArrayList<>();
    private static final String PNG_SUFFIX = ".png";
    private static final String SPECIAL_PREFIX = "s5";

    static {
        final String picPath = "pic\\";
        for (Type type : Type.values()) {
            for (int i = 1; i <= type.max; i++) {
                Mat mat = Imgcodecs.imread(picPath + i + type.abbreviation + PNG_SUFFIX);
                Mat smallMat = Util.mat2smallMat(mat);
                Mahjong mahjong = new Mahjong(i, type, mat, smallMat, false);
                MAHJONGS.add(mahjong);
            }
            if (type.hasSpecial) {
                Mat mat = Imgcodecs.imread(picPath + SPECIAL_PREFIX + type.abbreviation + PNG_SUFFIX);
                Mat smallMat = Util.mat2smallMat(mat);
                Mahjong mahjong = new Mahjong(5, type, mat, smallMat, true);
                MAHJONGS.add(mahjong);
            }
        }
    }

    private static final String[] zName = new String[]{"东", "南", "西", "北", "白", "发", "中"};

    private final int point;
    private final Type type;
    private final Mat mat;
    private final Mat smallMat;
    private final boolean isSpecial;

    private Mahjong(int point, Type type, Mat mat, Mat smallMat, boolean isSpecial) {
        this.point = point;
        this.type = type;
        this.mat = mat;
        this.smallMat = smallMat;
        this.isSpecial = isSpecial;
    }

    public int getPoint() {
        return point;
    }

    public Type getType() {
        return type;
    }

    public Mat getMat() {
        return mat;
    }

    public Mat getSmallMat() {
        return smallMat;
    }

    public boolean isSpecial() {
        return isSpecial;
    }

    public int getIndex(){
        return this.getPoint() + this.getType().getNum();
    }

    @Override
    public String toString() {
        if (type == Type.Z)
            return zName[point - 1];
        String str = point + type.name;
        return isSpecial() ? "红" + str : str;
    }

    enum Type {
        M(9, "万", "m", true,0),
        P(9, "筒", "p", true,10),
        S(9, "索", "s", true,20),
        Z(7, "字", "z", false,30);

        private int max;
        private String name;
        private String abbreviation;
        private boolean hasSpecial;
        private int num;

        public int getNum(){
            return num;
        }

        Type(int max, String name, String abbreviation, boolean hasSpecial,int num) {
            this.max = max;
            this.name = name;
            this.abbreviation = abbreviation;
            this.hasSpecial = hasSpecial;
            this.num = num;
        }
    }
}
