package com.strzal.snakeminer.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Queue;
import com.strzal.snakeminer.achievement.AchievementEnum;
import com.strzal.snakeminer.config.GameConfig;

public class AchievementBanner extends Table {

    private static final float BANNER_HEIGHT   = 44f;
    private static final float SLIDE_DURATION  = 0.35f;
    private static final float DISPLAY_SECONDS = 3f;

    private final Label nameLabel;
    private final Texture bgTexture;
    private final Queue<AchievementEnum> queue = new Queue<>();
    private boolean isShowing = false;

    public AchievementBanner() {
        BitmapFont font = new BitmapFont();

        // Semi-transparent dark background
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0f, 0f, 0f, 0.82f);
        pixmap.fill();
        bgTexture = new Texture(pixmap);
        pixmap.dispose();
        setBackground(new TextureRegionDrawable(new TextureRegion(bgTexture)));

        Label.LabelStyle headerStyle = new Label.LabelStyle(font, Color.YELLOW);
        Label.LabelStyle nameStyle   = new Label.LabelStyle(font, Color.WHITE);

        Label headerLabel = new Label("Achievement Unlocked!", headerStyle);
        nameLabel = new Label("", nameStyle);

        pad(4);
        add(headerLabel).row();
        add(nameLabel);

        setSize(GameConfig.SCREEN_WIDTH, BANNER_HEIGHT);
        // Start off-screen above the top edge
        setPosition(0, GameConfig.SCREEN_HEIGHT);
    }

    /** Queues an achievement to be shown; plays immediately if nothing is showing. */
    public void show(AchievementEnum ach) {
        queue.addLast(ach);
        if (!isShowing) showNext();
    }

    private void showNext() {
        if (queue.size == 0) {
            isShowing = false;
            return;
        }
        isShowing = true;
        AchievementEnum ach = queue.removeFirst();
        nameLabel.setText(ach.name);

        float visibleY = GameConfig.SCREEN_HEIGHT - BANNER_HEIGHT;
        clearActions();
        addAction(Actions.sequence(
            Actions.moveTo(0, visibleY, SLIDE_DURATION),
            Actions.delay(DISPLAY_SECONDS),
            Actions.moveTo(0, GameConfig.SCREEN_HEIGHT, SLIDE_DURATION),
            Actions.run(new Runnable() {
                @Override public void run() { showNext(); }
            })
        ));
    }

    public void dispose() {
        bgTexture.dispose();
    }
}
