package com.strzal.snakeminer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.strzal.gdxUtilLib.screenManager.ScreenManager;
import com.strzal.gdxUtilLib.utils.GdxUtils;
import com.strzal.snakeminer.SnakeGoldMiner;
import com.strzal.snakeminer.achievement.AchievementEnum;
import com.strzal.snakeminer.config.GameConfig;
import com.strzal.snakeminer.config.ImagesPaths;
import com.strzal.snakeminer.handler.LevelStats;
import com.strzal.snakeminer.hud.Hud;
import com.strzal.snakeminer.levels.LevelData;
import com.strzal.snakeminer.screenManager.ScreenEnum;

import java.util.List;

public class StoryGameScreen extends ScreenAdapter {

    private static final int POINTS_PER_GOLD = 20;
    private static final int CELL      = 32;
    private static final int MAP_COLS  = 20;
    private static final int MAP_ROWS  = 14;
    private static final int RIGHT = 0, LEFT = 1, UP = 2, DOWN = 3;

    private enum State { PLAYING, LEVEL_COMPLETE, GAME_OVER, YOU_WON }

    // ── Core references ───────────────────────────────────────────────────
    private final SnakeGoldMiner game;
    private final int startLevel;
    private final GameDifficulty difficulty;

    // ── Snake state ───────────────────────────────────────────────────────
    private int truckX, truckY, truckXBefore, truckYBefore;
    private int truckDirection;
    private boolean directionSet;
    private float moveTimer;
    private Array<int[]> bodyParts;   // each element is {x, y}

    // ── Level state ───────────────────────────────────────────────────────
    private int currentLevel;
    private LevelData levelData;
    private Array<Explosive> explosives;
    private int goldCollectedThisLevel;
    private int score;
    private boolean goldAvailable;
    private int goldX, goldY;
    private State state;
    private float sessionTime;
    private boolean sessionSaved;
    private int lives;

    // ── Rendering ─────────────────────────────────────────────────────────
    private SpriteBatch batch;
    private BitmapFont font;
    private GlyphLayout layout;
    private OrthographicCamera camera;
    private FitViewport viewport;
    private Texture truckHeadTex, goldTex, truckBodyTex, wallTex, tntTex;
    private Hud hud;

    // ─────────────────────────────────────────────────────────────────────

    public StoryGameScreen(final SnakeGoldMiner game, int startLevel, GameDifficulty difficulty) {
        this.game = game;
        this.startLevel = startLevel;
        this.difficulty = difficulty;
        this.lives = difficulty.maxLives;
        bodyParts = new Array<int[]>();
        explosives = new Array<Explosive>();
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT, camera);
        camera.position.set(GameConfig.SCREEN_WIDTH / 2f, GameConfig.SCREEN_HEIGHT / 2f, 0);
        camera.update();

        batch = new SpriteBatch();
        font = new BitmapFont();
        layout = new GlyphLayout();

        truckHeadTex = new Texture(Gdx.files.internal("truck_up.png"));
        goldTex      = new Texture(Gdx.files.internal("gold.png"));
        truckBodyTex = new Texture(Gdx.files.internal("cart.png"));
        wallTex      = new Texture(Gdx.files.internal(ImagesPaths.WALL));
        tntTex       = new Texture(Gdx.files.internal(ImagesPaths.TNT));

        hud = new Hud(game, new Runnable() {
            @Override public void run() {
                saveSessionIfNeeded(false);
                ScreenManager.getInstance().showScreen(ScreenEnum.MENU_SCREEN, game);
            }
        });

        InputMultiplexer im = new InputMultiplexer();
        im.addProcessor(hud.getStage());
        Gdx.input.setInputProcessor(im);

