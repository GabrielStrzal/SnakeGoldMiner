package com.strzal.snakeminer.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.strzal.snakeminer.SnakeGoldMiner;
import com.strzal.snakeminer.config.GameConfig;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration ();
		config.setWindowedMode( (int)GameConfig.SCREEN_WIDTH, (int) GameConfig.SCREEN_HEIGHT);
		new Lwjgl3Application(new SnakeGoldMiner(), config);
	}
}
