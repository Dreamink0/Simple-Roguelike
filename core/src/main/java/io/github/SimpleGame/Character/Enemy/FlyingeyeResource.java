package io.github.SimpleGame.Character.Enemy;

import com.badlogic.gdx.graphics.Texture;
import io.github.SimpleGame.Tool.AnimationTool;

public class FlyingeyeResource extends EnemyAnimation{
    private final AnimationTool[] animationTools;

    public FlyingeyeResource(){
        animationTools = new AnimationTool[5];
        Texture[] texture = new Texture[5];
        String[] action = {
            "Enemy/Flyingeye/Flight.png",
            "Enemy/Flyingeye/Flight.png",
            "Enemy/Flyingeye/Attack.png",
            "Enemy/Flyingeye/Take Hit.png",
            "Enemy/Flyingeye/Death.png"
        };
        for(String  name: action) assetManager.load(name,Texture.class);
        assetManager.finishLoading();
        for(int i=0;i<animationTools.length;i++) animationTools[i] = new AnimationTool();
        for(int i=0;i<animationTools.length;i++) texture[i] = assetManager.get(action[i],Texture.class);
        animationTools[0].create("Idle", texture[0],1,8,0.1f);
        animationTools[1].create("Run", texture[1],1,8,0.1f);
        animationTools[2].create("Hit", texture[2],1,8,0.15f);
        animationTools[3].create("Hurt", texture[3],1,4,0.1f);
        animationTools[4].create("Death", texture[4],1,4,0.1f);
    }
    public AnimationTool[] getAnimationTool(){
        return animationTools;
    }
}
