package com.strzal.snakeminer.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.strzal.gdxUtilLib.screenManager.ScreenManager;
import com.strzal.snakeminer.SnakeGoldMiner;
import com.strzal.snakeminer.config.GameConfig;
import com.strzal.snakeminer.config.ImagesPaths;
import com.strzal.snakeminer.screenManager.ScreenEnum;
import com.strzal.snakeminer.screens.GameScreen;


public class Hud {

    private AssetManager assetManager;
    private Stage stage;
    private SnakeGoldMiner game;
    private GameScreen screen;



    //Constants
    private static float LABELS_Y_POSITION = GameConfig.SCREEN_HEIGHT - 30;
    private static float EXIT_BUTTON_X_POSITION = GameConfig.SCREEN_WIDTH - 80;



    public Hud(SnakeGoldMiner game, GameScreen screen) {
        this.game = game;
        this.screen = screen;
        stage = new Stage(new FitViewport(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT));
        assetManager = game.getAssetManager();


        addGoToMenuButton();
    }

    private void addGoToMenuButton() {


        Texture button = assetManager.get(ImagesPaths.MENU_BUTTON);
        Texture button_pressed = assetManager.get(ImagesPaths.MENU_BUTTON_PRESSED);

        BitmapFont font = new BitmapFont();


        ImageTextButton.ImageTextButtonStyle style =
                new ImageTextButton.ImageTextButtonStyle(
                        new TextureRegionDrawable(button),
                        new TextureRegionDrawable(button_pressed),
                        new TextureRegionDrawable(button),
                        font);

        //Create buttons
        ImageTextButton menuButton = new ImageTextButton("Exit" ,style);
        menuButton.setSize(60,20);


        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.getGameStatsHandler().saveLevelData(
                        screen.getScore()
                );

                ScreenManager.getInstance().showScreen(
                        ScreenEnum.MENU_SCREEN, game
                );
            }
        });
        menuButton.setPosition(EXIT_BUTTON_X_POSITION, LABELS_Y_POSITION);
        stage.addActor(menuButton);
    }


    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }

    public void draw() {
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }






    public Stage getStage() {
        return stage;
    }
}