        loadLevel(startLevel);
    }

    // ── Level loading ─────────────────────────────────────────────────────

    private void loadLevel(int n) {
        currentLevel = n;
        levelData    = LevelData.forLevel(n);

        truckX = 0; truckY = 0;
        truckXBefore = 0; truckYBefore = 0;
        truckDirection = RIGHT;
        directionSet = false;
        moveTimer = difficulty.moveTime;
        bodyParts.clear();
        goldAvailable = false;
        goldCollectedThisLevel = 0;
        score = 0;
        sessionTime = 0;
        sessionSaved = false;
        state = State.PLAYING;

        explosives.clear();
        for (int row = 0; row < MAP_ROWS; row++) {
            for (int col = 0; col < MAP_COLS; col++) {
                if (levelData.map[row][col] == 2) {
                    explosives.add(new Explosive(col * CELL, (MAP_ROWS - 1 - row) * CELL,
                            levelData.explosiveOnTime));
                }
            }
        }
    }

    // ── Render loop ───────────────────────────────────────────────────────

    @Override
    public void render(float delta) {
        switch (state) {
            case PLAYING:
                if (!hud.isPaused()) {
                    sessionTime += delta;
                    queryInput();
                    updateExplosives(delta);
                    updateSnake(delta);
                }
                break;
            case LEVEL_COMPLETE:
                if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER))
                    loadLevel(currentLevel + 1);
                break;
            case GAME_OVER:
                if (!hud.isPaused()) {
                    if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER))
                        loadLevel(currentLevel);
                    if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                        ScreenManager.getInstance().showScreen(ScreenEnum.MENU_SCREEN, game);
                        return;
                    }
                }
                break;
            case YOU_WON:
                if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) ||
                        Gdx.input.isKeyJustPressed(Input.Keys.ENTER) ||
                        Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                    ScreenManager.getInstance().showScreen(ScreenEnum.MENU_SCREEN, game);
                    return;
                }
                break;
        }

        String goldInfo = "Gold: " + goldCollectedThisLevel + "/" + levelData.goldTarget
                + "  Score: " + score;

        GdxUtils.clearScreen();
        hud.drawBackground(camera, "Level: " + currentLevel, "Lives: " + lives, goldInfo);
        renderWalls();
        renderExplosives();
        renderGame();
        renderOverlay();
        hud.drawStage();
    }

    // ── Input ─────────────────────────────────────────────────────────────

    private void queryInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))  updateDirection(LEFT);
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) updateDirection(RIGHT);
        if (Gdx.input.isKeyPressed(Input.Keys.UP))    updateDirection(UP);
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))  updateDirection(DOWN);
    }

    private void updateDirection(int newDir) {
        if (directionSet || truckDirection == newDir) return;
        int opposite = (newDir == RIGHT) ? LEFT  : (newDir == LEFT)  ? RIGHT :
                       (newDir == UP)    ? DOWN  : UP;
        if (truckDirection != opposite) { truckDirection = newDir; directionSet = true; }
    }

    // ── Snake movement ────────────────────────────────────────────────────

    private void updateSnake(float delta) {
        moveTimer -= delta;
        if (moveTimer > 0) return;
        moveTimer = difficulty.moveTime;

        truckXBefore = truckX;
        truckYBefore = truckY;

        switch (truckDirection) {
            case RIGHT: truckX += CELL; break;
            case LEFT:  truckX -= CELL; break;
            case UP:    truckY += CELL; break;
            case DOWN:  truckY -= CELL; break;
        }

        // Wrap around play area edges
        if (truckX >= GameConfig.SCREEN_WIDTH)    truckX = 0;
        if (truckX < 0)                           truckX = (MAP_COLS - 1) * CELL;
        if (truckY >= GameConfig.PLAY_AREA_HEIGHT) truckY = 0;
        if (truckY < 0)                           truckY = (MAP_ROWS - 1) * CELL;

        if (isWall(truckX, truckY) || isOnVisibleExplosive(truckX, truckY)) {
            triggerGameOver(); return;
        }

        updateBodyParts();
        checkSelfCollision();
        if (state != State.PLAYING) return;
        checkGoldCollision();
        checkAndPlaceGold();
        directionSet = false;
    }

    private void updateBodyParts() {
        if (bodyParts.size > 0) {
            int[] tail = bodyParts.removeIndex(0);
            tail[0] = truckXBefore;
            tail[1] = truckYBefore;
            bodyParts.add(tail);
        }
    }

    private void checkSelfCollision() {
        for (int[] part : bodyParts) {
            if (part[0] == truckX && part[1] == truckY) { triggerGameOver(); return; }
        }
    }

    private void checkGoldCollision() {
        if (goldAvailable && goldX == truckX && goldY == truckY) {
            bodyParts.insert(0, new int[]{truckX, truckY});
            score += POINTS_PER_GOLD;
            goldCollectedThisLevel++;
            goldAvailable = false;
            if (goldCollectedThisLevel >= levelData.goldTarget) triggerLevelComplete();
        }
    }

    private void checkAndPlaceGold() {
        if (goldAvailable) return;
        Array<int[]> candidates = new Array<int[]>();
        for (int row = 0; row < MAP_ROWS; row++) {
            for (int col = 0; col < MAP_COLS; col++) {
                if (levelData.map[row][col] == 0) {
                    int cx = col * CELL, cy = (MAP_ROWS - 1 - row) * CELL;
                    if (cx != truckX || cy != truckY) candidates.add(new int[]{cx, cy});
                }
            }
        }
        if (candidates.size > 0) {
            int[] pos = candidates.get(MathUtils.random(candidates.size - 1));
            goldX = pos[0]; goldY = pos[1]; goldAvailable = true;
        }
    }

    // ── Explosives ────────────────────────────────────────────────────────

    private void updateExplosives(float delta) {
        if (levelData.explosiveOnTime <= 0) return;
        for (Explosive e : explosives) {
            e.timer -= delta;
            if (e.timer <= 0) {
                e.visible = !e.visible;
                e.timer = e.visible ? levelData.explosiveOnTime : levelData.explosiveOffTime;
            }
        }
        if (isOnVisibleExplosive(truckX, truckY)) triggerGameOver();
    }

    // ── Collision helpers ─────────────────────────────────────────────────

    private boolean isWall(int x, int y) {
        int col = x / CELL, row = MAP_ROWS - 1 - y / CELL;
        if (col < 0 || col >= MAP_COLS || row < 0 || row >= MAP_ROWS) return false;
        return levelData.map[row][col] == 1;
    }

    private boolean isOnVisibleExplosive(int x, int y) {
        for (Explosive e : explosives) {
            if (e.visible && e.x == x && e.y == y) return true;
        }
        return false;
    }

    // ── State transitions ─────────────────────────────────────────────────

    private void triggerGameOver() {
        lives--;
        state = State.GAME_OVER;
        saveSessionIfNeeded(false);
        if (lives <= 0) {
            hud.showGameOverPopup(
                new Runnable() {
                    @Override public void run() {
                        lives = difficulty.maxLives;
                        loadLevel(1);
                    }
                },
                new Runnable() {
                    @Override public void run() {
                        ScreenManager.getInstance().showScreen(ScreenEnum.MENU_SCREEN, game);
                    }
                }
            );
        }
    }

    private void triggerLevelComplete() {
        boolean won = currentLevel >= LevelData.totalLevels();
        state = won ? State.YOU_WON : State.LEVEL_COMPLETE;
        saveSessionIfNeeded(won);
    }

    private void saveSessionIfNeeded(boolean storyJustCompleted) {
        if (!sessionSaved) {
            game.getGameStatsHandler().saveLevelData(score, goldCollectedThisLevel, (int) sessionTime);
            if (storyJustCompleted) game.getGameStatsHandler().saveStoryCompleted();
            checkAchievements(storyJustCompleted);
            sessionSaved = true;
        }
    }

    private void checkAchievements(boolean storyCompleted) {
        LevelStats saved = game.getGameStatsHandler().getSavedData();
        List<AchievementEnum> newlyUnlocked = game.getAchievementHandler().checkAndUnlock(
                saved.getTotalTimesPlayed(),
                saved.getTotalGoldCollected(),
                saved.getTotalPlayTimeSeconds(),
                storyCompleted
        );
        for (AchievementEnum ach : newlyUnlocked) {
            hud.showAchievementBanner(ach);
        }
    }

    // ── Rendering ─────────────────────────────────────────────────────────

    private void renderWalls() {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for (int row = 0; row < MAP_ROWS; row++) {
            for (int col = 0; col < MAP_COLS; col++) {
                if (levelData.map[row][col] == 1)
                    batch.draw(wallTex, col * CELL, (MAP_ROWS - 1 - row) * CELL, CELL, CELL);
            }
        }
        batch.end();
    }

    private void renderExplosives() {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for (Explosive e : explosives) {
            if (e.visible) batch.draw(tntTex, e.x, e.y, CELL, CELL);
        }
        batch.end();
    }

    private void renderGame() {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        for (int[] part : bodyParts) {
            if (part[0] != truckX || part[1] != truckY)
                batch.draw(truckBodyTex, part[0], part[1]);
        }

        TextureRegion region = new TextureRegion(truckHeadTex);
        float ox = region.getRegionWidth() / 2f, oy = region.getRegionHeight() / 2f;
        float rotation = (truckDirection == RIGHT) ? -90 : (truckDirection == LEFT) ? 90 :
                         (truckDirection == UP)    ?   0 : 180;
        batch.draw(region, truckX, truckY, ox, oy,
                region.getRegionWidth(), region.getRegionHeight(), 1, 1, rotation);

        if (goldAvailable) batch.draw(goldTex, goldX, goldY);

        batch.end();
    }

    private void renderOverlay() {
        if (state == State.PLAYING) return;
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        if (state == State.LEVEL_COMPLETE)
            drawCentered("Level " + currentLevel + " Complete!  SPACE/ENTER for next level", 0);
        else if (state == State.GAME_OVER && lives > 0)
            drawCentered("Game Over!  " + lives + " lives left.  SPACE/ENTER to retry   ESC for menu", 0);
        else if (state == State.YOU_WON)
            drawCentered("You Won! Congratulations!  SPACE/ENTER or ESC for menu", 0);
        batch.end();
    }

    private void drawCentered(String text, float offsetY) {
        layout.setText(font, text);
        font.draw(batch, text,
                (GameConfig.SCREEN_WIDTH  - layout.width)  / 2f,
                (GameConfig.PLAY_AREA_HEIGHT - layout.height) / 2f + offsetY);
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        hud.resize(width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        truckHeadTex.dispose();
        goldTex.dispose();
        truckBodyTex.dispose();
        wallTex.dispose();
        tntTex.dispose();
        hud.dispose();
    }

    // ── Inner class: Explosive ────────────────────────────────────────────

    private static class Explosive {
        int x, y;
        boolean visible = true;
        float timer;
        Explosive(int x, int y, float onTime) { this.x = x; this.y = y; this.timer = onTime; }
    }
}
