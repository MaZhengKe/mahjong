package com.mk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class XiangTing {
    private static int S;

    public static void main(String[] args) {
        int[] shouPai = {1, 4, 7, 8,9, 14, 15, 18, 22, 25, 28, 29,  32, 32};
        long l = System.currentTimeMillis();
        System.out.println(getXiangTing(shouPai));
        System.out.println(System.currentTimeMillis() - l);
    }

    private static int getXiangTing(int[] shouPai){
        S = 9;
        takeMianZi(shouPai, new ArrayList<>(), 0, 4 - (shouPai.length - 2) / 3);
        return S;
    }

    private static void takeMianZi(int[] shouPai, List<int[]> mianzi, int i, int m) {
        if (i >= shouPai.length - 2) {
            takeDuizi(shouPai, mianzi, m);
            return;
        }
        int[][] removed = removeMianzi(shouPai, i);
        if (removed != null) {
            mianzi.add(removed[1]);
            takeMianZi(removed[0], mianzi, i, m + 1);
            mianzi.remove(removed[1]);
        }
        takeMianZi(shouPai, mianzi, i + 1, m);
    }

    private static void takeDaZi(int[] shouPai, List<int[]> mianzi, List<int[]> dazi, int[] duzi, int i, int m, int p, int d) {

        if (i >= shouPai.length - 1 || d + m == 4) {
            d = Math.min(d, 4 - m);
            int mys = 9 - 2 * m - d - p;
            S = Math.min(S, mys);
            System.out.println("S:" + mys);
            System.out.print("mianzi :");
            mianzi.forEach(mian -> System.out.print(Arrays.toString(mian)));
            System.out.println(" ");
            System.out.println("duizi  :" + Arrays.toString(duzi));
            System.out.print("dazi   :");
            dazi.forEach(mian -> System.out.print(Arrays.toString(mian)));
            System.out.println(" ");
            System.out.println("shouPai:" + Arrays.toString(shouPai));
            System.out.println(" ");
            return;
        }
        if (isDazi(shouPai, i)) {
            int[][] removed = remove(shouPai, i, 2);
            dazi.add(removed[1]);
            takeDaZi(removed[0], mianzi, dazi, duzi, i, m, p, d + 1);
            dazi.remove(removed[1]);
        }
        takeDaZi(shouPai, mianzi, dazi, duzi, i + 1, m, p, d);
    }

    private static void takeDuizi(int[] shouPai, List<int[]> mianzi, int m) {
        for (int i = 0; i < shouPai.length - 1; i++) {
            if (shouPai[i] == shouPai[i + 1]) {
                int[][] removed = remove(shouPai, i, 2);
                if (i + 2 < shouPai.length && shouPai[i] == shouPai[i + 2])
                    i++;
                takeDaZi(removed[0], mianzi, new ArrayList<>(), removed[1], 0, m, 1, 0);
            }
        }
        takeDaZi(shouPai, mianzi, new ArrayList<>(), null, 0, m, 0, 0);
    }

    private static int[] next(int[] shouPai, int i) {
        int current = shouPai[i];
        for (int j = i + 1; j < shouPai.length; j++) {
            if (shouPai[j] != current)
                return new int[]{shouPai[j], j};
        }
        return null;
    }

    private static int[][] removeMianzi(int[] shouPai, int i) {

        if (shouPai[i] == shouPai[i + 1] && shouPai[i] == shouPai[i + 2]) {
            return remove(shouPai, i, 3);
        }
        int[] next = next(shouPai, i);
        if (next == null)
            return null;
        int[] nextNext = next(shouPai, next[1]);
        if (nextNext == null) {
            return null;
        }

        boolean lianXu = shouPai[i] == next[0] - 1 && shouPai[i] == nextNext[0] - 2;
        boolean pt = nextNext[0] <= 9 || shouPai[i] >= 11 && nextNext[0] <= 19 || shouPai[i] >= 21 && nextNext[0] <= 29;

        if (lianXu && pt) {
            int[] reserved = new int[shouPai.length - 3];
            int[] removed = new int[3];
            for (int j = 0; j < shouPai.length; j++) {
                if (j < i) {
                    reserved[j] = shouPai[j];
                } else if (j == i) {
                    removed[0] = shouPai[j];
                } else if (j == next[1]) {
                    removed[1] = shouPai[j];
                } else if (j == nextNext[1]) {
                    removed[2] = shouPai[j];
                } else if (j < next[1]) {
                    reserved[j - 1] = shouPai[j];
                } else if (j < nextNext[1]) {
                    reserved[j - 2] = shouPai[j];
                } else {
                    reserved[j - 3] = shouPai[j];
                }
            }
            return new int[][]{reserved, removed};
        }
        return null;
    }


    private static boolean isDazi(int[] shouPai, int i) {
        if (shouPai[i] == shouPai[i + 1]) {
            return true;
        }
        boolean lianXu = shouPai[i] == shouPai[i + 1] - 1 || shouPai[i] == shouPai[i + 1] - 2;
        boolean pt = shouPai[i + 1] <= 9 || shouPai[i] >= 11 && shouPai[i + 1] <= 19 || shouPai[i] >= 21 && shouPai[i + 1] <= 29;
        return lianXu && pt;
    }

    private static int[][] remove(int[] shouPai, int i, int num) {
        int[] reserved = new int[shouPai.length - num];
        int[] removed = new int[num];
        for (int j = 0; j < shouPai.length; j++) {
            if (j < i)
                reserved[j] = shouPai[j];
            else if (j >= i + num) {
                reserved[j - num] = shouPai[j];
            } else {
                removed[j - i] = shouPai[j];
            }
        }
        return new int[][]{reserved, removed};
    }
}
