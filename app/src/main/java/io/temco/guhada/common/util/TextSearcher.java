package io.temco.guhada.common.util;

public class TextSearcher {

    // -------- LOCAL VALUE --------
    private static final char HANGUL_BEGIN_UNICODE = 44032; // 가
    private static final char HANGUL_LAST_UNICODE = 55203; // 힣
    private static final char HANGUL_BASE_UNIT = 588; // 각자음마다 가지는 글자수
    private static final char[] HANGUL_INITIAL_SOUND = {'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'};
    // -----------------------------

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public TextSearcher() {
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    // Match
    public static boolean matchString(String value, String search) {
        int seof = value.length() - search.length();
        if (seof < 0) return false;
        int slen = search.length();
        int t;
        for (int i = 0; i <= seof; i++) {
            t = 0;
            while (t < slen) {
                if (isInitialHangulSound(search.charAt(t)) && isHangul(value.charAt(i + t))) {
                    if (getInitialHangulSound(value.charAt(i + t)) == search.charAt(t)) {
                        t++;
                    } else {
                        break;
                    }
                } else {
                    if (value.charAt(i + t) == search.charAt(t)) {
                        t++;
                    } else {
                        break;
                    }
                }
            }
            if (t == slen) return true;
        }
        return false;
    }

    public static char getInitialHangulSound(char c) {
        int hanBegin = (c - HANGUL_BEGIN_UNICODE);
        int index = hanBegin / HANGUL_BASE_UNIT;
        return HANGUL_INITIAL_SOUND[index];
    }

//    public static char getInitialAlphabetSound(char c) {
//        for (char c : HANGUL_INITIAL_SOUND) {
//            if (c == searchar) {
//                return true;
//            }
//        }
//        return ALPHABET_INITIAL_SOUND[index];
//    }

    public static boolean isInitialHangulSound(char searchar) {
        for (char c : HANGUL_INITIAL_SOUND) {
            if (c == searchar) {
                return true;
            }
        }
        return false;
    }

    public static boolean isHangul(char c) {
        return HANGUL_BEGIN_UNICODE <= c && c <= HANGUL_LAST_UNICODE;
    }

    public static boolean isAlphabet(char c) {
        return Character.isAlphabetic(c);
    }

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    ////////////////////////////////////////////////
}