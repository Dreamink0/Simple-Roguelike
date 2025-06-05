package io.github.SimpleGame.Item;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.SimpleGame.Character.Player.Player;
import io.github.SimpleGame.Tool.AnimationTool;

public class WeaponEffects {
    public int ID;
    public int WeaponID;
    private static Player player;
    private static AnimationTool animationTool;
    public WeaponEffects(int ID,int WeaponID,Player player){
        this.ID=ID;
        this.WeaponID=WeaponID;
        WeaponEffects.player =player;
        WeaponEffectsTextureManager manager = new WeaponEffectsTextureManager();
        WeaponEffectsTextureManager.loadAssets();
        animationTool= manager.create(ID,WeaponID);
    }
    public void render(SpriteBatch batch,WeaponAttribute Attribute){
        int flag=1;
        if(player.getPlayerController().isFlipped){
            flag=-1;
        }
        batch.begin();
        animationTool.render(batch,player.getX()+Attribute.getRange()*flag,player.getY()+Attribute.getRange(),0.2f,false);
        batch.end();
    }

    public static AnimationTool getAnimationTool() {
        return animationTool;
    }
}
