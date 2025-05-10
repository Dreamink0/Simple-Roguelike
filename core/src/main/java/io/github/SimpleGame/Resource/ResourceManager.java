package io.github.SimpleGame.Resource;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

public class ResourceManager {
    private AssetManager assetManager;
    public void loadResource(String fileName) {
        assetManager = new AssetManager();
        assetManager.load(fileName, Texture.class);
    }
}
