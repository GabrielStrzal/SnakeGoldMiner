package com.strzal.snakeminer;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.strzal.snakeminer.screens.MenuScreen;

public class SnakeGoldMiner extends Game {

	public SpriteBatch batch;
	@Override
	public void create() {
		batch = new SpriteBatch();
		setScreen(new MenuScreen(this));
	}
}
