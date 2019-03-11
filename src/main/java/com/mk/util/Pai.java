package com.mk.util;

import lombok.Data;

import java.util.Map;

@Data
public class Pai implements Comparable {
    private String name;
    private int jinZhangShu;
    private double value;
    private Map<String, Integer> jinZhang;

    public Pai(int number, String type) {
        name = number + type;
    }

    @Override
    public String toString() {
        String res = name + " " + jinZhangShu;
        if (jinZhang != null)
            res += " " + jinZhang;
        return res;
    }

    @Override
    public int compareTo(Object o) {
        Pai t = (Pai) o;

        if (jinZhangShu != t.jinZhangShu)
            return t.jinZhangShu - jinZhangShu ;
        boolean z = name.contains("Z");
        boolean tz = t.name.contains("Z");
        if (z && !tz)
            return -1;
        else if (!z && tz)
            return 1;

        int i = name.charAt(0) - 48;
        int ti = t.name.charAt(0) - 48;
        return Math.abs(ti - 5) - Math.abs(i - 5) ;
    }
}
