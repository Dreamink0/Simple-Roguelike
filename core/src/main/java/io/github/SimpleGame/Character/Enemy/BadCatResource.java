package io.github.SimpleGame.Character.Enemy;

import com.badlogic.gdx.graphics.Texture;
import io.github.SimpleGame.Tool.AnimationTool;

public class BadCatResource extends EnemyAnimation{
    private final AnimationTool[] animationTools;

    public BadCatResource(){
        animationTools = new AnimationTool[5];
        Texture[] texture = new Texture[5];
        String[] action = {
            "Enemy/VeryVeryVeryVeryVeryBadCat/Badcat-idle.png",
            "Enemy/VeryVeryVeryVeryVeryBadCat/Badcat-run.png",
            "Enemy/VeryVeryVeryVeryVeryBadCat/Badcat-Hit.png",
            "Enemy/VeryVeryVeryVeryVeryBadCat/Badcat-hurt.png",
            "Enemy/VeryVeryVeryVeryVeryBadCat/BadCat-dead.png"
        };
        for(String  name: action) assetManager.load(name,Texture.class);
        assetManager.finishLoading();
        for(int i=0;i<animationTools.length;i++) animationTools[i] = new AnimationTool();
        for(int i=0;i<animationTools.length;i++) texture[i] = assetManager.get(action[i],Texture.class);
        animationTools[0].create("Idle", texture[0],1,12,0.08f);
        animationTools[1].create("Run", texture[1],1,9,0.08f);
        animationTools[2].create("Hit", texture[2],1,11,0.08f);
        animationTools[3].create("Hurt", texture[3],1,12,0.08f);
        animationTools[4].create("Death", texture[4],1,4,0.08f);
    }
    public AnimationTool[] getAnimationTool(){
        return animationTools;
    }
}
