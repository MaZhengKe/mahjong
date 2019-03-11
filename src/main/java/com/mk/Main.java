package com.mk;

import com.mk.util.Shoupai;

import java.util.ArrayList;

public class Main {
    public static void main(String [] args){
        long now =System.currentTimeMillis();
        Shoupai shoupai = new Shoupai("1468m24667889p1s1z");
        System.out.println(shoupai.getRes());
        System.out.println(System.currentTimeMillis() - now);
    }
}
