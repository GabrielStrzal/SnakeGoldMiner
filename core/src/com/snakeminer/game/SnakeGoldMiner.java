package com.snakeminer.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.snakeminer.game.screens.MenuScreen;

public class SnakeGoldMiner extends Game {

	public SpriteBatch batch;
	@Override
	public void create() {
		batch = new SpriteBatch();
		setScreen(new MenuScreen(this));
	}
}
