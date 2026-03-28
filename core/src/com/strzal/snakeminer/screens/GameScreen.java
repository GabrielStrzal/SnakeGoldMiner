package com.strzal.snakeminer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.strzal.gdxUtilLib.screenManager.ScreenManager;
import com.strzal.gdxUtilLib.utils.GdxUtils;
import com.strzal.snakeminer.SnakeGoldMiner;
import com.strzal.snakeminer.achievement.AchievementEnum;
import com.strzal.snakeminer.achievement.AchievementHandler;
import com.strzal.snakeminer.config.GameConfig;
import com.strzal.snakeminer.handler.GameStatsHandler;
import com.strzal.snakeminer.handler.LevelStats;
import com.strzal.snakeminer.hud.Hud;
import com.strzal.snakeminer.screenManager.ScreenEnum;

import java.util.List;

/**
 * Created by Gabriel on 11/12/2017.
 */

public class GameScreen extends ScreenAdapter{

    public static final String SCORE_TEXT = "Score: ";
    public static final String HIGH_SCORE_TEXT = "Hight Score: ";
    private static final String GAME_OVER_TEXT = "Game Over... Tap SPACE/ENTER to restart!";
    private static final int POINTS_PER_GOLD = 20;
    private static final int GRID_CELL = 32;
    private static final int TRUCK_MOVEMENT = 32;
    private static final int RIGHT = 0;
    private static final int LEFT = 1;
    private static final int UP = 2;
    private static final int DOWN = 3;
    private static final float ACHIEVEMENT_CHECK_INTERVAL = 10f;


    private int truckDirection = RIGHT;
    private float timer;
    private int score = 0;
    private int highScore;
    private int lives;

    // Session stats
    private float sessionTime = 0;
    private int sessionGoldCollected = 0;
    private float achievementCheckTimer = 0;

    private SpriteBatch batch;
    private Texture truckHead;
    private Texture gold;
    private Texture truckBody;
    private Viewport viewport;
    private Camera camera;
    private BitmapFont bitmapFont;
    private GlyphLayout layout = new GlyphLayout();
    private Hud hud;

    private SnakeGoldMiner game;
    private GameDifficulty difficulty;
    private GameStatsHandler gameStatsHandler;
    private AchievementHandler achievementHandler;

    private enum STATE {
        PLAYING, GAME_OVER
    }
    private STATE state = STATE.PLAYING;

    private int truckX = 0, truckY = 0;
    private boolean goldAvailable = false;
    private int goldX, goldY;
    private Array<BodyPart> bodyParts = new Array<BodyPart>();

    private boolean directionSet;
    private boolean hasHit = false;

    private int direction = 1;

    private int truckXBeforeUpdate = 0, truckYBeforeUpdate = 0;


