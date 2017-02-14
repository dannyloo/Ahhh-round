package com.gamestridestudios.ahhh_round.utils;

import java.text.NumberFormat;
import java.util.Locale;

public class ThousandsFormatter {
    public static String format(int score) {
        return NumberFormat.getInstance(Locale.US).format(score);
    }
}