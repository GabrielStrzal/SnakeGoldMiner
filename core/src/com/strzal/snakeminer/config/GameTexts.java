package com.strzal.snakeminer.config;

import com.strzal.snakeminer.handler.LevelStats;

public class GameTexts {

    public static String buildStatsText(LevelStats stats) {
        return "Number of times Played:  " + stats.getTotalTimesPlayed() + "\n\n" +
               "High Score:  " + stats.getHighScore();
    }

    private GameTexts() {}
}
