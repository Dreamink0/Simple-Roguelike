package io.github.SimpleGame.Character.Enemy;

import com.badlogic.gdx.graphics.Texture;
import io.github.SimpleGame.Resource.Game;
import io.github.SimpleGame.Tool.AnimationTool;

public class GoblinResource extends EnemyAnimation{
    private final AnimationTool[] animationTools;

    public GoblinResource(){
        animationTools = new AnimationTool[5];
        Texture[] texture = new Texture[5];
        String[] action = {
            "Enemy/goblin/goblin scout - silhouette all animations-idle.png",
            "Enemy/goblin/goblin scout - silhouette all animations-run.png",
            "Enemy/goblin/goblin scout - silhouette all animations-hit.png",
            "Enemy/goblin/goblin scout - silhouette all animations-hurt.png",
            "Enemy/goblin/goblin scout - silhouette all animations-death 1.png"
        };
        for(String  name: action) assetManager.load(name,Texture.class);
        assetManager.finishLoading();
        for(int i=0;i<animationTools.length;i++) animationTools[i] = new AnimationTool();
        for(int i=0;i<animationTools.length;i++) texture[i] = assetManager.get(action[i],Texture.class);
        animationTools[0].create("Idle", texture[0],1,8,0.1f);
        animationTools[1].create("Run", texture[1],1,8,0.1f);
        animationTools[2].create("Hit", texture[2],1,3,0.1f);
        animationTools[3].create("Hurt", texture[3],1,3,0.1f);
        animationTools[4].create("Death", texture[4],1,12,0.1f);
    }
    public AnimationTool[] getAnimationTool(){
        return animationTools;
    }
}
