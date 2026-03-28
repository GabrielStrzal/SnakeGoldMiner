package com.strzal.snakeminer.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.strzal.snakeminer.SnakeGoldMiner;
import com.strzal.snakeminer.achievement.AchievementEnum;
import com.strzal.snakeminer.config.GameConfig;
import com.strzal.snakeminer.config.ImagesPaths;

public class Hud {

    private static final Color HUD_BG = new Color(0.12f, 0.12f, 0.12f, 1f);
    private static final int   CELL       = 32;
    private static final float POPUP_W    = 220;
    private static final float POPUP_H    = 110;

    private final Stage             stage;
    private final ShapeRenderer     shapeRenderer;
    private final SpriteBatch       hudBatch;
    private final BitmapFont        font;
    private final BitmapFont        btnFont;
    private final Texture           popupBgTexture;
    private final Texture           bk1;
    private final Texture           bk2;
    private final GamePopup         optionsPopup;
    private final GamePopup         gameOverPopup;
    private final Runnable          onExitRunnable;
    private final AchievementBanner achievementBanner;

    /**
     * @param onExit called when Exit is chosen inside the options popup.
     */
    public Hud(SnakeGoldMiner game, final Runnable onExit) {
        this.onExitRunnable = onExit;

        stage = new Stage(new FitViewport(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT));

        Texture btn  = game.getAssetManager().get(ImagesPaths.MENU_BUTTON);
        Texture btnP = game.getAssetManager().get(ImagesPaths.MENU_BUTTON_PRESSED);
        btnFont = new BitmapFont();
        font    = new BitmapFont();
        bk1 = new Texture(Gdx.files.internal(ImagesPaths.BK_1));
        bk2 = new Texture(Gdx.files.internal(ImagesPaths.BK_2));

        ImageTextButton.ImageTextButtonStyle style = new ImageTextButton.ImageTextButtonStyle(
                new TextureRegionDrawable(btn), new TextureRegionDrawable(btnP),
                new TextureRegionDrawable(btn), btnFont);

        // ── Shared popup background ───────────────────────────────────────
        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(0.10f, 0.10f, 0.10f, 0.95f);
        pm.fill();
        popupBgTexture = new Texture(pm);
        pm.dispose();

        float popupX = (GameConfig.SCREEN_WIDTH    - POPUP_W) / 2f;
        float popupY = (GameConfig.PLAY_AREA_HEIGHT - POPUP_H) / 2f;

        // ── Options popup ─────────────────────────────────────────────────
        optionsPopup = new GamePopup(stage, popupBgTexture, font, style,
                "OPTIONS", new String[]{"Resume", "Exit"},
                POPUP_W, POPUP_H, popupX, popupY);

        // ── Options button (HUD bar) ──────────────────────────────────────
        ImageTextButton optionsButton = new ImageTextButton("Options", style);
        optionsButton.setSize(70, 20);
        optionsButton.setPosition(GameConfig.SCREEN_WIDTH - 80, GameConfig.PLAY_AREA_HEIGHT + 6);
        optionsButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { toggleOptions(); }
        });
        stage.addActor(optionsButton);

        // ── Game-over popup ───────────────────────────────────────────────
        gameOverPopup = new GamePopup(stage, popupBgTexture, font, style,
                "GAME OVER", new String[]{"Restart", "Main Menu"},
                POPUP_W, POPUP_H, popupX, popupY);

        // ── Keyboard navigation — delegate to whichever popup is open ─────
        stage.addListener(new InputListener() {
            @Override public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE && !gameOverPopup.isShowing()) {
                    toggleOptions();
                    return true;
                }
                if (optionsPopup.isShowing())  return optionsPopup.handleKey(keycode);
                if (gameOverPopup.isShowing()) return gameOverPopup.handleKey(keycode);
                return false;
            }
        });

        achievementBanner = new AchievementBanner();
        stage.addActor(achievementBanner);

        shapeRenderer = new ShapeRenderer();
        hudBatch      = new SpriteBatch();
    }

    // ── Popup control ──────────────────────────────────────────────────────

    private void toggleOptions() {
        if (optionsPopup.isShowing()) {
            optionsPopup.hide(); // clicking Options again = Resume
        } else {
            optionsPopup.show(new Runnable[]{
                new Runnable() { @Override public void run() { /* no-op: popup auto-hides */ } },
                new Runnable() { @Override public void run() { onExitRunnable.run(); } }
            });
        }
    }

    /** Show the "out of lives" popup. Game is frozen until the player chooses. */
    public void showGameOverPopup(Runnable onRestart, Runnable onMainMenu) {
        gameOverPopup.show(new Runnable[]{ onRestart, onMainMenu });
    }

    // ── Drawing ────────────────────────────────────────────────────────────

    /**
     * Draw the checkerboard play-area background and the HUD bar with stats.
     * Call this BEFORE drawing game entities so sprites appear on top.
     */
    public void drawBackground(Camera camera, String levelText, String livesText, String scoreText) {
        drawCheckerboard(camera);
        drawHudBar(camera, levelText, livesText, scoreText);
    }

    /**
     * Act and draw the HUD stage (options button, popups, achievement banners).
     * Call this AFTER all game entities are drawn so the UI appears on top.
     */
    public void drawStage() {
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    /** Returns true while any popup is open; game logic should be suspended. */
    public boolean isPaused() { return optionsPopup.isShowing() || gameOverPopup.isShowing(); }

    public void showAchievementBanner(AchievementEnum ach) { achievementBanner.show(ach); }

    public Stage getStage() { return stage; }

    public void resize(int width, int height) { stage.getViewport().update(width, height); }

    public void dispose() {
        stage.dispose();
        shapeRenderer.dispose();
        hudBatch.dispose();
        font.dispose();
        btnFont.dispose();
        popupBgTexture.dispose();
        bk1.dispose();
        bk2.dispose();
        achievementBanner.dispose();
    }

    // ── Private drawing ────────────────────────────────────────────────────

    private void drawCheckerboard(Camera camera) {
        int cols = (int) (GameConfig.SCREEN_WIDTH     / CELL);
        int rows = (int) (GameConfig.PLAY_AREA_HEIGHT / CELL);
        hudBatch.setProjectionMatrix(camera.combined);
        hudBatch.begin();
        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                hudBatch.draw((col + row) % 2 == 0 ? bk1 : bk2, col * CELL, row * CELL, CELL, CELL);
            }
        }
        hudBatch.end();
    }

    private void drawHudBar(Camera camera, String levelText, String livesText, String scoreText) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(HUD_BG);
        shapeRenderer.rect(0, GameConfig.PLAY_AREA_HEIGHT, GameConfig.SCREEN_WIDTH, GameConfig.HUD_HEIGHT);
        shapeRenderer.end();

        float textY = GameConfig.PLAY_AREA_HEIGHT + 22;
        hudBatch.setProjectionMatrix(camera.combined);
        hudBatch.begin();
        font.draw(hudBatch, levelText, 10, textY);
        font.draw(hudBatch, livesText, GameConfig.SCREEN_WIDTH / 2f - 30, textY);
        font.draw(hudBatch, scoreText, GameConfig.SCREEN_WIDTH - 180, textY);
        hudBatch.end();
    }
}
