package com.strzal.snakeminer.handler;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class GameStatsHandler {

    Preferences prefs = Gdx.app.getPreferences("snake_gold_miner_v1_0");

    public GameStatsHandler() {
    }

    public void saveLevelData(int levelHighScore){

        //total plays
        int totalTimesPlayed = prefs.getInteger("totalTimesPlayed", 0);
        prefs.putInteger("totalTimesPlayed", ++totalTimesPlayed);


        //high score
        int highScore = prefs.getInteger("highScore", 0);
        if(highScore < levelHighScore) {
            prefs.putInteger("highScore", levelHighScore);
        }

        prefs.flush();
    }

    public LevelStats getSavedData() {
        return new LevelStats(
                prefs.getInteger("totalTimesPlayed", 0),
                prefs.getInteger("highScore", 0)
        );
    }
}
