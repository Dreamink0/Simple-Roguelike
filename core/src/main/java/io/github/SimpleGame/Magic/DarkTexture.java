package io.github.SimpleGame.Magic;

import com.badlogic.gdx.graphics.Texture;
import io.github.SimpleGame.Tool.AnimationTool;

public class DarkTexture {
    private static final String[] ICON={
        "Magic/Dark/ICON2.png",
        "Magic/Dark/ICON.png"
    };
    private static final String[] effectPaths = {
        "Magic/Dark/Dark.png",
    };
    public static Texture[] getICONTexture(){
        Texture[] textures=new Texture[ICON.length];
        for (int i = 0; i < ICON.length; i++) {textures[i]=new Texture(ICON[i]);}
        return textures;
    }
    public static Texture[] getEffectTexture(){
        Texture[] textures=new Texture[getEffectCount()];
        for (int i = 0; i < getEffectCount(); i++) {textures[i]=new Texture(effectPaths[i]);}
        return textures;
    }
    public static AnimationTool[] getAnimation(){
        AnimationTool[] animations=new AnimationTool[getEffectCount()];
        for (int i = 0; i < animations.length; i++) {animations[i]=new AnimationTool();}
        animations[0].create("Dark",new Texture(effectPaths[0]),2,10,0.1f);
        return animations;
    }
    public static String[] getICON() {
        return ICON;
    }

    public static String[] getEffectPaths() {
        return effectPaths;
    }

    public static int getEffectCount() {
        return effectPaths.length;
    }
}