    public GameScreen(final SnakeGoldMiner game, GameDifficulty difficulty){
        this.game = game;
        this.difficulty = difficulty;
        this.lives = difficulty.maxLives;
        this.timer = difficulty.moveTime;
        gameStatsHandler = game.getGameStatsHandler();
        achievementHandler = game.getAchievementHandler();
        highScore = gameStatsHandler.getSavedData().getHighScore();
        hud = new Hud(game, new Runnable() {
            @Override public void run() {
                gameStatsHandler.saveLevelData(score, sessionGoldCollected, (int) sessionTime);
                ScreenManager.getInstance().showScreen(ScreenEnum.MENU_SCREEN, game);
            }
        });
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(hud.getStage());
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void show() {
        viewport = new FitViewport(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT, camera);
        camera = new OrthographicCamera(viewport.getWorldWidth(), viewport.getWorldHeight());
        camera.position.set(GameConfig.SCREEN_WIDTH / 2, GameConfig.SCREEN_HEIGHT / 2, 0);
        camera.update();
        bitmapFont = new BitmapFont();
        batch = new SpriteBatch();
        truckHead = new Texture(Gdx.files.internal("truck_up.png"));
        gold = new Texture(Gdx.files.internal("gold.png"));
        truckBody = new Texture(Gdx.files.internal("cart.png"));
    }

    @Override
    public void render(float delta) {
        switch(state) {
            case PLAYING: {
                if (!hud.isPaused()) {
                    sessionTime += delta;
                    achievementCheckTimer += delta;

                    queryInput();
                    queryTouchInput();
                    updateTruck(delta);
                    checkGoldCollision();
                    checkAndPlaceGold();

                    if (achievementCheckTimer >= ACHIEVEMENT_CHECK_INTERVAL) {
                        achievementCheckTimer = 0;
                        checkAchievementsMidGame();
                    }
                }
            }
            break;
            case GAME_OVER: {
                if (!hud.isPaused()) checkForRestart();
            }
            break;
        }
        GdxUtils.clearScreen();
        hud.drawBackground(camera, difficulty.displayName, "Lives: " + lives, SCORE_TEXT + score);
        draw();
        hud.drawStage();
    }

    @Override
    public void resize(int width, int height) {
        hud.resize(width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();
        bitmapFont.dispose();
        truckHead.dispose();
        gold.dispose();
        truckBody.dispose();
        hud.dispose();
    }

    private void updateTruck(float delta) {
        if(!hasHit) {
            timer -= delta;
            if (timer <= 0) {
                timer = difficulty.moveTime;
                moveTruck();
                checkForOutOfBounds();
                updateBodyPartsPosition();
                checkTruckBodyCollision();
                directionSet = false;
            }
        }
    }

    private void draw() {
        batch.setProjectionMatrix(camera.projection);
        batch.setTransformMatrix(camera.view);
        batch.begin();

        drawTruckHead();

        for (BodyPart bodyPart : bodyParts) {
            bodyPart.draw(batch);
        }
        if (goldAvailable) {
            batch.draw(gold, goldX, goldY);
        }
        drawGameOver();
        batch.end();
    }

    private void drawTruckHead(){
        TextureRegion region = new TextureRegion(truckHead);
        float originX = (float) region.getRegionWidth() / 2;
        float originY = (float) region.getRegionHeight() / 2;
        float width = region.getRegionWidth();
        float height = region.getRegionHeight();
        float scaleX = 1;
        float scaleY = 1;
        float rotation = -90;
        switch (truckDirection) {
            case RIGHT: { rotation = -90; break; }
            case LEFT:  { rotation =  90; break; }
            case UP:    { rotation =   0; break; }
            case DOWN:  { rotation = 180; break; }
        }
        batch.draw(region, truckX, truckY, originX, originY, width, height, scaleX, scaleY, rotation);
    }

    private void checkForOutOfBounds() {
        if (truckX >= viewport.getWorldWidth())        truckX = 0;
        if (truckX < 0)                                truckX = (int) (viewport.getWorldWidth() - TRUCK_MOVEMENT);
        if (truckY >= GameConfig.PLAY_AREA_HEIGHT)     truckY = 0;
        if (truckY < 0)                                truckY = (int) (GameConfig.PLAY_AREA_HEIGHT - TRUCK_MOVEMENT);
    }

    private void moveTruck() {
        truckXBeforeUpdate = truckX;
        truckYBeforeUpdate = truckY;
        switch (truckDirection) {
            case RIGHT: { truckX += TRUCK_MOVEMENT; return; }
            case LEFT:  { truckX -= TRUCK_MOVEMENT; return; }
            case UP:    { truckY += TRUCK_MOVEMENT; return; }
            case DOWN:  { truckY -= TRUCK_MOVEMENT; return; }
        }
    }

    private void queryInput() {
        boolean lPressed = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean rPressed = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        boolean uPressed = Gdx.input.isKeyPressed(Input.Keys.UP);
        boolean dPressed = Gdx.input.isKeyPressed(Input.Keys.DOWN);
        if (lPressed) updateDirection(LEFT);
        if (rPressed) updateDirection(RIGHT);
        if (uPressed) updateDirection(UP);
        if (dPressed) updateDirection(DOWN);
    }

    private void queryTouchInput(){
        if (Gdx.input.isTouched() && !directionSet) {
            if (Gdx.input.getX() > Gdx.graphics.getWidth() / 2) {
                if(direction == 3) { direction = 0; } else { direction++; }
            } else {
                if(direction == 0) { direction = 3; } else { direction--; }
            }
            directionSet = true;
            if (direction == 0) truckDirection = UP;
            if (direction == 1) truckDirection = RIGHT;
            if (direction == 2) truckDirection = DOWN;
            if (direction == 3) truckDirection = LEFT;
        }
    }

    private void checkAndPlaceGold() {
        if (!goldAvailable) {
            do {
                goldX = MathUtils.random((int) (viewport.getWorldWidth()    / TRUCK_MOVEMENT) - 1) * TRUCK_MOVEMENT;
                goldY = MathUtils.random((int) (GameConfig.PLAY_AREA_HEIGHT / TRUCK_MOVEMENT) - 1) * TRUCK_MOVEMENT;
                goldAvailable = true;
            } while (goldX == truckX && goldY == truckY);
        }
    }

    private void checkGoldCollision() {
        if (goldAvailable && goldX == truckX && goldY == truckY) {
            BodyPart bodyPart = new BodyPart(truckBody);
            bodyPart.updateBodyPosition(truckX, truckY);
            bodyParts.insert(0, bodyPart);
            addToScore();
            goldAvailable = false;
            sessionGoldCollected++;
            checkAchievementsMidGame();
        }
    }

    private void checkAchievementsMidGame() {
        LevelStats saved = gameStatsHandler.getSavedData();
        int totalGold  = saved.getTotalGoldCollected()   + sessionGoldCollected;
        int totalTime  = saved.getTotalPlayTimeSeconds()  + (int) sessionTime;
        int totalPlays = saved.getTotalTimesPlayed();

        List<AchievementEnum> newlyUnlocked = achievementHandler.checkAndUnlock(totalPlays, totalGold, totalTime, false, false, false);
        for (AchievementEnum ach : newlyUnlocked) {
            hud.showAchievementBanner(ach);
        }
    }

    private void updateBodyPartsPosition() {
        if (bodyParts.size > 0) {
            BodyPart bodyPart = bodyParts.removeIndex(0);
            bodyPart.updateBodyPosition(truckXBeforeUpdate, truckYBeforeUpdate);
            bodyParts.add(bodyPart);
        }
    }

    private class BodyPart {
        private int x, y;
        private Texture texture;
        public BodyPart(Texture texture) { this.texture = texture; }
        public void updateBodyPosition(int x, int y) { this.x = x; this.y = y; }
        public void draw(Batch batch) {
            if (!(x == truckX && y == truckY)) batch.draw(texture, x, y);
        }
    }

    private void updateIfNotOppositeDirection(int newTruckDirection, int oppositeDirection) {
        if (truckDirection != oppositeDirection) truckDirection = newTruckDirection;
    }

    private void updateDirection(int newTruckDirection) {
        if (!directionSet && truckDirection != newTruckDirection) {
            directionSet = true;
            switch (newTruckDirection) {
                case LEFT:  { updateIfNotOppositeDirection(newTruckDirection, RIGHT); } break;
                case RIGHT: { updateIfNotOppositeDirection(newTruckDirection, LEFT);  } break;
                case UP:    { updateIfNotOppositeDirection(newTruckDirection, DOWN);  } break;
                case DOWN:  { updateIfNotOppositeDirection(newTruckDirection, UP);    } break;
            }
        }
    }

    private void checkTruckBodyCollision() {
        for (BodyPart bodyPart : bodyParts) {
            if (bodyPart.x == truckX && bodyPart.y == truckY) {
                lives--;
                state = STATE.GAME_OVER;
                gameStatsHandler.saveLevelData(score, sessionGoldCollected, (int) sessionTime);
                checkAchievementsMidGame();
                if (lives <= 0) {
                    hud.showGameOverPopup(
                        new Runnable() {
                            @Override public void run() { doRestart(); }
                        },
                        new Runnable() {
                            @Override public void run() {
                                gameStatsHandler.saveLevelData(score, sessionGoldCollected, (int) sessionTime);
                                ScreenManager.getInstance().showScreen(ScreenEnum.MENU_SCREEN, game);
                            }
                        }
                    );
                }
            }
        }
    }

    private void checkForRestart() {
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) || Gdx.input.isKeyPressed(Input.Keys.ENTER)) doRestart();
    }

