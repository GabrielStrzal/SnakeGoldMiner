package com.strzal.snakeminer.achievement;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import java.util.ArrayList;
import java.util.List;

public class AchievementHandler {

    private static final String PREFS_NAME = "snake_gold_miner_achievements";

    private final Preferences prefs;

    public AchievementHandler() {
        prefs = Gdx.app.getPreferences(PREFS_NAME);
    }

    /**
     * Checks all achievements against the given cumulative stats.
     * Returns the list of achievements that were newly unlocked by this call.
     */
    public List<AchievementEnum> checkAndUnlock(int totalTimesPlayed, int totalGoldCollected, int totalPlayTimeSeconds, boolean storyCompleted) {
        List<AchievementEnum> newlyUnlocked = new ArrayList<>();
        for (AchievementEnum ach : AchievementEnum.values()) {
            if (!isUnlocked(ach) && isConditionMet(ach, totalTimesPlayed, totalGoldCollected, totalPlayTimeSeconds, storyCompleted)) {
                prefs.putBoolean(ach.name(), true);
                prefs.flush();
                newlyUnlocked.add(ach);
            }
        }
        return newlyUnlocked;
    }

    public boolean isUnlocked(AchievementEnum ach) {
        return prefs.getBoolean(ach.name(), false);
    }

    private boolean isConditionMet(AchievementEnum ach, int totalTimesPlayed, int totalGoldCollected, int totalPlayTimeSeconds, boolean storyCompleted) {
        switch (ach) {
            case STORY_COMPLETED: return storyCompleted;
            case MATCHES_20:      return totalTimesPlayed >= 20;
            case PLAYTIME_2H:     return totalPlayTimeSeconds >= 7200;
            case GOLD_TOTAL_20:   return totalGoldCollected >= 1000;
            default:              return false;
        }
    }

    /** Generates plain text listing all achievements for the Trophies screen. */
    public String getDisplayText() {
        StringBuilder sb = new StringBuilder();
        for (AchievementEnum ach : AchievementEnum.values()) {
            boolean unlocked = isUnlocked(ach);
            if (ach.secret && !unlocked) {
                sb.append("[?] ???\n");
                sb.append("    ???\n\n");
            } else if (unlocked) {
                sb.append("[X] ").append(ach.name).append("\n");
                sb.append("    ").append(ach.description).append("\n\n");
            } else {
                sb.append("[ ] ").append(ach.name).append("\n");
                sb.append("    ").append(ach.description).append("\n\n");
            }
        }
        return sb.toString();
    }
}
