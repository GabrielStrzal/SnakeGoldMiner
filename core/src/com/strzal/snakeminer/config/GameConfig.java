package com.strzal.snakeminer.config;


public class GameConfig {

    public static final float SCREEN_WIDTH = 640;
    public static final float SCREEN_HEIGHT = 480;


    //Only used for HTML Display size
    public static final int SCREEN_HTML_DISPLAY_WIDTH = (int)SCREEN_WIDTH; //pixels
    public static final int SCREEN_HTML_DISPLAY_HEIGHT = (int)SCREEN_HEIGHT; //pixels

    //Only used for Desktop Display size
    public static final int SCREEN_DESKTOP_DISPLAY_WIDTH = (int)SCREEN_WIDTH ; //pixels
    public static final int SCREEN_DESKTOP_DISPLAY_HEIGHT = (int)SCREEN_HEIGHT ; //pixels

    public static final String GAME_VERSION = "V.1.1";

    public static final boolean debug = true;

    private GameConfig(){}
}
