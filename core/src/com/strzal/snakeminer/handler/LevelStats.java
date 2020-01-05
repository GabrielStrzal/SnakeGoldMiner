package com.strzal.snakeminer.handler;


public class LevelStats {
    private int totalTimesPlayed;
    private int highScore;

    public LevelStats(int totalTimesPlayed, int highScore) {
        this.totalTimesPlayed = totalTimesPlayed;
        this.highScore = highScore;
    }

    public int getTotalTimesPlayed() {
        return totalTimesPlayed;
    }

    public void setTotalTimesPlayed(int totalTimesPlayed) {
        this.totalTimesPlayed = totalTimesPlayed;
    }

    public int getHighScore() {
        return highScore;
    }

    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }
}
