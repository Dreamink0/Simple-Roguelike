package io.github.SimpleGame.Item;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import io.github.SimpleGame.Character.Player.Player;
import io.github.SimpleGame.Tool.AnimationTool;

import java.util.ArrayList;
import java.util.Random;

public class Weapon {
    private static final AssetManager assetManager = new AssetManager();
    private final World world;
    private boolean isEquip = false;
    private final WeaponRender weaponRender;
    private WeaponAttribute weaponAttribute;
    private WeaponEffects weaponEffects;
    private final int ID;
    private final int weaponID;
    public static final ArrayList<Weapon> WEAPONS = new ArrayList<>();
    public static final ArrayList<WeaponRender> globalEquippedWeapons = new ArrayList<>();
    public static boolean FLAG=false;
    private final Random random=new Random();
    private final int[] num = {0,1,2,3,4,5,6,7,12,23};
    public Weapon(World world, float x, float y, float scale) {
        this.world = world;
        ID =0; //random.nextInt(2);
        weaponID =num[random.nextInt(num.length)];
        assetManager.load("Items/Weapon" + ID + "/" + weaponID + ".png", Texture.class);
        assetManager.finishLoading();
        Texture texture = assetManager.get("Items/Weapon" + ID + "/" + weaponID + ".png", Texture.class);
        WeaponHitBox weaponHitBox = new WeaponHitBox(texture, world);
        weaponHitBox.create(x, y, scale);
        weaponRender = new WeaponRender(weaponHitBox, globalEquippedWeapons,weaponID);
        weaponAttribute = new WeaponAttribute();
        WEAPONS.add(this);
    }

    public void render(SpriteBatch batch, SpriteBatch UIbatch, Player player) {
        if (player.isIsequipped() && weaponRender.isEquip()) {
            weaponRender.getWeaponIndex();
        }

        weaponRender.render(batch, UIbatch, player);

        //修复装备状态检测逻辑
        boolean wasEquipped = isEquip;
        isEquip = player.isIsequipped() && weaponRender.isEquip();

        if (!wasEquipped && isEquip) {
            //武器刚被装备时初始化属性
            player.weaponID = weaponID;
            weaponAttribute = weaponAttribute.readData(ID, weaponID);
            weaponAttribute.setData(player);
            weaponEffects = new WeaponEffects(ID, weaponID, player);
        } else if (isEquip && Gdx.input.isKeyJustPressed(Input.Keys.Q)||player.checkDeath()) {
            //当前武器被卸下时重置状态
            isEquip = false;
            if (weaponEffects != null) {
                weaponEffects.dispose();
                weaponEffects = null;
            }
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.Q)){
            weaponAttribute.resetData(player);
        }
        if(isEquip&&weaponEffects!=null&&Gdx.input.isKeyPressed(Input.Keys.J)){
            FLAG = true;
        }
        if(FLAG){
            if (weaponEffects != null) {
                weaponEffects.render(batch);
            }
        }
    }

    public void dispose() {
        if (weaponRender != null) {
            weaponRender.dispose();
        }
        if (weaponEffects != null) {
            weaponEffects.dispose();
        }
    }

    public World getWorld() {
        return world;
    }
}
