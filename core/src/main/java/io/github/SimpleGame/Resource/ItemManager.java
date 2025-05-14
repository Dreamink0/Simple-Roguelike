package io.github.SimpleGame.Resource;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import io.github.SimpleGame.Item.Weapon;

public class ItemManager{
    private Weapon weapon;
    private TextureAtlas textureAtlas;
    private ResourceManager resourceManager;
    public ItemManager() {

    }
    public ItemManager(ResourceManager resourceManager) {
        this.resourceManager=resourceManager;
    }
    public void render(){

    }
}
