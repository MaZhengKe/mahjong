package com.mk.util;


import java.util.*;

public class XiangTingNotList {
    private static int S;

    public static void main(String[] args) {
        int[] shouPai = {1, 2, 3, 3, 4, 5, 7, 8, 9, 11, 12, 18, 19, 25};
        Desk desk = new Desk(shouPai);

        long l = System.currentTimeMillis();
        List<Pai> pais = get(desk);
        System.out.println(System.currentTimeMillis() - l);
        assert pais != null;
        pais.stream().sorted(Comparator.comparing(Pai::getJzs).reversed()).forEach(System.out::println);
    }


    private static int[] transform(int[] shoupai) {
        int[] res = new int[38];
        for (int i : shoupai) {
            res[i]++;
        }
        return res;
    }

    private static int duiduihu(int[] shoupai) {
        if (shoupai.length != 14)
            return 9;
        int count = 0;
        for (int i = 0; i < 13; i++) {
            if (shoupai[i] == shoupai[i + 1]) {
                count++;
                i++;
            }
        }
        return 7 - count;
    }


    private static int getXiangTing(int[] shouPaib) {
        int[] shouPai = shouPaib.clone();
        Arrays.sort(shouPai);
        S = duiduihu(shouPai);
        takeMianZi(shouPai, 0, 4 - (shouPai.length - 2) / 3);
        return S;
    }


    private static List<Pai> get(Desk desk) {
        int[] shouPai = desk.getShouPai();
        int[] paiHe = desk.getPaiHe();
        int[] allShouPai = transform(shouPai);
        List<Pai> pais = new ArrayList<>();
        int XT = getXiangTing(shouPai);
        if (XT == 0)
            return null;
        for (int i = 0; i < shouPai.length; i++) {
            final int index = shouPai[i];
            if (pais.stream().anyMatch(pai -> pai.getIndex() == index)) {
                continue;
            }
            Pai pai = new Pai(index);
            pai.setXts(XT - 1);
            int tmp = shouPai[i];
            int jzNum = 0;
            for (int j = 1; j <= 37; j++) {
                if (j % 10 == 0)
                    continue;
                shouPai[i] = j;
                int nextXT = getXiangTing(shouPai);
                if (nextXT < XT) {
                    int jz = 4 - paiHe[j] - allShouPai[j];
                    jzNum += jz;
                    pai.getJz().put(j, jz);
                }
            }
            shouPai[i] = tmp;
            pai.setJzs(jzNum);
            if (jzNum > 0)
                pais.add(pai);
        }
        return pais;
    }

    private static void takeMianZi(int[] shouPai, int i, int m) {
        if (i >= shouPai.length - 2) {
            takeDuizi(shouPai, m);
            return;
        }
        int[] removed = removeMianzi(shouPai, i);
        if (removed != null) {
            takeMianZi(removed, i, m + 1);
        }
        takeMianZi(shouPai, i + 1, m);
    }

    private static void takeDaZi(int[] shouPai, int i, int m, int p, int d) {

        if (i >= shouPai.length - 1 || d + m == 4) {
            d = Math.min(d, 4 - m);
            int mys = 9 - 2 * m - d - p;
            S = Math.min(S, mys);
            return;
        }
        if (isDazi(shouPai, i)) {
            int[] removed = remove(shouPai, i, 2);
            takeDaZi(removed, i, m, p, d + 1);
        }
        takeDaZi(shouPai, i + 1, m, p, d);
    }

    private static void takeDuizi(int[] shouPai, int m) {
        for (int i = 0; i < shouPai.length - 1; i++) {
            if (shouPai[i] == shouPai[i + 1]) {
                int[] removed = remove(shouPai, i, 2);
                if (i + 2 < shouPai.length && shouPai[i] == shouPai[i + 2])
                    i++;
                takeDaZi(removed, 0, m, 1, 0);
            }
        }
        takeDaZi(shouPai, 0, m, 0, 0);
    }

    private static int[] next(int[] shouPai, int i) {
        int current = shouPai[i];
        for (int j = i + 1; j < shouPai.length; j++) {
            if (shouPai[j] != current)
                return new int[]{shouPai[j], j};
        }
        return null;
    }

    private static int[] removeMianzi(int[] shouPai, int i) {

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
            for (int j = 0; j < shouPai.length; j++) {
                if (j < i) {
                    reserved[j] = shouPai[j];
                } else if (j < next[1] && j > i) {
                    reserved[j - 1] = shouPai[j];
                } else if (j < nextNext[1] && j > next[1]) {
                    reserved[j - 2] = shouPai[j];
                } else if (j > nextNext[1]) {
                    reserved[j - 3] = shouPai[j];
                }
            }
            return reserved;
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

    private static int[] remove(int[] shouPai, int i, int num) {
        int[] reserved = new int[shouPai.length - num];
        for (int j = 0; j < shouPai.length; j++) {
            if (j < i)
                reserved[j] = shouPai[j];
            else if (j >= i + num) {
                reserved[j - num] = shouPai[j];
            }
        }
        return reserved;
    }

    static List<Pai> get(List<Integer> paiList) {
        int[] shoupai = paiList.stream().mapToInt(Integer::valueOf).map(i -> i % 10 == 0 ? i + 5 : i).toArray();
        Desk desk = new Desk(shoupai);
        int[] paihe = new int[38];
        for (int i = 1; i < 38; i++) {
            if (i < 10)
                paihe[i] = Desk.M[i];
            else if (i > 10 && i < 20)
                paihe[i] = Desk.P[i - 10];
            else if (i > 20 && i < 30)
                paihe[i] = Desk.S[i - 20];
            else if (i > 30)
                paihe[i] = Desk.Z[i - 30];
        }
        desk.setPaiHe(paihe);
        return get(desk);
    }
}
