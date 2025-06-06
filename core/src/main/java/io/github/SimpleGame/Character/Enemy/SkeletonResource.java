package io.github.SimpleGame.Character.Enemy;

import com.badlogic.gdx.graphics.Texture;
import io.github.SimpleGame.Tool.AnimationTool;

public class SkeletonResource extends EnemyAnimation{
    private final AnimationTool[] animationTools;

    public SkeletonResource(){
        animationTools = new AnimationTool[5];
        Texture[] texture = new Texture[5];
        String[] action = {
            "Enemy/Skeleton/Skeleton Idle.png",
            "Enemy/Skeleton/Skeleton Walk.png",
            "Enemy/Skeleton/Skeleton Attack.png",
            "Enemy/Skeleton/Skeleton Hit.png",
            "Enemy/Skeleton/Skeleton Dead.png"
        };
        for(String  name: action) assetManager.load(name,Texture.class);
        assetManager.finishLoading();
        for(int i=0;i<animationTools.length;i++) animationTools[i] = new AnimationTool();
        for(int i=0;i<animationTools.length;i++) texture[i] = assetManager.get(action[i],Texture.class);
        animationTools[0].create("Idle", texture[0],1,11,0.1f);
        animationTools[1].create("Run", texture[1],1,13,0.1f);
        animationTools[2].create("Hit", texture[2],1,18,0.2f);
        animationTools[3].create("Hurt", texture[3],1,8,0.1f);
        animationTools[4].create("Death", texture[4],1,15,0.08f);
    }
    public AnimationTool[] getAnimationTool(){
        return animationTools;
    }
}
