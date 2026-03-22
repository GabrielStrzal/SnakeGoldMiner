package com.strzal.snakeminer.handler;

public class LevelStats {

    private int totalTimesPlayed;
    private int highScore;
    private int totalGoldCollected;
    private int totalPlayTimeSeconds;

    public LevelStats(int totalTimesPlayed, int highScore, int totalGoldCollected, int totalPlayTimeSeconds) {
        this.totalTimesPlayed = totalTimesPlayed;
        this.highScore = highScore;
        this.totalGoldCollected = totalGoldCollected;
        this.totalPlayTimeSeconds = totalPlayTimeSeconds;
    }

    public int getTotalTimesPlayed() { return totalTimesPlayed; }
    public void setTotalTimesPlayed(int totalTimesPlayed) { this.totalTimesPlayed = totalTimesPlayed; }

    public int getHighScore() { return highScore; }
    public void setHighScore(int highScore) { this.highScore = highScore; }

    public int getTotalGoldCollected() { return totalGoldCollected; }
    public void setTotalGoldCollected(int totalGoldCollected) { this.totalGoldCollected = totalGoldCollected; }

    public int getTotalPlayTimeSeconds() { return totalPlayTimeSeconds; }
    public void setTotalPlayTimeSeconds(int totalPlayTimeSeconds) { this.totalPlayTimeSeconds = totalPlayTimeSeconds; }
}
