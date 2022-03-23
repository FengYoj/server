package com.jemmy.framework.utils.config;

public class BinaryUtils {

    /**
     * 将 字符串 转换成 二进制字符串，以 空格 相隔
     * @param str 字符串
     * @return 二进制
     */
    public static String stringToBinary(String str) {
        char[] strChar = str.toCharArray();
        StringBuilder result = new StringBuilder();

        for (char c : strChar) {
            result.append(Integer.toBinaryString(c)).append(" ");
        }

        return result.toString();
    }

    /**
     * 将 二进制字符串 转换为 char
     * @param binary 二进制
     * @return char
     */
    public static char binaryToChar(String binary){
        int[] arr = binaryToIntArray(binary);
        int sum = 0;

        for(int i = 0; i < arr.length; i++){
            sum += arr[arr.length - 1 - i] << i;
        }

        return (char) sum;
    }

    /**
     * 将 二进制字符串 转换成 int 数组
     * @param binary 二进制
     * @return int 数组
     */
    public static int[] binaryToIntArray(String binary) {
        char[] chars = binary.toCharArray();
        int[] result = new int[chars.length];

        for (int i = 0; i < chars.length; i++) {
            result[i] = chars[i] - 48;
        }

        return result;
    }

    public static String toString(String str) {
        String[] strings = str.split("\\s+");
        StringBuilder sb = new StringBuilder();

        for (String ss : strings) {
            sb.append(binaryToChar(ss));
        }

        return sb.toString();
    }
}
