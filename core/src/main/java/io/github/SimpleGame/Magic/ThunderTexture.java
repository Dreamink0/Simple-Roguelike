package io.github.SimpleGame.Magic;

import com.badlogic.gdx.graphics.Texture;
import io.github.SimpleGame.Config;
import io.github.SimpleGame.Tool.AnimationTool;

public class ThunderTexture extends MagicAnimation{
    private static final String[] ICON={
        "Magic/Lightning/ICON2.png",
        "Magic/Lightning/ICON.png"
    };
    private static final String[] effectPaths = {
        "Magic/Lightning/Thunder splash w blur.png",
        "Magic/Lightning/Thunderstruck w blur.png",
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
        animations[0].create("Thunder splash w blur",new Texture(effectPaths[0]),1,14,1f);
        animations[1].create("Thunderstruck w blur",new Texture(effectPaths[1]),1,13,1f);
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
