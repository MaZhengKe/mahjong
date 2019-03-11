package com.mk;

import lombok.Data;

@Data
public class Image {
    private boolean[][] RGB;
    private int width;
    private int height;
    private int total;
    private int effective;
    private float rate;
    private String name;

    public Image(boolean[][] RGB,String name) {
        this.name = name;
        this.RGB = RGB;
        width = RGB[0].length;
        height = RGB.length;
        total = width * height;
        effective = 0;
        for (boolean[] booleans : RGB) {
            for (boolean b : booleans) {
                if (b)
                    effective++;
            }
        }
        rate = (float) effective / total;
    }

    public boolean get(int height, int width) {
        return RGB[height][width];
    }

    @Override
    public String toString() {
        return "Image{" +
                "width=" + width +
                ", height=" + height +
                ", total=" + total +
                ", effective=" + effective +
                ", rate=" + rate +
                '}';
    }
}
