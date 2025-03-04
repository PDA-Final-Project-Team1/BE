package com.team1.etcore.stock.util;

public final class HangulUtils {

    private static final char HANGUL_BASE = 0xAC00;
    private static final char HANGUL_END = 0xD7A3;
    private static final char[] INITIAL = {'ㄱ','ㄲ','ㄴ','ㄷ','ㄸ','ㄹ','ㅁ','ㅂ','ㅃ','ㅅ',
                                           'ㅆ','ㅇ','ㅈ','ㅉ','ㅊ','ㅋ','ㅌ','ㅍ','ㅎ'};

    private HangulUtils() {
    }

    public static String getInitials(String text) {
        StringBuilder sb = new StringBuilder();
        for (char ch : text.toCharArray()) {
            if (ch >= HANGUL_BASE && ch <= HANGUL_END) {
                int index = (ch - HANGUL_BASE) / (21 * 28);
                sb.append(INITIAL[index]);
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }
}