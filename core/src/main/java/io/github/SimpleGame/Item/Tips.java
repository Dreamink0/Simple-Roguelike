package io.github.SimpleGame.Item;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.SimpleGame.Tool.AnimationTool;

public class Tips {
    public static void E(SpriteBatch batch,float x,float y){
        AnimationTool Eanimation=new AnimationTool();
        Texture E=new Texture("UI/OTHER/KEYBOARD/Keyboard/KeyE.png");
        Eanimation.create("E", E, 1, 2, 0.00000009f);
        batch.begin();
        Eanimation.render(batch, x, y + 3, 0.1f, true);
        batch.end();
    }
}
