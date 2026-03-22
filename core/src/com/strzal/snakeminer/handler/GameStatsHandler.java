package com.strzal.snakeminer.handler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class GameStatsHandler {

    private static final String KEY_TIMES_PLAYED    = "totalTimesPlayed";
    private static final String KEY_HIGH_SCORE       = "highScore";
    private static final String KEY_TOTAL_GOLD       = "totalGoldCollected";
    private static final String KEY_TOTAL_TIME       = "totalPlayTimeSeconds";
    private static final String KEY_STORY_COMPLETED  = "storyCompleted";

    private final Preferences prefs = Gdx.app.getPreferences("snake_gold_miner_v1_0");

    public GameStatsHandler() {}

    /**
     * Saves end-of-session data.
     *
     * @param score               final score for this session
     * @param sessionGoldCollected gold pieces collected this session
     * @param sessionTimeSeconds  seconds spent playing this session
     */
    public void saveLevelData(int score, int sessionGoldCollected, int sessionTimeSeconds) {
        int totalTimesPlayed = prefs.getInteger(KEY_TIMES_PLAYED, 0);
        prefs.putInteger(KEY_TIMES_PLAYED, ++totalTimesPlayed);

        int highScore = prefs.getInteger(KEY_HIGH_SCORE, 0);
        if (highScore < score) prefs.putInteger(KEY_HIGH_SCORE, score);

        int totalGold = prefs.getInteger(KEY_TOTAL_GOLD, 0) + sessionGoldCollected;
        prefs.putInteger(KEY_TOTAL_GOLD, totalGold);

        int totalTime = prefs.getInteger(KEY_TOTAL_TIME, 0) + sessionTimeSeconds;
        prefs.putInteger(KEY_TOTAL_TIME, totalTime);

        prefs.flush();
    }

    public void saveStoryCompleted() {
        prefs.putBoolean(KEY_STORY_COMPLETED, true);
        prefs.flush();
    }

    public boolean isStoryCompleted() {
        return prefs.getBoolean(KEY_STORY_COMPLETED, false);
    }

    public LevelStats getSavedData() {
        return new LevelStats(
            prefs.getInteger(KEY_TIMES_PLAYED, 0),
            prefs.getInteger(KEY_HIGH_SCORE, 0),
            prefs.getInteger(KEY_TOTAL_GOLD, 0),
            prefs.getInteger(KEY_TOTAL_TIME, 0)
        );
    }
}
