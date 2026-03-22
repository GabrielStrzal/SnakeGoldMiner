package com.strzal.snakeminer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
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

    public TextScreen(BasicGame game, String textToBeDisplayed, GameModeEnum gameMode) {
        super(game);
        this.textToBeDisplayed = textToBeDisplayed;
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

        ImageTextButton backButton = new ImageTextButton("Back", style);
        backButton.getLabel().setColor(com.badlogic.gdx.graphics.Color.YELLOW);

        Image background = new Image((Texture) game.getAssetManager().get(ImagesPaths.GAME_TEXT_BACKGROUND));

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                goBack();
            }
        });

        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.ENTER || keycode == Input.Keys.BACKSPACE) {
                    goBack();
                    return true;
                }
                return false;
            }
        });

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.bottom().padBottom(30);
        mainTable.add(backButton).padBottom(10);

        stage.addActor(background);
        stage.addActor(mainTable);

        initText();
    }

    private void goBack() {
        ScreenManager.getInstance().showScreen(ScreenEnum.MENU_SCREEN, game);
    }

    private void initText() {
        Label label = new Label(textToBeDisplayed, skin);
        label.setWrap(true);

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.left().top();
        mainTable.padLeft(90);
        mainTable.padTop(100);
        mainTable.add(label).width(460f);

        stage.addActor(mainTable);
    }
}
