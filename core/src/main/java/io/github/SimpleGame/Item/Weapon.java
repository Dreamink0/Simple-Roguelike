package io.github.SimpleGame.Item;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import io.github.SimpleGame.Character.Player.Player;
import io.github.SimpleGame.Magic.BodyPool;
import io.github.SimpleGame.Tool.AnimationTool;

import java.util.Random;

public class Weapon {
    private static final AssetManager assetManager = new AssetManager();
    private final World world;
    private Boolean isEquip = false;
    private final WeaponRender weaponRender;
    private WeaponAttribute weaponAttribute;
    private WeaponEffects weaponEffects;
    private final int ID;
    private final int weaponID;
    private final Boolean flag = false;
    public Weapon(World world, float x, float y, float scale) {
        this.world = world;
        Random random = new Random();
        ID = 0; //random.nextInt(2);
        weaponID=23;//random.nextInt(29);
        assetManager.load("Items/Weapon"+ID+"/"+weaponID+".png", Texture.class);
        assetManager.finishLoading();
        Texture texture = assetManager.get("Items/Weapon"+ID+"/"+weaponID+".png", Texture.class);
        WeaponHitBox weaponHitBox = new WeaponHitBox(texture, world);
        weaponHitBox.create(x,y,scale);
        weaponRender = new WeaponRender(weaponHitBox);
        weaponAttribute = new WeaponAttribute();
    }
    public void render(SpriteBatch batch,SpriteBatch UIbatch,Player player) {
         weaponRender.render(batch,UIbatch,player);
         if(!isEquip){
             if(weaponRender.isEquip()){
                 isEquip = true;
                 weaponAttribute = WeaponAttribute.readData(ID,weaponID,player);
                 weaponAttribute.setData(player);
                 weaponEffects = new WeaponEffects(ID,weaponID,player);
             }
         }
         if(weaponEffects!=null){
             if(Gdx.input.isKeyPressed(Input.Keys.J)){
                 weaponEffects.render(batch);
             }
         }
    }
    public void dispose(){
        if(weaponRender!=null){
            weaponRender.dispose();
        }
        if(weaponEffects!=null){
            weaponEffects.dispose();
        }
        assetManager.dispose();
    }

    public World getWorld() {
        return world;
    }

    public Boolean getFlag() {
        return flag;
    }
}
