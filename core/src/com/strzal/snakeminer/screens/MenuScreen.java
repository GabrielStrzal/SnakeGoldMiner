package com.strzal.snakeminer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.strzal.snakeminer.SnakeGoldMiner;
import com.strzal.snakeminer.config.GameConfig;
import com.strzal.snakeminer.utils.GdxUtils;

/**
 * Created by lelo on 15/12/17.
 */

public class MenuScreen implements Screen {

    private OrthographicCamera camera;
    private Viewport viewport;
    private ShapeRenderer renderer;
    private SnakeGoldMiner game;

    Texture backgroundImage;

    public MenuScreen(SnakeGoldMiner game){
        this.game = game;
        camera = new OrthographicCamera();
        viewport = new FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, camera);
        renderer = new ShapeRenderer();

        backgroundImage = new Texture("menu/menu_screen_640x480.png");
    }

    @Override
    public void show() {

    }
    private void handleInput(float dt) {
        if (Gdx.input.isTouched() || Gdx.input.isKeyJustPressed(Input.Keys.SPACE) ){
            game.setScreen(new GameScreen());
        }
    }
    public void update(float dt){
        handleInput(dt);

    }

    @Override
    public void render(float delta) {
        update(delta);
        GdxUtils.clearScreen(new Color(0.15f, 0.15f, 0.3f, 1));
        viewport.apply();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.batch.draw(backgroundImage, 0,0,GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT);
        game.batch.end();

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        backgroundImage.dispose();
        renderer.dispose();
    }
}
