package mongo.dao.util;


import mongo.dao.exception.BaseException;

import java.util.Random;

public class MathUtil {

    /**
     * 范围随机数(包含min,max)
     *
     * @param min 最小值
     * @param max 最大值
     * @return 随机数
     */
    public static int random(int min, int max) {
        Random random = new Random();
        return random.nextInt(max) % (max - min + 1) + min;
    }

    /**
     * 范围随机数(不包含min,max)
     *
     * @param min 最小值
     * @param max 最大值
     * @return 随机数
     */
    public static int randomNoContain(int min, int max) {
        if (max - 1 <= 0) {
            throw new BaseException("max is error");
        }
        return random(min + 1, max - 1);
    }

    /**
     * 随机指定范围内N个不重复的数
     * 最简单最基本的方法
     *
     * @param min 指定范围最小值
     * @param max 指定范围最大值
     * @param n   随机数个数
     */
    public static int[] randomCommon(int min, int max, int n) {
        if (n > (max - min + 1) || max < min) {
            return null;
        }
        int[] result = new int[n];
        int count = 0;
        while (count < n) {
            int num = (int) (Math.random() * (max - min)) + min;
            boolean flag = true;
            for (int j = 0; j < n; j++) {
                if (num == result[j]) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                result[count] = num;
                count++;
            }
        }
        return result;
    }


    /**
     * 随机指定范围内N个不重复的数
     * 最简单最基本的方法
     *  @param min 指定范围最小值
     * @param max 指定范围最大值
     * @param n   随机数个数
     */
    public static long[] randomCommon(long min, long max, int n) {
        if (n > (max - min + 1) || max < min) {
            return null;
        }
        long[] result = new long[n];
        int count = 0;
        while (count < n) {
            long num = (long) ((Math.random() * (max - min)) + min);
            boolean flag = true;
            for (int j = 0; j < n; j++) {
                if (num == result[j]) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                result[count] = num;
                count++;
            }
        }
        return result;
    }
}
