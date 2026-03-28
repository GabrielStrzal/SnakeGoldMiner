package com.strzal.snakeminer.hud;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * A reusable modal popup with a title and up to N buttons.
 * Supports both mouse clicks and LEFT/RIGHT + ENTER/SPACE keyboard navigation.
 * The focused button is tinted yellow. Activating a button hides the popup
 * automatically before running the registered callback.
 */
public class GamePopup {

    private static final Color BTN_NORMAL  = Color.WHITE;
    private static final Color BTN_FOCUSED = Color.YELLOW;

    private final Table             table;
    private final ImageTextButton[] buttons;
    private final Runnable[]        callbacks;
    private int focusIndex = 0;

    /**
     * @param stage        stage to add this popup to
     * @param bgTexture    1×1 solid-color texture used as the panel background
     * @param titleFont    font for the title label
     * @param btnStyle     shared button style
     * @param title        popup title text
     * @param buttonLabels one label per button (left to right)
     * @param width/height size of the popup panel
     * @param x/y          bottom-left position of the panel in world coordinates
     */
    public GamePopup(Stage stage,
                     Texture bgTexture,
                     BitmapFont titleFont,
                     ImageTextButton.ImageTextButtonStyle btnStyle,
                     String title,
                     String[] buttonLabels,
                     float width, float height,
                     float x, float y) {

        buttons   = new ImageTextButton[buttonLabels.length];
        callbacks = new Runnable[buttonLabels.length];

        table = new Table();
        table.setBackground(new TextureRegionDrawable(new TextureRegion(bgTexture)));
        table.setSize(width, height);
        table.setPosition(x, y);

        table.add(new Label(title, new Label.LabelStyle(titleFont, Color.WHITE)))
             .colspan(buttonLabels.length).padBottom(12);
        table.row();

        for (int i = 0; i < buttonLabels.length; i++) {
            final int idx = i;
            buttons[i] = new ImageTextButton(buttonLabels[i], btnStyle);
            buttons[i].addListener(new ClickListener() {
                @Override public void clicked(InputEvent event, float x, float y) {
                    activate(idx);
                }
            });
            table.add(buttons[i]).size(90, 28).pad(4);
        }

        table.setVisible(false);
        stage.addActor(table);
    }

    // ── Public API ────────────────────────────────────────────────────────

    /**
     * Show the popup. Callbacks are assigned positionally to each button.
     * The first button is focused. The popup auto-hides before each callback runs.
     */
    public void show(Runnable[] actions) {
        for (int i = 0; i < actions.length && i < callbacks.length; i++) {
            callbacks[i] = actions[i];
        }
        focusIndex = 0;
        updateFocus();
        table.setVisible(true);
    }

    /**
     * Hide the popup without triggering any action (e.g. toggling via Options button).
     */
    public void hide() {
        table.setVisible(false);
        resetFocus();
    }

    public boolean isShowing() { return table.isVisible(); }

    /**
     * Forward a raw keycode to this popup for keyboard navigation.
     * LEFT/RIGHT moves focus; ENTER/SPACE activates the focused button.
     *
     * @return true if the key was consumed by this popup.
     */
    public boolean handleKey(int keycode) {
        if (!isShowing()) return false;
        if (keycode == Input.Keys.LEFT || keycode == Input.Keys.RIGHT) {
            focusIndex = (focusIndex + 1) % buttons.length;
            updateFocus();
            return true;
        }
        if (keycode == Input.Keys.ENTER || keycode == Input.Keys.SPACE) {
            activate(focusIndex);
            return true;
        }
        return false;
    }

    // ── Private helpers ───────────────────────────────────────────────────

    private void activate(int index) {
        hide(); // always hide before running the callback
        if (index < callbacks.length && callbacks[index] != null) {
            callbacks[index].run();
        }
    }

    private void updateFocus() {
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setColor(i == focusIndex ? BTN_FOCUSED : BTN_NORMAL);
        }
    }

    private void resetFocus() {
        for (ImageTextButton b : buttons) b.setColor(BTN_NORMAL);
    }
}
