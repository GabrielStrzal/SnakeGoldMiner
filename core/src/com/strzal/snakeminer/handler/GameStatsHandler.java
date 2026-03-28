package com.strzal.snakeminer.handler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import static com.strzal.snakeminer.config.GameConfig.PREFS_NAME;

public class GameStatsHandler {

    private static final String KEY_TIMES_PLAYED      = "totalTimesPlayed";
    private static final String KEY_HIGH_SCORE         = "highScore";
    private static final String KEY_TOTAL_GOLD         = "totalGoldCollected";
    private static final String KEY_TOTAL_TIME         = "totalPlayTimeSeconds";
    private static final String KEY_STORY_COMPLETED    = "storyCompleted";
    private static final String KEY_HARD_COMPLETED     = "hardCompleted";
    private static final String KEY_HARDCORE_COMPLETED = "hardcoreCompleted";

    private final Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);

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

    public void saveHardCompleted() {
        prefs.putBoolean(KEY_HARD_COMPLETED, true);
        prefs.flush();
    }

    public void saveHardcoreCompleted() {
        prefs.putBoolean(KEY_HARDCORE_COMPLETED, true);
        prefs.flush();
    }

    public boolean isStoryCompleted()    { return prefs.getBoolean(KEY_STORY_COMPLETED,    false); }
    public boolean isHardCompleted()     { return prefs.getBoolean(KEY_HARD_COMPLETED,     false); }
    public boolean isHardcoreCompleted() { return prefs.getBoolean(KEY_HARDCORE_COMPLETED, false); }

    public LevelStats getSavedData() {
        return new LevelStats(
            prefs.getInteger(KEY_TIMES_PLAYED, 0),
            prefs.getInteger(KEY_HIGH_SCORE, 0),
            prefs.getInteger(KEY_TOTAL_GOLD, 0),
            prefs.getInteger(KEY_TOTAL_TIME, 0)
        );
    }
}
