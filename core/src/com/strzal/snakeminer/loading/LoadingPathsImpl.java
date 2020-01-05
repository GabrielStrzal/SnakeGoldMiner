package com.strzal.snakeminer.loading;

import com.strzal.gdxUtilLib.loading.LoadingPaths;
import com.strzal.snakeminer.config.ImagesPaths;

import java.util.ArrayList;
import java.util.List;

public class LoadingPathsImpl implements LoadingPaths {
    @Override
    public List<String> getTexturePaths() {
        List<String> list = new ArrayList<>();

        //Menu
        list.add(ImagesPaths.MENU_BACKGROUND);
        list.add(ImagesPaths.MENU_BUTTON);
        list.add(ImagesPaths.MENU_BUTTON_PRESSED);

        list.add(ImagesPaths.GAME_TEXT_BACKGROUND);


        return list;
    }

    @Override
    public List<String> getBitmapPaths() {
        List<String> list = new ArrayList<>();
        //list.add(GameAssets.GAME_FONT);
        return list;
    }

    @Override
    public List<String> getTileMapPaths() {
        List<String> list = new ArrayList<>();

        //Levels
        //list.add(LevelNames.LEVEL_1);

        return list;
    }

    @Override
    public List<String> getMusicPaths() {
        return null;
    }

    @Override
    public List<String> getSoundPaths() {
        List<String> list = new ArrayList<>();

        //Menu
        //list.add(SoundPaths.CORRECT_AUDIO);

        return list;
    }

}
