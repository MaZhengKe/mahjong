package com.mk.util;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Pai implements Comparable{
    private int index;
    private int xts;
    private int jzs;
    private Map<Integer,Integer> jz= new HashMap<>();

    Pai(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return map(index) +
                " " + xts +
                " " + jzs +
                " " + jz;
    }


    @Override
    public int compareTo(Object o) {
        Pai t = (Pai) o;

        if (jzs != t.jzs)
            return t.jzs - jzs ;
        boolean z = index>30;
        boolean tz = t.index>30;
        if (z && !tz)
            return -1;
        else if (!z && tz)
            return 1;

        int i = index % 10;
        int ti = t.index % 10;
        return Math.abs(ti - 5) - Math.abs(i - 5) ;
    }

    private static String map(int index){
        int type = index / 10;
        int num = index % 10;
        switch (type){
            case 0:
                return num + "万";
            case 1:
                return num + "筒";
            case 2:
                return num + "索";
            case 3:
                switch (num){
                    case 1:
                        return "  东";
                    case 2:
                        return "  南";
                    case 3:
                        return "  西";
                    case 4:
                        return "  北";
                    case 5:
                        return "  白";
                    case 6:
                        return "  发";
                    case 7:
                        return "  中";
                }
        }
        return "error";
    }
}
