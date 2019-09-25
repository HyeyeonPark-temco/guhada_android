package io.temco.guhada.common.util;

import android.text.TextUtils;

import java.text.Collator;
import java.util.Comparator;

import io.temco.guhada.data.model.Brand;

public class CustomComparator {

    // -------- LOCAL VALUE --------
    private static final int REVERSE = -1;
    private static final int LEFT_FIRST = -1;
    private static final int RIGHT_FIRST = 1;
    // -----------------------------

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    public static boolean isEnglish(char ch) {
        return (ch >= (int) 'A' && ch <= (int) 'Z')
                || (ch >= (int) 'a' && ch <= (int) 'z');
    }

    public static boolean isKorean(char ch) {
        return ch >= Integer.parseInt("AC00", 16)
                && ch <= Integer.parseInt("D7A3", 16);
    }

    public static boolean isNumber(char ch) {
        return ch >= (int) '0' && ch <= (int) '9';
    }

    public static boolean isSpecial(char ch) {
        return (ch >= (int) '!' && ch <= (int) '/') // !"#$%&'()*+,-./
                || (ch >= (int) ':' && ch <= (int) '@') //:;<=>?@
                || (ch >= (int) '[' && ch <= (int) '`') //[\]^_`
                || (ch >= (int) '{' && ch <= (int) '~'); //{|}~
    }

    public static Comparator<String> getComparator() {
        return (left, right) -> compareString(left, right);
    }

    public static Comparator<Brand> getBrandComparator(boolean isNormal, boolean isAlphabet) {
        return isNormal ? new NormalBrandComparator(isAlphabet) : new CustomBrandComparator(isAlphabet);
    }

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private static class NormalBrandComparator implements Comparator<Brand> {

        private boolean mIsAlphabet;

        public NormalBrandComparator(boolean isAlphabet) {
            mIsAlphabet = isAlphabet;
        }

        @Override
        public int compare(Brand o1, Brand o2) {
            return mIsAlphabet ? compareString(o1.nameEn, o2.nameEn) : compareString(o1, o2);
        }
    }

    private static class CustomBrandComparator implements Comparator<Brand> {

        private final Collator collator = Collator.getInstance();
        private boolean mIsAlphabet;

        public CustomBrandComparator(boolean isAlphabet) {
            mIsAlphabet = isAlphabet;
        }

        @Override
        public int compare(Brand o1, Brand o2) {
            // MediaStore.Audio.keyFor
            return mIsAlphabet ? collator.compare(o1.nameEn, o2.nameEn) : collator.compare(o1.nameKo, o2.nameKo);
        }
    }


    private static int compareString(String left, String right) {
        //if(CustomLog.getFlag())CustomLog.L("compareString","left",left,"right",right);

        left = left.toUpperCase().replaceAll(" ", "");
        right = right.toUpperCase().replaceAll(" ", "");

        int leftLen = left.length();
        int rightLen = right.length();
        int minLen = Math.min(leftLen, rightLen);

        for (int i = 0; i < minLen; ++i) {
            char leftChar = left.charAt(i);
            char rightChar = right.charAt(i);
            if (leftChar != rightChar) {
                if (isKoreanAndEnglish(leftChar, rightChar)
                        || isKoreanAndNumber(leftChar, rightChar)
                        || isEnglishAndNumber(leftChar, rightChar)
                        || isKoreanAndSpecial(leftChar, rightChar)) {
                    return (leftChar - rightChar) * REVERSE;
                } else if (isEnglishAndSpecial(leftChar, rightChar)
                        || isNumberAndSpecial(leftChar, rightChar)) {
                    if (isEnglish(leftChar) || isNumber(leftChar)) {
                        return LEFT_FIRST;
                    } else {
                        return RIGHT_FIRST;
                    }
                } else {
                    return leftChar - rightChar;
                }
            }
        }

        return leftLen - rightLen;
    }

    private static int compareString(Brand o1, Brand o2) {
        //if(CustomLog.getFlag())CustomLog.L("compareString","o1",o1.toString(),"o2",o2.toString());
        String left = (!TextUtils.isEmpty(o1.nameKo) && !"null".equals(o1.nameKo)) ? o1.nameKo : o1.nameDefault;
        String right = (!TextUtils.isEmpty(o2.nameKo) && !"null".equals(o2.nameKo)) ? o2.nameKo : o2.nameDefault;

        left = left.toUpperCase().replaceAll(" ", "");
        right = right.toUpperCase().replaceAll(" ", "");

        int leftLen = left.length();
        int rightLen = right.length();
        int minLen = Math.min(leftLen, rightLen);

        for (int i = 0; i < minLen; ++i) {
            char leftChar = left.charAt(i);
            char rightChar = right.charAt(i);
            if (leftChar != rightChar) {
                if (isKoreanAndEnglish(leftChar, rightChar)
                        || isKoreanAndNumber(leftChar, rightChar)
                        || isEnglishAndNumber(leftChar, rightChar)
                        || isKoreanAndSpecial(leftChar, rightChar)) {
                    return (leftChar - rightChar) * REVERSE;
                } else if (isEnglishAndSpecial(leftChar, rightChar)
                        || isNumberAndSpecial(leftChar, rightChar)) {
                    if (isEnglish(leftChar) || isNumber(leftChar)) {
                        return LEFT_FIRST;
                    } else {
                        return RIGHT_FIRST;
                    }
                } else {
                    return leftChar - rightChar;
                }
            }
        }

        return leftLen - rightLen;
    }

    private static boolean isKoreanAndEnglish(char ch1, char ch2) {
        return (isEnglish(ch1) && isKorean(ch2))
                || (isKorean(ch1) && isEnglish(ch2));
    }

    private static boolean isKoreanAndNumber(char ch1, char ch2) {
        return (isNumber(ch1) && isKorean(ch2))
                || (isKorean(ch1) && isNumber(ch2));
    }

    private static boolean isEnglishAndNumber(char ch1, char ch2) {
        return (isNumber(ch1) && isEnglish(ch2))
                || (isEnglish(ch1) && isNumber(ch2));
    }

    private static boolean isKoreanAndSpecial(char ch1, char ch2) {
        return (isKorean(ch1) && isSpecial(ch2))
                || (isSpecial(ch1) && isKorean(ch2));
    }

    private static boolean isEnglishAndSpecial(char ch1, char ch2) {
        return (isEnglish(ch1) && isSpecial(ch2))
                || (isSpecial(ch1) && isEnglish(ch2));
    }

    private static boolean isNumberAndSpecial(char ch1, char ch2) {
        return (isNumber(ch1) && isSpecial(ch2))
                || (isSpecial(ch1) && isNumber(ch2));
    }

    ////////////////////////////////////////////////
}