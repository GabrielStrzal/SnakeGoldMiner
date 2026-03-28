package com.strzal.snakeminer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.strzal.gdxUtilLib.BasicGame;
import com.strzal.gdxUtilLib.screenManager.ScreenManager;
import com.strzal.snakeminer.config.GameConfig;
import com.strzal.snakeminer.config.GamePositions;
import com.strzal.snakeminer.config.GameTexts;
import com.strzal.snakeminer.config.ImagesPaths;
import com.strzal.snakeminer.levels.LevelData;
import com.strzal.snakeminer.screenManager.ScreenEnum;

import java.util.ArrayList;
import java.util.List;

public class MenuScreen extends BasicMenuScreen {

    private final List<ImageTextButton> navButtons = new ArrayList<>();
    private final List<Runnable> navActions = new ArrayList<>();
    private int focusedIndex = 0;
    private int debugStartLevel = 1;
    private Label debugLevelLabel;

    public MenuScreen(BasicGame game) {
        super(game);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        //mainTable.bottom().padBottom(30);
        mainTable.right().padRight(40);

        Texture button         = assetManager.get(ImagesPaths.MENU_BUTTON);
        Texture button_pressed = assetManager.get(ImagesPaths.MENU_BUTTON_PRESSED);

        BitmapFont font = new BitmapFont();

        ImageTextButton.ImageTextButtonStyle style =
                new ImageTextButton.ImageTextButtonStyle(
                        new TextureRegionDrawable(button),
                        new TextureRegionDrawable(button_pressed),
                        new TextureRegionDrawable(button),
                        font);

        ImageTextButton storyButton     = new ImageTextButton("Normal", style);
        ImageTextButton hardButton      = new ImageTextButton("Hard", style);
        ImageTextButton hardcoreButton  = new ImageTextButton("Hardcore", style);
        ImageTextButton classicButton   = new ImageTextButton("Classic", style);
        ImageTextButton gameStatsButton = new ImageTextButton("Game Stats", style);
        ImageTextButton trophiesButton  = new ImageTextButton("Trophies", style);

        boolean storyCompleted = game.getGameStatsHandler().isStoryCompleted();
        if (!storyCompleted) {
            classicButton.getLabel().setColor(Color.GRAY);
            hardButton.getLabel().setColor(Color.GRAY);
            hardcoreButton.getLabel().setColor(Color.GRAY);
        }

        Image background = new Image((Texture) game.getAssetManager().get(ImagesPaths.MENU_BACKGROUND));

        final Runnable goStory = new Runnable() {
            @Override public void run() {
                ScreenManager.getInstance().showScreen(ScreenEnum.STORY_GAME_SCREEN, game, debugStartLevel, GameDifficulty.NORMAL);
            }
        };
        final Runnable goClassic = new Runnable() {
            @Override public void run() {
                ScreenManager.getInstance().showScreen(ScreenEnum.GAME_SCREEN, game, GameDifficulty.CLASSIC);
            }
        };
        final Runnable goHard = new Runnable() {
            @Override public void run() {
                ScreenManager.getInstance().showScreen(ScreenEnum.STORY_GAME_SCREEN, game, debugStartLevel, GameDifficulty.HARD);
            }
        };
        final Runnable goHardcore = new Runnable() {
            @Override public void run() {
                ScreenManager.getInstance().showScreen(ScreenEnum.STORY_GAME_SCREEN, game, debugStartLevel, GameDifficulty.HARDCORE);
            }
        };
        final Runnable goStats = new Runnable() {
            @Override public void run() {
                ScreenManager.getInstance().showScreen(
                        ScreenEnum.TEXT_SCREEN, game,
                        GameTexts.buildStatsText(game.getGameStatsHandler().getSavedData()),
                        GameModeEnum.GAME_STATS
                );
            }
        };
        final Runnable goTrophies = new Runnable() {
            @Override public void run() {
                ScreenManager.getInstance().showScreen(
                        ScreenEnum.TEXT_SCREEN, game,
                        game.getAchievementHandler().getDisplayText(),
                        GameModeEnum.TROPHIES
                );
            }
        };

        storyButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { goStory.run(); }
        });
        if (storyCompleted) {
            classicButton.addListener(new ClickListener() {
                @Override public void clicked(InputEvent event, float x, float y) { goClassic.run(); }
            });
            hardButton.addListener(new ClickListener() {
                @Override public void clicked(InputEvent event, float x, float y) { goHard.run(); }
            });
            hardcoreButton.addListener(new ClickListener() {
                @Override public void clicked(InputEvent event, float x, float y) { goHardcore.run(); }
            });
        }
        gameStatsButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { goStats.run(); }
        });
        trophiesButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { goTrophies.run(); }
        });

        navButtons.clear();
        navActions.clear();
        navButtons.add(storyButton);     navActions.add(goStory);
        if (storyCompleted) {
            navButtons.add(classicButton);  navActions.add(goClassic);
            navButtons.add(hardButton);     navActions.add(goHard);
            navButtons.add(hardcoreButton); navActions.add(goHardcore);
        }
        navButtons.add(gameStatsButton); navActions.add(goStats);
        navButtons.add(trophiesButton);  navActions.add(goTrophies);

        if (GameConfig.debug) {
            Label.LabelStyle debugStyle = new Label.LabelStyle(new BitmapFont(), Color.YELLOW);
            debugLevelLabel = new Label(debugLevelText(), debugStyle);
            mainTable.add(debugLevelLabel).padBottom(6);
            mainTable.row();
        }

        mainTable.add(storyButton).padBottom(10);
        mainTable.row();
        mainTable.add(classicButton).padBottom(10);
        mainTable.row();
        mainTable.add(hardButton).padBottom(10);
        mainTable.row();
        mainTable.add(hardcoreButton).padBottom(10);
        mainTable.row();
        mainTable.add(gameStatsButton).padBottom(10);
        mainTable.row();
        mainTable.add(trophiesButton).padBottom(10);

        stage.addActor(background);
        stage.addActor(mainTable);
        addVersionText();

        focusedIndex = 0;
        updateFocusVisual();

        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.DOWN) {
                    focusedIndex = (focusedIndex + 1) % navButtons.size();
                    updateFocusVisual();
                    return true;
                }
                if (keycode == Input.Keys.UP) {
                    focusedIndex = (focusedIndex - 1 + navButtons.size()) % navButtons.size();
                    updateFocusVisual();
                    return true;
                }
                if (keycode == Input.Keys.ENTER || keycode == Input.Keys.SPACE) {
                    navActions.get(focusedIndex).run();
                    return true;
                }
                if (GameConfig.debug && keycode == Input.Keys.RIGHT) {
                    debugStartLevel = Math.min(debugStartLevel + 1, LevelData.totalLevels());
                    debugLevelLabel.setText(debugLevelText());
                    return true;
                }
                if (GameConfig.debug && keycode == Input.Keys.LEFT) {
                    debugStartLevel = Math.max(debugStartLevel - 1, 1);
                    debugLevelLabel.setText(debugLevelText());
                    return true;
                }
                return false;
            }
        });
    }

    private String debugLevelText() {
        return "< Start Level: " + debugStartLevel + " / " + LevelData.totalLevels() + " >";
    }

    private void updateFocusVisual() {
        for (int i = 0; i < navButtons.size(); i++) {
            navButtons.get(i).getLabel().setColor(i == focusedIndex ? Color.YELLOW : Color.WHITE);
        }
    }

    private void addVersionText() {
        Label.LabelStyle style = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
        Label textLabel = new Label(GameConfig.GAME_VERSION, style);
        textLabel.setPosition(GamePositions.GAME_VERSION_X_POSITION, GamePositions.GAME_VERSION_Y_POSITION);
        stage.addActor(textLabel);
    }
}
