package com.kmaebashi.samplan.util;
import java.util.*;

public class Util {
    public static int[] arrayListToArrayInt(ArrayList<Integer> arrayList) {
        int[] ret = new int[arrayList.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = arrayList.get(i);
        }
        return ret;
    }

    public static double[] arrayListToArrayDouble(ArrayList<Double> arrayList) {
        double[] ret = new double[arrayList.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = arrayList.get(i);
        }
        return ret;
    }
}