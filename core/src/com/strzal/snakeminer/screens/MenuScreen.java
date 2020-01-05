package com.strzal.snakeminer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.strzal.gdxUtilLib.BasicGame;
import com.strzal.gdxUtilLib.screenManager.ScreenManager;
import com.strzal.snakeminer.config.GameConfig;
import com.strzal.snakeminer.config.GamePositions;
import com.strzal.snakeminer.config.GameTexts;
import com.strzal.snakeminer.config.ImagesPaths;
import com.strzal.snakeminer.screenManager.ScreenEnum;

public class MenuScreen extends BasicMenuScreen {


    public MenuScreen(BasicGame game) {
        super(game);
    }

    @Override
    public void show() {
        //Stage should control input:
        Gdx.input.setInputProcessor(stage);

        //Create Table
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.bottom().padBottom(30);



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
        ImageTextButton playImageButton = new ImageTextButton("Play" ,style);
        ImageTextButton gameStatsButton = new ImageTextButton("Game Stats" ,style);
        ImageTextButton tutorialStatsButton = new ImageTextButton("Instructions" ,style);


        Image background = new Image((Texture) game.getAssetManager().get(ImagesPaths.MENU_BACKGROUND));

        //Add listeners to buttons
        playImageButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ScreenManager.getInstance().showScreen(
                        ScreenEnum.GAME_SCREEN, game
                );
            }
        });

        gameStatsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ScreenManager.getInstance().showScreen(
                        ScreenEnum.TEXT_SCREEN, game, GameTexts.GAME_STATS_TEXT, GameModeEnum.GAME_STATS
                );
            }
        });


        //Add buttons to table
        mainTable.add(playImageButton).padBottom(10);
        mainTable.row();
        mainTable.add(gameStatsButton).padBottom(10);
        mainTable.row();
        //mainTable.add(tutorialStatsButton);

        stage.addActor(background);
        //Add table to stage
        stage.addActor(mainTable);
        addVersionText();
    }

    private void addVersionText() {
        Label.LabelStyle style = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
        Label textLabel = new Label(GameConfig.GAME_VERSION, style);
        textLabel.setPosition(GamePositions.GAME_VERSION_X_POSITION, GamePositions.GAME_VERSION_Y_POSITION);
        stage.addActor(textLabel);
    }
}
