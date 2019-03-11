package com.mk.util;

import org.opencv.core.Mat;

import static java.lang.Math.min;

public class XiangTingShu {
    public static int getXTS(byte[]... b) {
        byte[][] a = clone(b);
        Integer S = 8, C_max = 0;
        getQT(a, size(a), S, C_max, (size(a) - 2) / 3);
        return S;
    }

    public static int size(byte[]... a) {
        int count = 0;
        for (byte[] bytes : a) {
            for (byte aByte : bytes) {
                count += aByte;
            }
        }
        return count;
    }

    public static void getQT(byte[][] a, int C_rem, Integer S, Integer C_max, int K) {
        for (byte[] m : a) {
            for (int i = 1; i < m.length; i++) {
                if (m[i] >= 2) {
                    m[i] -= 2;
                    byte[][] clone = a.clone();
                    m[i] += 2;
                    getKS(clone, 0, 0,C_rem - 2, S, C_max, K, 1, 0);
                }
            }
        }
        getKS(a, 0, 0, C_rem, S, C_max, K, 0, 0);
    }


    public static void getKS(byte[][] b, int i, int j, int C_rem, Integer S, Integer C_max, int K, int P, int G) {
        byte[][] a = b.clone();
        if (i == 3 && j == 7)
            getKSS(a, C_rem, S, C_max, K, P, G, 0);

        boolean get = putKS(a, i, j);
//
//        C_rem -= 3 * count;
//        G += count;

        getKSS(a, C_rem, S, C_max, K, P, G, 0);
    }

