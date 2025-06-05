package io.github.SimpleGame.Item;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import io.github.SimpleGame.Tool.AnimationTool;

public class WeaponEffectsTextureManager {
    protected static final AssetManager assettManager = new AssetManager();
    private static String[] prePath={
        "Effects/WeaponEffects/Weapon0/",
        "Effects/WeaponEffects/Weapon1/"
    };
    private static Texture effects;
    private static AnimationTool animation;
    public static void loadAssets(){
        assettManager.load(prePath[0]+0+".png", Texture.class);
        assettManager.finishLoading();
        effects = assettManager.get(prePath[0]+0+".png", Texture.class);
    }
    public AnimationTool create(int ID,int weaponID){
        animation = new AnimationTool();
        if(ID==0){
            if(weaponID==0) animation.create("Weapon0",effects,1,13, 0.02f);
        }
        return animation;
    }
}
