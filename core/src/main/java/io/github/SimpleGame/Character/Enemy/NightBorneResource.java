package io.github.SimpleGame.Character.Enemy;

import com.badlogic.gdx.graphics.Texture;
import io.github.SimpleGame.Tool.AnimationTool;

public class NightBorneResource extends EnemyAnimation{
    private final AnimationTool[] animationTools;
    public NightBorneResource (){
        animationTools = new AnimationTool[5];
        Texture[] texture = new Texture[5];
        String[] action = {
            "Enemy/NightBorne/NightBorneidle-sheet.png",
            "Enemy/NightBorne/NightBornerun-sheet.png",
            "Enemy/NightBorne/NightBorneattack-sheet.png",
            "Enemy/NightBorne/NightBornehurt-sheet.png",
            "Enemy/NightBorne/NightBornedead-sheet.png"
        };
        for(String  name: action) assetManager.load(name,Texture.class);
        assetManager.finishLoading();
        for(int i=0;i<animationTools.length;i++) animationTools[i] = new AnimationTool();
        for(int i=0;i<animationTools.length;i++) texture[i] = assetManager.get(action[i],Texture.class);
        animationTools[0].create("Idle", texture[0],1,9,0.08f);
        animationTools[1].create("Run", texture[1],1,6,0.08f);
        animationTools[2].create("Hit", texture[2],1,12,0.08f);
        animationTools[3].create("Hurt", texture[3],1,5,0.08f);
        animationTools[4].create("Death", texture[4],1,23,0.05f);
    }
    public AnimationTool[] getAnimationTool(){
        return animationTools;
    }
}
