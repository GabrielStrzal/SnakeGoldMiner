package com.snakeminer.game;

import com.badlogic.gdx.Game;
import com.snakeminer.game.screens.GameScreen;

public class SnakeGoldMiner extends Game {
	@Override
	public void create() {
		setScreen(new GameScreen());
	}
}
