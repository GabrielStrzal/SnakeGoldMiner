package com.strzal.snakeminer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.strzal.gdxUtilLib.BasicGame;
import com.strzal.gdxUtilLib.screenManager.ScreenManager;
import com.strzal.snakeminer.config.ImagesPaths;
import com.strzal.snakeminer.screenManager.ScreenEnum;


public class TextScreen extends BasicMenuScreen {

    private String textToBeDisplayed;
    private GameModeEnum gameMode;

    public TextScreen(BasicGame game, String textToBeDisplayed, GameModeEnum gameMode) {
        super(game);
        this.textToBeDisplayed = textToBeDisplayed;
        this.gameMode = gameMode;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        Texture button         = assetManager.get(ImagesPaths.MENU_BUTTON);
        Texture button_pressed = assetManager.get(ImagesPaths.MENU_BUTTON_PRESSED);

        BitmapFont font = new BitmapFont();

        ImageTextButton.ImageTextButtonStyle style =
                new ImageTextButton.ImageTextButtonStyle(
                        new TextureRegionDrawable(button),
                        new TextureRegionDrawable(button_pressed),
                        new TextureRegionDrawable(button),
                        font);

        ImageTextButton nextImageButton = new ImageTextButton("Next", style);

        Image background = new Image((Texture) game.getAssetManager().get(ImagesPaths.GAME_TEXT_BACKGROUND));

        nextImageButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                switch (gameMode) {
                    case STORY_MODE:
                        ScreenManager.getInstance().showScreen(ScreenEnum.GAME_SCREEN, game, false);
                        break;
                    case ENDLESS_MODE:
                        ScreenManager.getInstance().showScreen(ScreenEnum.GAME_SCREEN, game, true);
                        break;
                    case LEVEL_COMPLETED:
                        ScreenManager.getInstance().showScreen(ScreenEnum.GAME_SCREEN, game, false);
                        break;
                    case LEVEL_COMPLETED_ENDLESS:
                        ScreenManager.getInstance().showScreen(ScreenEnum.GAME_SCREEN, game, true);
                        break;
                    default:
                        ScreenManager.getInstance().showScreen(ScreenEnum.MENU_SCREEN, game);
                        break;
                }
            }
        });

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.bottom().padBottom(30);
        mainTable.add(nextImageButton).padBottom(10);

        stage.addActor(background);
        stage.addActor(mainTable);

        initText();
    }

    private void initText() {
        Label label = new Label(textToBeDisplayed, skin);
        label.setWrap(true);

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.left().top();
        mainTable.padLeft(90);
        mainTable.padTop(200);
        mainTable.add(label).width(460f);

        stage.addActor(mainTable);
    }
}
