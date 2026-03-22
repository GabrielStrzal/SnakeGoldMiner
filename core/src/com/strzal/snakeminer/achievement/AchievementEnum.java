package com.strzal.snakeminer.achievement;

public enum AchievementEnum {

    STORY_COMPLETED("Story Finisher", "Complete all 10 story mode levels", false),
    MATCHES_20("Veteran Miner", "Play 20 matches", false),
    PLAYTIME_2H("Marathon Miner", "Play for 2 hours total", false),
    GOLD_TOTAL_20("???", "Collect 1000 gold in total", true);

    public final String name;
    public final String description;
    public final boolean secret;

    AchievementEnum(String name, String description, boolean secret) {
        this.name = name;
        this.description = description;
        this.secret = secret;
    }
}
