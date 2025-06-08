package io.github.SimpleGame.Item;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import io.github.SimpleGame.Magic.BodyPool;
import io.github.SimpleGame.Tool.AnimationTool;

public class WeaponEffectsTextureManager {
    protected AnimationTool[] animation;
    private static final String[] prePath={
        "Effects/WeaponEffects/Weapon0/",
        "Effects/WeaponEffects/Weapon1/",
    };

    public void create(int ID,int weaponID){
        if(ID==0){
            if(weaponID==0){
                Texture effects = new Texture(prePath[0] + 0 + ".png");
                animation = new AnimationTool[1];
                animation[0] = new AnimationTool();
                animation[0].create("Weapon0", effects,1,28, 0.05f);
            }
            if(weaponID==1){
                Texture effects = new Texture(prePath[0] + 1 + ".png");
                animation = new AnimationTool[1];
                animation[0] = new AnimationTool();
                animation[0].create("Weapon1", effects,1,4, 0.1f);
            }
            if(weaponID==23){
                Texture texture0 = new Texture(prePath[0]+23+"a.png");
                Texture texture1 = new Texture(prePath[0]+23+"b.png");
                Texture texture2 = new Texture(prePath[0]+23+"c.png");
                texture0.unsafeSetFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                texture1.unsafeSetFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                texture2.unsafeSetFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                animation = new AnimationTool[3];
                animation[0] = new AnimationTool();
                animation[1] = new AnimationTool();
                animation[2] = new AnimationTool();
                animation[0].create("Weapon23",texture0,5,2,0.05f);
                animation[1].create("Weapon23",texture1,5,2,0.03f);
                animation[2].create("Weapon23",texture2,4,2,0.1f);
            }
        }
    }

    public AnimationTool[] getAnimation() {
        return animation;
    }
}
