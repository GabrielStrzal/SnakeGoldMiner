package com.snakeminer.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.snakeminer.game.SnakeGoldMiner;
import com.snakeminer.game.config.GameConfig;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = (int) GameConfig.WORLD_WIDTH;
		config.height = (int)GameConfig.WORLD_HEIGHT;
		new LwjglApplication(new SnakeGoldMiner(), config);
	}
}
