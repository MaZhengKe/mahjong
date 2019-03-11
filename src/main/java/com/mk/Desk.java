package com.mk;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Desk {
    Desk(int[] shouPai) {
        this.shouPai = shouPai;
    }

    public static byte[] M = new byte[10];
    public static byte[] P = new byte[10];
    public static byte[] S = new byte[10];
    public static byte[] Z = new byte[8];

    private int[] shouPai;
    private int[] baoPai;
    private int[] paiHe;
}