    private void doRestart() {
        if (lives <= 0) lives = difficulty.maxLives;
        state = STATE.PLAYING;
        bodyParts.clear();
        truckDirection = RIGHT;
        directionSet = false;
        timer = difficulty.moveTime;
        truckX = 0;
        truckY = 0;
        truckXBeforeUpdate = 0;
        truckYBeforeUpdate = 0;
        goldAvailable = false;
        score = 0;
        sessionTime = 0;
        sessionGoldCollected = 0;
        achievementCheckTimer = 0;
    }

    private void addToScore() {
        score += POINTS_PER_GOLD;
    }

    private void drawGameOver() {
        if (state == STATE.GAME_OVER && lives > 0) {
            String highScoreAsString = Integer.toString(gameStatsHandler.getSavedData().getHighScore());
            drawTextOnScreenCenter(HIGH_SCORE_TEXT + highScoreAsString, 0, 40);
            drawTextOnScreenCenter(SCORE_TEXT + score, 0, 20);
            drawTextOnScreenCenter(lives + " lives left  —  SPACE/ENTER to continue", 0, 0);
        }
    }

    private void drawTextOnScreenCenter(String text, float differenceX, float differenceY){
        layout.setText(bitmapFont, text);
        bitmapFont.draw(batch, text,
                ((viewport.getWorldWidth() - layout.width) / 2) + differenceX,
                ((viewport.getWorldHeight() - layout.height) / 2) + differenceY);
    }

    public int getScore() { return score; }
    public int getSessionGoldCollected() { return sessionGoldCollected; }
    public int getSessionTimeSeconds() { return (int) sessionTime; }
}
