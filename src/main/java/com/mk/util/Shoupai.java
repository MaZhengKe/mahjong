package com.mk.util;

import com.sun.istack.internal.NotNull;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Shoupai {
    private byte[] M = new byte[10];
    private byte[] P = new byte[10];
    private byte[] S = new byte[10];
    private byte[] Z = new byte[8];

    int[] pos = new int[14];
    private int level = 0;
    private List<Pai> ps = Collections.singletonList(new Pai(0, "t"));

    private String str;
    //private static Long count = 0L;


    public Shoupai(String s) {
        str = s;
        Pattern p = Pattern.compile("([0-9]*?)m([0-9]*)p([0-9]*)s([0-9]*)z");

        Matcher m = p.matcher(s);

        if (m.find()) {
            char[] ms = m.group(1).toCharArray();
            char[] ps = m.group(2).toCharArray();
            char[] ss = m.group(3).toCharArray();
            char[] zs = m.group(4).toCharArray();
            for (char c : ms) {
                M[(int) c - 48]++;
            }
            for (char c : ps) {
                P[(int) c - 48]++;
            }
            for (char c : ss) {
                S[(int) c - 48]++;
            }
            for (char c : zs) {
                Z[(int) c - 48]++;
            }
        }
//        fuzhang(M);
//        fuzhang(P);
//        fuzhang(S);
    }

    public boolean isWin() {
        //count++;
//        int key = AgariIndex.calc_key(M, P, S, Z);
//        int[] ret = AgariIndex.agari(key);
//        return ret != null;
        return search(M, 10) || search(P, 10) || search(S, 10) || search(Z, 8);
    }

    public boolean isTingPai() {
        List<Pai> pais = tingPai();
        if (pais.size() != 0)
            return true;
        return false;
    }

    public boolean isT1() {

        int i = XiangTingNotList.getXT(M, P, S, Z);
        return (i == 1);
    }

    public boolean isT2() {

        int i = XiangTingNotList.getXT(M, P, S, Z);
        return (i == 2);
    }

    public boolean isT3() {

        int i = XiangTingNotList.getXT(M, P, S, Z);
        return (i == 3);
    }

    public List<Pai> tingPai() {
        //返回打出当前每张牌后，该牌再来N张时的情况
        List<Pai> res = new ArrayList<>();

        search(M, "M", 10, res);
        if (res.size() > 0 && level > 1)
            return res;
        search(P, "P", 10, res);
        if (res.size() > 0 && level > 1)
            return res;
        search(S, "S", 10, res);
        if (res.size() > 0 && level > 1)
            return res;
        search(Z, "Z", 8, res);
        return res;
    }

    public List<Pai> t1() {
        List<Pai> res = new ArrayList<>();

        List<Pai> m = search1t(M, "M", 10);
        if (m.size() > 0 && level > 2)
            return m;
        res.addAll(m);
        List<Pai> p = search1t(P, "P", 10);
        if (p.size() > 0 && level > 2)
            return p;
        res.addAll(p);
        List<Pai> s = search1t(S, "S", 10);
        if (s.size() > 0 && level > 2)
            return s;
        res.addAll(s);
        List<Pai> z = search1t(Z, "Z", 8);
        if (z.size() > 0 && level > 2)
            return z;
        res.addAll(z);

        return res;
    }

    public List<Pai> t2() {
        List<Pai> res = new ArrayList<>();

        List<Pai> m = search2t(M, "M", 10);
        if (m.size() > 0 && level > 3)
            return m;
        res.addAll(m);
        List<Pai> p = search2t(P, "P", 10);
        if (p.size() > 0 && level > 3)
            return p;
        res.addAll(p);
        List<Pai> s = search2t(S, "S", 10);
        if (s.size() > 0 && level > 3)
            return s;
        res.addAll(s);
        List<Pai> z = search2t(Z, "Z", 8);
        if (z.size() > 0 && level > 3)
            return z;
        res.addAll(z);
        return res;
    }

    public List<Pai> t3() {
        List<Pai> res = new ArrayList<>();

        res.addAll(search3t(M, "M", 10));
        res.addAll(search3t(P, "P", 10));
        res.addAll(search3t(S, "S", 10));
        res.addAll(search3t(Z, "Z", 8));

        return res;
    }

    public List<Pai> t4() {
        List<Pai> res = new ArrayList<>();

        res.addAll(search4t(M, "M", 10));
        res.addAll(search4t(P, "P", 10));
        res.addAll(search4t(S, "S", 10));
        res.addAll(search4t(Z, "Z", 8));

        return res;
    }

    private void search(byte[] Q, String name, int num, List<Pai> res) {
        for (int i = 1; i < num; i++) {
            if (Q[i] > 0) {

                Q[i]--;
                Map<String, Integer> m = addToWin(M, "m", 10);
                if (level > 1 && m.size() > 0) {
                    Q[i]++;
                    return;
                }
                Map<String, Integer> jinZhang = new HashMap<>(m);
                Map<String, Integer> p = addToWin(P, "p", 10);
                if (level > 1 && p.size() > 0) {
                    Q[i]++;
                    return;
                }
                jinZhang.putAll(p);
                Map<String, Integer> s = addToWin(S, "s", 10);
                if (level > 1 && s.size() > 0) {
                    Q[i]++;
                    return;
                }
                jinZhang.putAll(s);
                Map<String, Integer> z = addToWin(Z, "z", 8);
                if (level > 1 && z.size() > 0) {
                    Q[i]++;
                    return;
                }
                jinZhang.putAll(z);
                Q[i]++;
                Pai pai = new Pai(i, name);
                pai.setJinZhang(jinZhang);
                if (jinZhang.size() > 0) {
                    int jzs = 0;
                    for (Integer value : jinZhang.values()) {
                        jzs += value;
                    }
                    pai.setJinZhangShu(jzs);
                    res.add(pai);
                }
            }
        }
    }

    private List<Pai> search1t(byte[] Q, String name, int num) {
        List<Pai> res = new ArrayList<>();
        for (int i = 1; i < num; i++) {
            if (Q[i] > 0) {

                Q[i]--;
                Map<String, Integer> jinZhang = new HashMap<>();
                Map<String, Integer> m = addTo1t(M, "m", 10);
                if (level > 2 && m.size() > 0) {
                    Q[i]++;
                    return ps;
                }
                jinZhang.putAll(m);
                Map<String, Integer> p = addTo1t(P, "p", 10);
                if (level > 2 && p.size() > 0) {
                    Q[i]++;
                    return ps;
                }
                jinZhang.putAll(p);
                Map<String, Integer> s = addTo1t(S, "s", 10);
                if (level > 2 && s.size() > 0) {
                    Q[i]++;
                    return ps;
                }
                jinZhang.putAll(s);
                Map<String, Integer> z = addTo1t(Z, "z", 8);
                if (level > 2 && z.size() > 0) {
                    Q[i]++;
                    return ps;
                }
                jinZhang.putAll(z);
                Q[i]++;
                Pai pai = new Pai(i, name);
                pai.setJinZhang(jinZhang);
                if (jinZhang.size() > 0) {
                    int jzs = 0;
                    for (Integer value : jinZhang.values()) {
                        jzs += value;
                    }
                    pai.setJinZhangShu(jzs);
                    res.add(pai);
                }
            }
        }
        return res;
    }

    private List<Pai> search2t(byte[] Q, String name, int num) {
        List<Pai> res = new ArrayList<>();
        for (int i = 1; i < num; i++) {
            if (Q[i] > 0) {

                Q[i]--;
                Map<String, Integer> jinZhang = new HashMap<>();
                Map<String, Integer> m = addTo2t(M, "m", 10);
                if (level > 3 && m.size() > 0) {
                    Q[i]++;
                    return ps;
                }
                jinZhang.putAll(m);
                Map<String, Integer> p = addTo2t(P, "p", 10);
                if (level > 3 && p.size() > 0) {
                    Q[i]++;
                    return ps;
                }
                jinZhang.putAll(p);
                Map<String, Integer> s = addTo2t(S, "s", 10);
                if (level > 3 && s.size() > 0) {
                    Q[i]++;
                    return ps;
                }
                jinZhang.putAll(s);
                Map<String, Integer> z = addTo2t(Z, "z", 8);
                if (level > 3 && z.size() > 0) {
                    Q[i]++;
                    return ps;
                }
                jinZhang.putAll(z);
                Q[i]++;
                Pai pai = new Pai(i, name);
                pai.setJinZhang(jinZhang);
                if (jinZhang.size() > 0) {
                    int jzs = 0;
                    for (Integer value : jinZhang.values()) {
                        jzs += value;
                    }
                    pai.setJinZhangShu(jzs);
                    res.add(pai);
                }
            }
        }
        return res;
    }


    private List<Pai> search2taaa(byte[] Q, String name, int num) {
        List<Pai> res = new ArrayList<>();
        for (int i = 1; i < num; i++) {
            if (Q[i] > 0) {

                Q[i]--;
                Map<String, Integer> jinZhang = new HashMap<>();
                Map<String, Integer> m = addTo2t(M, "m", 10);
                if (level > 3 && m.size() > 0) {
                    Q[i]++;
                    return ps;
                }
                jinZhang.putAll(m);
                Map<String, Integer> p = addTo2t(P, "p", 10);
                if (level > 3 && p.size() > 0) {
                    Q[i]++;
                    return ps;
                }
                jinZhang.putAll(p);
                Map<String, Integer> s = addTo2t(S, "s", 10);
                if (level > 3 && s.size() > 0) {
                    Q[i]++;
                    return ps;
                }
                jinZhang.putAll(s);
                Map<String, Integer> z = addTo2t(Z, "z", 8);
                if (level > 3 && z.size() > 0) {
                    Q[i]++;
                    return ps;
                }
                jinZhang.putAll(z);
                Q[i]++;
                Pai pai = new Pai(i, name);
                pai.setJinZhang(jinZhang);
                if (jinZhang.size() > 0) {
                    int jzs = 0;
                    for (Integer value : jinZhang.values()) {
                        jzs += value;
                    }
                    pai.setJinZhangShu(jzs);
                    res.add(pai);
                }
            }
        }
        return res;
    }

    private List<Pai> search3t(byte[] Q, String name, int num) {
        List<Pai> res = new ArrayList<>();
        for (int i = 1; i < num; i++) {
            if (Q[i] > 0) {
                //便利依次切出手牌

                Q[i]--;
                Map<String, Integer> jinZhang = new HashMap<>();
                jinZhang.putAll(addTo3t(M, "m", 10));
                jinZhang.putAll(addTo3t(P, "p", 10));
                jinZhang.putAll(addTo3t(S, "s", 10));
                jinZhang.putAll(addTo3t(Z, "z", 8));
                Q[i]++;
                Pai pai = new Pai(i, name);
                pai.setJinZhang(jinZhang);
                if (jinZhang.size() > 0) {
                    int jzs = 0;
                    for (Integer value : jinZhang.values()) {
                        jzs += value;
                    }
                    pai.setJinZhangShu(jzs);
                    res.add(pai);
                }
            }
        }
        return res;
    }

    private List<Pai> search4t(byte[] Q, String name, int num) {
        List<Pai> res = new ArrayList<>();
        for (int i = 1; i < num; i++) {
            if (Q[i] > 0) {
                //便利依次切出手牌

                Q[i]--;
                Map<String, Integer> jinZhang = new HashMap<>();
                jinZhang.putAll(addTo4t(M, "m", 10));
                jinZhang.putAll(addTo4t(P, "p", 10));
                jinZhang.putAll(addTo4t(S, "s", 10));
                jinZhang.putAll(addTo4t(Z, "z", 8));
                Q[i]++;
                Pai pai = new Pai(i, name);
                pai.setJinZhang(jinZhang);
                if (jinZhang.size() > 0) {
                    int jzs = 0;
                    for (Integer value : jinZhang.values()) {
                        jzs += value;
                    }
                    pai.setJinZhangShu(jzs);
                    res.add(pai);
                }
            }
        }
        return res;
    }

    private Map<String, Integer> addToWin(byte[] Q, String name, int num) {

        Map<String, Integer> jinZhang = new HashMap<>();
        boolean z = name.equals("z");
        for (int j = 1; j < num; j++) {
            if (!z)
                switch (j) {
                    case 1:
                        if (Q[1] == 0 && Q[2] == 0)
                            continue;
                        break;
                    case 9:
                        if (Q[9] == 0 && Q[8] == 0)
                            continue;
                        break;
                    default:
                        if (Q[j - 1] == 0 && Q[j] == 0 && Q[j + 1] == 0)
                            continue;
                        break;
                }
            else if (Q[j] != 2)
                continue;

            int q = getQFromName(name, j);

            if (Q[j] < (4 - q)) {
                Q[j]++;
                if (isWin()) {
                    jinZhang.put(j + name, 5 - q - Q[j]);
                    if (level > 1) {
                        Q[j]--;
                        return jinZhang;
                    }
                }
                Q[j]--;
            }
        }
        return jinZhang;
    }

    @NotNull
    private int getQFromName(String name, int j) {
        switch (name) {
            case "m":
            case "M":
                return Dask.M[j] + Dask.PM[j];
            case "p":
            case "P":
                return Dask.P[j] + Dask.PP[j];
            case "s":
            case "S":
                return Dask.S[j] + Dask.PS[j];
            case "z":
            case "Z":
            default:
                return Dask.Z[j] + Dask.PZ[j];
        }
    }

    private Map<String, Integer> addTo1t(byte[] Q, String name, int num) {

        Map<String, Integer> jinZhang = new HashMap<>();
        for (int j = 1; j < num; j++) {
            int q = getQFromName(name, j);

            if (Q[j] < (4 - q)) {
                Q[j]++;
                if (isTingPai()) {
                    jinZhang.put(j + name, 5 - q - Q[j]);
                    if (level > 2) {
                        Q[j]--;
                        return jinZhang;
                    }
                }
                Q[j]--;
            }
        }
        return jinZhang;
    }

    private Map<String, Integer> addTo2t(byte[] Q, String name, int num) {

        Map<String, Integer> jinZhang = new HashMap<>();
        for (int j = 1; j < num; j++) {
            int q = getQFromName(name, j);

            if (Q[j] < (4 - q)) {
                Q[j]++;
                if (isT1()) {
                    jinZhang.put(j + name, 5 - q - Q[j]);
                    if (level > 3) {
                        Q[j]--;
                        return jinZhang;
                    }
                }
                Q[j]--;
            }
        }
        return jinZhang;
    }

    private Map<String, Integer> addTo3t(byte[] Q, String name, int num) {

        Map<String, Integer> jinZhang = new HashMap<>();
        for (int j = 1; j < num; j++) {
            int q = getQFromName(name, j);

            if (Q[j] < (4 - q)) {
                Q[j]++;
                if (isT2()) {
                    jinZhang.put(j + name, 5 - q - Q[j]);
                    if (level > 4) {
                        Q[j]--;
                        return jinZhang;
                    }
                }
                Q[j]--;
            }
        }
        return jinZhang;
    }

    private Map<String, Integer> addTo4t(byte[] Q, String name, int num) {

        Map<String, Integer> jinZhang = new HashMap<>();
        for (int j = 1; j < num; j++) {
            int q = getQFromName(name, j);

            if (Q[j] < (4 - q)) {
                Q[j]++;
                if (isT3()) {
                    jinZhang.put(j + name, 5 - q - Q[j]);
                    if (level > 4) {
                        Q[j]--;
                        return jinZhang;
                    }
                }
                Q[j]--;
            }
        }
        return jinZhang;
    }

    private boolean search(byte[] m, int num) {
        for (int i = 1; i < num; i++) {
            if (m[i] >= 2) {
                m[i] -= 2;
                if (isToWin()) {
                    m[i] += 2;
                    return true;
                } else {
                    m[i] += 2;
                }
            }
        }
        return false;
    }

    private boolean isToWin() {
        return searchMPSZ(M) && searchMPSZ(P) && searchMPSZ(S) && searchZ(Z);
    }

    private boolean searchZ(byte[] z) {
        for (int i = 1; i < 8; i++) {
            switch (z[i]) {
                case 0:
                    break;
                case 1:
                case 2:
                    return false;
                case 3:
                case 4:
                    z[i] -= 3;
                    if (isToWin()) {
                        z[i] += 3;
                        return true;
                    } else {
                        z[i] += 3;
                        return false;
                    }
                default:
                    return false;
            }
        }
        return true;
    }

    private boolean searchMPSZ(byte[] Q) {
        //检查该组
        int size = 0;
        for (int i = 1; i < 10; i++) {
            if (Q[i] == 0) {
                if (size % 3 != 0)
                    return false;
            } else {
                size += Q[i];
            }
        }
        if (size % 3 != 0)
            return false;

        for (int i = 1; i < 10; i++) {
            switch (Q[i]) {
                case -1:
                case 0:
                    break;
                case 1:
                case 2:
                    return isKezi(Q, i);
                case 3:
                    Q[i] -= 3;
                    if (isToWin()) {
                        Q[i] += 3;
                        return true;
                    } else {
                        Q[i] += 3;
                        return isKezi(Q, i);
                    }
                case 4:
                    Q[i] -= 3;
                    if (isToWin()) {
                        Q[i] += 3;
                        return true;
                    } else {
                        Q[i] += 3;
                        return false;
                    }
            }
        }
        return true;
    }

    private boolean isKezi(byte[] Q, int i) {
        switch (i) {
            case 1:
                if (Q[2] > 0 && Q[3] > 0) {
                    sub(Q, 1);
                    if (isToWin()) {
                        add(Q, 1);
                        return true;
                    } else {
                        add(Q, 1);
                        return false;
                    }
                } else {
                    return false;
                }
            case 2:
                if (Q[1] > 0 && Q[3] > 0) {
                    sub(Q, 1);
                    if (isToWin()) {
                        add(Q, 1);
                        return true;
                    } else {
                        add(Q, 1);
                        if (Q[3] > 0 && Q[4] > 0) {
                            sub(Q, 2);
                            if (isToWin()) {
                                add(Q, 2);
                                return true;
                            } else {
                                add(Q, 2);
                                return false;
                            }
                        } else {
                            return false;
                        }
                    }
                } else if (Q[3] > 0 && Q[4] > 0) {
                    sub(Q, 2);
                    if (isToWin()) {
                        add(Q, 2);
                        return true;
                    } else {
                        add(Q, 2);
                        return false;
                    }
                } else {
                    return false;
                }
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                if (Q[i - 2] > 0 && Q[i - 1] > 0) {
                    sub(Q, i - 2);
                    if (isToWin()) {
                        add(Q, i - 2);
                        return true;
                    } else {
                        add(Q, i - 2);
                        if (Q[i - 1] > 0 && Q[i + 1] > 0) {
                            sub(Q, i - 1);
                            if (isToWin()) {
                                add(Q, i - 1);
                                return true;
                            } else {
                                add(Q, i - 1);
                                if (Q[i + 1] > 0 && Q[i + 2] > 0) {
                                    sub(Q, i);
                                    if (isToWin()) {
                                        add(Q, i);
                                        return true;
                                    } else {
                                        add(Q, i);
                                        return false;
                                    }
                                } else {
                                    return false;
                                }
                            }
                        } else if (Q[i + 1] > 0 && Q[i + 2] > 0) {
                            sub(Q, i);
                            if (isToWin()) {
                                add(Q, i);
                                return true;
                            } else {
                                add(Q, i);
                                return false;
                            }
                        } else {
                            return false;
                        }
                    }
                } else if (Q[i - 1] > 0 && Q[i + 1] > 0) {
                    sub(Q, i - 1);
                    if (isToWin()) {
                        add(Q, i - 1);
                        return true;
                    } else {
                        add(Q, i - 1);
                        if (Q[i + 1] > 0 && Q[i + 2] > 0) {
                            sub(Q, i);
                            if (isToWin()) {
                                add(Q, i);
                                return true;
                            } else {
                                add(Q, i);
                                return false;
                            }
                        } else {
                            return false;
                        }
                    }
                } else if (Q[i + 1] > 0 && Q[i + 2] > 0) {
                    sub(Q, i);
                    if (isToWin()) {
                        add(Q, i);
                        return true;
                    } else {
                        add(Q, i);
                        return false;
                    }
                } else {
                    return false;
                }
            case 8:
                if (Q[9] > 0 && Q[7] > 0) {
                    sub(Q, 7);
                    if (isToWin()) {
                        add(Q, 7);
                        return true;
                    } else {
                        add(Q, 7);
                        if (Q[7] > 0 && Q[6] > 0) {
                            sub(Q, 6);
                            if (isToWin()) {
                                add(Q, 6);
                                return true;
                            } else {
                                add(Q, 6);
                                return false;
                            }
                        } else {
                            return false;
                        }
                    }
                } else if (Q[7] > 0 && Q[6] > 0) {
                    sub(Q, 6);
                    if (isToWin()) {
                        add(Q, 6);
                        return true;
                    } else {
                        add(Q, 6);
                        return false;
                    }
                } else {
                    return false;
                }
            case 9:
                if (Q[8] > 0 && Q[7] > 0) {
                    sub(Q, 7);
                    if (isToWin()) {
                        add(Q, 7);
                        return true;
                    } else {
                        add(Q, 7);
                        return false;
                    }
                } else {
                    return false;
                }

        }
        System.out.println("ERROR 01");
        return false;
    }

    public void add(byte[] a, int i) {
        a[i]++;
        a[i + 1]++;
        a[i + 2]++;
    }

    public void sub(byte[] a, int i) {
        a[i]--;
        a[i + 1]--;
        a[i + 2]--;
    }

    public void fuzhang(byte[] Q) {
        for (int i = 1; i < 10; i++) {
            switch (i) {
                case 1:
                    if (Q[1] <= 0 && Q[2] <= 0 && Q[3] <= 0)
                        Q[1] = -1;
                    break;
                case 9:
                    if (Q[7] <= 0 && Q[8] <= 0 && Q[9] <= 0)
                        Q[9] = -1;
                    break;
                case 2:
                    if (Q[1] <= 0 && Q[2] <= 0 && Q[3] <= 0 && Q[4] <= 0)
                        Q[2] = -1;
                    break;
                case 8:
                    if (Q[6] <= 0 && Q[7] <= 0 && Q[8] <= 0 && Q[9] <= 0)
                        Q[8] = -1;
                    break;
                default:
                    if (Q[i - 2] <= 0 && Q[i - 1] <= 0 && Q[i] <= 0 && Q[i + 1] <= 0 && Q[i + 2] <= 0)
                        Q[i] = -1;
            }
        }
    }


    public Pai getRes() {

        level = 0;
        boolean win = isWin();
        if (win) {
            return null;
        }
        System.out.println();

        List<Pai> tingPai = tingPai();
        Optional<Pai> pai0 = tingPai.stream().sorted().findFirst();
        if (pai0.isPresent()) {
            return pai0.get();
        }

        List<Pai> t1 = t1();
        //t1.forEach(System.out::println);
        Optional<Pai> pai1 = t1.stream().sorted().findFirst();
        if (pai1.isPresent()) {
            return pai1.get();
        }

        List<Pai> t2 = t2();
        //t2.forEach(System.out::println);
        Optional<Pai> pai2 = t2.stream().sorted().findFirst();
        if (pai2.isPresent()) {
            return pai2.get();
        }

        List<Pai> t3 = t3();
        //t3.forEach(System.out::println);
        Optional<Pai> pai3 = t3.stream().sorted().findFirst();
        if (pai3.isPresent()) {
            return pai3.get();
        }

        List<Pai> t4 = t4();
        //t4.forEach(System.out::println);
        Optional<Pai> pai4 = t4.stream().sorted().findFirst();
        return pai4.orElse(null);

    }

    public static void main(String[] args) throws InterruptedException {
        Shoupai shoupai = new Shoupai("77m45889p8s444z");
        int i = XiangTingShu.getXT(shoupai.M, shoupai.P, shoupai.S, shoupai.Z);
        System.out.println(i);


        while (true) {
            long start = System.currentTimeMillis();
            boolean win = shoupai.isWin();
            if (win) {
                shoupai.level = 0;
                System.out.println("Win");
                System.out.println("time:" + (System.currentTimeMillis() - start));
                return;
            }
            if (shoupai.isTingPai()) {
                System.out.println("听牌 ");
                for (Pai p : shoupai.tingPai()) {
                    System.out.println(p);
                }
                System.out.println("time:" + (System.currentTimeMillis() - start));
                return;
            }
            if (shoupai.isT1()) {
                System.out.println("一向听");
                for (Pai p : shoupai.t1()) {
                    System.out.println(p);
                }
                System.out.println("time:" + (System.currentTimeMillis() - start));
                return;
            }
            if (shoupai.isT2()) {
                System.out.println("两向听");
                for (Pai p : shoupai.t2()) {
                    System.out.println(p);
                }
                System.out.println("time:" + (System.currentTimeMillis() - start));
                return;
            }
            start = System.currentTimeMillis();
            System.out.println("三向听");
            for (Pai p : shoupai.t3()) {
                System.out.println(p);
            }
            System.out.println("time:" + (System.currentTimeMillis() - start));
            return;
        }
    }
}
