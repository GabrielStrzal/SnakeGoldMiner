package com.strzal.snakeminer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;
import com.strzal.gdxUtilLib.BasicGame;
import com.strzal.gdxUtilLib.screenManager.ScreenManager;
import com.strzal.snakeminer.config.ImagesPaths;
import com.strzal.snakeminer.handler.LevelStats;
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
        //Stage should control input:
        Gdx.input.setInputProcessor(stage);

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
        ImageTextButton nextImageButton = new ImageTextButton("Next" ,style);

        Image background = new Image((Texture) game.getAssetManager().get(ImagesPaths.GAME_TEXT_BACKGROUND));

        //Add listeners to buttons
        nextImageButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                //game.getAudioHandler().playButtonSound();

                // Cases for Text Screen
                // menu > game (story mode)
                // menu > game (endless)
                // game > game over (menu) > future HIGH SCORE
                // game > you won (menu) > future HIGH SCORE
                // game > level completed (next level, but this should not be here)
                switch(gameMode){
                    case STORY_MODE:
                        ScreenManager.getInstance().showScreen( ScreenEnum.GAME_SCREEN, game, false );
                        break;
                    case ENDLESS_MODE:
                        ScreenManager.getInstance().showScreen( ScreenEnum.GAME_SCREEN, game, true );
                        break;
                    case LEVEL_COMPLETED:
                        ScreenManager.getInstance().showScreen( ScreenEnum.GAME_SCREEN, game, false );
                        break;
                    case LEVEL_COMPLETED_ENDLESS:
                        ScreenManager.getInstance().showScreen( ScreenEnum.GAME_SCREEN, game, true );
                        break;

                    default:
                        ScreenManager.getInstance().showScreen( ScreenEnum.MENU_SCREEN, game );
                        break;
                }
            }
        });


        //Create Table
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        //
        mainTable.bottom().padBottom(30);

        //Add buttons to table
        mainTable.add(nextImageButton).padBottom(10);


        stage.addActor(background);
        //Add table to stage
        stage.addActor(mainTable);

        initText();
    }

    private void initText(){
        //labels and text
        TypingLabel label;

        //Create Table
        Table mainTable = new Table();
        //Set table to fill stage
        mainTable.setFillParent(true);
        //Set alignment of contents in the table.
        mainTable.left().top();
        mainTable.padLeft(90);
        mainTable.padTop(200);

        // Create a TypingLabel instance with your custom text
        label = new TypingLabel(textToBeDisplayed, skin);

        if(gameMode == GameModeEnum.GAME_STATS){
            LevelStats stats = game.getGameStatsHandler().getSavedData();
            label.setVariable("timesPlayed", "" + stats.getTotalTimesPlayed());
            label.setVariable("highScore", "" + stats.getHighScore());
        }
//        if(gameMode == GameModeEnum.STORY_MODE){
//            label.setVariable("weeks", "" + GameSetting.MAXIMUM_WAVE_IN_GAME_MODE);
//        }

        //Add buttons to table
        mainTable.add(label);
        mainTable.row();


        //Add table to stage
        stage.addActor(mainTable);
    }

}
