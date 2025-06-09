//package io.github.SimpleGame.Magic;
//
//import com.badlogic.gdx.graphics.Texture;
//import io.github.SimpleGame.Tool.AnimationTool;
//
//public class IceTexture {
//    private static final String[] effectPaths = {
//        "Effects/WeaponEffects/Weapon0/1.png",
//    };
//    public static Texture[] getEffectTexture(){
//        Texture[] textures=new Texture[getEffectCount()];
//        for (int i = 0; i < getEffectCount(); i++) {textures[i]=new Texture(effectPaths[i]);}
//        for(int i = 0; i < getEffectCount(); i++){
//            textures[i].setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
//        }
//        return textures;
//    }
//    public static AnimationTool[] getAnimation(){
//        AnimationTool[] animations=new AnimationTool[getEffectCount()];
//        for (int i = 0; i < animations.length; i++) {animations[i]=new AnimationTool();}
//        animations[0].create("ICE",new Texture(effectPaths[0]),3,4,0.08f);
//        return animations;
//    }
//
//    public static String[] getEffectPaths() {
//        return effectPaths;
//    }
//
//    public static int getEffectCount() {
//        return effectPaths.length;
//    }
//}
