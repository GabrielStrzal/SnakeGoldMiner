package com.strzal.snakeminer.config;


public class GameTexts {

    public static final String GAME_STATS_TEXT =
            "{FASTER} " +
                    "                     " +
                    "{COLOR=WHITE}Number of times Played:    {COLOR=GREEN} {VAR=timesPlayed} \n \n" +
                    "                     " +
                    "{COLOR=WHITE}High Score:    {COLOR=GREEN} {VAR=highScore} \n \n";


    public static final String GAME_TROPHIES_TEXT =
            "{FASTER} " +
                    "                     " +
                    "{COLOR=WHITE}Win:    {COLOR=GREEN} {VAR=win} \n \n" +
                    "                     " +
                    "{COLOR=WHITE}game finished:    {COLOR=GREEN} {VAR=finished} \n \n";


    private GameTexts(){}
}
