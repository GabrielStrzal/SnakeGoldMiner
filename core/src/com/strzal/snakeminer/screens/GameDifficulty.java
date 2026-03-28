package com.strzal.snakeminer.screens;

import com.strzal.snakeminer.config.GameConfig;

public enum GameDifficulty {
    CLASSIC ("Classic",  GameConfig.MAX_LIVES_CLASSIC, GameConfig.MOVE_TIME_CLASSIC),
    NORMAL  ("Normal",   GameConfig.MAX_LIVES_CLASSIC, GameConfig.MOVE_TIME_CLASSIC),
    HARD    ("Hard",     GameConfig.MAX_LIVES_CLASSIC, GameConfig.MOVE_TIME_HARD),
    HARDCORE("Hardcore", 0,                            GameConfig.MOVE_TIME_HARD);

    public final String displayName;
    public final int    maxLives;
    public final float  moveTime;

    GameDifficulty(String displayName, int maxLives, float moveTime) {
        this.displayName = displayName;
        this.maxLives    = maxLives;
        this.moveTime    = moveTime;
    }
}
