package io.github.SimpleGame.Item;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.SimpleGame.Character.Player.Player;
import io.github.SimpleGame.Tool.AnimationTool;

public class WeaponEffects {
    public int ID;
    public int WeaponID;
    private static Player player;
    private AnimationTool[] animationTool;
    WeaponEffectsTextureManager manager;
    public WeaponEffects(int ID,int WeaponID,Player player){
        this.ID=ID;
        this.WeaponID=WeaponID;
        WeaponEffects.player =player;
        manager = new WeaponEffectsTextureManager();
        WeaponEffectsTextureManager.loadAssets();
        manager.create(ID,WeaponID);
        animationTool=manager.getAnimation();//获得基础特效，而渲染方式可以重写，然后组合更多特效,记得用get获得动画
    }
    public void render(SpriteBatch batch){
        AnimationTool[] animationTools = animationTool;
        batch.begin();
        if(ID==0){
            if(WeaponID==0){
                animationTools[0].render(batch,player.getX(),player.getY(),0.1f,true);
            }
            if(WeaponID==23){
                batch.setColor(255,255,6,0.5f);
                animationTools[0].render(batch,player.getX(),player.getY(),0.15f,false);
                if(animationTools[0].isAnimationFinished()){
                    animationTools[1].render(batch,player.getX(),player.getY(),0.15f,false);
                   if(animationTools[1].isAnimationFinished()){
                       animationTools[2].render(batch,player.getX(),player.getY(),0.15f,false);
                   }
                }else if(animationTools[2].isAnimationFinished()){
                    animationTools[0].resetStateTime();
                    animationTools[1].resetStateTime();
                    animationTools[2].resetStateTime();
                    batch.setColor(1,1,1,1);
                }

            }
        }
        batch.setColor(1,1,1,1);
        batch.end();
    }
    public AnimationTool[] getAnimationTool() {
        return animationTool;
    }
    public void dispose(){
        if(animationTool!=null){
            for(AnimationTool animationTool:animationTool){
                animationTool.dispose();
            }
        }
        manager.dispose();
    }
}
