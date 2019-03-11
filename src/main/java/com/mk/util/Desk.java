package com.mk.util;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Desk {
    public Desk(int[] shoupai) {
        this.shoupai = shoupai;
    }

    private int[] shoupai;
    private int[] baopai;
    private int[] paihe;
}
