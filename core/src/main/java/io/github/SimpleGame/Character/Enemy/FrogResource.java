package io.github.SimpleGame.Character.Enemy;

import com.badlogic.gdx.graphics.Texture;
import io.github.SimpleGame.Tool.AnimationTool;

public class FrogResource extends EnemyAnimation{
    private final AnimationTool[] animationTools;

    public FrogResource(){
        animationTools = new AnimationTool[5];
        Texture[] texture = new Texture[5];
        String[] action = {
            "Enemy/Frog/frog_idle.png",
            "Enemy/Frog/frog_walk.png",
            "Enemy/Frog/frog_attack.png",
            "Enemy/Frog/frog_hurt.png",
            "Enemy/Frog/frog_dead.png"
        };
        for(String  name: action) assetManager.load(name,Texture.class);
        assetManager.finishLoading();
        for(int i=0;i<animationTools.length;i++) animationTools[i] = new AnimationTool();
        for(int i=0;i<animationTools.length;i++) texture[i] = assetManager.get(action[i],Texture.class);
        animationTools[0].create("Idle", texture[0],1,7,0.1f);
        animationTools[1].create("Run", texture[1],1,4,0.1f);
        animationTools[2].create("Hit", texture[2],1,7,0.1f);
        animationTools[3].create("Hurt", texture[3],1,5,0.1f);
        animationTools[4].create("Death", texture[4],1,9,0.1f);
    }
    public AnimationTool[] getAnimationTool(){
        return animationTools;
    }
}
