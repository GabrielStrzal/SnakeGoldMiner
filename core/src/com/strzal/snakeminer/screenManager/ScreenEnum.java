package com.strzal.snakeminer.screenManager;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.strzal.gdxUtilLib.loading.LoadingPaths;
import com.strzal.gdxUtilLib.screenManager.ScreenEnumInterface;
import com.strzal.gdxUtilLib.screens.LoadingScreen;
import com.strzal.snakeminer.SnakeGoldMiner;
import com.strzal.snakeminer.screens.GameModeEnum;
import com.strzal.snakeminer.screens.GameScreen;
import com.strzal.snakeminer.screens.MenuScreen;
import com.strzal.snakeminer.screens.TextScreen;


/**
 * Based on http://www.pixnbgames.com/blog/libgdx/how-to-manage-screens-in-libgdx/
 */

public enum ScreenEnum implements ScreenEnumInterface {
    GAME_SCREEN {
        public Screen getScreen(Object... params) {
            return new GameScreen((SnakeGoldMiner)params[0]);
        }
    },
    TEXT_SCREEN {
        public Screen getScreen(Object... params) {
            return new TextScreen((SnakeGoldMiner)params[0], (String) params[1], (GameModeEnum) params[2]);
        }
    },
    LOADING_SCREEN {
        public Screen getScreen(Object... params) {
            return new LoadingScreen((SnakeGoldMiner)params[0], (LoadingPaths)params[1], (ScreenAdapter)params[2]);
        }
    },
    MENU_SCREEN {
        public Screen getScreen(Object... params) {
            return new MenuScreen((SnakeGoldMiner)params[0]);
        }
    },
//    TUTORIAL_SCREEN {
//        public Screen getScreen(Object... params) {
//            return new TutorialScreen((SnakeGoldMiner)params[0]);
//        }
//    };

}
