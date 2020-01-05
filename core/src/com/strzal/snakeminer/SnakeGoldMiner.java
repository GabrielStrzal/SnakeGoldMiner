package com.strzal.snakeminer;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.strzal.gdxUtilLib.BasicGame;
import com.strzal.gdxUtilLib.screenManager.ScreenManager;
import com.strzal.snakeminer.handler.GameStatsHandler;
import com.strzal.snakeminer.loading.LoadingPathsImpl;
import com.strzal.snakeminer.screenManager.ScreenEnum;
import com.strzal.snakeminer.screens.MenuScreen;

public class SnakeGoldMiner extends BasicGame {

	private SpriteBatch batch;
	private GameStatsHandler gameStatsHandler;


	@Override
	public void create() {
		batch = new SpriteBatch();
		loadingPaths = new LoadingPathsImpl();
		gameStatsHandler = new GameStatsHandler();

		ScreenManager.getInstance().initialize(this);
		ScreenManager.getInstance()
				.showScreen(ScreenEnum.LOADING_SCREEN, this, loadingPaths, new MenuScreen(this));
	}

	@Override
	public void dispose () {
		batch.dispose();
	}

	public SpriteBatch getBatch() {
		return batch;
	}

	public GameStatsHandler getGameStatsHandler() {
		return gameStatsHandler;
	}
}
