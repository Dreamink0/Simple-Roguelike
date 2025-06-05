package io.github.SimpleGame.Item;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import io.github.SimpleGame.Character.Player.Player;
import io.github.SimpleGame.Config;
import io.github.SimpleGame.Tool.AnimationTool;
import io.github.SimpleGame.Tool.Listener;

import java.util.Random;

import static io.github.SimpleGame.Config.PIXELS_PER_METER;

public class Weapon {
    private static final AssetManager assetManager = new AssetManager();;
    private final World world;
    private Boolean isEquip = false;
    private final WeaponRender weaponRender;
    private WeaponAttribute weaponAttribute;
    private WeaponEffects weaponEffects;
    public Weapon(World world, float x, float y, float scale) {
        this.world = world;
        Random random = new Random();
        int temp = random.nextInt(29);
        int ID = random.nextInt(2);
        assetManager.load("Items/Weapon"+0+"/"+0+".png", Texture.class);
        assetManager.finishLoading();
        Texture texture = assetManager.get("Items/Weapon"+0+"/"+0+".png", Texture.class);
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
                 weaponEffects=new WeaponEffects(0,0,player);
                 weaponAttribute=WeaponAttribute.readData(0,0,player);
                 float Damage = player.getAttributeHandler().getDamage();
                 float Range = player.getAttributeHandler().getAttackrange();
                 float Speed =  player.attackCooldown;
                 player.getAttributeHandler().setDamage(Damage+weaponAttribute.getDamage());
                 player.getAttributeHandler().setAttackrange(Range+weaponAttribute.getRange());
                 player.attackCooldown = Speed-weaponAttribute.getAttackSpeed();
             }
         }
         if(weaponEffects!=null){
             AnimationTool animationTool=weaponEffects.getAnimationTool();
             if(Gdx.input.isKeyPressed(Input.Keys.J)&&!animationTool.isAnimationFinished()){
                 weaponEffects.render(batch,weaponAttribute);
             }
             animationTool.resetStateTime();
         }

    }
    public void dispose(){
        weaponRender.dispose();
    }

    public World getWorld() {
        return world;
    }
}