    public static int putKS(byte[]... a) {
        int count = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 1; j < 10; j++) {
                if (j < 8) {
                    if (a[i][j] >= 3) {
                        a[i][j] -= 3;
                        count++;
                    }

                    if (a[i][j] > 0 && a[i][j + 1] > 0 && a[i][j + 2] > 0) {
                        a[i][j]--;
                        a[i][j + 1]--;
                        a[i][j + 2]--;
                        count++;
                    }

                    if (a[i][j] > 0 && a[i][j + 1] > 0 && a[i][j + 2] > 0) {
                        a[i][j]--;
                        a[i][j + 1]--;
                        a[i][j + 2]--;
                        count++;
                    }

                } else {
                    if (a[i][j] >= 3) {
                        a[i][j] -= 3;
                        count++;
                    }
                }
            }
        }
        for (int j = 1; j < 8; j++) {
            if (a[3][j] >= 3) {
                a[3][j] -= 3;
                count++;
            }
        }
        return count;
    }

    public static boolean putKS(byte[][] a, int ii, int jj) {
        for (int i = ii; i < 3; i++) {
            for (int j = jj; j < 10; j++) {
                if (j < 8) {
                    if (a[i][j] >= 3) {
                        a[i][j] -= 3;
                        return true;
                    }

                    if (a[i][j] > 0 && a[i][j + 1] > 0 && a[i][j + 2] > 0) {
                        a[i][j]--;
                        a[i][j + 1]--;
                        a[i][j + 2]--;
                        return true;
                    }

                } else {
                    if (a[i][j] >= 3) {
                        a[i][j] -= 3;
                        return true;
                    }
                }
            }
        }
        if (ii < 3)
            for (int j = jj; j < 8; j++) {
                if (a[3][j] >= 3) {
                    a[3][j] -= 3;
                    return true;
                }
            }
        return false;
    }

    public static int getXT(byte[]... a) {
        int size = 4 - (size(a) - 2) / 3;

        int min = 10;
        byte[][] b = clone(a);
        int mianzi = putKS(b) + size;

        for (byte[] m : b) {
            for (int i = 1; i < m.length; i++) {
                if (m[i] >= 2) {
                    m[i] -= 2;
                    int dazi = getKSS(b);
                    if (dazi + mianzi > 4)
                        dazi = 4 - mianzi;
                    min = min(min, 7 - mianzi * 2 - dazi);
                    m[i] += 2;
                }
            }
        }
        int dazi = getKSS(b);
        if (dazi + mianzi > 4)
            dazi = 4 - mianzi;
        min = min(min, 8 - mianzi * 2 - dazi);
        return min;
    }

    private static byte[][] clone(byte[][] a) {
        byte[][] b = new byte[a.length][];
        for (int i = 0; i < a.length; i++) {
            b[i] = a[i].clone();
        }
        return b;
    }

    public static int getKSS(byte[][] b) {
        byte[][] a = clone(b);
        int count = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 1; j < 10; j++) {
                if (j < 8) {
                    if (a[i][j] >= 2) {
                        a[i][j] -= 2;
                        count++;
                    }

                    if (a[i][j] > 0 && a[i][j + 1] > 0) {
                        a[i][j]--;
                        a[i][j + 1]--;
                        count++;
                    }

                    if (a[i][j] > 0 && a[i][j + 2] > 0) {
                        a[i][j]--;
                        a[i][j + 2]--;
                        count++;
                    }

                } else if (j < 9) {

                    if (a[i][j] >= 2) {
                        a[i][j] -= 2;
                        count++;
                    }

                    if (a[i][j] > 0 && a[i][j + 1] > 0) {
                        a[i][j]--;
                        a[i][j + 1]--;
                        count++;
                    }
                } else {
                    if (a[i][j] >= 2) {
                        a[i][j] -= 2;
                        count++;
                    }
                }
            }
        }
        for (int j = 1; j < 8; j++) {
            if (a[3][j] >= 2) {
                a[3][j] -= 2;
                count++;
            }
        }
        return count;
    }


    public static void main(String[] args) {

    }

    public static void getKSS(byte[][] b, int C_rem, Integer S, Integer C_max, int K, int P, int G, int Gs) {
        byte[][] a = b.clone();
        if (S == -1) {
            return;
        }
        if ((G + Gs) > K) {
            return;
        }
        int C = 3 * G + 2 * Gs + 2 * P;
        if (C_rem < C_max - C) {
            return;
        }
        if (C_rem == 0) {
            S = min(S, 2 * (K - G) - Gs - P);
            C_max = Math.max(C_max, C);
            return;

        }
        int count = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 1; j < 10; j++) {
                if (j < 8) {
                    if (a[i][j] >= 2) {
                        a[i][j] -= 2;
                        count++;
                    }

                    if (a[i][j] > 0 && a[i][j + 1] > 0) {
                        a[i][j]--;
                        a[i][j + 1]--;
                        count++;
                    }

                    if (a[i][j] > 0 && a[i][j + 2] > 0) {
                        a[i][j]--;
                        a[i][j + 2]--;
                        count++;
                    }

                } else if (j < 9) {

                    if (a[i][j] >= 2) {
                        a[i][j] -= 2;
                        count++;
                    }

                    if (a[i][j] > 0 && a[i][j + 1] > 0) {
                        a[i][j]--;
                        a[i][j + 1]--;
                        count++;
                    }
                } else {
                    if (a[i][j] >= 2) {
                        a[i][j] -= 2;
                        count++;
                    }
                }
            }
        }
        for (int j = 1; j < 8; j++) {
            if (a[3][j] >= 2) {
                a[3][j] -= 2;
                count++;
            }
        }
        col(C_rem - 2 * count, S, C_max, K, P, G, Gs + count);
    }

    public static void col(int C_rem, Integer S, Integer C_max, int K, int P, int G, int Gs) {
        if (S == -1) {
            return;
        }
        if ((G + Gs) > K) {
            return;
        }
        int C = 3 * G + 2 * Gs + 2 * P;
        if (C_rem < C_max - C) {
            return;
        }
        if (C_rem == 0) {
            S = min(S, 2 * (K - G) - Gs - P);
            C_max = Math.max(C_max, C);
            return;
        }
        col(C_rem - 1, S, C_max, K, P, G, Gs);
    }
}

