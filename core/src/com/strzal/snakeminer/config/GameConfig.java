package com.strzal.snakeminer.config;


public class GameConfig {

    public static final float SCREEN_WIDTH = 640;
    public static final float SCREEN_HEIGHT = 480;
    public static final float HUD_HEIGHT = 32;
    public static final float PLAY_AREA_HEIGHT = SCREEN_HEIGHT - HUD_HEIGHT;


    //Only used for HTML Display size
    public static final int SCREEN_HTML_DISPLAY_WIDTH = (int)SCREEN_WIDTH; //pixels
    public static final int SCREEN_HTML_DISPLAY_HEIGHT = (int)SCREEN_HEIGHT; //pixels

    //Only used for Desktop Display size
    public static final int SCREEN_DESKTOP_DISPLAY_WIDTH = (int)SCREEN_WIDTH ; //pixels
    public static final int SCREEN_DESKTOP_DISPLAY_HEIGHT = (int)SCREEN_HEIGHT ; //pixels

    public static final float MOVE_TIME_CLASSIC = 0.20f;
    public static final float MOVE_TIME_HARD    = 0.10f;
    public static final int   MAX_LIVES_CLASSIC = 5;

    public static final String GAME_VERSION = "V.2.0";
    public static final String PREFS_NAME = "snake_gold_miner_v2_0";


    public static final boolean debug = false;

    private GameConfig(){}
}
